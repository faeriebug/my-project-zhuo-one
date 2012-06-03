package SVM.FeatureWeighting;

import java.util.Map;


public class TFWeighting extends FeatureWeight {
	@Override
	public Number getWight(int docID, int wordID, CategorizingDataManager CDM) {
		// TODO Auto-generated method stub
		Map<Integer, Map<Integer, Integer>> cp=CDM.getMap();
		return cp.get(wordID).containsKey(docID)?cp.get(wordID).get(docID):0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		long start=System.currentTimeMillis();
//		TFWeighting TFW=new TFWeighting();
//		TFW.CalcFeaturesForAllFiles_Training("Test\\FeatureDictionary.txt", "TrainingData\\", "Test\\CategoriedFiles");
//		long end=System.currentTimeMillis();
//		System.out.println("≥Ã–Ú÷¥––£∫"+(end-start)/1000.0+"s");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "TF";
	}

}
