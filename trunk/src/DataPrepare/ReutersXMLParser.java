package DataPrepare;

//�ļ��� 
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
//���߰� 
import java.util.Iterator;
import java.util.List;
//dom4j�� 
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.mysql.jdbc.StringUtils;

public class ReutersXMLParser {
	/**
	 * load ����һ��xml�ĵ�
	 * 
	 * @return �ɹ�����Document����ʧ�ܷ���null
	 * @param uri
	 *            �ļ�·��
	 */
	public static Document load(File file) {
		Document document = null;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}

	/**
	 * ��ʾ��ȡ�ļ��ľ���ĳ���ڵ��ֵ
	 */
	public static void xmlReadDemo(File file) {
		Document doc = load(file);
		// Element root = doc.getRootElement();
		/** ����xpath��������ftp�ڵ� ���������name����ֵ */
		List<?> list = doc.selectNodes("/LEWIS/REUTERS");
		Iterator<?> it = list.iterator();
		while (it.hasNext()) {
			Element reutersElement = (Element) it.next();

			if (reutersElement.attribute("LEWISSPLIT").getValue()
					.equalsIgnoreCase("test")
					&& reutersElement.attribute("TOPICS").getValue()
							.equalsIgnoreCase("YES")) {
				System.out.println("NEWID="
						+ reutersElement.attribute("NEWID").getValue());
				List<?> list1 = reutersElement.selectNodes("TEXT/TITLE");
				// if(list1.isEmpty())
				// continue;
				Element hostElement = null;
				try {
					hostElement = (Element) list1.iterator().next();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("body=" + hostElement.getText());
			}

		}
		// /** ֱ��������pathȡ��nameֵ */
		// list = doc.selectNodes("/config/ftp/@name" );
		// it = list.iterator();
		// while(it.hasNext())
		// {
		// Attribute attribute = (Attribute)it.next();
		// System.out.println("@name="+attribute.getValue());
		// }
		// /** ֱ��ȡ��DongDian ftp�� ftp-host ��ֵ */
		// list = doc.selectNodes("/config/ftp/ftp-host" );
		// it = list.iterator();
		// Element hostElement=(Element)it.next();
		// System.out.println("DongDian's ftp_host="+hostElement.getText());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File s=new File("reuterxml");
		File[] fs=s.listFiles();
		for (File file : fs) {
			xmlReadDemo(file);
		}
		
	}

}
