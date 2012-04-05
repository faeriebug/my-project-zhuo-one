package com.openlab.publicopinion.DataClassify.Sentiment;

import java.util.Map;


public interface negDegProc {
	/**
	 * 识别出是否是自己可以处理的否定词形式
	 * @param sent 句子数组
	 * @return 如果可以处理则返回处理的结果，否则为null 
	 */
	String[] match(String[] sent);
	/**
	 * 加入程度副词对词语频数的影响
	 * @param dic 特征词典
	 * @param sent
	 * @return
	 */
	Map<String,Integer> matchDeg(Map<String,Integer> dic,String[] sent);
	
}
