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
	/**
	 * Basic constructor
	 * @param name given to the super constructor"
	 * @param portEntree port TCP" 
	 */
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
		/* INITIALIZATION */
		/* Receive and check certificated from A3 */
		System.out.println("Je demarre 3");
		connect();
		
		byte[] certif = receive(serverIn);
		byte[] clePubA2;
		if(this.checkCert(certif)){
			System.out.println("A3 : Certificat A2 valide");
			sendREQ("1", serverOut);
			/* FIRST STEP */
			/* Sends certificate to A2 */
			sendCert(serverOut);
			clePubA2 = extractPubKeyCert(certif);
			/* SECOND STEP */
			/* Waits for A2's validation */
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
		
		//envoi clé 3des en RSA
		String cle3DES = generateString(8);
		System.out.println("A3 : clé 3DES générée : " + cle3DES);
		this.sendRSA(cle3DES, certif, serverOut);
		

		// réception demande chiffrée en 3DES
		byte [] requete =receive(serverIn);
		if(decrypt3DES(requete, cle3DES.getBytes()).equals("acheter")){			
			sendREQ(crypt3DES("OK", cle3DES.getBytes()), serverOut);
		}else{
			sendREQ(crypt3DES("KO", cle3DES.getBytes()), serverOut);
		
		}
		
	}	
	
	
}
