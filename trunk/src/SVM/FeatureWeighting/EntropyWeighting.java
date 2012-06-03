package SVM.FeatureWeighting;

import java.util.Map;

@Deprecated
public class EntropyWeighting extends FeatureWeight {

	@Override
	public Number getWight(int docID, int wordID, CategorizingDataManager CDM) {
		// TODO Auto-generated method stub
		Map<Integer, Map<Integer, Integer>> cp=CDM.getMap();
		int 	tf=cp.get(wordID).containsKey(docID)?cp.get(wordID).get(docID):0;
//		int 	n=cp.get(wordID).size();
		double de=0;
		for(Integer p:cp.keySet()){
			int tfp=cp.get(p).containsKey(docID)?cp.get(p).get(docID):0;
			int np=cp.get(p).size();
			de+=Math.log(tfp/(double)np)*tfp/np;
		}
		return Math.log(tf+1)*(1+de/Math.log(CDM.N));
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
