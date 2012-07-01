package SVM.FeatureWeighting;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import SVM.DataClassify.*;

public abstract class FeatureWeight{
	public abstract String getName();
	/**
	 * 训练时或者测试时使用，自动将类别信息加上
	 * 为语料库中所有文件计算特征值，并保存在本地文件(libsvm格式)
	 * <p>类别序号从1开始
	 * @param dicPath 词典目录
	 * @param libPath 语料库目录，其下存放待分类类的语料。
	 * @param resultFilePath 保存为libsvm格式的语料文档特征值文件
	 */
	public  void CalcFeaturesForAllFiles_Training(FeatureMaker FM,FeatureCounter FC,String dicPath,String libPath,String resultFilePath) {
		CategorizingDataManager CDM=new CategorizingDataManager(FC,dicPath, libPath,true);
		Dictionary dicEx=new Dictionary();
		String[] docs=CDM.getDocs();
		try{
			FileWriter fw=new FileWriter(resultFilePath);
			int docID=0;// 文本ID在同一目录中递增
//			fw.write(CDM.N+"\n");
			for(int i=0;i<docs.length;i++){//对每一个类别
				for(String doc:CDM.getFilesPath(docs[i])){//对每一篇文档
					//写入特征向量
					String temp=FeatureVectorOfDoc(FM,dicEx.readAll(doc),docID++,CDM);
					//System.out.println((i+1)+" "+temp+"\n");
					fw.write((i*2-1)+" "+temp+"\n");
				}
			}
			fw.flush();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
//	/**
//	 * 分类时使用，无类别信息(类别同一为1)
//	 * 为语料库中所有文件计算特征值，并保存在本地文件(libsvm格式),按照训练模式(即同时输出类别ID)
//	 * @param dicPath 词典目录
//	 * @param libPath 语料库目录，其下存放待分类类的语料。
//	 * @param resultFilePath 保存为libsvm格式的语料文档特征值文件
//	 */
//	public  void CalcFeaturesForAllFiles(FeatureMaker FM,FeatureCounter FC,String dicPath,String libPath,String resultFilePath) {
//		CategorizingDataManager CDM=new CategorizingDataManager(FC,dicPath, libPath,false);
//		Dictionary dicEx=new Dictionary();
//		String[] docs=CDM.getDocs();
//		try{
//			FileWriter fw=new FileWriter(resultFilePath);
//			int docID=0;
//			for(int i=0;i<docs.length;i++){//对每一个类别
//				for(String doc:CDM.getFilesPath(docs[i])){//对每一篇文档
//					//写入特征向量
//					String temp=FeatureVectorOfDoc(FM,dicEx.readAll(doc),docID++,CDM);
//					fw.write(1+" "+temp+"\n");
//				}
//			}
//			fw.flush();
//			fw.close();
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 计算文档的特征向量
	 * @param docStr 文档字符串
	 * @param docID 文档标号
	 * @param CDM 数据处理类
	 * @return
	 */
	public  String FeatureVectorOfDoc(FeatureMaker FM,String docStr,int docID,CategorizingDataManager CDM){
		StringBuilder sb=new StringBuilder();
		String[] features=FM.getFeatureFromDoc(docStr);
		Map<Integer, Number> FeatureVector = new HashMap<Integer, Number>();
		Map<String,Integer> dic=CDM.dic;
		for(String word:features){
			Integer wordID=dic.get(word);
			if(wordID!=null){//为特征词典中含有的词语
				if(!FeatureVector.containsKey(wordID))//如果还没有为此词语计算权重
					FeatureVector.put(wordID,getWight(docID, wordID, CDM));
			}
		}
		
		//对标号进行排序（libSVM格式要求）
		Integer[] keys=new Integer[]{};
		keys=FeatureVector.keySet().toArray(keys);
		Arrays.sort(keys,new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return	 o1-o2;
			}
		});
		
		for(Integer wordID:keys){
			sb.append(wordID+":"+FeatureVector.get(wordID)+" ");
		}
		return sb.toString();
		
	}
	
	/**
	 * 计算a[docID][wordID]权重
	 * @param docID
	 * @param wordID
	 * @param CDM
	 * @return
	 */
	public abstract Number getWight(int docID,int wordID,CategorizingDataManager CDM);
	
}
