package SVM.Subject;

import SVM.DataClassify.FeatureMaker;
import SVM.DataClassify.KeyWord;
import SVM.DataClassify.TextClsWordFilter;
import SVM.DataClassify.WordFilter;

/**
 * 从文本中获取特征的类
 * @author HuHaixiao
 *
 */
public class SubjectFeatureMaker implements FeatureMaker {
	/**主题分类词语过滤器*/
	private static WordFilter wf;
	
	/**
	 * 不查词典，获取所有的特征
	 */
	public SubjectFeatureMaker(){
		wf=new TextClsWordFilter();
	}

	@Override
	public String[] getFeatureFromDoc(String text) {
		// TODO Auto-generated method stub
		KeyWord words = new KeyWord();
		return words.WordsExtract(text, wf);
	}

	@Override
	public int estimateMapSize() {
		// TODO Auto-generated method stub
		return 4000;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
