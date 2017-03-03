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
	private boolean reqOK;

	public Client1(String name, int port, boolean bool) throws UnknownHostException {
		super(name);
		this.portEnvoi = port;
		this.reqOK = bool;
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
		byte[] certif;
		byte[] clePubA2;
		
		if(s.equals("1")){
			System.out.println("A1 : mon certificat été validé par A2");
			certif = receive(in);
			if(this.checkCert(certif)){
				System.out.println("A1 : Certificat A2 valide par A2");
				sendREQ("1", out);
				clePubA2 = extractPubKeyCert(certif);
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
		byte [] receive= receive(in);
		byte [] cle3DES = decryptRSA(receive, privKeyFileName);
		
		System.out.println("A1 : Clé 3DES recue " + new String(cle3DES));
		
		byte [] msgChiffre;
		String requete;
		if(this.reqOK)
			requete = "acheter";
		else
			requete = "refuser";
		 
		
		msgChiffre= crypt3DES(requete, cle3DES);
		sendREQ(msgChiffre, out);
		
		System.out.println("A1 : envoi de la requête \"" + requete + "\"");
		
		byte [] reponse = receive(in);
		if(decrypt3DES(reponse, cle3DES).equals("OK"))
			System.out.println("Le serveur a accepte la requete");
		else
			System.out.println("Le serveur a refuse la requete");
		
		//System.out.println("J'ai envoyé");
		
	}

	public static void main(String[] args) throws IOException {
		Process p_cmd;
		String strcmd = "./scriptCA.sh";
		Runtime runtime = Runtime.getRuntime();
		p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
		try {
			p_cmd.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Thread thread1 = new Thread(new Client1("A1", 10001, false));
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
		
	}	
	
	
}
