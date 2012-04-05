package com.openlab.publicopinion.DataClassify;

/**
 * 词语过滤类
 * <p>实现此接口的类可以判断给定词性的词语是否被过滤掉
 * @author HuHaixiao
 *
 */
public interface WordFilter {
	/**
	 * 判断词语是否停用
	 * @param word 词语
	 * @param pos 词性:以ICTCLAS词性标注为准
	 * @return true,如果被停用；false,如果可以使用
	 */
	boolean isStoped(String word,String pos);
}
