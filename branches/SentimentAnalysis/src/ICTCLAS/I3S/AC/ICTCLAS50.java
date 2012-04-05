package ICTCLAS.I3S.AC;

public class ICTCLAS50 {
	public native boolean ICTCLAS_Init(byte[] sPath);
	public native boolean ICTCLAS_Exit();
	public native int ICTCLAS_ImportUserDictFile(byte[] sPath,int eCodeType);
	public native int ICTCLAS_SaveTheUsrDic();
	public native int ICTCLAS_SetPOSmap(int nPOSmap);
	public native boolean ICTCLAS_FileProcess(byte[] sSrcFilename, int eCodeType, int bPOSTagged,byte[] sDestFilename);
	public native byte[] ICTCLAS_ParagraphProcess(byte[] sSrc, int eCodeType, int bPOSTagged);
	public native byte[] nativeProcAPara(byte[] sSrc, int eCodeType, int bPOStagged);
	/* Use static intializer */
	static
	{
		String path=System.getProperty("user.dir");//获取用户当前目录
		System.load(path+"\\lib\\libICTCLAS\\ICTCLAS50.dll");
	}
}


