package DataPrepare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mysql.jdbc.PreparedStatement;

public class GetWhatwewant {
	File xmlFile;

	public GetWhatwewant(String path) {
		xmlFile = new File(path);
	}

	boolean beforeTdate(String Year,String Day,String month) {
		if (Integer.parseInt(Year) <= 1987) {
			if (month.equalsIgnoreCase("Jan") || month.equalsIgnoreCase("Feb")
					|| month.equalsIgnoreCase("Mar")||month.equalsIgnoreCase("APR")) {
				if (month.equalsIgnoreCase("APR")) {
					if (Integer.parseInt(Day) <= 7) {
						return true;
					} else {
						return false;
					}
				}else{
					return true;
				}			
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	void process() throws DocumentException, SQLException, IOException {

		SAXReader saxReader = new SAXReader();
		DatabaseCom dbcom = new DatabaseCom();
		int count_Test = 0, count_train = 0;
		int Test_id_acq = 1,Test_id_wheat=1, Train_id_wheat = 1, Train_id_acq=1;
		
		String Destination ="FileFormatResult";
		
		File[] xmlFilelist = xmlFile.listFiles();
		int count = 0;
		for (File F : xmlFilelist) {
			Document document = saxReader.read(F.getAbsolutePath());
			List list = document.selectNodes("/LEWIS/REUTERS");
			List Topiclist = document.selectNodes("/LEWIS/REUTERS/TEXT");

			Iterator iter = list.iterator();
			Iterator iter2 = Topiclist.iterator();

			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				Element element2 = (Element) iter2.next();

				Iterator DateIter = element.elementIterator("DATE");
				Iterator TopicIter = element.elementIterator("TOPICS");

				Iterator iterText = element2.elementIterator("TITLE");
				Iterator iterbODY = element2.elementIterator("BODY");
				StringBuilder std=new StringBuilder();
				if (DateIter.hasNext()) {
					Element eleDate = (Element) DateIter.next();
					String date = eleDate.getStringValue().trim().split(" ")[0];
					// System.out.println(date);
					String[] tmmp = date.split("-");
					String dd = tmmp[0];
					String mm = tmmp[1];
					String yy = tmmp[2];
					
					if (beforeTdate(yy, dd, mm)) {// ÑµÁ·¼¯

						String title, body, topic;
						Element eleTopic = (Element) TopicIter.next();
						topic = eleTopic.getStringValue();
						if (iterText.hasNext()) {
							Element eleTITLE = (Element) iterText.next();
							title = eleTITLE.getStringValue();
							// System.out.println(title);
						} else {
							title = "";
						}

						if (iterbODY.hasNext()) {
							Element eleBODY = (Element) iterbODY.next();
							body = eleBODY.getStringValue();
							// System.out.println(body);
						} else {
							body = "";
						}

						if (eleTopic.getStringValue().equalsIgnoreCase("acq")) {
							System.out.println("acq");
							std.append(title);
							std.append("\n");
							std.append(topic);
							std.append("\n");
							std.append(body);
							File acqfile=new File("FileFormatResult/Train/acq/"+Train_id_acq);
							BufferedWriter bw= new BufferedWriter(new FileWriter(acqfile));
							bw.write(std.toString());
							bw.flush();
							bw.close();
							
							Train_id_acq++;
						}
						if (eleTopic.getStringValue().contains("wheat")) {
							System.out.println("wheat");
							std.append(title);
							std.append("\n");
							std.append("wheat");
							std.append("\n");
							std.append(body);
							File wheatfile=new File("FileFormatResult/Train/wheat/"+Train_id_wheat);
							BufferedWriter bw= new BufferedWriter(new FileWriter(wheatfile));
							bw.write(std.toString());
							bw.flush();
							bw.close();
							Train_id_wheat++;
						}

						System.out.println("Train:" + count_train);
						System.out.println("Train_id_wheat:"+Train_id_wheat);
						System.out.println("Train_id_acq:"+Train_id_acq);
						count_train++;

					} else {// ²âÊÔ¼¯
						String title, body, topic;
						Element eleTopic = (Element) TopicIter.next();
						topic = eleTopic.getStringValue();
						if (iterText.hasNext()) {
							Element eleTITLE = (Element) iterText.next();
							title = eleTITLE.getStringValue();
							// System.out.println(title);
						} else {
							title = "";
						}

						if (iterbODY.hasNext()) {
							Element eleBODY = (Element) iterbODY.next();
							body = eleBODY.getStringValue();
							// System.out.println(body);
						} else {
							body = "";
						}

						if (eleTopic.getStringValue().equalsIgnoreCase("acq")) {
							System.out.println("acq");
							std.append(title);
							std.append("\n");
							std.append(topic);
							std.append("\n");
							std.append(body);
							File acqfile=new File("FileFormatResult/Test/acq/"+Test_id_acq);
							BufferedWriter bw= new BufferedWriter(new FileWriter(acqfile));
							bw.write(std.toString());
							bw.flush();
							bw.close();
							
							Test_id_acq++;

						}
						if (eleTopic.getStringValue().contains("wheat")) {
							System.out.println("wheat");
							std.append(title);
							std.append("\n");
							std.append("wheat");
							std.append("\n");
							std.append(body);
							File wheatfile=new File("FileFormatResult/Test/wheat/"+Test_id_wheat);
							BufferedWriter bw= new BufferedWriter(new FileWriter(wheatfile));
							bw.write(std.toString());
							bw.flush();
							bw.close();
							Test_id_wheat++;
						
						}
						System.out.println("Test:" + count_Test);
						System.out.println("Test_id_wheat:"+Test_id_wheat);
						System.out.println("Test_id_acq:"+Test_id_acq);
						count_Test++;
					}
					count++;
				}

			}
			System.out.println(count);
		}

	}

	/**
	 * @param args
	 * @throws DocumentException
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DocumentException,
			SQLException, IOException {
		// TODO Auto-generated method stub
		GetWhatwewant hehe = new GetWhatwewant("reuterxml");
		hehe.process();
	}

}
