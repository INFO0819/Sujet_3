import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client2 extends Client implements Runnable{
	Socket socket;
	ServerSocket server;
	/* The PORT that will be used for the communications */
	public int portEnvoi;
	public int portRecept;


	public Client2(String name, int portEntree, int portSortie) throws UnknownHostException {
		super(name);
		this.portEnvoi = portSortie;
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
		
		System.out.println("Connection 2<--1 établie");
		System.out.println("Connection 2-->3 établie");
		
	}

	@Override
	public void run() {
		cafe();
		
	}
	
	
}
