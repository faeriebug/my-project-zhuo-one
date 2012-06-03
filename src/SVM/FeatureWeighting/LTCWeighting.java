package SVM.FeatureWeighting;

import java.util.Map;


public class LTCWeighting extends FeatureWeight {
	public static final String Name="LTCWeighting";
	private int docID_calced=-1;//表示已经计算的文档ID，可以减少计算量,因为同一个文档分母是一样的
	private double de=0;//已经计算的文档的结果。
	@Override
	public Number getWight(int docID, int wordID, CategorizingDataManager CDM) {
		// TODO Auto-generated method stub
		Map<Integer, Map<Integer, Integer>> cp=CDM.getMap();
		int 	tf=cp.get(wordID).containsKey(docID)?cp.get(wordID).get(docID):0;
		int 	n=cp.get(wordID).size();
		double  nu=Math.log(tf+1)*Math.log(CDM.N/(double)n+1);
		if(docID_calced!=docID){
			docID_calced=docID;
			de=0;
			for(Integer p:cp.keySet()){
				int tfp=cp.get(p).containsKey(docID)?cp.get(p).get(docID):0;
				int np=cp.get(p).size();
				double tmp=Math.log(tfp+1)*Math.log(CDM.N/(double)np+1);
				de+=tmp*tmp;
			}
		}

		return nu/Math.sqrt(de);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		long start=System.currentTimeMillis();
//		LTCWeighting TFW=new LTCWeighting();
//		TFW.CalcFeaturesForAllFiles_Training("Test\\FeatureDictionary.txt", "TrainingData\\", "Test\\CategoriedFiles");
//		long end=System.currentTimeMillis();
//		System.out.println("程序执行："+(end-start)/1000.0+"s");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "LTC";
	}
}
