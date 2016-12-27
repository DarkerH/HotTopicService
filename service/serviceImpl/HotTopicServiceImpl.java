package cn.edu.bjtu.weibo.impl;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.edu.bjtu.weibo.model.Topic;
import cn.edu.bjtu.weibo.service.*;
import cn.edu.bjtu.weibo.dao.*;

@Service
public class HotTopicServiceImpl implements HotTopicService {
        @Autowired
	private TopicDAO topicDao;
        @Autowired
	private WeiboDAO weiboDao;

	@Override
	public List<Topic> HotTopic(int pageIndex, int numberPerPage) {
		// TODO Auto-generated method stub
		

		List<String> topicIdList = topicDao.getAllTopic(); // ��ȡ���е�Topicid,�����б�

		Map<String, Double> topicMap = new HashMap<String, Double>();

		for (int i = 0; i < topicIdList.size(); i++) {
			String topicId = topicIdList.get(i); // ѭ��ȡ��topicId

			if (DateTest(topicDao.getTimeofTopic(topicId)) == true) {         //�����Լ������DateTest������ɸѡ������Ļ���

				List<String> weiboIdList = topicDao.getAllWeibo(topicId); // ����topicId��ѯ�����˻��������΢��

				double hot = 0.0;

				for (int j = 0; j < weiboIdList.size(); j++) {
					String weiboId = weiboIdList.get(j);             //����TopicId��ѯ��Ӧ��weiboid
					String commentNumber = weiboDao.getCommentNumber(weiboId);    //����weiboDao��ѯ����΢����������
					String forwordNumber = weiboDao.getForwardNumber(weiboId);    //����weiboDao��ѯ����΢����ת����
					String likeNumber = weiboDao.getLikeNumber(weiboId);          //����weiboDao��ѯ����΢���ĵ�����

					hot = hot + 0.2 * Float.parseFloat(likeNumber) + 0.4
							* Float.parseFloat(forwordNumber) + 0.4
							* Float.parseFloat(commentNumber);            //����������������Ȩ���Զ��廰���ȶȣ���һ������Ķ�Ӧ��΢�����ȶȽ����ۼӣ����Ǵ���������ȶ�
				}
				topicMap.put(topicId, hot);     //��topicid�Ͷ�Ӧ���ȶ��Լ�ֵ�Ե���ʽ����Map��
			}

		}

		List<Topic> topicList = null;


		topicMap = sortByValue(topicMap);          //�����Լ�����������㷨������Map��value��������topicMap

		int k = 1;

		for (String key : topicMap.keySet()) {    //���ݴ����pageIndex��numberPerPage�������������ҳ

			if (k >= ((pageIndex - 1) * numberPerPage) + 1
					&& k <= pageIndex * numberPerPage) {
				String content = topicDao.getContent(key);
				String date = topicDao.getTimeofTopic(key);
				Topic topic = new Topic();
				topic.setTopic(content);
				topic.setDate(date);
				topicList.add(topic);              //����topicid�����µ�Topic�������TopicList
			}
			k++;
		}

		return topicList;              //����TopicList
	}

        //Map�����㷨������value��Map������������
	private <String, Double extends Comparable<? super Double>> Map<String, Double> sortByValue(
			Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<String, Double> result = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

        //����ɸѡ�㷨������ϵͳʱ���ѯ��һ�ܵ����ڣ���⴫��������Ƿ��ڽ�һ���У����򷵻�True
	private boolean DateTest(String date) {
        Date newDate = new Date();      //�õ�ϵͳʱ��
        
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");    //�涨���ڸ�ʽ
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        
        calendar.add(Calendar.DATE, -4);
        Date startDate = calendar.getTime();
        //String startDate = format.format(date1);
        //System.out.println(out1);
        calendar.add(Calendar.DATE, 7);
        Date endDate = calendar.getTime();             //��ȡ��ǰ���ڽ�һ�ܵ���ʼ���ںͽ�������
        //String endDate = format.format(date2);
        
        Date dateTopic = format.parse(date,new ParsePosition(0));
        
        if(dateTopic.after(startDate)&&dateTopic.before(endDate)){         //�жϴ���������Ƿ���֮ǰ�õ������ڷ�Χ�ڣ��ڼ�����True
        	return true;
        }
        
		return false;
	}

	/*
	 * public static void main(String[] args){ Date
	 * 
	 * }
	 */

}
