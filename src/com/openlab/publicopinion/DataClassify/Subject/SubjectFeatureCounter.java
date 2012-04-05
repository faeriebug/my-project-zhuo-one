package com.openlab.publicopinion.DataClassify.Subject;

import java.util.HashMap;
import java.util.Map;

import com.openlab.publicopinion.DataClassify.FeatureCounter;

/**
 * 主题分类的特征权值的计算，（仅计算特征词典中含有的特征）
 * @author HuHaixiao
 *
 */
public class SubjectFeatureCounter implements FeatureCounter {

	@Override
	public Map<String,Integer> Count(Map<String, Integer> dic, String text) {
		// TODO Auto-generated method stub
		SubjectFeatureMaker SF=new SubjectFeatureMaker(dic);
		String[] features=SF.getFeatureFromDoc(text);//带有重复的特征，频数为1；
		//接下来，要进行频数统计，考虑是否进行合并处理还是，直接传出所有特征，外部合并计算
		//合并处理
		Map<String,Integer> setFeature=new HashMap<String, Integer>(features.length);
		for (String f : features) {
			if (setFeature.containsKey(f)) {//如果包含
				setFeature.put(f, setFeature.get(f)+1);//频数+1
			}else{
				setFeature.put(f, 1);
			}
			
		}
		return setFeature;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
