package DataPrepare;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * split reuters records into files (GBK encode)将路透社文件（*.sgm）转换成独立的文件
 * 
 * @author WuyaMony
 * 
 */
public class ReutersParser {
	private static final Pattern reuters=Pattern.compile("(<REUTERS[\\s]*([^>]*)>([\\s\\S]+?)</REUTERS>)+?");;
	private static final Pattern topics=Pattern.compile("<TOPICS>([\\s\\S]+?)</TOPICS>");
	private static final Pattern title=Pattern.compile("<TITLE>([\\s\\S]+?)</TITLE>");
	private static final Pattern body=Pattern.compile("<BODY>([\\s\\S]+?)</BODY>");
	private static final Pattern lewissplit=Pattern.compile("LEWISSPLIT=\"([^\"]+)\"");
	private static final Pattern newid=Pattern.compile("NEWID=\"([^\"]+)\"");
	private Matcher m=null;
	
	public ReutersParser(String filePath){
		m=reuters.matcher(Read(filePath));
	}
	
	public static void main(String[] args) {
		//example
		ReutersParser rp=new ReutersParser("test/testReuters");
		ParseResult p=null;
		while((p=rp.next())!=null){
			System.out.println("NewID="+p.newid+"\ntopics="+p.topics+"\nlewissplit="+p.lewissplit+"\ntitle="+p.title+"\nbody="+p.body);
			System.out.println("##############################################################");
		}
//		File pathin = new File("/home/zzl/reuters_data/");
//		File pathout = new File("/home/zzl/reuters_data_output");
//		String out_path = "/home/zzl/reuters_data_output";
//		// String
//		// test="<REUTERS TOPICS=\"NO\" LEWISSPLIT=\"TRAIN\" CGISPLIT=\"TRAINING-SET\" OLDID=\"12555\" NEWID=\"373\"><DATE> 2-MAR-1987 08:38:57.06</DATE></REUTERS>";
//		// Convert("test/1","test");
//
//		File[] listfile = pathin.listFiles();
//		for (File file : listfile) {
//			System.out.println(file.getAbsolutePath());
//			Parse(file.getAbsolutePath());
//		}
	}
	
	/**
	 * returns the next "Reuters Record" or null if no more reuters record.
	 * @return parse result or null if no more record.
	 */
	public ParseResult next(){
		while(m.find()){
			String find = m.group(3);
			Matcher tmp=topics.matcher(find);
			if(!tmp.find()){//topics field is null, continue searching until find one record or reach the end.
				continue;
			}else{
				String _topics=tmp.group(1);
				tmp=title.matcher(find);tmp.find();
				String _title=tmp.group(1);
				tmp=body.matcher(find);tmp.find();
				String _body=tmp.group(1);
				//get attributes of reuters field
				String attr=m.group(2);
				tmp=lewissplit.matcher(attr);tmp.find();
				String _lewissplit=tmp.group(1);
				tmp=newid.matcher(attr);tmp.find();
				String _newid=tmp.group(1);
				return new ParseResult(_topics, _lewissplit, _newid, _title, _body);
			}
		}
		return null;
	}
	

	
	/**
	 * Read file and store the whole content into a string. (GBK
	 * encode)读取文件所有内容到字符串
	 * 
	 * @param filePath
	 *            (GBK encode)文件路径
	 * @return (GBK encode)返回的文件字符串
	 */
	public static String Read(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			StringBuilder sb = new StringBuilder();
			String text;
			while ((text = br.readLine()) != null) {
				sb.append(text + "\n");
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public void test(){
////if(m.find()){
////	String find = m.group(3);
////	System.out.println(find);
////	Matcher topics_f=topics.matcher(find);
////	System.out.println(topics_f.group());
////}
//String find="<TOPICS><D>cocoa</D></TOPICS>";
//Matcher topics_f=topics.matcher(find);
//System.out.println(topics_f.find());
//System.out.println(topics_f.group());
//}
	
//	/**
//	 * begin conveting,and store the found records into files with the
//	 * increasing number as file name (GBK encode)进行转换 将找到的记录按递NewId文件名保存
//	 * 
//	 * @param filePath
//	 *            (GBK encode)输入文件路径 input file path
//	 */
//	public void Parse(String filePath) {
//		String str = Read(filePath);
//		if (str == null)
//			return;
//		Matcher m = reuters.matcher(str);
//		String find;
//		while (m.find()) {
//			find = m.group();
//			Matcher topics_f=topics.matcher(find);
//			if(!topics_f.find()){//topics field is null
//				continue;
//			}
//			
//			// System.out.println(find=m.group());
//			// System.out.println("########################################");
////			try (BufferedWriter bw = new BufferedWriter(new FileWriter(outDir
////					+ "/" + gettemp() + ".txt"))) {
////				bw.write(find);
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
//		}
//
//	}

	
	
	
}
