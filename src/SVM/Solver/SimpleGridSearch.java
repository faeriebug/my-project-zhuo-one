package SVM.Solver;

/**
 * 简单遍历的网格搜索
 * @author Administrator
 *
 */
public class SimpleGridSearch {
	private  int c_begin=-5,c_end=15,c_step=2;
	private  int g_begin=3,g_end=-15,g_step=-2;
	SMO_Solver ss;
	public SimpleGridSearch(){
		ss=new SMO_Solver();
	}
	
	public double[] StartGridSearch(){
		ss.ReadTrainProblemForTrainning("heart_scale");
		double rate,best_rate=0;
		int c,g,best_c=0,best_g=0;
		for ( c= c_begin;  c<=c_end; c+=c_step) {
			for ( g= g_begin;  g>=g_end; g+=g_step) {
				rate=ss.TrainWithCrossValidation(Math.pow(2, c),Math.pow(2, g));
				System.out.println("rate="+rate+" c="+c+" g="+g);
				if((rate>best_rate) || (rate==best_rate && g==best_g && c<best_c)){
					best_rate=rate;
					best_c=c;
					best_g=g;
				}
			}
		}
		return new double[]{best_rate,Math.pow(2,best_c),Math.pow(2,best_g)};
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleGridSearch sgs=new SimpleGridSearch();
		double[] re=sgs.StartGridSearch();
		System.out.println("best_rate="+re[0]+" best_c="+re[1]+" best_g="+re[2]);
	}

}
