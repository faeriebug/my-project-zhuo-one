package StanfordNLP;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * 测试了斯坦福英文处理工具，功能包括：分词，词性标注，词形还原等
 * 
 * @author Administrator
 * 
 */
public class Test {
	public void run() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		// props.put("annotators",
		// "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.put("annotators", "tokenize, ssplit,pos, lemma");
		System.err.close();
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// read some text in the text variable
		String text = "The Stanford NLP Group makes parts of our Natural Language Processing software available to the public. These are statistical NLP toolkits for various major computational linguistics problems. They can be incorporated into applications with human language technology needs."; // Add
		// create an empty Annotation just with the given text
		//中文注释测试
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
//		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
//		List<CoreLabel> sentences = ;
//		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : document.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				// String ne = token.get(NamedEntityTagAnnotation.class);
				String lemma = token.get(LemmaAnnotation.class);
				// System.out.println(word + " pos:" + pos + " ne:" + ne
				// + " lemma:" + lemma);
				System.out.println(word + " pos:" + pos + " lemma:" + lemma);
			}

			// this is the parse tree of the current sentence
			// Tree tree = sentence.get(TreeAnnotation.class);
			//
			// // this is the Stanford dependency graph of the current sentence
			// SemanticGraph dependencies = sentence
			// .get(CollapsedCCProcessedDependenciesAnnotation.class);
//		}

		// This is the coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		// Map<Integer, CorefChain> graph = document
		// .get(CorefChainAnnotation.class);
	}

	public static void main(String[] args) {
		Test t = new Test();
		t.run();
	}

}
