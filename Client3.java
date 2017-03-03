import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client3 extends Client implements Runnable {
	Socket socket;
	ServerSocket server;
	/* The PORT that will be used for the communications */
	public int portRecept;


	public Client3(String name, int portEntree) throws UnknownHostException {
		super(name);
		this.portRecept = portEntree;
	}
	
	public void cafe(){
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
	 
		// Association d'un flux d'entrée et de sortie
		BufferedReader inputServer = null;
		PrintWriter outputServer = null;
		try {
			inputServer = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
		    outputServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream())), true);
		} catch(IOException e) {
		    System.err.println("Association des flux impossible : " + e);
		    System.exit(-1);
		}
		
		System.out.println("Connection 3<--2 établie");
		
	}

	@Override
	public void run() {
		cafe();
		
	}	
	
	
}
