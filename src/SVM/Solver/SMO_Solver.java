package SVM.Solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * ʹ��SMO�㷨��SVM
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
		int d;// ά��
		double value;// ֵ

		public node(int d, double value) {
			this.d = d;
			this.value = value;
		}
	}

	int fold = 5;// �������Ļ��ָ���������Խ�࣬ѵ��ʱ��Խ��������Ե�׼ȷ��ҲԽ��
	int N, train_num, test_num;// ѵ��������Ŀ
	double c, tolerance, g, eps;
	double alph[];// �������ճ���
	int target[];// ѵ�������������Ŀ��ֵ
	double b;
	private double[] error_cache;// ���non-bound�������
	private double[][] self_dot_product;// Ԥ��dot_product_func(i,i)��ֵ���Լ��ټ�����
	private node[][] dense_points;// ϡ����󣬴��ѵ�������������0��train_num-1ѵ����train_num��N-1����
	private int[] train_points, test_points;// ѵ���Ͳ���ָ�룬���汣������Ϊѵ����������ź���Ϊ���Ե��������

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
	 * �˺��� i������ڵ���k
	 * 
	 * �����Ǿ��� 1 11 111 1111 11111
	 * 
	 * @param i_prim
	 * @param k_prim
	 * @return
	 */
	private double kernel_func(int i_prim, int k_prim) {
		// double sum = dot_product_func(i, k);
		if (i_prim < k_prim) {// ����i��k��ֵ
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
//	 * ����
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
	 * ����
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
	 *  ���ļ��ж�ȡ����������ѵ��
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
				// ����Ԥ����������ѵ�����������ã����ڲ�������ҲҪ���ǣ�
				self_dot_product[i][j] = dot_product_func(dense_points[i],
						dense_points[j]);
			}
		}
	}

	// ������***************************************************
	private void mainRoutine() {
		int k;
		int numChanged = 0; // number of alpha[i], alpha[j] pairs changed in a
		// single step in the outer loop
		int examineAll = 1; // flag indicating whether the outer loop has to be
		// made on all the alpha[i]

		/*
		 * ��������ѭ������ʼʱ�������������ѡ�񲻷���KKT�������������ӽ����Ż���ѡ��ɹ�������1�����򣬷���0
		 * ���Գɹ��ˣ�numChanged��Ȼ����0���ӵڶ���ѭ��ʱ���Ͳ�������������ȥѰ�Ҳ�����KKT�������������ӽ����Ż���
		 * ���Ǵӱ߽�ĳ�����ȥѰ�ң���Ϊ�Ǳ߽�������Ҫ�����Ŀ����Ը��󣬱߽�������������������ʼ��ͣ���ڱ߽��ϡ�
		 * �����û�ҵ����ٴ�����������ȥ�ң�ֱ��������������Ҳ�Ҳ�����Ҫ�ı�ĳ���Ϊֹ����ʱ���㷨������
		 */
		while (numChanged > 0 || examineAll == 1) {
			numChanged = 0;
			if (examineAll == 1) {
				for (k = 0; k < train_num; k++)
					numChanged += examineExample(k); // �����������
			} else {
				for (k = 0; k < train_num; k++)
					if (alph[k] != 0 && alph[k] != c)
						numChanged += examineExample(k); // Ѱ�����зǱ߽�������lagrange����
			}
			if (examineAll == 1)
				examineAll = 0;
			else if (numChanged == 0)
				examineAll = 1;
		}
	}

	/**
	 * ʹ��c��g��������ѵ��
	 * @param c
	 * @param g
	 */
	public void Train(double c, double g) {
		this.c = c;
		this.g = g; // ��ʼ��
		b = 0;
		train_num = N;
		alph = new double[train_num];
		error_cache = new double[train_num];
		train_points = new int[train_num];
		for (int j = 0; j < train_points.length; j++) {
			train_points[j] = j;
		}
		mainRoutine();
		// ���ѵ����Ĳ���
		// System.out
		// .println("----------------------------�������------------------------------");
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
		// System.out.println(re); // ����
	}
	/**
	 * ���ԣ�������׼ȷ��
	 * @param filepath
	 * @return
	 */
	public double Predict(String filepath) {
		svmProblem sp = ReadProblem(filepath);
		int ac = 0;
		double tar;
		// System.out
		// .println("----------------------------���Խ��------------------------------");
		for (int i = 0; i < sp.target.length; i++) {
			tar = learned_func_nonlinear(sp.dense_points[i]);
			if ((tar > 0 && sp.target[i] > 0) || (tar < 0 && sp.target[i] < 0))
				ac++;
		}
		// System.out.println("��ȷ�ȣ�" + accuracy * 100 + "��");
		return (double) ac / target.length;
	}

	double test() {
		return Predict("heart_scale");
	}

	// ���������
	private double Predict() {
		int ac = 0;
		double tar;
		// System.out
		// .println("----------------------------���Խ��------------------------------");
		for (int i = 0; i < test_num; i++) {
			tar = learned_func_nonlinear(test_points[i]);
			if ((tar > 0 && target[test_points[i]] > 0)
					|| (tar < 0 && target[test_points[i]] < 0))
				ac++;
		}
		// System.out.println("��ȷ�ȣ�" + accuracy * 100 + "��");
		return (double) ac / test_num;
	}

	/**
	 * ʹ��CrossValidationѵ����֤��fold=5
	 * @param c
	 * @param g
	 * @return
	 */
	public double TrainWithCrossValidation(double c, double g) {
		this.c = c;
		this.g = g; // ��ʼ��
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
				if (test_num * i <= j && j < test_num * (i + 1)) {// ���ڲ��Լ�
					test_points[te++] = j;
				} else {// ����ѵ����
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
	 * �����Ż�
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
		// ��ȡ����������
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
		// ���㡡�ȣ���
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
		// ����eta��2K12-K11-K22
		k11 = kernel_func(train_points[i1_index], train_points[i1_index]);
		k12 = kernel_func(train_points[i1_index], train_points[i2_index]);
		k22 = kernel_func(train_points[i2_index], train_points[i2_index]);
		eta = 2 * k12 - k11 - k22;
		/********************************************************/
		// ��ets��0�ͣ����������µ�alph��
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
		// �����µ�alph��
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
		// ���£�
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
		// ����E
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
		// �洢�µģ���裱�ͣ���裲
		alph[i1_index] = a1; /* Store a1 in the alpha array. */
		alph[i2_index] = a2; /* Store a2 in the alpha array. */
		return true;
	}

	// ---------------------------------------------------------------------------------------------------
	/*
	 * ��������ѭ������ʼʱ�������������ѡ�񲻷���KKT�������������ӽ����Ż���ѡ��ɹ�������1�����򣬷���0
	 * ���Գɹ��ˣ�numChanged��Ȼ>0���ӵڶ���ѭ��ʱ���Ͳ�������������ȥѰ�Ҳ�����KKT�������������ӽ����Ż���
	 * ���Ǵӱ߽�ĳ�����ȥѰ�ң���Ϊ�Ǳ߽�������Ҫ�����Ŀ����Ը��󣬱߽�������������������ʼ��ͣ���ڱ߽��ϡ�
	 * �����û�ҵ����ٴ�����������ȥ�ң�ֱ��������������Ҳ�Ҳ�����Ҫ�ı�ĳ���Ϊֹ����ʱ���㷨������
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
			// ///////////ʹ�����ַ���ѡ��ڶ�������
			// 1����non-bound������Ѱ��maximum fabs(E1-E2)������
			// 2���������ûȡ�ý�չ,��ô�����λ�ò���non-boundary ����
			// 3���������Ҳʧ�ܣ�������λ�ò�����������,��Ϊbound����
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
		// /û�н�չ
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

	// 2���������ûȡ�ý�չ,��ô�����λ�ò���non-boundary ����
	private boolean examineNonBound(int i1_index) {
		int k, k0 = (new Random()).nextInt(train_num);
		int i2;
		for (k = 0; k < train_num; k++) {
			i2 = (k + k0) % train_num; // �����λ��ʼ
			if (alph[i2] > 0.0 && alph[i2] < c) {
				if (takeStep(i1_index, i2)) {
					return true;
				}
			}
		}
		return false;
	}

	// 3���������Ҳʧ�ܣ�������λ�ò�����������,(��Ϊbound����)
	private boolean examineBound(int i1_index) {
		int k, k0 = (new Random()).nextInt(train_num);
		int i2;
		for (k = 0; k < train_num; k++) {
			i2 = (k + k0) % train_num; // �����λ��ʼ
			if (takeStep(i1_index, i2)) {
				return true;
			}
		}
		return false;
	}
}
