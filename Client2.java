import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client2 extends Client implements Runnable{
	Socket socket;
	ServerSocket socketServeur;
	/* The PORT that will be used for the communications */
	public int portEnvoi;
	public int portRecept;
	
	InputStream in;
	OutputStream out;
	InputStream serverIn;
	OutputStream serverOut;
	Socket socketClient ;


	public Client2(String name, int portEntree, int portSortie) throws UnknownHostException {
		super(name);
		this.portEnvoi = portSortie;
		this.portRecept = portEntree;
	}
	
	public void connect(){
		try {	
		    socketServeur = new ServerSocket(portRecept);
		} catch(IOException e) {
		    System.err.println("Création de la socket impossible : " + e);
		    System.exit(-1);
		}
	 
		// Attente d'une connexion d'un client
		try {
		    socketClient = socketServeur.accept();
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'attente d'une connexion : " + e);
		    System.exit(-1);
		}
	 
		
		
		try {
			serverIn = socketClient.getInputStream();
			serverOut = socketClient.getOutputStream();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
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
	 
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Connection 2<--1 établie");
		System.out.println("Connection 2-->3 établie");
		
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Je demarre 2");
		connect();
		
		//Négociation avec A1
		byte[] certif = receive(serverIn);
		byte[] clePubA1;
		byte[] clePubA3;
		byte[] certifA2 = certif;
		if(this.checkCert(certif)){
			System.out.println("A2 : Certificat A1 valide");
			sendREQ("1", serverOut);
			clePubA1 = extractPubKeyCert(certif);
			sendCert(serverOut);
			if(new String(receive(serverIn)).equals("0")){
				System.out.println("A2 : mon certificat n'a pas été validé par A1");
				return;
			}else{
				System.out.println("A2 : mon certificat a été validé par A1");
			}
		}else{
			System.out.println("A2 : Certificat A1 non valide");
			return;
		}
		
		
		// Négociation avec A3
		sendCert(out);
		String s = new String(receive(in));
		if(s.equals("1")){
			System.out.println("A2 : mon certificat été validé par A3");
			certif = receive(in);
			if(this.checkCert(certif)){
				System.out.println("A2: Certificat A3 valide");
				sendREQ("1", out);
				clePubA3 = extractPubKeyCert(certif);
			}else{
				sendREQ("0", out);
				System.out.println("A2 : Certificat A3 non valide");
				return;
			}
		}else{
			System.out.println("A2 : mon certificat n'a pas été validé par A3" + s);
			return;
		}
		
		//réception de la clé 3DES en RSA
		byte [] receive= receive(in);
		receive = decryptRSA(receive, privKeyFileName);
		
		this.sendRSA(new String(receive), certifA2, serverOut);
		
		// réception demande chiffrée en 3DES
		byte [] requete =receive(serverIn);
		sendREQ(requete, out);

		receive= receive(in);
		sendREQ(receive, serverOut);
	}
	
	
}
