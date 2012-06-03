package SVM.Subject;

import java.util.Map;

import SVM.DataClassify.Dictionary;
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
	/**�ֵ�Ŀ¼*/
	private  String dicPath;
	/**�����ֵ��ӳ���*/
	private  Map<String, Integer> dic;
	/**���������������*/
	private static WordFilter wf;
	
	/**
	 * ��ȡ�ʵ����е�����
	 * @param dic
	 */
	public SubjectFeatureMaker(String dictionaryPath){
		this.dicPath=dictionaryPath;
		dic=(new Dictionary()).loadDictionary(dicPath);
		wf=new TextClsWordFilter(dic);
	}
	
	/**
	 * ��ȡ�ʵ����е�����
	 * @param dic
	 */
	public SubjectFeatureMaker(Map<String,Integer> dic){
		this.dic=dic;
		wf=new TextClsWordFilter(dic);
	}
	
	/**
	 * ����ʵ䣬��ȡ���е�����
	 */
	public SubjectFeatureMaker(){
		this.dic=null;
		wf=new TextClsWordFilter();
	}

	@Override
	public String[] getFeatureFromDoc(String text) {
		// TODO Auto-generated method stub
		KeyWord words = new KeyWord();
		return words.WordsExtract(text, wf, false);
	}

	@Override
	public int estimateMapSize() {
		// TODO Auto-generated method stub
		return dic.size();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
