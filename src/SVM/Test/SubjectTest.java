package SVM.Test;

import java.io.FileWriter;
import java.io.IOException;

import SVM.FeatureSelection.*;
import SVM.FeatureWeighting.*;
import SVM.Solver.process.GridSearch;
import SVM.Solver.process.svm_predict;
import SVM.Solver.process.svm_train;
//import SVM.Solver.GridSearch;
//import SVM.Solver.SMO_Solver;
import SVM.Subject.SubjectFeatureCounter;
import SVM.Subject.SubjectFeatureMaker;

public class SubjectTest {
	private FeatureSelection[] FeatureDictionary;
	private FeatureWeight[] TrainingFeaturesCalc;
	private int[] wordNum;

	public SubjectTest(FeatureSelection[] FeatureDictionary,
			FeatureWeight[] TrainingFeaturesCalc, int[] wordNum) {
		this.FeatureDictionary = FeatureDictionary;
		this.TrainingFeaturesCalc = TrainingFeaturesCalc;
		this.wordNum = wordNum;
	}

	public void StartTesting() throws IOException {
		FileWriter fileWriter = new FileWriter("Test\\AlgorithmTestResult");
		fileWriter
				.write("FeatureDictionary+FeatureNumber+TrainingFeaturesCalc+Accuracy\n");
		double count = 0, total = FeatureDictionary.length
				* TrainingFeaturesCalc.length * wordNum.length;
		long init,start, end;
		double timepast = 0, timeneed = 0;
		SubjectFeatureMaker SF = new SubjectFeatureMaker();
		SubjectFeatureCounter SFC = new SubjectFeatureCounter();
		for (FeatureSelection FS : FeatureDictionary) {
			for (int wNr : wordNum) {
				System.out.println(">>Feature selection: " + FS.getName()
						+ ". feature number:" + wNr);
				start = System.currentTimeMillis();
				FS.ConstructFeatureDictionary(SF, "ClsTest\\Train\\",
						"ClsTest\\Result\\FeatureDictionary", wNr);
				System.out.println("\t>>finished. time cost:"
						+ (System.currentTimeMillis() - start) / 1000.0 + "s");
				for (FeatureWeight FC : TrainingFeaturesCalc) {
					System.out.println(">>Feature weighting: " + FC.getName()
							+ " is calculating training Feature vector");
					init=start = System.currentTimeMillis();
					FC.CalcFeaturesForAllFiles_Training(SF, SFC,
							"ClsTest\\Result\\FeatureDictionary",
							"ClsTest\\Train\\",
							"ClsTest\\Result\\CategoriedFiles");
					System.out.println("\t>>finished. time cost:"+(System.currentTimeMillis() - start)/1000.0 + " s");
					
					System.out.println(">>Feature weighting: " + FC.getName()
							+ " is calculating testing Feature vector");
					start = System.currentTimeMillis();
					FC.CalcFeaturesForAllFiles_Training(SF, SFC,
							"ClsTest\\Result\\FeatureDictionary",
							"ClsTest\\Test\\", "ClsTest\\Result\\TestFiles");
					System.out.println("\t>>finished. time cost:"+(System.currentTimeMillis() - start) /1000.0 + " s");
					
					System.out.println(">>Grid search");
					String[] args = new String[] { "ClsTest\\Result\\CategoriedFiles" };
					GridSearch GS = new GridSearch();
					start = System.currentTimeMillis();
					double[] t = GS.ParameterSelection(args);
					System.out.println("\t>>finished. time cost:"+(System.currentTimeMillis() - start) /1000.0 + " s");
					
					String[] argv;
					svm_train svmtrain = new svm_train();
					try {
						argv = new String[] { "-c", Double.toString(t[0]),
								"-g", Double.toString(t[1]),
								"ClsTest\\Result\\CategoriedFiles" };
						System.out.println(">>SVM train");
						start = System.currentTimeMillis();
						svmtrain.run(argv);
						System.out.println("\t>>finished. time cost:"+(System.currentTimeMillis() - start) /1000.0 + " s");
						
						System.out.println(">>Feedback testing");
						start = System.currentTimeMillis();
						argv = new String[] {
								"ClsTest\\Result\\CategoriedFiles",
								"ClsTest\\Result\\CategoriedFiles.model",
								"ClsTest\\Result\\CategoriedFiles.out" };
						String file = svm_predict.Feedback(argv);
						System.out.println("\t>>finished. time cost:"+(System.currentTimeMillis() - start) /1000.0 + " s");
						if (file != null) {
//							System.out.println(">>begin to feedback!");
							args = new String[] { file };
							start = System.currentTimeMillis();
							t = GS.ParameterSelection(args);
							argv = new String[] { "-c", Double.toString(t[0]),
									"-g", Double.toString(t[1]), file };
							svmtrain.run(argv);
							System.out.println("\t>>finished. time cost:"+(System.currentTimeMillis() - start) /1000.0 + " s");
						} else {
//							System.out.println(">>no need to feedback");
							file = "ClsTest\\Result\\CategoriedFiles";
						}
						file += ".model";
						
						System.out.println(">>svm Predict");
						start = System.currentTimeMillis();
						argv = new String[] { "ClsTest\\Result\\TestFiles",
								file, "ClsTest\\Result\\TestFiles.out" };
						int[] tmp = svm_predict.Init(argv);
						System.out.println("\t>>finished. time cost:"+(System.currentTimeMillis() - start) /1000.0 + " s");
						System.out.println("\t>>result: "+tmp[0] + "/" + tmp[1]+" accuracy="+1.0*tmp[0]/tmp[1]*100+"%");

						fileWriter.write(FS.getName() + "+" + wNr + "+"
								+ FC.getName() + "+" + tmp[0] + "/" + tmp[1]
								+ "+" + (100.0 * tmp[0] / tmp[1]) + "\n");
						fileWriter.flush();
						end = System.currentTimeMillis();
						timepast += end - init;
						timeneed = (long) (total - ++count) * (end - init);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out
							.printf("finish:%.2f%%  Time past:%.2f h  Time needs:%.2f h\n",
									(count) / total * 100,
									timepast / 1000 / 60 / 60,
									timeneed / 1000 / 60 / 60);
				}
				
			}
			System.out.println("calculating finished.");
		}
		fileWriter.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 构建特征词典的类数组
		FeatureSelection[] FeatureDictionary = new FeatureSelection[] { new GlobalDFWordSelection() };
		// 计算训练语料库特征向量的类数组
		FeatureWeight[] TrainingFeaturesCalc = new FeatureWeight[] { new TF_IDFWeighting() };
		// //计算测试语料特征向量的类数组
		// FeatureWeight[] TestingFeatureCalc=new FeatureWeight[]{new
		// TFCWeighting(),new TFWeighting()};
		// 特征词选择的数量
		int[] wordNum = new int[] { 100 };

		SubjectTest AT = new SubjectTest(FeatureDictionary,
				TrainingFeaturesCalc, wordNum);
		try {
			AT.StartTesting();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
