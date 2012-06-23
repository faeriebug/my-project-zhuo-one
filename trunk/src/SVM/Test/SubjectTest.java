package SVM.Test;

import java.io.FileWriter;
import java.io.IOException;

import SVM.FeatureSelection.*;
import SVM.FeatureWeighting.*;
import SVM.Solver.GridSearch;
import SVM.Solver.SMO_Solver;
import SVM.Subject.SubjectFeatureCounter;
import SVM.Subject.SubjectFeatureMaker;


public class SubjectTest {
	private FeatureSelection[] FeatureDictionary;
	private FeatureWeight[] TrainingFeaturesCalc;
	private int[] wordNum;
	
	
	public SubjectTest(FeatureSelection[] FeatureDictionary,FeatureWeight[] TrainingFeaturesCalc,int[] wordNum){
		this.FeatureDictionary=FeatureDictionary;
		this.TrainingFeaturesCalc=TrainingFeaturesCalc;
		this.wordNum=wordNum;
	}
	
	public void StartTesting() throws IOException{
		FileWriter fileWriter=new FileWriter("Test\\SubjectTest\\Result\\AlgorithmTestResult.txt");
		fileWriter.write("FeatureDictionary+FeatureNumber+TrainingFeaturesCalc+Accuracy\n");
		double count=0,total=FeatureDictionary.length*TrainingFeaturesCalc.length*wordNum.length;
		long start,end;
		double timepast=0,timeneed=0;
		SubjectFeatureMaker SF=new SubjectFeatureMaker();
		SubjectFeatureCounter SFC=new SubjectFeatureCounter();
		for(FeatureSelection FS:FeatureDictionary){
			for(int wNr:wordNum){
						System.out.println(">>"+FS.getName()+" is constructing dictionary>word number:"+wNr);
				FS.ConstructFeatureDictionary(SF,"Test\\SubjectTest\\TrainingData\\","Test\\SubjectTest\\Result\\FeatureDictionary.txt",wNr);
				for(FeatureWeight FC:TrainingFeaturesCalc){
					//for(FeatureWeight FW:TestingFeatureCalc){
							System.out.printf("finish:%.2f%%  Time past:%.2f h  Time needs:%.2f h\n", (count++)/total*100,timepast/1000/60/60,timeneed/1000/60/60);
							start=System.currentTimeMillis();
							System.out.print("\t>>"+FC.getName()+" is calculating training Feature vector");
					FC.CalcFeaturesForAllFiles_Training(SF,SFC,"Test\\SubjectTest\\Result\\FeatureDictionary.txt", "Test\\SubjectTest\\TrainingData\\", "Test\\SubjectTest\\Result\\CategoriedFiles");
							System.out.println("->finished");
							System.out.print("\t>>"+FC.getName()+" is calculating testing Feature vector");
					FC.CalcFeaturesForAllFiles_Training(SF,SFC,"Test\\SubjectTest\\Result\\FeatureDictionary.txt", "Test\\SubjectTest\\TestingData\\", "Test\\SubjectTest\\Result\\TestFiles");
							System.out.println("->finished");
						
					String[] args=new String[]{"Test\\SubjectTest\\Result\\CategoriedFiles"};
					GridSearch gs=new GridSearch();
							System.out.print("\t>>Grid search svmtrain svmpredict");
					double[] t=gs.ParameterSelection();
					String[] argv;
					SMO_Solver ss = new SMO_Solver();
						
					ss.ReadTrainProblemForTrainning("heart_scale");
					ss.Train(0.125,8.0);
					argv=new String[]{"Test\\SubjectTest\\Result\\TestFiles","Test\\SubjectTest\\Result\\CategoriedFiles.model","Test\\SubjectTest\\Result\\TestFiles.out"};
//								System.out.println("->finished");
//								System.out.printf("\t%s--%d--%s:  %d/%d,%.2f%%\n\n", FS.getName(),wNr,FC.getName(),tmp[0],tmp[1],100.0*tmp[0]/tmp[1]);
//							fileWriter.write(FS.getName()+"+"+wNr+"+"+FC.getName()+"+"+tmp[0]+"/"+tmp[1]+"+"+(100.0*tmp[0]/tmp[1])+"\n");
						
						fileWriter.flush();
						end=System.currentTimeMillis();
						timepast+=end-start;
						timeneed=(long)(total-count)*(end-start);
					//}
				}
			}
		}
		System.out.println("calculating finished.");
		fileWriter.close();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//���������ʵ��������
		FeatureSelection[] FeatureDictionary=new FeatureSelection[]{
				new GlobalDFWordSelection()};
		//����ѵ�����Ͽ�����������������
		FeatureWeight[] TrainingFeaturesCalc=new FeatureWeight[]{new TF_IDFWeighting()
				};
//		//���������������������������
//		FeatureWeight[] TestingFeatureCalc=new FeatureWeight[]{new TFCWeighting(),new TFWeighting()};
		//������ѡ�������
		int[] wordNum=new int[]{1000};
		
		SubjectTest AT=new SubjectTest(FeatureDictionary, TrainingFeaturesCalc, wordNum);
		try {
			AT.StartTesting();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
