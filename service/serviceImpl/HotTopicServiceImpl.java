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
		

		List<String> topicIdList = topicDao.getAllTopic(); // 获取所有的Topicid,返回列表

		Map<String, Double> topicMap = new HashMap<String, Double>();

		for (int i = 0; i < topicIdList.size(); i++) {
			String topicId = topicIdList.get(i); // 循环取出topicId

			if (DateTest(topicDao.getTimeofTopic(topicId)) == true) {         //调用自己定义的DateTest方法，筛选近七天的话题

				List<String> weiboIdList = topicDao.getAllWeibo(topicId); // 根据topicId查询包含此话题的所有微博

				double hot = 0.0;

				for (int j = 0; j < weiboIdList.size(); j++) {
					String weiboId = weiboIdList.get(j);             //根据TopicId查询对应的weiboid
					String commentNumber = weiboDao.getCommentNumber(weiboId);    //调用weiboDao查询该条微博的评论数
					String forwordNumber = weiboDao.getForwardNumber(weiboId);    //调用weiboDao查询该条微博的转发数
					String likeNumber = weiboDao.getLikeNumber(weiboId);          //调用weiboDao查询该条微博的点赞数

					hot = hot + 0.2 * Float.parseFloat(likeNumber) + 0.4
							* Float.parseFloat(forwordNumber) + 0.4
							* Float.parseFloat(commentNumber);            //根据三个变量设置权重自定义话题热度，将一条话题的对应的微博的热度进行累加，即是此条话题的热度
				}
				topicMap.put(topicId, hot);     //将topicid和对应的热度以键值对的形式存入Map中
			}

		}

		List<Topic> topicList = null;


		topicMap = sortByValue(topicMap);          //调用自己定义的排序算法，按照Map的value降序排列topicMap

		int k = 1;

		for (String key : topicMap.keySet()) {    //根据传入的pageIndex和numberPerPage参数，将结果分页

			if (k >= ((pageIndex - 1) * numberPerPage) + 1
					&& k <= pageIndex * numberPerPage) {
				String content = topicDao.getContent(key);
				String date = topicDao.getTimeofTopic(key);
				Topic topic = new Topic();
				topic.setTopic(content);
				topic.setDate(date);
				topicList.add(topic);              //根据topicid创建新的Topic对象存入TopicList
			}
			k++;
		}

		return topicList;              //返回TopicList
	}

        //Map排序算法，根据value将Map降序重新排序
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

        //日期筛选算法，根据系统时间查询近一周的日期，检测传入的日期是否在近一周中，是则返回True
	private boolean DateTest(String date) {
        Date newDate = new Date();      //得到系统时间
        
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");    //规定日期格式
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        
        calendar.add(Calendar.DATE, -4);
        Date startDate = calendar.getTime();
        //String startDate = format.format(date1);
        //System.out.println(out1);
        calendar.add(Calendar.DATE, 7);
        Date endDate = calendar.getTime();             //获取当前日期近一周的起始日期和结束日期
        //String endDate = format.format(date2);
        
        Date dateTopic = format.parse(date,new ParsePosition(0));
        
        if(dateTopic.after(startDate)&&dateTopic.before(endDate)){         //判断传入的日期是否在之前得到的日期范围内，在即返回True
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
