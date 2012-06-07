package SVM.Solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * 使用SMO算法解SVM
 * 
 * @author WuyaMony
 * 
 */
public class SMO_Solver {
	private class svmProblem {
		int[] target;
		node[][] dense_points;
		// public svmProblem(Integer[] target, node[][] dense_points) {
		// this.target = target;
		// this.dense_points = dense_points;
		// }
	}

	private class node {
		int d;// 维数
		double value;// 值

		public node(int d, double value) {
			this.d = d;
			this.value = value;
		}
	}

	int fold = 5;// 交叉检验的划分个数，次数越多，训练时间越长，但相对的准确率也越高
	int N, train_num, test_num;// 训练数据数目
	double c, tolerance, g, eps;
	double alph[];// 拉格朗日乘子
	int target[];// 训练与测试样本的目标值
	double b;
	private double[] error_cache;// 存放non-bound样本误差
	private double[][] self_dot_product;// 预存dot_product_func(i,i)的值，以减少计算量
	private node[][] dense_points;// 稀疏矩阵，存放训练与测试样本，0～train_num-1训练；train_num～N-1测试
	private int[] train_points, test_points;// 训练和测试指针，里面保存了作为训练的样本编号和作为测试的样本编号

	public SMO_Solver() {
		tolerance = 0.001;
		eps = 1e-12;
	}

	private double learned_func_nonlinear(int k_prim) {
		double sum = 0;
		for (int i = 0; i < train_num; i++) {
			try {
				if (alph[i] > 0)
					sum += alph[i] * target[train_points[i]]
							* kernel_func(train_points[i], k_prim);
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		sum -= b;
		return sum;
	}

	private double learned_func_nonlinear(node[] k_prim) {
		double sum = 0;
		for (int i = 0; i < train_num; i++) {
			try {
				if (alph[i] > 0)
					sum += alph[i] * target[train_points[i]]
							* kernel_func(train_points[i], k_prim);
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		sum -= b;
		return sum;
	}

	/**
	 * 核函数 i必须大于等于k
	 * 
	 * 下三角矩阵 1 11 111 1111 11111
	 * 
	 * @param i_prim
	 * @param k_prim
	 * @return
	 */
	private double kernel_func(int i_prim, int k_prim) {
		// double sum = dot_product_func(i, k);
		if (i_prim < k_prim) {// 交换i，k的值
			i_prim ^= k_prim;
			k_prim ^= i_prim;
			i_prim ^= k_prim;
		}
		double sum = self_dot_product[i_prim][k_prim];
		sum *= -2;
		sum += self_dot_product[i_prim][i_prim]
				+ self_dot_product[k_prim][k_prim];
		return Math.exp(-sum / g);
	}

	private double kernel_func(int i_prim, node[] k_prim) {
		double sum = dot_product_func(dense_points[i_prim], k_prim);
		sum *= -2;
		sum += self_dot_product[i_prim][i_prim]
				+ dot_product_func(k_prim, k_prim);
		return Math.exp(-sum / g);
	}

//	/**
//	 * 求点积
//	 * 
//	 * @param i_prim
//	 * @param k_prim
//	 * @return
//	 */
//	private double dot_product_func(int i_prim, int k_prim) {
//		double dot = 0;
//		int di = 0, dk = 0;
//		int leni = dense_points[i_prim].length, lenk = dense_points[k_prim].length;
//		while (di < leni && dk < lenk) {
//			if (dense_points[i_prim][di].d == dense_points[k_prim][dk].d) {
//				dot += dense_points[i_prim][di].value
//						* dense_points[k_prim][dk].value;
//				di++;
//				dk++;
//			} else if (dense_points[i_prim][di].d < dense_points[k_prim][dk].d) {
//				di++;
//			} else {
//				dk++;
//			}
//		}
//		return dot;
//	}

	/**
	 * 求点积
	 * 
	 * @param i_prim
	 * @param k_prim
	 * @return
	 */
	private double dot_product_func(node[] i_prim, node[] k_prim) {
		double dot = 0;
		int di = 0, dk = 0;
		int leni = i_prim.length, lenk = k_prim.length;
		while (di < leni && dk < lenk) {
			if (i_prim[di].d == k_prim[dk].d) {
				dot += i_prim[di].value * k_prim[dk].value;
				di++;
				dk++;
			} else if (i_prim[di].d < k_prim[dk].d) {
				di++;
			} else {
				dk++;
			}
		}
		return dot;
	}

	private svmProblem ReadProblem(String filepath) {
		svmProblem sp = null;
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
			sp = new svmProblem();
			String line;
			// ArrayList<Integer> list_target = new ArrayList<>();
			// ArrayList<node[]> list_dense = new ArrayList<>();
			line = br.readLine();
			int size = Integer.parseInt(line);
			sp.target = new int[size];
			sp.dense_points = new node[size][];
			int i = 0;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
				// target[i] = Integer.valueOf(st.nextToken());
				sp.target[i] = Integer.valueOf(st.nextToken());
				// list_target.add(Integer.valueOf(st.nextToken()));
				int m = st.countTokens() / 2;
				// dense_points[i]=new node[m];
				sp.dense_points[i] = new node[m];
				for (int j = 0; j < m; j++) {
					int dim = Integer.valueOf(st.nextToken());
					// dense_points[i][j] = new
					// node(dim,Double.valueOf(st.nextToken()));
					sp.dense_points[i][j] = new node(dim, Double.valueOf(st
							.nextToken()));
				}
				// list_dense.add(d);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sp;
	}

	/**
	 *  从文件中读取样本，用来训练
	 * @param filepath
	 */
	public void ReadTrainProblemForTrainning(String filepath) {
		svmProblem sp = ReadProblem(filepath);
		N = sp.target.length;
		target = sp.target;
		dense_points = sp.dense_points;
		self_dot_product = new double[N][];
		for (int i = 0; i < N; i++) {
			self_dot_product[i] = new double[i + 1];
			for (int j = 0; j <= i; j++) {
				// 设置预计算点积（对训练样本的设置，对于测试样本也要考虑）
				self_dot_product[i][j] = dot_product_func(dense_points[i],
						dense_points[j]);
			}
		}
	}

	// 主函数***************************************************
	private void mainRoutine() {
		int k;
		int numChanged = 0; // number of alpha[i], alpha[j] pairs changed in a
		// single step in the outer loop
		int examineAll = 1; // flag indicating whether the outer loop has to be
		// made on all the alpha[i]

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
					if (alph[k] != 0 && alph[k] != c)
						numChanged += examineExample(k); // 寻找所有非边界样本的lagrange乘子
			}
			if (examineAll == 1)
				examineAll = 0;
			else if (numChanged == 0)
				examineAll = 1;
		}
	}

	/**
	 * 使用c，g进行最终训练
	 * @param c
	 * @param g
	 */
	public void Train(double c, double g) {
		this.c = c;
		this.g = g; // 初始化
		b = 0;
		train_num = N;
		alph = new double[train_num];
		error_cache = new double[train_num];
		train_points = new int[train_num];
		for (int j = 0; j < train_points.length; j++) {
			train_points[j] = j;
		}
		mainRoutine();
		// 存放训练后的参数
		// System.out
		// .println("----------------------------参数输出------------------------------");
		// System.out.println("sample number N = " + N);
		// System.out.println("train_num = " + train_num);
		// System.out.println("test_num = " + (N - train_num));
		// System.out.println("Threshold b = " + b);
		// System.out
		// .println("RBF kernel function's parameter two_sigma_squared = "
		// + gamma);
		// int support_vectors = 0;
		// for (i = 0; i < train_num; i++) {
		// if (alph[i] > 0 && alph[i] <= C) {
		// support_vectors++;
		// }
		// }
		// System.out.println("support_vectors = " + support_vectors);
		// System.out.println(re); // 测试
	}
	/**
	 * 测试，并返回准确率
	 * @param filepath
	 * @return
	 */
	public double Predict(String filepath) {
		svmProblem sp = ReadProblem(filepath);
		int ac = 0;
		double tar;
		// System.out
		// .println("----------------------------测试结果------------------------------");
		for (int i = 0; i < sp.target.length; i++) {
			tar = learned_func_nonlinear(sp.dense_points[i]);
			if ((tar > 0 && sp.target[i] > 0) || (tar < 0 && sp.target[i] < 0))
				ac++;
		}
		// System.out.println("精确度：" + accuracy * 100 + "％");
		return (double) ac / target.length;
	}

	double test() {
		return Predict("heart_scale");
	}

	// 计算误差率
	private double Predict() {
		int ac = 0;
		double tar;
		// System.out
		// .println("----------------------------测试结果------------------------------");
		for (int i = 0; i < test_num; i++) {
			tar = learned_func_nonlinear(test_points[i]);
			if ((tar > 0 && target[test_points[i]] > 0)
					|| (tar < 0 && target[test_points[i]] < 0))
				ac++;
		}
		// System.out.println("精确度：" + accuracy * 100 + "％");
		return (double) ac / test_num;
	}

	/**
	 * 使用CrossValidation训练验证，fold=5
	 * @param c
	 * @param g
	 * @return
	 */
	public double TrainWithCrossValidation(double c, double g) {
		this.c = c;
		this.g = g; // 初始化
		test_num = N / fold;
		train_num = N - test_num;
		alph = new double[train_num];
		error_cache = new double[train_num];
		train_points = new int[train_num];
		test_points = new int[test_num];

		double re = 0;
		int tr, te;
		for (int i = 0; i < fold; i++) {
			tr = te = 0;
			for (int j = 0; j < N; j++) {
				if (test_num * i <= j && j < test_num * (i + 1)) {// 属于测试集
					test_points[te++] = j;
				} else {// 属于训练集
					train_points[tr++] = j;
				}
			}
			for (int j = 0; j < train_num; j++) {
				alph[j] = 0;
				error_cache[j] = 0;
			}
			mainRoutine();
			re += Predict();
		}
		return re / fold;
	}

	public static void main(String[] args) {
		SMO_Solver ss = new SMO_Solver();
		ss.ReadTrainProblemForTrainning("heart_scale");
		long start = System.currentTimeMillis();
//		 System.out.println(ss.TrainWithCrossValidation(0.125, 8.0));
		 ss.Train(0.125, 8.0);
		 System.out.println(ss.test());
		System.out.println(System.currentTimeMillis() - start);
	}

	/**
	 * 迭代优化
	 * 
	 * @param i1_index
	 * @param i2_index
	 * @return
	 */
	private boolean takeStep(int i1_index, int i2_index) {
		int y1, y2, s;
		double delta_b;
		double alph1, alph2 = 0; // * old_values of alpha_1, alpha_2
		double a1, a2; // * new values of alpha_1, alpha_2
		double E1, E2, L, H, k11, k22, k12, eta, Lobj, Hobj;

		if (i1_index == i2_index)
			return false;
		/******************************************************/
		// 读取参量ｙ，Ｅ
		alph1 = alph[i1_index];
		y1 = target[train_points[i1_index]];
		if (alph1 > 0 && alph1 < c) {
			E1 = error_cache[i1_index];
		} else {
			E1 = learned_func_nonlinear(train_points[i1_index]) - y1;
		}
		try {
			alph2 = alph[i2_index];
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		y2 = target[train_points[i2_index]];
		if (alph2 > 0 && alph2 < c) {
			E2 = error_cache[i2_index];
		} else {
			E2 = learned_func_nonlinear(train_points[i2_index]) - y2;
		}
		s = y1 * y2;
		/*******************************************************/
		// 计算　Ｈ，Ｌ
		if (y1 == y2) {
			double gamma = alph1 + alph2;
			if (gamma > c) {
				L = gamma - c;
				H = c;
			} else {
				L = 0;
				H = gamma;
			}
		} else {
			double gamma = alph1 - alph2;
			if (gamma > 0) {
				L = 0;
				H = c - gamma;
			} else {
				L = -gamma;
				H = c;
			}
		}
		if (L == H)
			return false;
		/********************************************************/
		// 计算eta＝2K12-K11-K22
		k11 = kernel_func(train_points[i1_index], train_points[i1_index]);
		k12 = kernel_func(train_points[i1_index], train_points[i2_index]);
		k22 = kernel_func(train_points[i2_index], train_points[i2_index]);
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
		} else if (a1 > c) {
			a2 += s * (a1 - c);
			a1 = c;
		}

		/**********************************************************/
		// 更新ｂ
		double b1, b2, bnew;
		if (a1 > 0 && a1 < c) {
			bnew = b + E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
		} else {
			if (a2 > 0 && a2 < c)
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

		/************************************************************/
		double t1 = y1 * (a1 - alph1);
		double t2 = y2 * (a2 - alph2);
		// 更新E
		for (int i = 0; i < train_num; i++) {
			if (0 < alph[i] && alph[i] < c) {
				error_cache[i] += t1
						* kernel_func(train_points[i1_index], train_points[i])
						+ t2
						* kernel_func(train_points[i2_index], train_points[i])
						- delta_b;
			}
		}
		error_cache[i1_index] = 0.;
		error_cache[i2_index] = 0.;
		/***********************************************************/
		// 存储新的ａｌｐｈ１和ａｌｐｈ２
		alph[i1_index] = a1; /* Store a1 in the alpha array. */
		alph[i2_index] = a2; /* Store a2 in the alpha array. */
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
	private int examineExample(int k_index) {
		int y1;
		double alph1, E1, r1;
		y1 = target[train_points[k_index]];
		alph1 = alph[k_index];
		if (alph1 > 0 && alph1 < c)
			E1 = error_cache[k_index];
		else
			E1 = learned_func_nonlinear(train_points[k_index]) - y1;
		r1 = y1 * E1;
		if ((r1 < -tolerance && alph1 < c) || (r1 > tolerance && alph1 > 0)) {
			// ///////////使用三种方法选择第二个乘子
			// 1：在non-bound乘子中寻找maximum fabs(E1-E2)的样本
			// 2：如果上面没取得进展,那么从随机位置查找non-boundary 样本
			// 3：如果上面也失败，则从随机位置查找整个样本,改为bound样本
			if (examineFirstChoice(k_index, E1)) // 1
			{
				return 1;
			}
			if (examineNonBound(k_index)) // 2
			{
				return 1;
			}
			if (examineBound(k_index)) // 3
			{
				return 1;
			}
		}
		// /没有进展
		return 0;
	}

	private boolean examineFirstChoice(int i1_index, double E1) {

		int k, i2;
		double tmax;
		double E2, temp;
		for (i2 = -1, tmax = 0.0, k = 0; k < train_num; k++) {
			if (alph[k] > 0 && alph[k] < c) {
				E2 = error_cache[k];
				temp = Math.abs(E1 - E2);
				if (temp > tmax) {
					tmax = temp;
					i2 = k;
				}
			}
		}
		if (i2 >= 0) {
			if (takeStep(i1_index, i2))
				return true;
		}
		return false;
	}

	// 2：如果上面没取得进展,那么从随机位置查找non-boundary 样本
	private boolean examineNonBound(int i1_index) {
		int k, k0 = (new Random()).nextInt(train_num);
		int i2;
		for (k = 0; k < train_num; k++) {
			i2 = (k + k0) % train_num; // 从随机位开始
			if (alph[i2] > 0.0 && alph[i2] < c) {
				if (takeStep(i1_index, i2)) {
					return true;
				}
			}
		}
		return false;
	}

	// 3：如果上面也失败，则从随机位置查找整个样本,(改为bound样本)
	private boolean examineBound(int i1_index) {
		int k, k0 = (new Random()).nextInt(train_num);
		int i2;
		for (k = 0; k < train_num; k++) {
			i2 = (k + k0) % train_num; // 从随机位开始
			if (takeStep(i1_index, i2)) {
				return true;
			}
		}
		return false;
	}
}
