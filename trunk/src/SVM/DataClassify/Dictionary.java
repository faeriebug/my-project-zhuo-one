package SVM.DataClassify;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 字典处理类
 * <p>处理字典文件的导入
 * <p>构造字典内容的正映射表和逆映射表。
 * @author HuHaixiao
 *
 */
public class Dictionary {
	
	/**
	 * 加载字典
	 * <p>结果保存在正映射表中
	 * <p>可以由词语字符串索引到其编号。如：美好->120
	 * <p>选取Map的原因是：可以通过字符串关键字直接找到其标号
	 * @param dictionary 字典路径
	 * @return  字典正映射表
	 */
	public  Map<String, Integer> loadDictionary(String dictionary){
		try {
			String text;
			File file = new File(dictionary);
//			FileReader fileReader = new FileReader(file);
//			BufferedReader br = new BufferedReader(fileReader);
			InputStreamReader in=new InputStreamReader(new FileInputStream(file), FileEncode.charset);
			BufferedReader br = new BufferedReader(in);
			text = br.readLine();
			if(text==null)return null;
			int size=Integer.parseInt(text);//读取词典大小,用来构造map
			Map<String, Integer> map=new HashMap<String, Integer>(size);
			
			while ((text = br.readLine()) != null){
				String[] word=text.split(",");
				if(word.length==2)
					map.put(word[0], Integer.valueOf(word[1]));//存放至map中
			}
			br.close();
			in.close();
			
			return map;
		} catch (IOException err) {
			System.out.println(err.toString());
			err.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加载字典
	 * <p>结果保存在逆映射表中
	 * <p>可以由词语编号索引到对应的字符串。如：120->美好
	 * @param dictionary 字典路径
	 * @return  字典逆映射表
	 */
	public  String[] loadDictionaryInv(String dictionary){
		String[] allWords=null;
		try {
			String text;
			File file = new File(dictionary);
			InputStreamReader in=new InputStreamReader(new FileInputStream(file), FileEncode.charset);
			BufferedReader br = new BufferedReader(in);
			text = br.readLine();			//读取词典大小
			if(text==null)return null;
			int size=Integer.parseInt(text);
			allWords=new String[size];
			
			while ((text = br.readLine()) != null){
				String[] word=text.split(",");
				if(word.length==2)
					allWords[Integer.parseInt(word[1])]=word[0];
			}
			br.close();
			in.close();
		} catch (IOException err) {
			System.out.println(err.toString());
			err.printStackTrace();
		}
		return allWords;
	}
	
	/**
	 * 辅助函数
	 * <p>将文件内容以字符串的形式输出
	 * @return 输出文件内容字符串
	 */
	public String readAll(String filePath){
		StringBuilder sb = new StringBuilder();
		try {
			String text;
			File file = new File(filePath);
			InputStreamReader in=new InputStreamReader(new FileInputStream(file), FileEncode.charset);
			BufferedReader br = new BufferedReader(in);
			
			while ((text = br.readLine()) != null)
				sb.append(text+"\n");
			br.close();
			in.close();
		} catch (IOException err) {
			System.out.println(err.toString());
			err.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void main(String[] arg){
		
	}

	
}