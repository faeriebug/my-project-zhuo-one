package SVM.DataClassify;

import java.util.Map;

/**
 * 统计并处理特征出现的频数数据
 * @author HuHaixiao
 *
 */
public interface FeatureCounter {
	/**
	 * 统计文本中的特征的频数并处理
	 * @param dic
	 * @param text
	 * @return
	 */
	Map<String,Integer> Count(Map<String,Integer> dic,String text);

}
