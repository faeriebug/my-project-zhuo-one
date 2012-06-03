package SVM.DataClassify;

/**
 * 生成特征
 * @author HuHaixiao
 *
 */
public interface FeatureMaker {
	/**
	 * 从文本中获取特征
	 * @param text
	 * @return
	 */
	String[] getFeatureFromDoc(String text);
	
	/**
	 * 预估计映射表的大小
	 * <p>此方法的存在是基于效率上的考虑
	 * @return
	 */
	int estimateMapSize();
}
