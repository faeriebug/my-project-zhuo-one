package SVM.Solver;

/**
 * 不再使用
 * @author Administrator
 *
 */
public class SVM_Feedback {

	public int[] TrainAndTest(String trainFile,String testFile) {
		GridSearch gs = new GridSearch();
		System.out.print("\t>>Grid search");
		double[] t = gs.ParameterSelection(trainFile);
		// String[] argv;
		SMO_Solver ss = new SMO_Solver();
		ss.ReadTrainProblemForTrainning(trainFile);
		System.out.println("finish read problem");
		ss.Train(t[0], t[1]);
		System.out.println("finish train");
		String fileProd=ss.PredictCut(trainFile);
		t=gs.ParameterSelection(fileProd);
		ss.ReadTrainProblemForTrainning(fileProd);
		ss.Train(t[0], t[1]);
		return ss.Predict(testFile);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SVM_Feedback sf=new SVM_Feedback();
		int[] tmp=sf.TrainAndTest("ClsTest\\Result\\CategoriedFiles", "ClsTest\\Result\\TestFiles");
		System.out.println(1.0*tmp[0]/tmp[1]);
	}
}
