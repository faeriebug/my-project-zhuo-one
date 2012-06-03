package SVM.DataClassify;

import java.util.Map;


/**
 * 特征选择使用的词语过滤类
 * <p> 仅仅考虑词性信息进行过滤
 * @author HuHaixiao
 *
 */
public class TextClsWordFilter implements WordFilter {

	/**字典目录*/
	private  String dicPath;
	/**保存字典的映射表*/
	private  Map<String, Integer> dic;
	 
	/**
	 * 此构造函数将对判断的词语先进行词典查询，然后才是词性判断
	 * @param dictionaryPath
	 */
	public TextClsWordFilter(String dictionaryPath){
		this.dicPath=dictionaryPath;
		dic=(new Dictionary()).loadDictionary(dicPath);
	}
	
	/**
	 * 此构造函数将对判断的词语先进行词典查询，然后才是词性判断
	 * @param dic
	 */
	public TextClsWordFilter(Map<String,Integer> dic){
		this.dic=dic;
	}
	
	/**
	 * 直接进行词性判断
	 */
	public TextClsWordFilter(){
		this.dic=null;
	}
	
	/**
	 * 如果词典索引不为null，则先在词典中查找该词，存在则继续进行词性过滤，否则，直接过滤掉；
	 * <p>如果词典为null，则进行词性过滤
	 */
	@Override
	public boolean isStoped(String word, String pos) {
		// TODO Auto-generated method stub
		if (dic!=null && !dic.containsKey(word)) {
			return true;
		}
		return !((pos.startsWith("n")||pos.startsWith("v")||
				pos.startsWith("a")||pos.startsWith("b")||
				pos.startsWith("d")||
				pos.startsWith("z"))&&!pos.equals("vshi")&&
		!pos.equals("vyou")&&!pos.equals("vf"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
