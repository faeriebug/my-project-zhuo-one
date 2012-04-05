package com.openlab.publicopinion.DataClassify.Test;

import java.io.FileWriter;
import java.io.IOException;
import libsvm.process.*;

import com.openlab.publicopinion.DataClassify.FeatureSelection.*;
import com.openlab.publicopinion.DataClassify.FeatureWeighting.*;
import com.openlab.publicopinion.DataClassify.Sentiment.SemanticFeatureCounter;
import com.openlab.publicopinion.DataClassify.Sentiment.SemanticFeatureMaker;
import com.openlab.publicopinion.DataClassify.Sentiment.SentiDictionary;

public class SentimentTest {
	private FeatureSelection[] FeatureDictionary;
	private FeatureWeight[] TrainingFeaturesCalc;
	private int[] wordNum;
	
	
	public SentimentTest(FeatureSelection[] FeatureDictionary,FeatureWeight[] TrainingFeaturesCalc,int[] wordNum){
		this.FeatureDictionary=FeatureDictionary;
		this.TrainingFeaturesCalc=TrainingFeaturesCalc;
		this.wordNum=wordNum;
	}
	
	public void StartTesting() throws IOException{
		FileWriter fileWriter=new FileWriter("Test\\SentimentTest\\Result\\AlgorithmTestResult.txt");
		fileWriter.write("FeatureDictionary+FeatureNumber+TrainingFeaturesCalc+Accuracy\n");
		double count=0,total=FeatureDictionary.length*TrainingFeaturesCalc.length*wordNum.length;
		long start,end;
		double timepast=0,timeneed=0;
		SemanticFeatureMaker SF=new SemanticFeatureMaker(new SentiDictionary());
		SemanticFeatureCounter SFC=new SemanticFeatureCounter();
		for(FeatureSelection FS:FeatureDictionary){
			for(int wNr:wordNum){
						System.out.println(">>"+FS.getName()+" is constructing dictionary>word number:"+wNr);
				FS.ConstructFeatureDictionary(SF,"Test\\SentimentTest\\TrainingData\\","Test\\SentimentTest\\Result\\FeatureDictionary.txt",wNr);
				for(FeatureWeight FC:TrainingFeaturesCalc){
					//for(FeatureWeight FW:TestingFeatureCalc){
							System.out.printf("finish:%.2f%%  Time past:%.2f h  Time needs:%.2f h\n", (count++)/total*100,timepast/1000/60/60,timeneed/1000/60/60);
							start=System.currentTimeMillis();
							System.out.print("\t>>"+FC.getName()+" is calculating training Feature vector");
					FC.CalcFeaturesForAllFiles_Training(SF,SFC,"Test\\SentimentTest\\Result\\FeatureDictionary.txt", "Test\\SentimentTest\\TrainingData\\", "Test\\SentimentTest\\Result\\CategoriedFiles");
							System.out.println("->finished");
							System.out.print("\t>>"+FC.getName()+" is calculating testing Feature vector");
					FC.CalcFeaturesForAllFiles_Training(SF,SFC,"Test\\SentimentTest\\Result\\FeatureDictionary.txt", "Test\\SentimentTest\\TestingData\\", "Test\\SentimentTest\\Result\\TestFiles");
							System.out.println("->finished");
						
					String[] args=new String[]{"Test\\SentimentTest\\Result\\CategoriedFiles"};
					GridSearch GS=new GridSearch();
							System.out.print("\t>>Grid search svmtrain svmpredict");
					double[] t=GS.ParameterSelection(args);
					String[] argv;
					svm_train svmtrain = new svm_train();
						
					try {
						argv=new String[]{"-c",Double.toString(t[0]),"-g",Double.toString(t[1]),"Test\\SentimentTest\\Result\\CategoriedFiles"};
						svmtrain.run(argv);
						argv=new String[]{"Test\\SentimentTest\\Result\\TestFiles","Test\\SentimentTest\\Result\\CategoriedFiles.model","Test\\SentimentTest\\Result\\TestFiles.out"};
						int[] tmp=svm_predict.Init(argv);
								System.out.println("->finished");
								System.out.printf("\t%s--%d--%s:  %d/%d,%.2f%%\n\n", FS.getName(),wNr,FC.getName(),tmp[0],tmp[1],100.0*tmp[0]/tmp[1]);
							fileWriter.write(FS.getName()+"+"+wNr+"+"+FC.getName()+"+"+tmp[0]+"/"+tmp[1]+"+"+(100.0*tmp[0]/tmp[1])+"\n");
					} catch (IOException e) {
							// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
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
		//构建特征词典的类数组
		FeatureSelection[] FeatureDictionary=new FeatureSelection[]{
				new GlobalDFWordSelection()};
		//计算训练语料库特征向量的类数组
		FeatureWeight[] TrainingFeaturesCalc=new FeatureWeight[]{new TF_IDFWeighting()
				};
//		//计算测试语料特征向量的类数组
//		FeatureWeight[] TestingFeatureCalc=new FeatureWeight[]{new TFCWeighting(),new TFWeighting()};
		//特征词选择的数量
		int[] wordNum=new int[]{1000};
		
		SentimentTest AT=new SentimentTest(FeatureDictionary, TrainingFeaturesCalc, wordNum);
		try {
			AT.StartTesting();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
