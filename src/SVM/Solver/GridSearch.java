package SVM.Solver;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GridSearch {

	SMO_Solver ss=new SMO_Solver();
	private  double c_begin=-5,c_end=15,c_step=2;
	private  double g_begin=3,g_end=-15,g_step=-2;
	
	private int nr_local_worker = 1;
	
	private  double[] range_f(double begin,double end,double step){
		double[] seq=new double[(int) Math.round(((end-begin)/step)+1)];
		int i=0;
		while(true){
			if(step>0 && begin>end)break;
			if(step<0 && begin<end)break;
			seq[i++]=begin;
			begin=begin+step;
		}
		return seq;
	}
	
	
	private  double[] permute_sequence(double[] seq){
		int n=seq.length;
		if(n<=1)return seq;
		
		int mid=n/2;
			double[] left=new double[mid];
			System.arraycopy(seq, 0, left, 0, left.length);
		left=permute_sequence(left);
			double[] right=new double[n-mid-1];
			System.arraycopy(seq, mid+1, right, 0, right.length);
		right=permute_sequence(right);
		seq[0]=seq[mid];
		int L=0,R=0,K=1;
		while(L<left.length || R<right.length){
			if(L<left.length)	seq[K++]=left[L++];
			if(R<right.length)seq[K++]=right[R++];
		}
		return seq;
	}
	
	private  ConcurrentLinkedQueue<double[]> calculate_jobs(){
		double[] c_seq = permute_sequence(range_f(c_begin,c_end,c_step));
		double[] g_seq = permute_sequence(range_f(g_begin,g_end,g_step));
		double nr_c = c_seq.length;
		double nr_g = g_seq.length;
		int i = 0;
		int j = 0;
		ConcurrentLinkedQueue<double[]> jobs=new ConcurrentLinkedQueue<double[]>();

		while(i < nr_c || j < nr_g){
			if(i/nr_c < j/nr_g){
				// increase C resolution
				for(int k=0;k<j;k++)
					jobs.add(new double[]{c_seq[i],g_seq[k]});
				i = i + 1;
			}else{
				// increase g resolution
				for(int k=0;k<i;k++)
					jobs.add(new double[]{c_seq[k],g_seq[j]});
				j = j + 1;
			}
		}
		return jobs;
	}
	
	
	private  double trainAccuracy(double c,double g)throws Exception{
		return ss.mainRoutine(c, g);
	}
	
	public  double[] ParameterSelection(){
		// put jobs in queue
		ss.ReadProblem();
		ConcurrentLinkedQueue<double[]> job_queue = calculate_jobs();
		ConcurrentLinkedQueue<double[]> result_queue=new ConcurrentLinkedQueue<double[]>();
		// fire local workers
		Worker[] localworkers=new Worker[nr_local_worker];
		for(int i=0;i<localworkers.length;i++){
			localworkers[i]=new Worker("worker:"+i,job_queue, result_queue);
			localworkers[i].start();
		}
		
		for(Worker i:localworkers){
			try {
				i.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}//等待线程结束
		}

		// gather results

		double best_rate = -1;
		double best_c1,best_g1,best_c,best_g;
		best_c1=best_g1=best_c=best_g=0;
		Iterator<double[]> it=result_queue.iterator();
		double[] tmp;
		while(it.hasNext()){
			tmp=it.next();
			if( (tmp[2] > best_rate) || (tmp[2]==best_rate && tmp[1]==best_g1 && tmp[0]<best_c1)){
						best_rate = tmp[2];
						best_c1=tmp[0];
						best_g1=tmp[1];
						best_c = Math.pow(2,tmp[0]);
						best_g = Math.pow(2, tmp[1]);
						System.out.println("c="+best_c+" g="+best_g+" rate="+best_rate);
			}
		}
		return new double[]{best_c, best_g, best_rate};
	}
	
	/**
	 * 多线程计算
	 * @author HuHaixiao
	 *
	 */
	private class Worker extends Thread{
		private ConcurrentLinkedQueue<double[]> job_queue;
		private ConcurrentLinkedQueue<double[]> result_queue;
		public Worker(String name,ConcurrentLinkedQueue<double[]> job_queue,ConcurrentLinkedQueue<double[]> result_queue){
			super(name);
			this.job_queue=job_queue;
			this.result_queue=result_queue;
		}

		public void run() {
			//System.out.println(this.getName()+" start calcing...");
			double[] job;
			double cexp,gexp,rate;
			while(true){
				try {
					job=job_queue.poll();
					if(job==null)break;//无计算任务，则退出
					cexp=job[0];gexp=job[1];
					rate=trainAccuracy(Math.pow(2, cexp),Math.pow(2, gexp));
					System.out.println("c="+cexp+" g="+gexp+" rate="+rate);
					result_queue.add(new double[]{cexp, gexp, rate});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			//System.out.println("计算完毕！"+this.getName()+" calc job:"+jobCalc);
		}
		
	}

	public static void main(String[]args){
		GridSearch gs=new GridSearch();
		double[] re=gs.ParameterSelection();
		System.out.println("c="+re[0]+" g="+re[1]+" rate="+re[2]);
	}
	
}
