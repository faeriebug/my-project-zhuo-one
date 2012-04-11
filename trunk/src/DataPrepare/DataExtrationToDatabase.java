package DataPrepare;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * @author zzl
 * */

class DataExtrationToDatabase {

	private static Connection con = null;
	

	/**
	 * @param String NewID, String Topic, String Title, String body
	 * @return void
	 */

	public void todataBase(String NewID, String Topic, String Title, String body) {
		//需要添加插入，读出，更新的sql语句表项如下， 有两个表都一样， 分别是Test_set, Train_set
		/**+-------+---------------+------+-----+---------+-------+
| Field | Type          | Null | Key | Default | Extra |
+-------+---------------+------+-----+---------+-------+
| NewId | int(11)       | NO   | PRI | NULL    |       |
| Topic | varchar(128)  | YES  |     | NULL    |       |
| Title | varchar(64)   | YES  |     | NULL    |       |
| Body  | varchar(4096) | YES  |     | NULL    |       |
+-------+---------------+------+-----+---------+-------+
		 * */
		String sql1 ="insert into Test_set(NewID)";
		String sql2 ="update Test_set set NewID";
		
	
	}

	public static void main(String[] args)  {
		DatabaseCom con1 = new DatabaseCom();
		con = con1.Getconnection();
		System.out.println("sucessful");
		File pathin = new File("/home/zzl/reuters_data/");
		DataExtrationToDatabase todatabase = new DataExtrationToDatabase();
		ParseResult p = null;
		File[] listfile = pathin.listFiles();
		int count_file = 0;
		int count = 0;
		for (File file : listfile) {
			ReutersParser rp = new ReutersParser(file.getAbsolutePath());

			while ((p = rp.next()) != null) {
				if(p.lewissplit.equalsIgnoreCase("test")){
					System.out.println("test set");
					todatabase.todataBase(p.newid,  p.topics, p.title, p.body);
				}
				if(p.lewissplit.equalsIgnoreCase("Train")){
					System.out.println("train set");
					todatabase.todataBase(p.newid,  p.topics, p.title, p.body);
				}
				count++;
			}
			count_file++;
		}
		System.out.println("total_file:" + count_file + "  " + "Item:" + count);
	}
}