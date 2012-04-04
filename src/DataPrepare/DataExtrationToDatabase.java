package DataPrepare;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

class DataExtrationToDatabase {

	// private Connection =null;
	/**
	 * @param filepath
	 * @throws IOException
	 */

	public void extract(String filepath) throws IOException {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new File(filepath));
			Element root = document.addElement("root");// 创建根节点
			String text = "<TITLE></TITLE>";
			document = DocumentHelper.parseText(text);
			Element memberElm = root.element("TITLE");// "TITLE"是节点名
			String text2 = memberElm.getText();
			String text3= root.elementText("name");// 这个是取得根节点下的name字节点的文字.
			System.out.println(text2);
			System.out.println(text3);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {
		DatabaseCom con1 = new DatabaseCom();
		con1.Getconnection();
		System.out.println("sucessful");
		String name = "reut2-000.sgm";
		
		DataExtrationToDatabase parse = new DataExtrationToDatabase();
		parse.extract("/home/zzl/reut2-000.xml");
	}
}