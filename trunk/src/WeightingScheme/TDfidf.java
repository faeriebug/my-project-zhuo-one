package WeightingScheme;

/**
 * @author zzl
 * 
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Utility.Log;

public class TDfidf {
	static File filepath = new File("/home/zzl/TFIDF");
	private static HashMap<String, HashMap<String, Double>> TfInOneDoc = new HashMap<String, HashMap<String, Double>>();
	private static HashMap<String, HashMap<String, Double>> allTF = new HashMap<String, HashMap<String, Double>>();
	static HashMap<String, Double> uniqueTermsMap;
	static HashMap<String, Double> DftMap = new HashMap<String, Double>();
	static StringBuilder AllDoc = new StringBuilder();
	static StringBuilder oneDoc = new StringBuilder();

	public static void clear(StringBuilder a) {
		a.delete(0, a.length());
	}

	public static String removePunct(String s) {
		Pattern p = Pattern.compile("[.,\"\\?!:]");
		Matcher m = p.matcher(s);
		String first = m.replaceAll("");
		p = Pattern.compile(" {2,}");// È¥³ý¶àÓà¿Õ¸ñ
		m = p.matcher(first);
		String second = m.replaceAll(" ");
		second = first.replace(" ", "");
		return second;
	}

	public static double TermCountUnique() {
		String[] a = AllDoc.toString().split(" ");
		uniqueTermsMap = new HashMap<String, Double>();
		for (String e : a) {
			e = removePunct(e);
			if (uniqueTermsMap.containsKey(e)) {
				Double originalCount = uniqueTermsMap.get(e);
				uniqueTermsMap.put(e, originalCount + 1);
			} else {
				uniqueTermsMap.put(e, 1.0);
			}
		}
		double wordUnique = uniqueTermsMap.size();
		return wordUnique;

	}

	public static HashMap<String, Double> IDFcount(int DocNum) {
		Log math = new Log();
		HashMap<String, Double> map2 = new HashMap<String, Double>();
		HashMap<String, Double> idf = new HashMap<String, Double>();
		Set<String> bagOfUniueWords = uniqueTermsMap.keySet();
		Iterator<String> iter = bagOfUniueWords.iterator();
		File[] filelist = filepath.listFiles();
		while (iter.hasNext()) {
			String k = iter.next();
			double DFT = 0;
			for (File f : filelist) {
				if (TfInOneDoc.containsKey(f.getName())) {
					map2 = TfInOneDoc.get(f.getName());
					if (map2.containsKey(k)) {
						DftMap.put(k, ++DFT);
						idf.put(k, (double) math.log(DocNum / DFT, 10.0));
					} else {
						continue;
					}
				}
			}
		}
		return idf;
	}

	public static void CountTdfidf(HashMap<String, Double> idf) {
		double totalWord_inoneDoc = 0;
		HashMap<String, Double> tfidf_map = new HashMap<String, Double>();
		File[] filelist = filepath.listFiles();
		HashMap<String, Double> map2;
		for (File file : filelist) {
			map2 = TfInOneDoc.get(file.getName());
			Collection<Double> col = null;
			col = map2.values();
			Iterator<Double> iter = col.iterator();		
			while (iter.hasNext()) {
				totalWord_inoneDoc = iter.next() + totalWord_inoneDoc;
			}
		}
		Set<String> bagOfUniueWords = uniqueTermsMap.keySet();
		Iterator<String> iter2 = bagOfUniueWords.iterator();
		for (File file : filelist) {
			map2 = TfInOneDoc.get(file.getName());
			while (iter2.hasNext()) {
				String d=iter2.next();
				double IDF = idf.get(d);
				double TF;
				if(map2.containsKey(d)){
					TF= map2.get(d)/totalWord_inoneDoc;
				}
				else
					TF = 0;
				double TFIDF = TF * IDF;
				tfidf_map.put(file.getName(), TFIDF);
				allTF.put(d, tfidf_map);
			}
		}
	}

	public static void main(String args[]) {
		StringBuffer sb = new StringBuffer();
		try {
			String tmp;
			File[] filelist = filepath.listFiles();
			BufferedReader rd;
			int N = filelist.length; // all doc numbers
			for (File file : filelist) {
				sb.delete(0, sb.length());
				rd = new BufferedReader(new FileReader(file.getAbsolutePath()));
				HashMap<String, Double> TFcountInoneDoc = new HashMap<String, Double>();
				while ((tmp = rd.readLine()) != null) {
					sb.append(tmp + " ");
					AllDoc.append(tmp + " ");
				}
				String[] tar = sb.toString().split(" ");
				for (String e : tar) {
					e = removePunct(e);
					if (TFcountInoneDoc.containsKey(e)) {
						Double originalCount = TFcountInoneDoc.get(e);
						TFcountInoneDoc.put(e, originalCount + 1);
					} else {
						TFcountInoneDoc.put(e, 1.0);
					}
				}
				TfInOneDoc.put(file.getName(), TFcountInoneDoc);
			}

			TermCountUnique();

			CountTdfidf(IDFcount(N));
			System.out.println(allTF);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
