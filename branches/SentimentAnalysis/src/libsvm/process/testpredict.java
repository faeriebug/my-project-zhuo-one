package libsvm.process;

import java.io.IOException;

public class testpredict {
	public static void main(String argv[]) throws IOException
	{
		String[] arguments={"Test\\materialstoclassify.txt","Test\\classifymachine.txt.model","Test\\classifyresult.txt"};
		//String[] arguments={"Test\\a2a.txt","Test\\a1a.model","Test\\classifyresult.txt"};
		svm_predict.Init(arguments);
		System.out.print("Train process finished");
	}
}
