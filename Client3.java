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

public class Client3 extends Client implements Runnable {
	ServerSocket server;
	/* The PORT that will be used for the communications */
	public int portRecept;

	InputStream serverIn;
	OutputStream serverOut;

	public Client3(String name, int portEntree) throws UnknownHostException {
		super(name);
		this.portRecept = portEntree;
	}
	
	public void connect(){
		ServerSocket socketServeur = null;
		try {	
		    socketServeur = new ServerSocket(portRecept);
		} catch(IOException e) {
		    System.err.println("Création de la socket impossible : " + e);
		    System.exit(-1);
		}
	 
		// Attente d'une connexion d'un client
		Socket socketClient = null;
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
		
		System.out.println("Connection 3<--2 établie");
		
	}

	@Override
	public void run() {
		System.out.println("Je demarre 3");
		connect();
		
		byte[] certif = receive(serverIn);
		if(this.checkCert(certif)){
			System.out.println("A3 : Certificat A2 valide");
			sendREQ("1", serverOut);
			sendCert(serverOut);
			if(new String(receive(serverIn)).equals("0")){
				System.out.println("A3 : mon certificat n'a pas été validé par A2");
				return;
			}else{
				System.out.println("A3 : mon certificat a été validé par A2");
			}
		}else{
			System.out.println("A3 : Certificat A2 non valide");
			return;
		}
	}	
	
	
}
