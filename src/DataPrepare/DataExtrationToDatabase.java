package DataPrepare;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

class DataExtrationToDatabase {

//	private Connection =null;
	/**
	 * @param filepath
	 * @throws IOException
	 */
	
	public void extract(String filepath) throws IOException
	{   
			SAXReader reader = new SAXReader();  
			try {
				Document   document = reader.read(new File(filepath));
				
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		
	}
	public static void main(String[] args) throws IOException {
		DatabaseCom con1= new DatabaseCom();
		con1.Getconnection();
		System.out.println("sucessful");
		
		String name ="reut2-000.sgm";
		
		File path = new File("/home/zzl/reuters21578/"+name);
		DataExtrationToDatabase parse = new DataExtrationToDatabase();
//		parse.extract();
	}
}