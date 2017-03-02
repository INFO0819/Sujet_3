import java.io.IOException;
import java.lang.management.ManagementFactory;

public class MySSL {
	
	public static void RsaCle3DES(){
		
		try {
			Process p_cmd;
			String strcmd ="openssl rsa -in mykeyRSA1024.pem -des3 -out mykey3DES.pem";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(strcmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void creationCleRSA(){
		try {
			Process p_cmd;
			String strcmd ="openssl genrsa -out mykeyRSA1024.pem 1024";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(strcmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void CleRSAPubin(){
		try {
			Process p_cmd;
			String strcmd ="openssl rsa -in mykeyRSA1024.pem -pubout -out mykeyPUBRSA.pem";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(strcmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void CreationCA(){
		
	}
}
