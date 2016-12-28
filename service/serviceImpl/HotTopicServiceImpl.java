package cn.edu.bjtu.weibo.impl;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

import cn.edu.bjtu.weibo.model.Topic;
import cn.edu.bjtu.weibo.service.*;
import cn.edu.bjtu.weibo.dao.*;

@Service
public class HotTopicServiceImpl implements HotTopicService,Comparator{


    @Autowired
    private TopicDAO topicDao;
    @Autowired
    private WeiboDAO weiboDao;
    
    private static String[] array;
    
    public static int length = 0;
    
   // public static Map<String, Double> topicMap = new HashMap<String, Double>();
	@Override
	public List<Topic> HotTopic(int pageIndex, int numberPerPage) {
		// TODO Auto-generated method stub
		

		List<String> topicIdList = topicDao.getAllTopic();             //调用topicDao中的接口获取所有话题得到话题列表
		
		array = new String[topicIdList.size()];           //定义存储话题Id的String类型数组
		//array = new String[1000000];
		
		List<Topic> newTopicList = null;                 //定义存储排序后的新话题列表
		
		for (int i = 0; i < topicIdList.size(); i++) {
			String topicId = topicIdList.get(i); 

			if (DateTest(topicDao.getTimeofTopic(topicId)) == true) {     //根据自己编写的日期判断方法，读取系统时间，判断该话题是否是近一周的话题      
				
				array[i] = topicId;        //判断是近一周的话题，将话题Id存入array数组中
				
				length++;

				/*List<String> weiboIdList = topicDao.getAllWeibo(topicId); 

				double hot = 0.0;

				for (int j = 0; j < weiboIdList.size(); j++) {
					String weiboId = weiboIdList.get(j);             
					String commentNumber = weiboDao.getCommentNumber(weiboId);   
					String forwordNumber = weiboDao.getForwardNumber(weiboId);    
					String likeNumber = weiboDao.getLikeNumber(weiboId);          

					hot = hot + 0.2 * Float.parseFloat(likeNumber) + 0.4
							* Float.parseFloat(forwordNumber) + 0.4
							* Float.parseFloat(commentNumber);            
				}*/
				//topicMap.put(topicId, hot);     
			}

		}

		/*List<String> newTopicIdList = null;
		
		 Random rand = new Random();
			for(int i = 0;i<1000000;i++){
				array[i] = "topicId_"+String.valueOf(i);
				topicMap.put("topicId_"+String.valueOf(i), rand.nextDouble()%100+rand.nextInt()%10);
			}*/
			
			forkJoinSort();               //根据自己编写的多线程排序算法进行话题排序
			
			//int j = 0;
			/*for(int j = 0;j<1000000;j++){
				
				
				System.out.println(array[j]+"   "+topicMap.get(array[j]));
				
			}*/


		//topicMap = sortByValue(topicMap);          

	/*	int k = 1;

		for (String key : topicMap.keySet()) {    

			if (k >= ((pageIndex - 1) * numberPerPage) + 1
					&& k <= pageIndex * numberPerPage) {
				String content = topicDao.getContent(key);
				String date = topicDao.getTimeofTopic(key);
				Topic topic = new Topic();
				topic.setTopic(content);
				topic.setDate(date);
				topicList.add(topic);             
			}
			k++;
		}*/
			
			for(int j = 0; j<array.length ; j++){
				if(j >= ((pageIndex - 1) * numberPerPage)
						&& j <= pageIndex * numberPerPage - 1){          //根据传进来的分页参数，将排序好的话题Id分页
					String content = topicDao.getContent(array[j]);
					String date = topicDao.getTimeofTopic(array[j]);
					String owner = topicDao.getOwnerTopic(array[j]);
					Topic topic = new Topic();
					topic.setTopic(content);
					topic.setDate(date); 
                                        topic.setOwner(owner);					//根据话题Id创建话题
					newTopicList.add(topic);                        //将创建好的话题添加到话题列表中      
				}
			}

		//return null;
		return newTopicList;                            //返回所需要的那一页按照话题热度排序好的话题列表     
	}

        
	/*private <String, Double extends Comparable<? super Double>> Map<String, Double> sortByValue(
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
	}*/

    //日期判断方法，根据用户传进来的日期字符串，判断是否是近一周的日期，是则返回True，否则返回False
	private boolean DateTest(String date) {
        Date newDate = new Date();            //读取系统时间
        
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");    //定义日期格式
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        
        calendar.add(Calendar.DATE, -4);
        Date startDate = calendar.getTime();
        //String startDate = format.format(date1);
        //System.out.println(out1);
        calendar.add(Calendar.DATE, 7);
        Date endDate = calendar.getTime();                      //获取系统日期近一周的起始日期和结束日期，分别为startDate和endDate  
        //String endDate = format.format(date2);
        
        Date dateTopic = format.parse(date,new ParsePosition(0));
        
        if(dateTopic.after(startDate)&&dateTopic.before(endDate)){         
        	return true;
        }
        
		return false;
	}

	/*public static void main(String[] args){
        HotTopicServiceImpl a = new HotTopicServiceImpl();
		 
		 a.HotTopic(2, 10);
	}*/
	
	
	//自定义排序算法，根据话题的热度将话题Id数组进行降序排列
	public static void forkJoinSort() {
		//long beginTime = System.currentTimeMillis();
		ForkJoinPool forkJoinPool = new ForkJoinPool();

		forkJoinPool.submit(new SortTask(0, HotTopicServiceImpl.length - 1, array));
		forkJoinPool.shutdown();
		try {
			forkJoinPool.awaitTermination(10000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//long endTime = System.currentTimeMillis();
		//System.out.println("sort file:" + (endTime - beginTime) + "ms");
	}

	//重写compare方法，使其根据话题的自定义热度进行降序排列
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub

		String topicId1 = (String) arg0;
		String topicId2 = (String) arg1;

		List<String> weiboIdList1 = topicDao.getAllWeibo(topicId1); 
		List<String> weiboIdList2 = topicDao.getAllWeibo(topicId2);           //调用TopicDAO中的接口根据话题Id获取该话题对应的微博列表

		double hot1 = 0.0;
		double hot2 = 0.0;

		for (int j = 0; j < weiboIdList1.size(); j++) {
			String weiboId = weiboIdList1.get(j);             
			String commentNumber = weiboDao.getCommentNumber(weiboId);      //获取该微博的评论数
			String forwordNumber = weiboDao.getForwardNumber(weiboId);      //获取该微博的转发数
			String likeNumber = weiboDao.getLikeNumber(weiboId);            //获取该微博的点赞数

			hot1 = hot1 + 0.2 * Float.parseFloat(likeNumber) + 0.4
					* Float.parseFloat(forwordNumber) + 0.4
					* Float.parseFloat(commentNumber);                      //自定义权重，将该话题对应的所有微博的自定义热度进行累加即是该条话题的热度    
		}
		
		for (int i = 0; i < weiboIdList2.size(); i++) {
			String weiboId = weiboIdList1.get(i);             
			String commentNumber = weiboDao.getCommentNumber(weiboId);   
			String forwordNumber = weiboDao.getForwardNumber(weiboId);    
			String likeNumber = weiboDao.getLikeNumber(weiboId);          

			hot2 = hot2 + 0.2 * Float.parseFloat(likeNumber) + 0.4
					* Float.parseFloat(forwordNumber) + 0.4
					* Float.parseFloat(commentNumber);            
		}
		
		/*hot1 = HotTopicServiceImpl.topicMap.get(topicId1);
		hot2 = HotTopicServiceImpl.topicMap.get(topicId2);*/
		
		if (hot1 > hot2) {
			return -1;    
		} else if (hot1 < hot2) {
			return 1;
		} else {
			return 0;                //根据话题的热度进行降序排列
		}
	}
	
	 
}

//多线程排序
@SuppressWarnings("serial")
class SortTask extends RecursiveAction {
	
	    @Autowired
	    private TopicDAO topicDao;
	    @Autowired
	    private WeiboDAO weiboDao;

	final int start;
	final int end;
	private int THRESHOLD = 30_0000;
	//@Autowired
	//private UserDAO userDao;
	final String[] topicIdArray;

	public SortTask(String[] topicIdArray) {

		this.start = 0;
		this.end = HotTopicServiceImpl.length - 1;
		this.topicIdArray = topicIdArray;
	}

	public SortTask(int start, int end, String[] topicIdArray) {
		this.start = start;
		this.end = end;
		this.topicIdArray = topicIdArray;
	}

	@Override
	protected void compute() {
		if (end - start < THRESHOLD) {
			
			HotTopicServiceImpl top = new HotTopicServiceImpl();
			Arrays.sort(topicIdArray, start, end + 1,top);

		} else {
			int pivot = partition(start, end, topicIdArray);
			SortTask left = null;
			SortTask right = null;
			if (start < pivot - 1) {
				left = new SortTask(start, pivot - 1, topicIdArray);
			}
			if (pivot + 1 < end) {
				right = new SortTask(pivot + 1, end, topicIdArray);
			}
			if (left != null) {
				left.fork();
			}
			if (right != null) {
				right.fork();
			}
		}
	}

	private int partition(int start, int end, String[] topicIdArray) {
		int i = start;
		int j = end;
		String topicId = null;
		topicId = topicIdArray[i];
		while (i < j) {
			while (i < j && getValue(topicIdArray[j])<getValue(topicId)){//baseContentArray[j].getCommentNumber() + baseContentArray[j].getLike() > user.getCommentNumber()+ user.getLike()) {
				j--;
			}
			if (i < j) {
				topicIdArray[i++] = topicIdArray[j];
			}
			while (i < j &&getValue(topicIdArray[i])>getValue(topicId)){// baseContentArray[i].getCommentNumber() + baseContentArray[i].getLike() < user.getCommentNumber()+ user.getLike()) {
				i++;
			}
			if (i < j) {
				topicIdArray[j--] = topicIdArray[i];
			}
		}
		topicIdArray[i] = topicId;
		return i;
	}
	
	//获取该条话题的热度
	public double getValue(String topicId){
		List<String> weiboIdList = topicDao.getAllWeibo(topicId); 
		
		

		double hot = 0.0;

		for (int j = 0; j < weiboIdList.size(); j++) {
			String weiboId = weiboIdList.get(j);             
			String commentNumber = weiboDao.getCommentNumber(weiboId);   
			String forwordNumber = weiboDao.getForwardNumber(weiboId);    
			String likeNumber = weiboDao.getLikeNumber(weiboId);          

			hot = hot + 0.2 * Float.parseFloat(likeNumber) + 0.4
					* Float.parseFloat(forwordNumber) + 0.4
					* Float.parseFloat(commentNumber);            
		}
		
		//Random rand =  new Random();
		
		//return hot;
		//return HotTopicServiceImpl.topicMap.get(topicId);
		
		return hot;
	}

}
