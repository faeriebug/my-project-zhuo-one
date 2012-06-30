package SVM.FeatureWeighting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import SVM.DataClassify.Dictionary;
import SVM.DataClassify.FeatureCounter;
public class CategorizingDataManager {
	/**语料库目录*/
	public  final String catPath;
	/**字典目录*/
	public  final String dicPath;
	public  final Map<String, Integer> dic;
	public  String[] docs;
	/**<wordID,<docID,freq>>*/
	private  Map<Integer, Map<Integer, Integer>> cp;
//	/**词j在文档i中的出现频数*/
//	public  int tf[][];
//	/**词j的文档频数*/
//	public  int n[];
//	/**索引*/
//	public  Integer key[];
	/**文档集的文档总数*/
	public  int N;
	/**文档集的词总数*/
	public  int M;
	
	private Dictionary dicEx=new Dictionary();
	
	/**
	 * 
	 * @param dicPath 字典目录
	 * @param catPath 待分类文档集目录
	 */
	public CategorizingDataManager(FeatureCounter FC,String dicPath,String catPath,boolean isTraining){
		this.dicPath=dicPath;
		this.catPath=catPath;
		
		dic=dicEx.loadDictionary(dicPath);
		File catTextDir=new File(catPath);
		if (!catTextDir.isDirectory()) 
			throw new IllegalArgumentException("待分类语料库搜索失败！ [" +catPath + "]");
		if(isTraining){
			docs=catTextDir.list();//记录类别
			ArrayList<String> docAry=new ArrayList<String>();
			for(String cls:docs){
				for(String cl:getFilesPath(cls))
					docAry.add(cl);
			}
			docs=new String[docAry.size()];
			docs=docAry.toArray(docs);
		}else{
			docs=catTextDir.list();
			for(int i=0;i<docs.length;i++){
				docs[i]=catPath+File.separator+docs[i];
			}
		}
		ConstructMap(FC, this.catPath);
	}
	
	private void ConstructMap(FeatureCounter FC,String catPath){
		cp=new HashMap<Integer, Map<Integer,Integer>>(dic.size());
		
		for(int i=0;i<docs.length;i++){//对每篇文档统计特征频数，对于主题分类，只统计数量，并不做处理
			Map<String,Integer> fc=FC.Count(dic, dicEx.readAll(docs[i]));
			for(String sf : fc.keySet()){//对每一个词语
				Integer wordID=dic.get(sf);//词语的字典编号
				if(wordID!=null){
					addWordToMap(wordID, fc.get(sf),i, cp);
				}
			}
		}
		N=docs.length;M=cp.size();
	}
	
	private void addWordToMap(int wordID,int freq,int docID,Map<Integer, Map<Integer, Integer>> cp){
		if(cp.containsKey(wordID)){//已收录此词语
			if(cp.get(wordID).containsKey(docID)){//以含有此文档
				Map<Integer, Integer> tmpDc=cp.get(wordID);
				tmpDc.put(docID, tmpDc.get(docID)+freq);//原有加上freq
			}else{
				cp.get(wordID).put(docID, freq);
			}
		}else{//没收录此词语，将此词语收录其中
			Map<Integer, Integer> dc=new HashMap<Integer, Integer>();
			dc.put(docID, freq);
			cp.put(wordID,dc);
		}
	}
	
	public Map<Integer, Map<Integer, Integer>> getMap(){
		return cp;
	}
	
	/**
	* 根据所有待分类文本路径(训练模式下为类别)
	*/
	public String[] getDocs() 
	{
		return (new File(catPath)).list();
	}
	
	/**
	* 根据训练文本类别返回这个类别下的所有训练文本路径（full path）
	* @param classification 给定的分类
	* @return 给定分类下所有文件的路径（full path）
	*/
	public String[] getFilesPath(String classification) 
	{
		File classDir = new File(catPath +File.separator +classification);
		String[] ret = classDir.list();
		for (int i = 0; i < ret.length; i++) 
		{
			ret[i] = catPath +File.separator +classification +File.separator +ret[i];
		}
		return ret;
	}
	
	public String[] getFiles(String classification) {
		File classDir = new File(catPath +File.separator +classification);
		String[] ret = classDir.list();
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//CategorizingDataManager CDM=new CategorizingDataManager("Test\\FeatureDictionary.txt", "CategoringData\\",false);
		
		
//		System.out.println("M="+CDM.M);
	}

}
