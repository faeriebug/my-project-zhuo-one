package SVM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * 使用SMO算法解SVM
 * 
 * @author WuyaMony
 * 
 */
public class SMO_Solver {
	private class node {
		int d;// 维数
		double value;// 值
		public node(int d,double value){
			this.d=d;
			this.value=value;
		}
	}

	int N, train_num, first_test_i;// 训练数据数目
	double C, tolerance, gamma, eps;
	double alph[];// 拉格朗日乘子
	Integer target[];// 训练与测试样本的目标值
	double b;
	private double[] error_cache;// 存放non-bound样本误差
	private double[] self_dot_product;// 预存dot_product_func(i,i)的值，以减少计算量
	private node[][] dense_points;// 稀疏矩阵，存放训练与测试样本，0～train_num-1训练；train_num～N-1测试

	private double learned_func_nonlinear(int k) {
		double sum = 0;
		for (int i = 0; i < train_num; i++) {
			try {
				if (alph[i] > 0)
					sum += alph[i] * target[i] * kernel_func(i, k);
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sum -= b;
		return sum;
	}

	private double kernel_func(int i, int k) {
		double sum = dot_product_func(i, k);
		sum *= -2;
		sum += self_dot_product[i] + self_dot_product[k];
		return Math.exp(-sum / gamma);
	}

	private double dot_product_func(int i, int k) {
		double dot = 0;
		int di = 0, dk = 0;
		int leni=dense_points[i].length,lenk=dense_points[k].length;
		while(di<leni && dk<lenk){
			if (dense_points[i][di].d == dense_points[k][dk].d) {
				dot += dense_points[i][di].value * dense_points[k][dk].value;
				di++;
				dk++;
			}else if(dense_points[i][di].d < dense_points[k][dk].d){
				di++;
			}else{
				dk++;
			}
		}
		return dot;
	}

	private boolean takeStep(int i1, int i2) {
		int y1, y2, s;
		double delta_b;
		double alph1, alph2 = 0; // * old_values of alpha_1, alpha_2
		double a1, a2; // * new values of alpha_1, alpha_2
		double E1, E2, L, H, k11, k22, k12, eta, Lobj, Hobj;

		if (i1 == i2)
			return false;
		/******************************************************/
		// 读取参量ｙ，Ｅ
		alph1 = alph[i1];
		y1 = target[i1];
		if (alph1 > 0 && alph1 < C) {
			E1 = error_cache[i1];
		} else {
			E1 = learned_func_nonlinear(i1) - y1;
		}
		try {
			alph2 = alph[i2];
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		y2 = target[i2];
		if (alph2 > 0 && alph2 < C) {
			E2 = error_cache[i2];
		} else {
			E2 = learned_func_nonlinear(i2) - y2;
		}
		s = y1 * y2;
		/*******************************************************/
		// 计算　Ｈ，Ｌ
		if (y1 == y2) {
			double gamma = alph1 + alph2;
			if (gamma > C) {
				L = gamma - C;
				H = C;
			} else {
				L = 0;
				H = gamma;
			}
		} else {
			double gamma = alph1 - alph2;
			if (gamma > 0) {
				L = 0;
				H = C - gamma;
			} else {
				L = -gamma;
				H = C;
			}
		}
		if (L == H)
			return false;
		/********************************************************/
		// 计算eta＝2K12-K11-K22
		k11 = kernel_func(i1, i1);
		k12 = kernel_func(i1, i2);
		k22 = kernel_func(i2, i2);
		eta = 2 * k12 - k11 - k22;
		/********************************************************/
		// 分ets＞0和＜０，计算新的alph２
		if (eta < 0) {
			a2 = alph2 + y2 * (E2 - E1) / eta;
			if (a2 < L) {
				a2 = L;
			} else {
				if (a2 > H)
					a2 = H;
			}
		} else {
			double c1 = eta / 2;
			double c2 = y2 * (E1 - E2) - eta * alph2;
			Lobj = c1 * L * L + c2 * L;
			Hobj = c1 * H * H + c2 * H;
			if (Lobj > Hobj + eps)
				a2 = L;
			else if (Lobj < Hobj - eps)
				a2 = H;
			else
				a2 = alph2;
		}
		/********************************************************/
		// 计算新的alph１
		if (Math.abs(a2 - alph2) < eps)
			return false;
		a1 = alph1 - s * (a2 - alph2);
		if (a1 < 0) {
			a2 += s * a1;
			a1 = 0;
		} else if (a1 > C) {
			a2 += s * (a1 - C);
			a1 = C;
		}

		/**********************************************************/
		// 更新ｂ
		double b1, b2, bnew;
		if (a1 > 0 && a1 < C) {
			bnew = b + E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
		} else {
			if (a2 > 0 && a2 < C)
				bnew = b + E2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2)
						* k22;
			else {
				b1 = b + E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
				b2 = b + E2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22;
				bnew = (b1 + b2) / 2;
			}
		}
		delta_b = bnew - b;
		b = bnew;

		// /************************************************************/
		// //更新w
		double t1 = y1 * (a1 - alph1);
		double t2 = y2 * (a2 - alph2);
		// for (int i=0; i<d; i++)
		// {
		// w[i] += dense_points[i1][i] * t1 + dense_points[i2][i] * t2;
		// }

		/*************************************************************/
		// 更新E
		// float t1 = y1 * (a1-alph1);
		// float t2 = y2 * (a2-alph2);

		for (int i = 0; i < train_num; i++) {
			if (0 < alph[i] && alph[i] < C) {
				error_cache[i] += t1 * kernel_func(i1, i) + t2
						* kernel_func(i2, i) - delta_b;
			}
		}
		error_cache[i1] = 0.;
		error_cache[i2] = 0.;
		/***********************************************************/
		// 存储新的ａｌｐｈ１和ａｌｐｈ２
		alph[i1] = a1; /* Store a1 in the alpha array. */
		alph[i2] = a2; /* Store a2 in the alpha array. */
		return true;
	}

	// ---------------------------------------------------------------------------------------------------
	/*
	 * 以下两层循环，开始时检查所有样本，选择不符合KKT条件的两个乘子进行优化，选择成功，返回1，否则，返回0
	 * 所以成功了，numChanged必然>0，从第二遍循环时，就不从整个样本中去寻找不符合KKT条件的两个乘子进行优化，
	 * 而是从边界的乘子中去寻找，因为非边界样本需要调整的可能性更大，边界样本往往不被调整而始终停留在边界上。
	 * 如果，没找到，再从整个样本中去找，直到整个样本中再也找不到需要改变的乘子为止，此时，算法结束。
	 */
	// ---------------------------------------------------------------------------------------------------
	private int examineExample(int i1) {
		int y1;
		double alph1, E1, r1;
		y1 = target[i1];
		alph1 = alph[i1];
		if (alph1 > 0 && alph1 < C)
			E1 = error_cache[i1];
		else
			E1 = learned_func_nonlinear(i1) - y1;
		r1 = y1 * E1;
		if ((r1 < -tolerance && alph1 < C) || (r1 > tolerance && alph1 > 0)) {
			// ///////////使用三种方法选择第二个乘子
			// 1：在non-bound乘子中寻找maximum fabs(E1-E2)的样本
			// 2：如果上面没取得进展,那么从随机位置查找non-boundary 样本
			// 3：如果上面也失败，则从随机位置查找整个样本,改为bound样本
			if (examineFirstChoice(i1, E1)) // 1
			{
				return 1;
			}
			if (examineNonBound(i1)) // 2
			{
				return 1;
			}
			if (examineBound(i1)) // 3
			{
				return 1;
			}
		}
		// /没有进展
		return 0;
	}

	private boolean examineFirstChoice(int i1, double E1) {
		int k, i2;
		double tmax;
		double E2, temp;
		for (i2 = -1, tmax = 0.0, k = 0; k < train_num; k++) {
			if (alph[k] > 0 && alph[k] < C) {
				E2 = error_cache[k];
				temp = Math.abs(E1 - E2);
				if (temp > tmax) {
					tmax = temp;
					i2 = k;
				}
			}
		}
		if (i2 >= 0) {
			if (takeStep(i1, i2))
				return true;
		}
		return false;
	}

	// 2：如果上面没取得进展,那么从随机位置查找non-boundary 样本
	private boolean examineNonBound(int i1) {
		int k, k0 = (new Random()).nextInt(train_num);
		int i2;
		for (k = 0; k < train_num; k++) {
			i2 = (k + k0) % train_num; // 从随机位开始
			if (alph[i2] > 0.0 && alph[i2] < C) {
				if (takeStep(i1, i2)) {
					return true;
				}
			}
		}
		return false;
	}

	// 3：如果上面也失败，则从随机位置查找整个样本,(改为bound样本)
	private  boolean examineBound(int i1) {
		int k, k0 = (new Random()).nextInt(train_num);
		int i2;
		for (k = 0; k < train_num; k++) {
			i2 = (k + k0) % train_num; // 从随机位开始
			if (takeStep(i1, i2)) {
				return true;
			}
		}
		return false;
	}

	// 计算误差率
	public double error_rate() {
		int ac = 0;
		double tar;
//		System.out
//				.println("----------------------------测试结果------------------------------");
		for (int i = train_num; i < N; i++) {
			tar = learned_func_nonlinear(i);
			if (tar > 0 && target[i] > 0 || tar < 0 && target[i] < 0)
				ac++;
		}
//		System.out.println("精确度：" + accuracy * 100 + "％");
		return (double) ac / (N - train_num);
		
	}

	// 从文件中读取数据，设置target[]的值和样本
	public void ReadProblem() {
		try (BufferedReader br = new BufferedReader(new FileReader(
				"heart_scale"))) {
//			int i = 0;
			String line;
			ArrayList<Integer> list_target=new ArrayList<>();
			ArrayList<node[]> list_dense=new ArrayList<>();
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
//				target[i] = Integer.valueOf(st.nextToken());
				list_target.add(Integer.valueOf(st.nextToken()));
				int m = st.countTokens() / 2;
//				dense_points[i]=new node[m];
				node[] d=new node[m];
				for (int j = 0; j < m; j++) {
					int dim = Integer.valueOf(st.nextToken());
//					dense_points[i][j] = new node(dim,Double.valueOf(st.nextToken()));
					d[j]= new node(dim,Double.valueOf(st.nextToken()));
				}
				list_dense.add(d);
//				i++;
			}
			N=list_target.size();
			target=new Integer[N];
			dense_points = new node[N][];
			list_target.toArray(target);
			list_dense.toArray(dense_points);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 初始化
	private void Init(double c,double gamma) {
		C = c;
		tolerance = 0.001;
		this.gamma = gamma;
		first_test_i = 0;
		train_num = N * 2 / 3;
		eps = 1e-12;
		b = 0;
		alph = new double[train_num];
		error_cache = new double[train_num];
		self_dot_product = new double[N];
		for (int i = 0; i < N; i++)
			// 设置预计算点积（对训练样本的设置，对于测试样本也要考虑）
			self_dot_product[i] = dot_product_func(i, i);
	}

	// 主函数***************************************************
	public double mainRoutine(double c,double gamma) {
//		System.out.println("c="+c+" g="+gamma);
		int i, k;
		// clock_t start,finish;
		int numChanged = 0; // number of alpha[i], alpha[j] pairs changed in a
							// single step in the outer loop
		int examineAll = 1; // flag indicating whether the outer loop has to be
							// made on all the alpha[i]
		Init(c,gamma); // 初始化
		/*
		 * 以下两层循环，开始时检查所有样本，选择不符合KKT条件的两个乘子进行优化，选择成功，返回1，否则，返回0
		 * 所以成功了，numChanged必然大于0，从第二遍循环时，就不从整个样本中去寻找不符合KKT条件的两个乘子进行优化，
		 * 而是从边界的乘子中去寻找，因为非边界样本需要调整的可能性更大，边界样本往往不被调整而始终停留在边界上。
		 * 如果，没找到，再从整个样本中去找，直到整个样本中再也找不到需要改变的乘子为止，此时，算法结束。
		 */
		while (numChanged > 0 || examineAll == 1) {
			numChanged = 0;
			if (examineAll == 1) {
				for (k = 0; k < train_num; k++)
					numChanged += examineExample(k); // 检查所有样本
			} else {
				for (k = 0; k < train_num; k++)
					if (alph[k] != 0 && alph[k] != C)
						numChanged += examineExample(k); // 寻找所有非边界样本的lagrange乘子
			}
			if (examineAll == 1)
				examineAll = 0;
			else if (numChanged == 0)
				examineAll = 1;
		}
		// 存放训练后的参数
//		System.out
//				.println("----------------------------参数输出------------------------------");
		System.out.println("sample number N = " + N);
//		System.out.println("train_num = " + train_num);
//		System.out.println("test_num = " + (N - train_num));
//		System.out.println("Threshold b = " + b);
//		System.out
//				.println("RBF kernel function's parameter two_sigma_squared = "
//						+ gamma);
		int support_vectors = 0;
		for (i = 0; i < train_num; i++) {
			if (alph[i] > 0 && alph[i] <= C) {
				support_vectors++;
			}
		}
		System.out.println("support_vectors = " + support_vectors);
//		System.out.println(re); // 测试
		return error_rate();

	}

	public static void main(String[] args) {
		SMO_Solver ss = new SMO_Solver();
		ss.ReadProblem();
		System.out.println(ss.mainRoutine(0.125,8.0));
	}
}
