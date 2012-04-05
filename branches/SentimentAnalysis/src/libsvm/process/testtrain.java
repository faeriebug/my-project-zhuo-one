package libsvm.process;
import java.io.IOException;


public class testtrain {

	public static void main(String argv[]) throws IOException
	{
		svm_train stran= new svm_train();
		String[] argu={"Test\\classifymachine.txt"};
		stran.run(argu);
		System.out.print("Train process finished");
	}

}
