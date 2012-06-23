package SVM.DataClassify;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * 中文分词辅助类
 * 
 * @author HuHaixiao
 * 
 */
public class KeyWord {
	private static StanfordCoreNLP pipeline = null;
	static {
		Properties props = new Properties();
		// props.put("annotators",
		// "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		System.err.close();
		pipeline = new StanfordCoreNLP(props);
	}

	/**
	 * 分词,不合并重复的词语， 保证原文本词语的顺序
	 * 
	 * @param sInput
	 *            待分词的文本字符串
	 * @param wf
	 *            词语过滤器，若为null,则直接将词语及其词性信息
	 * @return 分词的结果
	 * @throws IOException
	 */
	public String[] WordsExtract(String sInput, WordFilter wf) {
		Annotation document = new Annotation(sInput);
		pipeline.annotate(document);
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);
		ArrayList<String> wordDiff = new ArrayList<String>(tokens.size());
		for (CoreLabel token : tokens) {
			// this is the text of the token
			// String word = token.get(TextAnnotation.class);
			// this is the POS tag of the token
			String pos = token.get(PartOfSpeechAnnotation.class);
			// this is the NER label of the token
			// String ne = token.get(NamedEntityTagAnnotation.class);
			String lemma = token.get(LemmaAnnotation.class);
			if (wf == null || !wf.isStoped(lemma, pos)) {
				wordDiff.add(lemma);
			}
			// System.out.println(word + " pos:" + pos + " ne:" + ne
			// + " lemma:" + lemma);
//			System.out.println(" pos:" + pos + " lemma:" + lemma);
		}

		return wordDiff.toArray(new String[wordDiff.size()]);
	}

	public static void main(String[] args) {
//		String text = "地毯很赃，床垫下的弹簧凸出来，睡得很不舒服，隔音很差，很吵，离市区有段距离， 不是很方便 ，我觉得糟糕透了"
//				+ "补充点评 2007年7月2日 ： 酒店的外观很美 很棒 但进去房间以后， 大失所望， 为什么里外会差这么多， 本来打算住二天后来住一天就退房了";
//
//		KeyWord words = new KeyWord();
//		String[] re = words.WordsExtract(text, null, true);
//		for (int i = 0; i < re.length; i++) {
//			System.out.println(re[i]);
//		}
		// String sInput="我是helloworlf!,,";
		// try {
		// for (byte b : sInput.getBytes("utf8")) {
		// System.out.println(b);
		// }
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
