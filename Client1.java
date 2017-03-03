import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client1 extends Client implements Runnable{
	Socket socket;
	/* The PORT that will be used for the communications */
	public int portEnvoi;

	public Client1(String name, int port) throws UnknownHostException {
		super(name);
		this.portEnvoi = port;
	}
	
	public void connect(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
		    socket = new Socket("localhost", this.portEnvoi);
		} catch(UnknownHostException e) {
		    System.err.println("Erreur sur l'hôte : " + e);
		    System.exit(-1);
		} catch(IOException e) {
		    System.err.println("Création de la socket impossible " + name + ": " + e);
		    System.exit(-1);
		}
	 
		// Association d'un flux d'entrée et de sortie
		BufferedReader input = null;
		PrintWriter output = null;
		try {
		    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch(IOException e) {
		    System.err.println("Association des flux impossible : " + e);
		    System.exit(-1);
		}
		
		System.out.println("Connection 1->2 établie");
	}

	@Override
	public void run() {
		connect();
		
	}

	public static void main(String[] args) throws IOException {
		Thread thread1 = new Thread(new Client1("A1", 10001));
		Thread thread2 = new Thread(new Client2("A2", 10001, 10002));
		Thread thread3 = new Thread(new Client3("A3", 10002));
		
		thread1.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		thread2.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		thread3.start();
		
		try {
			thread1.join();
			thread2.join();
			thread3.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//c.crypt3DES("toto", "tata", "123456789123456789.txt");
		//Client1.generateKeyPair("A2");
		//c.decrypt3DES("123456789123456789.txt", "tata");
		//c.test();
		
	}	
	
	
}
