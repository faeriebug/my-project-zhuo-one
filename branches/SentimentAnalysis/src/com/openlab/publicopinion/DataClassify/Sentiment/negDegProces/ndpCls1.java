package com.openlab.publicopinion.DataClassify.Sentiment.negDegProces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.openlab.publicopinion.DataClassify.Sentiment.*;

public class ndpCls1 implements negDegProc {
	
	//情感词典
	private SentiDictionary SD;
	public ndpCls1(SentiDictionary sd){
		this.SD=sd;
	}
	
	@Override
	public String[] match(String[] sent) {
		// TODO Auto-generated method stub
		ArrayList<String> lisF=new ArrayList<String>();
		String[] wrd;
		int preFe=-1;//表示提取的上一个特征所在的索引
		for (int i = 0; i < sent.length; i++) {
			wrd=sent[i].split("/");
			if (isSentiWrd(wrd)) {//如果为情感词,
				String[] fwrd;
				boolean negFlag,termi;
				for (int j = i-1; j>preFe; j--) {//向前搜索，直至满足终止条件;在上一个特征提取位置停止
					negFlag=false;termi=false;
					fwrd=sent[j].split("/");
					if(fwrd[1].startsWith("d")){//为副词
						if(SD.getNeg().contains(fwrd[0])){//判断否定副词，
							negFlag=true;
						}else if(!SD.getDeg().containsKey(fwrd[0]))//不为程度副词，终止匹配
							termi=true;
					}else if(fwrd[1].startsWith("v") || fwrd[1].startsWith("a")){//判断VSI需要多前进一位
						if(SD.getVSI().contains(fwrd[0])){//如果直接包含VSI
							negFlag=true;
						}else if(--j>preFe){//与前一个词语共同构成VSI
							String[] ffwrd=sent[j].split("/");
							if(SD.getVSI().contains(ffwrd[0]+fwrd[0])){
								negFlag=true;
							}else{
								termi=true;
							}
						}else{
							termi=true;
						}
					}else
						termi=true;
					if(negFlag)//找到否定修饰成分
						wrd[0]=getAntonym(wrd[0]);
					else if(termi)
						break;
				}
				preFe=i;
				lisF.add(wrd[0]);
			}
		}
		return lisF.toArray(new String[lisF.size()]);
	}

	/**
	 * 判断词语是否为情感词
	 * @param wrd
	 * @return
	 */
	public boolean isSentiWrd(String[] wrd){
		//首先判断词性，然后查找词典
		boolean re=wrd[1].startsWith("a")//形容词
								||(wrd[1].startsWith("n") && wrd[1]!="nr" && wrd[1]!="ns" && wrd[1]!="nt")//名词
								|| (wrd[1].startsWith("v") && wrd[1]!="vshi" && wrd[1]!="vyou");
		if(re)//满足条件，查词典
			return SD.getSenti().contains(wrd[0]);
		return false;
	}
	
	/**
	 * 获取词的反义词
	 * @return
	 */
	public String getAntonym(String wrd){
		if (wrd.endsWith("_N"))
			return wrd.substring(0, wrd.length()-2);
		return wrd+"_N";
	}


	@Override
	public Map<String, Integer> matchDeg(Map<String,Integer> dic,String[] sent) {
		// TODO Auto-generated method stub
		Map<String, Integer> mF=new HashMap<String, Integer>();
		DegValue DV=new DegValue1();
		String[] wrd;int degree;
		int preFe=-1;//表示提取的上一个特征所在的索引
		for (int i = 0; i < sent.length; i++) {
			wrd=sent[i].split("/");
			if (dic.containsKey(wrd[0]) || dic.containsKey(getAntonym(wrd[0]))) {//如果为情感词,
				degree=1;
				//下面进行否定词处理和程度副词的处理
				String[] fwrd;
				boolean negFlag,termi;
				//negFlag表示是否加入否定词了，termi表示是否终止这次的匹配
				for (int j = i-1; j>preFe; j--) {//向前搜索，直至满足终止条件;在上一个特征提取位置停止
					negFlag=false;termi=false;
					fwrd=sent[j].split("/");
					if(fwrd[1].startsWith("d")){//为副词,这里主要进行程度副词的处理，外加否定副词的处理
						if(SD.getNeg().contains(fwrd[0])){//判断否定副词，
							negFlag=true;
						}else if(SD.getDeg().containsKey(fwrd[0])){//为程度副词,修改修饰词的频数
							degree=DV.getValue(SD.getDeg().get(fwrd[0]), degree);
						}else{
							termi=true;
						}
					}//这里主要是VSI否定词的处理
					else if(fwrd[1].startsWith("v") || fwrd[1].startsWith("a")){//判断VSI需要多前进一位
						if(SD.getVSI().contains(fwrd[0])){//如果直接包含VSI
							negFlag=true;
						}else if(--j>preFe){//与前一个词语共同构成VSI
							String[] ffwrd=sent[j].split("/");
							if(SD.getVSI().contains(ffwrd[0]+fwrd[0])){
								negFlag=true;
							}else{
								termi=true;
							}
						}else{
							termi=true;
						}
					}else//前方的词语无需处理，结束匹配
						termi=true;
					if(negFlag)//找到否定修饰成分
						wrd[0]=getAntonym(wrd[0]);
					else if(termi)
						break;
				}
				preFe=i;
				//加入mF中
				if (mF.containsKey(wrd[0])) {
					mF.put(wrd[0], mF.get(wrd[0])+degree);//原来有责加上本次处理的频数
				}else
					mF.put(wrd[0], degree);
			}
		}
		return mF;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SentiDictionary SD=new SentiDictionary();
		ndpCls1 ndp1=new ndpCls1(SD);
		String[] fe=ndp1.match(new String[]{"这/rzv","件/q","客房/n","让/v","我/rr","很/d","不/d","满意/v","!/wt"});
		for (String feature : fe) {
			System.out.println(feature);
		}
//		System.out.println(ndp1.isSentiWrd(new String[]{"满意","/v"}));
	}
}
