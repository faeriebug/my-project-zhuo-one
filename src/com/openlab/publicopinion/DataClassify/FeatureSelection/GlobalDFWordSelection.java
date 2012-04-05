package com.openlab.publicopinion.DataClassify.FeatureSelection;

import java.io.*;
import java.util.*;

import com.openlab.publicopinion.DataClassify.FeatureMaker;

/**
 * 全局DF( 文档频率)特征词选取
 * @author HuHaixiao
 *
 */
public class GlobalDFWordSelection extends FeatureSelection{
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GlobalDF";
	}
	
	@Override
	public Number getFeatureForTermInClass(String wordID, int clsID,TrainingDataManager TDM) {
		// TODO Auto-generated method stub
		return GlobalDFFS_TermForAllClass(wordID, TDM);
	}
	
	@Override
	public void ConstructFeatureDictionary(FeatureMaker feature, String libPath,	String resultFilePath,int threshold) {
		// TODO Auto-generated method stub
		TrainingDataManager TDM=new TrainingDataManager(feature,libPath);
		Map<String, int[]> mp=TDM.getMap();
		final Map<String, Integer> df=new HashMap<String, Integer>();
		
		for(String wordID:mp.keySet()){//对每一个词语
			df.put(wordID,GlobalDFFS_TermForAllClass(wordID, TDM));
		}
		//按照DF降序排列
		Object[] keys=df.keySet().toArray();
		Arrays.sort(keys,new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return df.get(o2)-df.get(o1);
			}
		});
			
		try{
			FileWriter fw=new FileWriter(resultFilePath);
			int m=0;//词典从零开始
			fw.write((threshold>df.size()?df.size():threshold)+"\n");//写入词典大小
			for(Object i:keys){
				if(m>=threshold)break;//已经取满threshold个特征词
				//System.out.println(i+","+m);
				fw.write(i+","+m+"\n");
				m++;
			}
			fw.flush();
			fw.close();
		}catch(IOException e){
				e.printStackTrace();
		}
	}
	/**
	 * 选取至多k个特征词，将结果保存在本地文件。
	 * @param dicPath 字典目录
	 *@param libPath 训练语料库目录，其下存放分好类的语料。
	 *@param resultFilePath 输出结果保存文件的路径
	 *@param k 选取的特征词数量
	 */
	public void GlobalDFFS(FeatureMaker feature,String libPath,String resultFilePath,int k){
		TrainingDataManager TDM=new TrainingDataManager(feature,libPath);
		Map<String, int[]> mp=TDM.getMap();
		final Map<String, Integer> df=new HashMap<String, Integer>();
		
		for(String wordID:mp.keySet()){//对每一个词语
			df.put(wordID,GlobalDFFS_TermForAllClass(wordID, TDM));
		}
		//按照DF降序排列
		Object[] keys=df.keySet().toArray();
		Arrays.sort(keys,new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return df.get(o2)-df.get(o1);
			}
		});
			
		try{
			FileWriter fw=new FileWriter(resultFilePath);
			int m=1;
			for(Object i:keys){
				if(m>k)break;//已经取满k个特征词
				//System.out.println(i+","+m+":"+df.get(i));
				fw.write(i+","+m+"\n");
				m++;
			}
			fw.flush();
			fw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		 
	}
	
	/**
	 * 某一词语对所有类别的DF
	 * @param mp
	 * @param wordID
	 * @param classLabel
	 * @return
	 */
	public int GlobalDFFS_TermForAllClass(String wordID,TrainingDataManager TDM){
		int df=0;
		Map<String, int[]> mp=TDM.getMap();
		for(int i=0;i<TDM.getClsNum();i++)//对每个类别
			df+=mp.get(wordID)[i];
		return df;
	}

	public static void main(String[] args) {
		//GlobalDFWordSelection DFWS=new GlobalDFWordSelection();
//		long start=System.currentTimeMillis();
//		
//		//DFWS.GlobalDFFS("Test\\dictionary.txt","TanCorpMinTrain\\","Test\\FeatureDictionary.txt",500);
//		DFWS.ConstructFeatureDictionary("Test\\dictionary.txt","TrainingData\\","Test\\FeatureDictionary.txt",500);
//		long end=System.currentTimeMillis();
//		System.out.println("程序执行："+(end-start)/1000.0+"s");
		long start=System.currentTimeMillis();
		long end=System.currentTimeMillis();
		System.out.println("程序执行："+(end-start)/1000.0+"s");
	}
	
}
