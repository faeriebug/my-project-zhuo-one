package com.openlab.publicopinion.DataClassify.Sentiment;

import java.util.LinkedList;

import com.openlab.publicopinion.DataClassify.KeyWord;

/**
 * 将文章分割成句子的辅助类
 * <p>同时分词保留词性信息
 * @author HuHaixiao
 *
 */
public class SentenceSpliter {
	private static String[] SentSpliter={"/wj","/ww","/wt","/wd","/wf"};
	
	/**
	 * 返回句子矩阵,（分隔符去掉）
	 * @param text
	 * @return
	 */
	public String[][] getSentences(String text){
		KeyWord words=new KeyWord();
		LinkedList<String[]> sent=new LinkedList<String[]>();
		String[] re=words.WordsExtract(text, null, true);
		LinkedList<String> temp=new LinkedList<String>();
		int i,j;
		outer:for (i = 0; i < re.length; i++) {
						for (j = 0; j < SentSpliter.length; j++) {//遍历每个分隔符
							if (re[i].endsWith(SentSpliter[j]) && !temp.isEmpty()){//是句子的分隔符,且句子不为空
								sent.add(temp.toArray(new String[temp.size()]));
								temp.clear();
								continue outer;//继续外层循环
							}
						}
						//句中的成分
						temp.add(re[i]);
		}
		return sent.toArray(new String[sent.size()][]);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text="没有想到开封这个地方.居然有服务这样好的宾馆。酒店人员的培训是很到位了。" +
				"服务和我经历过的一些五星级酒店差不多了。果然是市委招待所的班子.缺点就是我们住的哪" +
				"个房间,设施有点陈旧.交通很便利.从酒店步行5分钟就到了最繁华的开封的小吃一条街.";
		SentenceSpliter SS=new SentenceSpliter();
		String[][] re=SS.getSentences(text);
		for (int i = 0; i < re.length; i++) {
			for (int j = 0; j < re[i].length; j++) {
				System.out.print(re[i][j]);
			}
			System.out.println();
		}
	}

}
