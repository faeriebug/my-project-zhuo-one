package SVM.DataClassify;


/**
 * ����ѡ��ʹ�õĴ��������
 * <p>
 * �������Ǵ�����Ϣ���й���
 * 
 * @author HuHaixiao
 * 
 */
public class TextClsWordFilter implements WordFilter {
	
//	String[] stopwords=new String[]{"be",};
	/**
	 * ����ʵ�������Ϊnull�������ڴʵ��в��Ҹôʣ�������������д��Թ��ˣ�����ֱ�ӹ��˵���
	 * <p>
	 * ����ʵ�Ϊnull������д��Թ���
	 */
	@Override
	public boolean isStoped(String word, String pos) {
		
		// TODO Auto-generated method stub
		return !(pos.startsWith("NN")
				|| (pos.startsWith("VB") && !word.equals("be")) || pos
					.startsWith("JJ"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
