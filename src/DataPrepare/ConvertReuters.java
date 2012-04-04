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
 * split reuters records into  files
 * (GBK encode)将路透社文件（*.sgm）转换成独立的文件
 * 
 * @author WuyaMony
 * 
 */
public class ConvertReuters {

	/**
	 * Read file and store the whole content into a string.
	 * (GBK encode)读取文件所有内容到字符串
	 * 
	 * @param filePath
	 *            (GBK encode)文件路径
	 * @return (GBK encode)返回的文件字符串
	 */
	public static String Read(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			StringBuilder sb = new StringBuilder();
			String text;
			while ((text = br.readLine()) != null)
				sb.append(text + "\n");
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * begin conveting,and store the found records into files with the increasing number as file name
	 * (GBK encode)进行转换 将找到的记录按递增的文件名保存 比如0,1,2,3...
	 * 
	 * @param filePath
	 *            (GBK encode)输入文件路径 input file path
	 * @param outDir
	 *            (GBK encode)输出目录 output folder
	 */
	public static void Convert(String filePath, String outDir) {
		String str = Read(filePath);
		if(str==null)return;
		String reuters = "<REUTERS[\\s]*[^>]*>[\\s\\S]+?</REUTERS>";
		Pattern p = Pattern.compile("(" + reuters + ")+?");
		Matcher m = p.matcher(str);
		int i = 0;
		String find;
		while (m.find()) {
			find = m.group();
			// System.out.println(find=m.group());
			// System.out.println("########################################");
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(outDir
					+ "/" + i++))) {
				bw.write(find);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		String test = Read("test/testReuters");
		// String
		// test="<REUTERS TOPICS=\"NO\" LEWISSPLIT=\"TRAIN\" CGISPLIT=\"TRAINING-SET\" OLDID=\"12555\" NEWID=\"373\"><DATE> 2-MAR-1987 08:38:57.06</DATE></REUTERS>";
		test += test;
		Convert("test/testReuters", "test");
	}
}
