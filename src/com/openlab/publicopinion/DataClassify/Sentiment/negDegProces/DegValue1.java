package com.openlab.publicopinion.DataClassify.Sentiment.negDegProces;

public class DegValue1 implements DegValue {

	//×î10;ºÜ8;½Ï5£»ÉÔ2
	@Override
	public int getValue(int degree,int orignDeg) {
		// TODO Auto-generated method stub
		switch (degree) {
		case 1:
			return orignDeg>=2?orignDeg:2;
		case 2:
			return orignDeg>=5?orignDeg:5;
		case 3:
			return orignDeg>=8?orignDeg:8;
		case 4:
			return orignDeg>=10?orignDeg:10;
		default:
			return orignDeg;
		}
		
	}

}
