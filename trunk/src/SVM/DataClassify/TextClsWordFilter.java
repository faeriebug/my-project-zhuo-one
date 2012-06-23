package SVM.DataClassify;


/**
 * 特征选择使用的词语过滤类
 * <p>
 * 仅仅考虑词性信息进行过滤
 * 
 * @author HuHaixiao
 * 
 */
public class TextClsWordFilter implements WordFilter {

	/**
	 * 如果词典索引不为null，则先在词典中查找该词，存在则继续进行词性过滤，否则，直接过滤掉；
	 * <p>
	 * 如果词典为null，则进行词性过滤
	 */
	@Override
	public boolean isStoped(String word, String pos) {
		// TODO Auto-generated method stub
		return !(pos.startsWith("N")
				|| (pos.startsWith("V") && !pos.startsWith("VB")) || pos
					.startsWith("J"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
