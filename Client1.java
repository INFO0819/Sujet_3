import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client1 extends Client implements Runnable{
	Socket socket;
	
	InputStream in;
	OutputStream out;
	
	/* The PORT that will be used for the communications */
	public int portEnvoi;

	public Client1(String name, int port) throws UnknownHostException {
		super(name);
		this.portEnvoi = port;
	}
	
	public void connect(){
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
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Connection 1->2 établie");
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		connect();
		
		// Négociation avec A2
		sendCert(out);
		String s = new String(receive(in));
		if(s.equals("1")){
			System.out.println("A1 : mon certificat été validé par A2");
			byte[] certif = receive(in);
			if(this.checkCert(certif)){
				System.out.println("A1 : Certificat A2 valide par A2");
				sendREQ("1", out);
			}else{
				sendREQ("0", out);
				System.out.println("A1 : Certificat A2 non valide par A2");
				return;
			}
		}else{
			System.out.println("A1 : mon certificat n'a pas été validé par A2" + s);
			return;
		}
		
		// Attente que A2 négocie avec A3
		byte[] desKey = receive(in);
		
		
		//System.out.println("J'ai envoyé");
		
	}

	public static void main(String[] args) throws IOException {
		Thread thread1 = new Thread(new Client1("A1", 10001));
		Thread thread2 = new Thread(new Client2("A2", 10001, 10002));
		Thread thread3 = new Thread(new Client3("A3", 10002));
		
		thread1.start();		
		thread2.start();		
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
