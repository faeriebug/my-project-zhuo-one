package DataPrepare;

import java.io.File;
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

	void process() throws DocumentException, SQLException {

		SAXReader saxReader = new SAXReader();
		DatabaseCom dbcom = new DatabaseCom();
		dbcom.Getconnection();
		int count_Test = 0, count_train = 0;
		int Test_id = 1, Train_id = 1;
		String sql1 = "INSERT INTO Test_set (NewId,Topic,Title,Body) values (?,?,?,?)";
		String sql2 = "INSERT INTO Train_set (NewId,Topic,Title,Body) values (?,?,?,?)";
		java.sql.PreparedStatement pre_Test = dbcom.con.prepareStatement(sql1);
		java.sql.PreparedStatement pre_Train = dbcom.con.prepareStatement(sql2);

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
						pre_Train.setInt(1, Train_id);
						pre_Train.setString(2, topic);
						pre_Train.setString(3, title);
						pre_Train.setString(4, body);
						if (eleTopic.getStringValue().equalsIgnoreCase("acq")) {
							System.out.println("acq");
							pre_Train.executeUpdate();
							Train_id++;
						}
						if (eleTopic.getStringValue().contains("wheat")) {
							System.out.println("wheat");
							pre_Train.executeUpdate();
							Train_id++;
						}

						System.out.println("Train:" + count_train);
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
						pre_Test.setInt(1, Test_id);
						pre_Test.setString(2, topic);
						pre_Test.setString(3, title);
						pre_Test.setString(4, body);
						if (eleTopic.getStringValue().equalsIgnoreCase("acq")) {
							System.out.println("acq");
							pre_Test.executeUpdate();
							Test_id++;

						}
						if (eleTopic.getStringValue().contains("wheat")) {
							System.out.println("wheat");
							pre_Test.executeUpdate();
							Test_id++;
						}
						System.out.println("Test:" + count_Test);
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
	 */
	public static void main(String[] args) throws DocumentException,
			SQLException {
		// TODO Auto-generated method stub
		GetWhatwewant hehe = new GetWhatwewant("reuterxml");
		hehe.process();
	}

}
