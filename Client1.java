import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class Client1 extends Client {

	public Client1(String name, int port) throws UnknownHostException {
		super(name, port);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		Client1 c = new Client1("A2", 10001);
		//c.sendRSA("toto", c.pubKey);
		
		c.crypt3DES("toto", "tata", "temptoto");
		
		
		c.decrypt3DES("temptoto", "tata");
		
		//c.crypt3DES("toto", "tata", "123456789123456789.txt");
		//Client1.generateKeyPair("A2");
		//c.decrypt3DES("123456789123456789.txt", "tata");
		//c.test();
		
	}	
	
	
}
