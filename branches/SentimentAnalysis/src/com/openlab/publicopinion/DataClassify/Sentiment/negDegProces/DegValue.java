package com.openlab.publicopinion.DataClassify.Sentiment.negDegProces;

/**
 * 确定程度副词对词语影响的频数策略
 * @author HuHaixiao
 *
 */
public interface DegValue {

	/**
	 * 依据程度，返回相应的频数增加的倍数
	 * @param degree
	 * @return
	 */
	int getValue(int degree,int orignDeg);
}
