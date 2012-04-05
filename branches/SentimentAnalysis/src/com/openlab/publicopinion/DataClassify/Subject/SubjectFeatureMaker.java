package com.openlab.publicopinion.DataClassify.Subject;

import java.util.Map;

import com.openlab.publicopinion.DataClassify.Dictionary;
import com.openlab.publicopinion.DataClassify.FeatureMaker;
import com.openlab.publicopinion.DataClassify.KeyWord;
import com.openlab.publicopinion.DataClassify.TextClsWordFilter;
import com.openlab.publicopinion.DataClassify.WordFilter;

/**
 * 从文本中获取特征的类
 * @author HuHaixiao
 *
 */
public class SubjectFeatureMaker implements FeatureMaker {
	/**字典目录*/
	private  String dicPath;
	/**保存字典的映射表*/
	private  Map<String, Integer> dic;
	/**主题分类词语过滤器*/
	private static WordFilter wf;
	
	/**
	 * 获取词典中有的特征
	 * @param dic
	 */
	public SubjectFeatureMaker(String dictionaryPath){
		this.dicPath=dictionaryPath;
		dic=(new Dictionary()).loadDictionary(dicPath);
		wf=new TextClsWordFilter(dic);
	}
	
	/**
	 * 获取词典中有的特征
	 * @param dic
	 */
	public SubjectFeatureMaker(Map<String,Integer> dic){
		this.dic=dic;
		wf=new TextClsWordFilter(dic);
	}
	
	/**
	 * 不查词典，获取所有的特征
	 */
	public SubjectFeatureMaker(){
		this.dic=null;
		wf=new TextClsWordFilter();
	}

	@Override
	public String[] getFeatureFromDoc(String text) {
		// TODO Auto-generated method stub
		KeyWord words = new KeyWord();
		return words.WordsExtract(text, wf, false);
	}

	@Override
	public int estimateMapSize() {
		// TODO Auto-generated method stub
		return dic.size();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
