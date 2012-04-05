package com.openlab.publicopinion.DataClassify.Sentiment;

import java.util.HashMap;
import java.util.Map;

import com.openlab.publicopinion.DataClassify.FeatureCounter;
import com.openlab.publicopinion.DataClassify.Sentiment.negDegProces.ndpCls1;

public class SemanticFeatureCounter implements FeatureCounter{
	private negDegProc ndProc;
	private SentenceSpliter SS=new SentenceSpliter();
	
	public SemanticFeatureCounter(String negPath,String degPath,String sentPath,String vsiPath){
		ndProc=new ndpCls1(new SentiDictionary(negPath, degPath, sentPath, vsiPath));
	}
	
	public SemanticFeatureCounter(){
		ndProc=new ndpCls1(new SentiDictionary());
	}
	
	@Override
	public Map<String,Integer> Count(Map<String, Integer> dic, String text) {
		String[][] sents=SS.getSentences(text);
		Map<String,Integer> setFeature=new HashMap<String, Integer>();
		for (String[] sent : sents) {
			// TODO Auto-generated method stub
			Map<String, Integer> re = ndProc.matchDeg(dic, sent);
			for (String s : re.keySet()) {
				if (setFeature.containsKey(s)) {
					setFeature.put(s, setFeature.get(s)+re.get(s));
				}else
					setFeature.put(s, re.get(s));
			}
		}
		return setFeature;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	

}
