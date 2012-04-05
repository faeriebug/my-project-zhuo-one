package com.openlab.publicopinion.DataClassify.Sentiment;

import java.io.*;
import java.util.*;

import com.openlab.publicopinion.DataClassify.FileEncode;

/**
 * 情感词典，包含正面/负面 情感/评价词语
 * 
 * @author HuHaixiao
 * 
 */
public class SentiDictionary {
	public String negPath, degPath, sentPath, vsiPath;

	// 否定词
	private Set<String> neg = null;
	// 程度副词
	private Map<String, Integer> deg = null;
	// 情感词语
	private Set<String> senti = null;
	// VSI词语
	private Set<String> vsi = null;

	public SentiDictionary() {
		this("Test\\SentimentTest\\SentiDictionary\\negDic.txt",
				"Test\\SentimentTest\\SentiDictionary\\degreeDic.txt",
				"Test\\SentimentTest\\SentiDictionary\\sentDic.txt",
				"Test\\SentimentTest\\SentiDictionary\\VSIDic.txt");
	}

	public SentiDictionary(String negPath, String degPath, String sentPath,
			String vsiPath) {
		this.negPath = negPath;
		this.degPath = degPath;
		this.sentPath = sentPath;
		this.vsiPath = vsiPath;
	}

	/**
	 * 情感词典的生成
	 * 
	 * @param path
	 */
	public void constructDict(String path) {// 基于知网的情感词典的建立

	}

	/**
	 * 获取否定词典
	 * 
	 * @return
	 */
	public Set<String> getNeg() {
		if (neg == null) {
			File negDic = new File(negPath);
			int siz_Neg;
			String text;
			try {
				// 加载否定词词典
				InputStreamReader in = new InputStreamReader(
						new FileInputStream(negDic), FileEncode.charset);
				BufferedReader br = new BufferedReader(in);
				text = br.readLine();
				if (text == null)
					return null;
				siz_Neg = Integer.parseInt(text);
				neg = new HashSet<String>(siz_Neg);
				while ((text = br.readLine()) != null)
					neg.add(text.trim());
				br.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return neg;
	}

	/**
	 * 获取程度副词典
	 * 
	 * @return
	 */
	public Map<String, Integer> getDeg() {
		if (deg == null) {
			try {
				File degDic = new File(degPath);
				int siz_Deg;
				String text;
				InputStreamReader in = new InputStreamReader(
						new FileInputStream(degDic), FileEncode.charset);
				BufferedReader br = new BufferedReader(in);
				// 加载程度副词词典

				text = br.readLine();
				siz_Deg = Integer.parseInt(text);
				deg = new HashMap<String, Integer>(siz_Deg);
				int n;
				for (int i = 0; i < 4; i++) {
					text = br.readLine();
					n = Integer.parseInt(text);
					for (int j = 0; j < n; j++) {
						text = br.readLine();
						deg.put(text.trim(), 4 - i);// 数字越大，等级越高
					}
				}
				br.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deg;

	}

	/**
	 * 获取情感词典
	 * 
	 * @return
	 */
	public Set<String> getSenti() {
		if (senti == null) {
			File sentDic = new File(sentPath);
			int siz_Sent;
			try {
				String text;
				// 加载否定词词典

				// 加载情感词典
				InputStreamReader in = new InputStreamReader(
						new FileInputStream(sentDic), FileEncode.charset);
				BufferedReader br = new BufferedReader(in);
				text = br.readLine();
				if (text == null)
					return null;
				siz_Sent = Integer.parseInt(text);
				senti = new HashSet<String>(siz_Sent);
				while ((text = br.readLine()) != null) {
					senti.add(text.trim());
				}
				br.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return senti;

	}

	/**
	 * 获取VSI词典
	 * 
	 * @return
	 */
	public Set<String> getVSI() {
		if (vsi == null) {
			File vsiDic = new File(vsiPath);
			int siz_Vsi;
			try {
				String text;
				// 加载否定词词典

				// 加载VSI词典
				InputStreamReader in = new InputStreamReader(
						new FileInputStream(vsiDic), FileEncode.charset);
				BufferedReader br = new BufferedReader(in);
				text = br.readLine();
				if (text == null)
					return null;
				siz_Vsi = Integer.parseInt(text);
				vsi = new HashSet<String>(siz_Vsi);
				while ((text = br.readLine()) != null) {
					vsi.add(text.trim());
				}
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return vsi;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SentiDictionary SD = new SentiDictionary();
		System.out.println(SD.getSenti().contains("满意"));
	}

}
