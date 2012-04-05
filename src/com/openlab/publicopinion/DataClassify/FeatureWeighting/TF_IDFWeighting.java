package com.openlab.publicopinion.DataClassify.FeatureWeighting;

import java.util.Map;


public class TF_IDFWeighting extends FeatureWeight {
	public static final String Name="TF_IDFWeighting";
	@Override
	public Number getWight(int docID, int wordID, CategorizingDataManager CDM) {
		// TODO Auto-generated method stub
		Map<Integer, Map<Integer, Integer>> cp=CDM.getMap();
		Integer tf=cp.get(wordID).containsKey(docID)?cp.get(wordID).get(docID):0;
		Integer n=cp.get(wordID).size();
		return tf*Math.log((CDM.N+1)/(double)n);//scaled
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		long start=System.currentTimeMillis();
//		TF_IDFWeighting TFW=new TF_IDFWeighting();
//		TFW.CalcFeaturesForAllFiles_Training("Test\\FeatureDictionary.txt", "TrainingData\\", "Test\\CategoriedFiles.txt");
//		long end=System.currentTimeMillis();
//		System.out.println("≥Ã–Ú÷¥––£∫"+(end-start)/1000.0+"s");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "TF-IDF";
	}
}
