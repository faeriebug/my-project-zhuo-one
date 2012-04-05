package com.openlab.publicopinion.DataClassify.FeatureSelection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.openlab.publicopinion.DataClassify.FeatureMaker;

public class InformationGainWordSelection extends FeatureSelection {
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "IG";
	}
	
	@Override
	public Number getFeatureForTermInClass(String wordID, int clsID,TrainingDataManager TDM) {
		// TODO Auto-generated method stub
		return IGFS_TermForAllClass(wordID, TDM);
	}

	@Override
	public void ConstructFeatureDictionary(FeatureMaker feature, String libPath,String resultFilePath, int threshold) {
		// TODO Auto-generated method stub
		TrainingDataManager TDM=new TrainingDataManager(feature, libPath);
		Map<String, int[]> mp=TDM.getMap();
		final HashMap<String, Double> igInfo=new HashMap<String, Double>();
		
		for(String wordID:mp.keySet()){//对所有词计算平均卡方值
			igInfo.put(wordID, IGFS_TermForAllClass(wordID, TDM));
		}
		
		//按照DF降序排列
		Object[] keys=igInfo.keySet().toArray();
		Arrays.sort(keys,new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return Double.compare(igInfo.get(o2), igInfo.get(o1));
			}
		});
		try{
			FileWriter fw=new FileWriter(resultFilePath);
			int m=0;//词典从零开始
			fw.write((threshold>igInfo.size()?igInfo.size():threshold)+"\n");//写入词典大小
			for(Object i:keys){
				if(m>=threshold)break;//已经取满threshold个特征词
				//System.out.println(i+","+m);
				fw.write(i+","+m+"\n");
				m++;
			}
			fw.flush();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private double IGFS_TermForAllClass(String wordID,TrainingDataManager TDM) {
		double A,B,C,D,IG=0,Pci,Pt,P_t,Pcit,Pci_t;
		int[] cl;int N=TDM.getTotalDocNum();
		String[] cls=TDM.getClassification();
		Map<String, int[]> mp=TDM.getMap();
		
		for(int m=0;m<cls.length;m++){
			cl=mp.get(wordID);
			A=cl[m];	
			B=TDM.getDocNumOfCls(m)-A;	
			C=0;															
			for(int n=0;n<cl.length;n++){
				if(n!=m)
					C+=cl[n];
			}
			D=TDM.getTotalDocNum()-TDM.getDocNumOfCls(m)-C;
			Pci=(A+B)/(double)N;
			Pt=(A+C)/(double)N;
			P_t=(B+D)/(double)N;
			Pcit=(double)A/(A+C);
			Pci_t=(double)B/(B+D);
			Pcit=Pcit<=0?1:Pcit;//Pcit可能为零，令它为1，可以表示为零
			Pci_t=Pci_t<=0?1:Pci_t;//Pci_t可能为零，令它为1，可以表示为零
			IG+=-Pci*Math.log(Pci)+Math.log(Pcit)*Pt*Pcit+Math.log(Pci_t)*P_t*Pci_t;
			
		}
		return IG;
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		InformationGainWordSelection DFWS=new InformationGainWordSelection();
//		long start=System.currentTimeMillis();
//		
//		//DFWS.GlobalDFFS("Test\\dictionary.txt","TanCorpMinTrain\\","Test\\FeatureDictionary.txt",500);
//		DFWS.ConstructFeatureDictionary("Test\\dictionary.txt","TrainingData\\","Test\\FeatureDictionary.txt",5000);
//		long end=System.currentTimeMillis();
//		System.out.println("程序执行："+(end-start)/1000.0+"s");
		long start=System.currentTimeMillis();
		long end=System.currentTimeMillis();
		System.out.println("程序执行："+(end-start)/1000.0+"s");
	}

}
