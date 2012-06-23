package SVM.Subject;

import SVM.DataClassify.FeatureMaker;
import SVM.DataClassify.KeyWord;
import SVM.DataClassify.TextClsWordFilter;
import SVM.DataClassify.WordFilter;

/**
 * ���ı��л�ȡ��������
 * @author HuHaixiao
 *
 */
public class SubjectFeatureMaker implements FeatureMaker {
	/**���������������*/
	private static WordFilter wf;
	
	/**
	 * ����ʵ䣬��ȡ���е�����
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
