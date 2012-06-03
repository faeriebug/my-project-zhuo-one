package SVM.FeatureSelection;

import java.io.*;
import java.util.*;

import SVM.DataClassify.FeatureMaker;
import SVM.Subject.SubjectFeatureMaker;

/**
 * <p>卡方特征词选取算法，三种选取策略
 * <p>1.按类别选取
 * <p>2.按平均卡方值选取
 * <p>3.按最大卡方值选取
 * @author HuHaixiao
 *
 */
public class ChiSqrWordSelection  extends FeatureSelection{
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ChiSqr";
	}
	
	@Override
	public void ConstructFeatureDictionary(FeatureMaker feature, String libPath, String resultFilePath,int threshold) {
		// TODO Auto-generated method stub
		TrainingDataManager TDM=new TrainingDataManager(feature, libPath);
		Set<String> FinalFeature=new HashSet<String>(threshold);
		for(int i=0;i<TDM.getClsNum();i++){//对每一个类别
				int thres=(int)((double)threshold/TDM.getTotalDocNum()*TDM.getDocNumOfCls(i));//取词数量取决于此类别中文档占总文档数的比例
				ChiSquare_AllTermsForclass(i, TDM, FinalFeature, thres);
			}
		try{
			//输出到文件中
			FileWriter fw=new FileWriter(resultFilePath);
			int m=0;//词典从零开始
			//System.out.println((threshold>FinalFeature.size()?FinalFeature.size():threshold));
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
	
	
	@Override
	public Number getFeatureForTermInClass(String wordID, int clsID,
			TrainingDataManager TDM) {
		// TODO Auto-generated method stub
		return ChiSquare_TermForClass(wordID, clsID, TDM);//按类别的卡方值
	}

	
	/**
	 * 依据平均卡方值进行特征词选取
	 * @param dicPath 字典目录
	 * @param libPath 训练语料库目录，其下存放分好类的语料。
	 * @param resultFilePath 输出结果保存文件的路径
	 * @param k 选取的特征词数量
	 */
	public void ChiSqrFSAvg(String dicPath,String libPath,String resultFilePath,int k){
		TrainingDataManager TDM=new TrainingDataManager(new SubjectFeatureMaker("Test\\dictionary.txt"), libPath);
		Map<String, int[]> mp=TDM.getMap();
		try{
			FileWriter fw=new FileWriter(resultFilePath);
			final HashMap<String, Double> chisquareInfo=new HashMap<String, Double>();
			
			for(String wordID:mp.keySet()){//对所有词计算平均卡方值
				chisquareInfo.put(wordID, ChiSquare_TermAvg(wordID, TDM));
			}
			
			//依据平均卡方值降序排列
			Object[] keys=chisquareInfo.keySet().toArray();
			Arrays.sort(keys,new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					// TODO Auto-generated method stub
					return	(int)Math.signum(chisquareInfo.get(o2)-chisquareInfo.get(o1));
					}
				});
			//输出到文件中
			int m=0;
			for(Object i:keys){
				if(m>=k)break;//已经取满threshold个特征词
				//System.out.println(i+","+chisquareInfo.get(i));
				fw.write(i+","+chisquareInfo.get(i)+"\n");
				m++;
			}
			fw.flush();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 依据最大卡方值进行特征词选取
	 * @param dicPath 字典目录
	 * @param libPath 训练语料库目录，其下存放分好类的语料。
	 * @param resultFilePath 输出结果保存文件的路径
	 * @param k 选取的特征词数量
	 */
	public void ChiSqrFSMax(String dicPath,String libPath,String resultFilePath,int k){
		TrainingDataManager TDM=new TrainingDataManager(new SubjectFeatureMaker("Test\\dictionary.txt"), libPath);
		Map<String, int[]> mp=TDM.getMap();
		try{
			FileWriter fw=new FileWriter(resultFilePath);
			final Map<String, Double> chisquareInfo=new HashMap<String, Double>();
			
			for(String wordID:mp.keySet()){//对所有词计算最大卡方值
				chisquareInfo.put(wordID, ChiSquare_TermMax(wordID,TDM));
			}
			
			//依据最大卡方值降序排列
			Object[] keys=chisquareInfo.keySet().toArray();
			Arrays.sort(keys,new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					// TODO Auto-generated method stub
					return	(int)Math.signum(chisquareInfo.get(o2)-chisquareInfo.get(o1));
					}
				});
			//输出到文件中
			int m=0;
			for(Object i:keys){
				if(m>=k)break;//已经取满threshold个特征词
				//System.out.println(i+","+chisquareInfo.get(i));
				fw.write(i+","+chisquareInfo.get(i)+"\n");
				m++;
			}
			fw.flush();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算某个词的平均卡方值
	 * @param mp 映射表，包含语料库所有信息
	 * @param wordID 词语ID
	 * @param classification 类别数组
	 * @param N 语料库中总文档数
	 * @return
	 */
	public	 double  ChiSquare_TermAvg(String wordID,TrainingDataManager TDM){
		double chiAvg=0.0,p;
		for(int i=0;i<TDM.getClsNum();i++){
			p=TDM.getDocNumOfCls(i)/(double)TDM.getTotalDocNum();
			chiAvg+=p*ChiSquare_TermForClass(wordID, i, TDM);
		}
		return chiAvg;
	}
	
	/**
	 * 计算某个词的最大卡方值
	 * @param mp 映射表，包含语料库所有信息
	 * @param wordID 词语ID
	 * @param classification 类别数组
	 * @param N 语料库中总文档数
	 * @return
	 */
	public 	 double  ChiSquare_TermMax(String wordID,TrainingDataManager TDM){
		double temp,chiMax=ChiSquare_TermForClass(wordID, 0, TDM);
		for(int i=1;i<TDM.getClsNum();i++){
			temp=ChiSquare_TermForClass(wordID, i, TDM);
			if(temp>chiMax)
				chiMax=temp;
		}
		return chiMax;
	}
		
	/**
	 * 计算所有词对此类别的卡方值，并按卡方值降序排列，输出前threshold个
	 * @param clsID类别
	 * @param TDM 特征辅助类
	 * @param FinalFeature 存储最终特征词的集合
	 * @param threshold 此类别取词的数量
	 */
public void ChiSquare_AllTermsForclass(int clsID,TrainingDataManager TDM,Set<String> FinalFeature,int threshold){  
	final Map<String, Double> chisquareInfo=new HashMap<String, Double>();
	Map<String, int[]> mp=TDM.getMap();
	Double chivalue;
	//计算卡方值
	for(String wordID:mp.keySet()){//对每一个词语
		chivalue=ChiSquare_TermForClass(wordID, clsID, TDM);
		if(chivalue!=null)	chisquareInfo.put(wordID,chivalue );
	}
	//按卡方值降序排列
	Object[] keys=chisquareInfo.keySet().toArray();
	Arrays.sort(keys,new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			return	(int)Math.signum(chisquareInfo.get(o2)-chisquareInfo.get(o1));
		}
	});
	threshold=Math.min(threshold, keys.length);
	//重复的只保留一个
	for(int i=0;i<threshold;i++){
		
		try {
			FinalFeature.add((String)keys[i]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}



/**
 * 计算某一个词对某一类别的卡方值
 * @param mp 映射表，包含语料库所有信息
 * @param wordID 词语ID
 * @param classLabel 类别标签
 * @param N 语料库中总文档数
 * @param Nc 某一类别中的文档总数
 * @return 卡方值，如果映射表中不存在此词语，则返回null
 */
public 	 Double  ChiSquare_TermForClass(String wordID,int clsID,TrainingDataManager TDM){ 
	Map<String, int[]> mp=TDM.getMap();
	
	if(!mp.containsKey(wordID))	return null;//如果映射表中不存在此词语，则返回null
	
	int[] cls=mp.get(wordID);
	
	double N11=cls[clsID];											//文档数：包含此词语，且在类classLabel中
	double N01=TDM.getDocNumOfCls(clsID)-N11;	//文档数：不包含此词语，且在类classLabel中
	double N10=0;															//文档数：包含此词语，且不在类classLabel中
	for(int i=0;i<cls.length;i++){
		if(i!=clsID)
			N10+=cls[i];
	}
	double N00=TDM.getTotalDocNum()-TDM.getDocNumOfCls(clsID)-N10;	//文档数：不包含此词语，且不在类classLabel中
	return CalChiSquareValue(N11,N10,N01,N00);
}
	
	/**
	 *  计算CHI-square 值   
	 * @param N11 该类别中有多少篇文章含有该词
	 * @param N10 该词出现的文章有多少篇不再该类中
	 * @param N01 该类别中有多少篇文章不含有该词
	 * @param N00 训练语料库中共有多少篇文章即不含该词，也不包含在该类中
	 * @return
	 * 
	 */
private double CalChiSquareValue(double N11,double N10,double N01,double N00){
	    double chiSquare=0;
	    chiSquare=(N11+N10+N01+N00)*(N11*N00-N10*N01)*(N11*N00-N10*N01)/((N11+N01)*(N11+N10)*(N10+N00)*(N01+N00));
	    return chiSquare;
	}
	
	public static void main(String[] args){
		ChiSqrWordSelection CSWS=new ChiSqrWordSelection();
		//CSWS.ChiSqrFSAvg("Test\\dictionary.txt","TanCorpMinTrain\\","keywordsInfo.txt", 5000);
		//CSWS.ChiSqrFSMax("Test\\dictionary.txt","TrainingData\\","keywordsInfo.txt", 5000);
//		long start=System.currentTimeMillis();
//		//CSWS.CalcFeaturesForAllFiles("Test\\dictionary.txt", "TrainingData\\", "Test\\Tdata.txt");
//		CSWS.CalcFeaturesForAllFiles("Test\\FeatureDictionary.txt", "TrainingData\\", "Test\\Tdata");
//		long end=System.currentTimeMillis();
//		System.out.println("程序执行："+(end-start)/60.0+"s");

		long start=System.currentTimeMillis();
		
		//DFWS.GlobalDFFS("Test\\dictionary.txt","TanCorpMinTrain\\","Test\\FeatureDictionary.txt",500);
		CSWS.ConstructFeatureDictionary(new SubjectFeatureMaker("Test\\dictionary.txt"),"TrainingData\\","Test\\FeatureDictionary.txt",5000);
//		CSWS.ConstructFeatureDictionary(new SemanticFeatureMaker(new SentiDictionary()),"TrainingData\\","Test\\FeatureDictionary.txt",5000);
		long end=System.currentTimeMillis();
		System.out.println("程序执行："+(end-start)/1000.0+"s");
	}


	


	

}
