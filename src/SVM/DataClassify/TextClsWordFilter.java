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

	/**
	 * ����ʵ�������Ϊnull�������ڴʵ��в��Ҹôʣ�������������д��Թ��ˣ�����ֱ�ӹ��˵���
	 * <p>
	 * ����ʵ�Ϊnull������д��Թ���
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
