package com.openlab.publicopinion.DataClassify.FeatureSelection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.openlab.publicopinion.DataClassify.FeatureMaker;

public class LocalDFWordSelection extends FeatureSelection{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "LocalDF";
	}
	@Override
	public Number getFeatureForTermInClass(String wordID, int clsID,
			TrainingDataManager TDM) {
		// TODO Auto-generated method stub
		return LocalDFFS_TermForClass(wordID, clsID, TDM);
	}

	@Override
	public void ConstructFeatureDictionary(FeatureMaker feature, String libPath,
			String resultFilePath, int threshold) {
		// TODO Auto-generated method stub
		TrainingDataManager TDM=new TrainingDataManager(feature, libPath);
		Set<String> FinalFeature=new HashSet<String>(threshold);
		for(int i=0;i<TDM.getClsNum();i++){//对每一个类别
				int thres=(int)((double)threshold/TDM.getTotalDocNum()*TDM.getDocNumOfCls(i));//取词数量取决于此类别中文档占总文档数的比例
				
				LocalDFFS_AllTermsForClass(i, TDM, FinalFeature, thres);
				
			}
		try{
			//输出到文件中
			FileWriter fw=new FileWriter(resultFilePath);
			int m=0;//词典从零开始
			fw.write((threshold>FinalFeature.size()?FinalFeature.size():threshold)+"\n");//写入词典大小
			
			for(Iterator<String> it=FinalFeature.iterator();it.hasNext();){
				String tmp=it.next();
				//System.out.println(dicInv[tmp]+","+m);
				fw.write(tmp+","+m+"\n");
				m++;
			}
			fw.flush();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 对一个类别取词
	 * @param wordID
	 * @param clsID
	 * @param TDM
	 * @param threshold
	 * @return
	 */
	public void LocalDFFS_AllTermsForClass(int clsID,TrainingDataManager TDM,Set<String> FinalFeature,int threshold){
		final Map<String, Integer> localInfo=new HashMap<String, Integer>();
		Map<String, int[]> mp=TDM.getMap();
		Integer localvalue;
		//计算卡方值
		for(String wordID:mp.keySet()){//对每一个词语
			localvalue=LocalDFFS_TermForClass(wordID, clsID, TDM);
			if(localvalue!=null)	localInfo.put(wordID,localvalue );
		}
		//按LDF值降序排列
		Object[] keys=localInfo.keySet().toArray();
		Arrays.sort(keys,new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return	(int)Math.signum(localInfo.get(o2)-localInfo.get(o1));
			}
		});
		threshold=Math.min(threshold, keys.length);
		//重复的只保留一个
		for(int i=0;i<threshold;i++){
			FinalFeature.add((String)keys[i]);
		}
	}
	
	/**
	 * 某一个词在某一个类别中出现的次数
	 * @param mp 映射表
	 * @param classLabel 类别
	 * @return
	 */
	public int LocalDFFS_TermForClass(String wordID, int clsID,TrainingDataManager TDM){
		return TDM.getMap().get(wordID)[clsID];
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	
		long start=System.currentTimeMillis();
		//LDF.CalcFeaturesForAllFiles("Test\\dictionary.txt", "TrainingData\\", "Test\\Tdata.txt");
		long end=System.currentTimeMillis();
		System.out.println("程序执行："+(end-start)/60.0+"s");

//		long start=System.currentTimeMillis();
//		
//		//DFWS.GlobalDFFS("Test\\dictionary.txt","TanCorpMinTrain\\","Test\\FeatureDictionary.txt",500);
//		LDF.ConstructFeatureDictionary("Test\\dictionary.txt","TrainingData\\","Test\\FeatureDictionary.txt",500);
//		long end=System.currentTimeMillis();
//		System.out.println("程序执行："+(end-start)/1000.0+"s");
	}

	

}
