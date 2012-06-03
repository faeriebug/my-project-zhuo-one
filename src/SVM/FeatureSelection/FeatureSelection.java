package SVM.FeatureSelection;

import SVM.DataClassify.FeatureMaker;

/**
 * 特征选择抽象基类
 * @author HuHaixiao
 *
 */
public abstract class FeatureSelection {
	public abstract String getName();
	/**
	 * 获得此词语在此类别的此篇文档中特征值 
	 * @param wordID
	 * @param clsID
	 * @param TDM
	 * @return
	 */
	public abstract Number getFeatureForTermInClass(String wordID,int clsID,TrainingDataManager TDM);
	
	/**
	 * 生成特征词字典，并保存在本地文件
	 * <p>词典索引从零开始
	 * @param feature 特征生成
	 * @param libPath 训练语料库目录，其下存放分好类的语料。
	 * @param threshold 特征词典中，特征词数量
	 * @param resultFilePath 词典路径
	 */
	public abstract void ConstructFeatureDictionary(FeatureMaker feature,String libPath,String resultFilePath,int threshold);
}
