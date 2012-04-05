package com.openlab.publicopinion.DataClassify;


import ICTCLAS.I3S.AC.ICTCLAS50;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
/**
 * 中文分词辅助类
 * @author HuHaixiao
 *
 */
public class KeyWord {

	/**
	 * 分词,不合并重复的词语，
	 * 保证原文本词语的顺序
	 * @param sInput 待分词的文本字符串
	 * @param wf 词语过滤器，若为null,则直接将词语及其词性信息
	 * @param posTagged 是否将词性标记一同保存在结果中
	 * @return 分词的结果
	 * @throws IOException
	 */
	public String[] WordsExtract(String sInput,WordFilter wf,boolean posTagged) 
	{
			String nativeStr="";
			try {
				nativeStr = WordsSplit(sInput);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			nativeStr.trim();
			String[] tempString=nativeStr.split("[\\s　]+");//去掉连续空格 全角空格，半角空格
			ArrayList<String> wordDiff=new ArrayList<String>(tempString.length);
			for(int i=0;i<tempString.length-1;i++)
			{
				String[] word=tempString[i].trim().split("/");
				if(word.length!=2)//出现分词错误
					continue;			//跳过此条
				if(wf==null || !wf.isStoped(word[0], word[1])){//过滤器为空或者未被停用，则加入列表
					if(posTagged)
						wordDiff.add(tempString[i]);//将词语及其词性加入列表
					else
						wordDiff.add(word[0]);
				}
			}	
					
			return wordDiff.toArray(new String[wordDiff.size()]);
	}

	/**
	 * 从文本字符串中取词，合并重复的词,无法保证原文本词语的顺序
	 * @param sInput 文本字符串
	 * @param wf 词语过滤器
	 * @param posTagged 是否将词性标记一同保存在结果中
	 * @return 词语数组
	 * @throws IOException
	 */
	public String[] WordsExtractMerge(String sInput,WordFilter wf,boolean posTagged) 
	{
			String nativeStr="";
			try {
				nativeStr = WordsSplit(sInput);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			nativeStr.trim();
			String[] tempString=nativeStr.split("[\\s　]+");//去掉连续空格
			HashSet<String> wordDiff=new HashSet<String>(tempString.length);
			for(int i=1;i<tempString.length-1;i++)
			{
				String[] word=tempString[i].trim().split("/");
				if(word.length!=2)
					continue;
				//提取名词、动词（除去 动词"是"、动词"有"和趋向动词）、形容词、区别词、状态词
				if(wf==null || !wf.isStoped(word[0], word[1])){//过滤器为空或者未被停用，则加入列表
					if(posTagged)
						wordDiff.add(tempString[i]);//将词语及其词性加入列表
					else
						wordDiff.add(word[0]);
				}
			}	

			return wordDiff.toArray(new String[wordDiff.size()]);
	}
	
	/**
	 * 直接调用ICTCLAS分词,结果保存为字符串
	 * @param sInput
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String WordsSplit(String sInput) throws UnsupportedEncodingException{
		ICTCLAS50 testICTCLAS30 = new ICTCLAS50();

		String argu = ".\\lib\\libICTCLAS";
		if (testICTCLAS30.ICTCLAS_Init((argu).getBytes(FileEncode.charset)) == false)
		{
			System.out.println("Init Fail!");
			return null;
		}
		testICTCLAS30.ICTCLAS_SetPOSmap(0);

		byte nativeBytes[] = testICTCLAS30.ICTCLAS_ParagraphProcess((sInput+"\0").getBytes(FileEncode.charset),0, 1);
		String nativeStr = new String(nativeBytes, 0, nativeBytes.length, FileEncode.charset);
		
		testICTCLAS30.ICTCLAS_Exit();
		
		return nativeStr;
		
	}
	
	public static void main(String[] args) {
		String text="地毯很赃，床垫下的弹簧凸出来，睡得很不舒服，隔音很差，很吵，离市区有段距离， 不是很方便 ，我觉得糟糕透了"+
		"补充点评 2007年7月2日 ： 酒店的外观很美 很棒 但进去房间以后， 大失所望， 为什么里外会差这么多， 本来打算住二天后来住一天就退房了";
				
		KeyWord words = new KeyWord();
		String[] re=words.WordsExtract(text, null,true);
		for (int i = 0; i < re.length; i++) {
			System.out.println(re[i]);
		}
//		String sInput="我是helloworlf!,,";
//		try {
//			for (byte b : sInput.getBytes("utf8")) {
//			System.out.println(b);
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
}
