package com.openlab.publicopinion.DataClassify.FeatureSelection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.openlab.publicopinion.DataClassify.Dictionary;
import com.openlab.publicopinion.DataClassify.FeatureMaker;

/**
 * <p>特征词选取的辅助类
 * <p>提供构造映射表、获取类别、文档等信息的方法。实际上这个类保存了语料库所有的统计信息
 * 各种基于统计的特征选择降维算法所需的数据为：
 * 每个特征在每个类别中的频数，至于怎么获取特征，那是基于实现的
 * @author HuHaixiao
 *
 */
public class TrainingDataManager{
	/**训练语料库目录*/
	private  String libPath;
	/**保存类别信息的数组*/
	private  String[] cls;
	/**保存各类别所包含的文档数目*/
	private int[] Nc;
	/**训练语料库中总文档数*/
	private  int N;
	/**映射表，方便特征选取计算*/
	private Map<String, int[]> mp;
	
	/**
	 * @param libraryPath 			语料库目录
	 */
	public TrainingDataManager(FeatureMaker feature, String libraryPath){
		this.libPath=libraryPath;
		
		File traningTextDir = new File(libPath);
		if (!traningTextDir.isDirectory()) 
		{
			throw new IllegalArgumentException("训练语料库搜索失败！ [" +libPath + "]");
		}
		cls=traningTextDir.list();
		Nc=new int[cls.length];
		N=0;
		for(int i=0;i<cls.length;i++){
			Nc[i]= (new File(libPath +File.separator +cls[i])).list().length;
			N+=Nc[i];
		}
		ConstructMap(feature);
	}

	/**
	 * <p>构造映射表，方便特征选取计算
	 * <p>HashMap<String,  int[]>
	 * <p>第一个String为词语,int[]为对应类别中特征的频数
	 * <p>map格式如下：
	 * <p>term1(词语ID)
	 * <p>0:2(表示此类别中有2篇文档包含term1)   1:3   
	 * <p>term2
	 * <p>0:45   1:2 
	 * <p>.
	 * <p>.
	 * <p>.
	 * <p>termN
	 * <p>......
	 * @param path 训练语料库目录，其下存放分好类的语料。
	 * @return 
	 */
	private  void ConstructMap(FeatureMaker feature){
		mp=new HashMap<String,int[]>(feature.estimateMapSize());
		String[] Docs;
		Dictionary dic=new Dictionary();
		for(int i=0;i<cls.length;i++){//对每一个类别
			Docs=getFilesPath(cls[i]);
			for(String doc:Docs){//对每一个文档
				String[] feats=feature.getFeatureFromDoc(dic.readAll(doc));
				for(String f : feats){//对每一个特征
					addWordToMap(f, i, mp);
				}
			}
		}

	}
	
	/**
	 * 将词语加入映射中
	 * @param wordID 词语ID
	 * @param clsID 词语所属类别ID
	 * @param mp 要处理的映射
	 * @see #ConstructMap()
	 */
	private void addWordToMap(String f,int clsID,Map<String, int[]> mp){
		if(mp.containsKey(f)){//已收录此词语
			mp.get(f)[clsID]++;
		}else{//没收录此词语，将此词语收录其中
			int[] cl=new int[cls.length];
			cl[clsID]++;
			mp.put(f,cl);
		}
	}
	
	/**
	 * 获取映射表
	 * @return
	 */
	public Map<String, int[]> getMap(){
		return mp;
	}
	
	/**
	 * 获取训练语料库中的类别集合
	 * @return
	 */
	public String[] getClassification(){
		return cls;
	}
	
	/**
	 * 获取语料库目录中类别的数目
	 * @return
	 */
	public int getClsNum(){
		return cls.length;
	}
	
	/**
	 * 获取训练语料库中文档总数目
	 * @return
	 */
	public  int getTotalDocNum(){
		return N;
	}
	
	/**
	 * 获取某一类别中的文档总数目
	 * @param cls 类别
	 * @return
	 */
	public int getDocNumOfCls(int clsID){
		return Nc[clsID];
	}
	
	/**
	* 根据训练文本类别返回这个类别下的所有训练文本路径（full path）
	* @param classification 给定的分类
	* @return 给定分类下所有文件的路径（full path）
	*/
	public String[] getFilesPath(String classification) 
	{
		File classDir = new File(libPath +File.separator +classification);
		String[] ret = classDir.list();
		for (int i = 0; i < ret.length; i++) 
		{
			ret[i] = libPath +File.separator +classification +File.separator +ret[i];
		}
		return ret;
	}
	
	
	
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Dictionary dicEx=new Dictionary();
		//HashMap<Integer, HashMap<String, HashSet<String>>> mp=FP.ConstructMap("TanCorpMinTrain\\");
		String[] dicInv=dicEx.loadDictionaryInv("Test\\dictionary.txt");
		for(int i=0;i<dicInv.length;i++)
			System.out.println(i+","+dicInv[i]);
//		FeatureProcess FP=new FeatureProcess();
//		Map<String, Integer> dic=FP.loadDictionary("Test\\dictionary.txt");
//		Iterator<Map.Entry<String, Integer>> it=dic.entrySet().iterator();
//		while(it.hasNext()){
//			Map.Entry<String, Integer> temp=it.next();
//			System.out.println(temp.getKey()+","+temp.getValue());
//		}
//		for(String key:dic.keySet()){
//			System.out.println(key+","+dic.get(key));
//		}
	}

}
