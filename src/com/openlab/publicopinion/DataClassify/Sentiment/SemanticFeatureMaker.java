package com.openlab.publicopinion.DataClassify.Sentiment;

import java.util.ArrayList;

import com.openlab.publicopinion.DataClassify.FeatureMaker;
import com.openlab.publicopinion.DataClassify.Sentiment.negDegProces.ndpCls1;

/**
 * 从句子中获取语义特征
 * 处理过程包括：
 * 否定词、程度副词处理
 * 情感词典检索
 * @author HuHaixiao
 *
 */
public class SemanticFeatureMaker implements FeatureMaker{
	//情感词典
	private SentiDictionary SD=null;
	private negDegProc ndProc;
	private SentenceSpliter SS=new SentenceSpliter();
	
	public SemanticFeatureMaker(SentiDictionary SD){
		this.SD=SD;
		ndProc=new ndpCls1(SD);
	}
	

	@Override
	public String[] getFeatureFromDoc(String text) {
		// TODO Auto-generated method stub
		String[][] sents=SS.getSentences(text);
		ArrayList<String> lisF=new ArrayList<String>();
		for (String[] sent : sents) {
			lisF.addAll(getFeatures(sent));
		}
		return lisF.toArray(new String[lisF.size()]);
	}

	@Override
	public int estimateMapSize() {
		// TODO Auto-generated method stub
		return SD.getSenti().size();
	}

	
	public ArrayList<String> getFeatures(String[] sent){
		ArrayList<String> lisD=new ArrayList<String>();
		String[] re;
		//首先进行副词处理
		re=ndProc.match(sent);//识别匹配
		for (String f : re) {
			lisD.add(f);
		}
		return lisD;		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SentiDictionary SD=new SentiDictionary();
		SentenceSpliter SS=new SentenceSpliter();
		SemanticFeatureMaker SF=new SemanticFeatureMaker(SD);
		String text="地毯很赃 床墊下的彈簧凸出來 睡得很不舒服 隔音很差 很吵 離市區有段距離 不是很方便 我覺得糟糕透了"+
"补充点评 2007年7月2日 ： 酒店的外覌很美 很棒 但進去房間以後 大失所望 為什麼裏外會差這麼多 本來打算住二天 後來住一天就退房了";
		String[][] sents=SS.getSentences(text);
		for (String[] s : sents) {
			for (String f : SF.getFeatures(s)) {
				System.out.print(f+"  ");
			}
		}
	}

}
