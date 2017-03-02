import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client1 extends Client {

	public Client1(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		Client1 c = new Client1("A1");
		c.crypt3DES("toto", "tata", "123456789123456789.txt");
		c.decrypt3DES("123456789123456789.txt", "tata");
		
		MySSL.CleRSAPubin();
		//c.test();
		
		Process p_cmd;
		String strcmd ="echo \"toto\" > toto.txt ";
		Runtime runtime = Runtime.getRuntime();
		
		p_cmd = runtime.exec(strcmd);
		BufferedReader std = new BufferedReader(new 
			     InputStreamReader(p_cmd.getInputStream()));
		
		
		System.out.println(std.readLine());
		
	}
	
	
	
}
