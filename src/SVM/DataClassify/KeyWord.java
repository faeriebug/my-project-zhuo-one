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
 * ���ķִʸ�����
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
	 * �ִ�,���ϲ��ظ��Ĵ�� ��֤ԭ�ı������˳��
	 * 
	 * @param sInput
	 *            ���ִʵ��ı��ַ���
	 * @param wf
	 *            �������������Ϊnull,��ֱ�ӽ����Ｐ�������Ϣ
	 * @return �ִʵĽ��
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
//		String text = "��̺���ߣ������µĵ���͹������˯�úܲ�����������ܲ�ܳ����������жξ��룬 ���Ǻܷ��� ���Ҿ������͸��"
//				+ "������� 2007��7��2�� �� �Ƶ����ۺ��� �ܰ� ����ȥ�����Ժ� ��ʧ������ Ϊʲô��������ô�࣬ ��������ס�������סһ����˷���";
//
//		KeyWord words = new KeyWord();
//		String[] re = words.WordsExtract(text, null, true);
//		for (int i = 0; i < re.length; i++) {
//			System.out.println(re[i]);
//		}
		// String sInput="����helloworlf!,,";
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
