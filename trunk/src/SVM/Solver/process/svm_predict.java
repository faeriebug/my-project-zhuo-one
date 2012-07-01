package SVM.Solver.process;

import java.io.*;
import java.util.*;

import SVM.Solver.svm;
import SVM.Solver.svm_model;
import SVM.Solver.svm_node;

public class svm_predict {
	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	private static int[] predict(BufferedReader input, DataOutputStream output,
			svm_model model) throws IOException {
		int correct = 0;
		int total = 0;
		svm.svm_get_svm_type(model);

		while (true) {
			String line = input.readLine();
			if (line == null)
				break;
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
			double target = atof(st.nextToken());
			int m = st.countTokens() / 2;
			svm_node[] x = new svm_node[m];
			for (int j = 0; j < m; j++) {
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}
			double v;
			double[] tmpp = svm.svm_predict(model, x);
			v = tmpp[0];
			output.writeBytes(v + "\n");

			if (v == target) {
				++correct;
				// System.out.println(v);
			}
			++total;
		}
		// System.out.print("Accuracy = "+(double)correct/total*100+"% ("+correct+"/"+total+") (classification)\n");
		return new int[] { correct, total };
	}

	private static ArrayList<Integer> Feedback(BufferedReader input,
			DataOutputStream output, svm_model model, double threshold)
			throws IOException {
		ArrayList<Integer> noise = new ArrayList<Integer>();
		int index = 0;
		svm.svm_get_svm_type(model);

		while (true) {
			String line = input.readLine();
			if (line == null)
				break;
			index++;
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
			double target = atof(st.nextToken());
			int m = st.countTokens() / 2;
			svm_node[] x = new svm_node[m];
			for (int j = 0; j < m; j++) {
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}
			double v, dis;
			double[] tmpp = svm.svm_predict(model, x);
			v = tmpp[0];
			dis = tmpp[1];
			output.writeBytes(v + "," + dis + "\n");

			if (v != target) {
				if (Math.abs(dis) > threshold) {
					noise.add(index);
				}
			}
		}
		// System.out.print("Accuracy = "+(double)correct/total*100+"% ("+correct+"/"+total+") (classification)\n");
		if (noise.size() > 0)
			return noise;
		return null;
	}

	public static String Feedback(String argv[]) throws IOException {
		int i = 0;
		try {
			BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(argv[i + 2])));
			svm_model model = svm.svm_load_model(argv[i + 1]);
			ArrayList<Integer> noise = Feedback(input, output, model, 1.0);
			input.close();
			output.close();
			if (noise != null) {
				try{
					BufferedReader input2 = new BufferedReader(new FileReader(argv[i]));
					DataOutputStream output2 = new DataOutputStream(
							new BufferedOutputStream(new FileOutputStream(argv[i] + "_feedback")));
					int index=0;
					while(true){
						String line = input2.readLine();
						if (line == null)
							break;
						index++;
						if(noise.indexOf(index)!=-1){
							output2.writeBytes(line + "\n");
						}
					}
					input2.close();
					output2.close();
					return argv[i] + "_feedback";
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int[] Init(String argv[]) throws IOException {
		int i = 0;
		try {
			BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(argv[i + 2])));
			svm_model model = svm.svm_load_model(argv[i + 1]);
			int[] re = predict(input, output, model);
			input.close();
			output.close();
			return re;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
