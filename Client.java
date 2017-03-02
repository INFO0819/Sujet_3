import java.util.Scanner; 	/* For file reading */


/**
 * Class designed to handle the client connections and actions
 * The class is abstract and should not be instantiated
 * @author Chaest
*/

public abstract class Client{
	
	/* The certificate to use */
	public String certifCA;
	
	/* The public key of the client */
	public String pubKey;
	
	/* The private key of the client */
	public String privKey; 
	
	/* The 3DES key of the client */
	public String desKey;
	
	/* The name of the client (A1, A2, A3) */
	public String name;
	
	/* The server the client will use */
	public InetAddress server;
	
	/* The port that will be used for the communications */
	public final static int port = 9632;
	
	/* The length of the received data */
	public final static int lenR = 9000;
	
	/* The time before timeout */
	public final static int timeout = 30000;
	
	
	/**
	 * Client main constructor
	 * Loads the client keys and the certificate of the CA thanks to its name
	 * Therefor the only names you should use are A1, A2, A3
	 * Will show a warning if another name is given
	 * @param name the name of the client 
	*/
	public Client(String name){
		this.name = name;
		
		if (name.compareTo("A1") != 0  && name.compareTo("A2") != 0 && name.compareTo("A3") != 0 ){ /* compare the name to "A1", "A2" and "A3" */
			System.out.println("The name you gave to the Client isn't a predefined name, you should reconsider using it for it may cause bugs.");
		}
		
		this.server =  = InetAddress.getByName("localhost");
		
		try{	
			
			certifCa = new String(Files.readAllBytes(Paths.get("certifCA.crt")), StandartCharsets.UTF_8);
			pubKey = new String(Files.readAllBytes(Paths.get(name + ".pub")), StandartCharsets.UTF_8);
			privKey = new String(Files.readAllBytes(Paths.get(name + ".priv")), StandartCharsets.UTF_8);
			
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Function used to send the Certificate
	 * @return a reference to this object
	*/
	public Client sendCert(){
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket sentData = new DatagramPacket(certifCA.getBytes(), certifCA.length(), server, port);
		DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);
		socket.setTimeout(timeout);
		
		socket.send(sentData);
		socket.receive(recvData);
		      
		return this;
	}
	
	/**
	 * Function used to receive the certificate
	 * @return a reference to this object
	*/
	public boolean receiveCert(){
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);
		
		socket.setTimeout(timeout);
		
		socket.receive(recvData);
		
		return checkCertif(new String(recvData.getData()));
	}
	
	/**
	 * Function used to send a message encrypted using the private key
	 * @return a reference to this object
	*/
	public Client sendRSA(String message){
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket sentData = new DatagramPacket(message.getBytes(), message.length(), server, port);
		DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);
		
		socket.setTimeout(timeout);
		
		socket.send(sentData);
		socket.receive(recvData);
		
		return this;
	}
	
	/**
	 * Function used to receive a message that has been encrypted using a private key and to decrypt it
	 * @return a reference to this object
	*/
	public Client receiveRSA(){
		DatagramSocket socket = new DatagramSocket();
		
		DatagramPacket sentData = new DatagramPacket(/*ANSWER*/.getBytes(), /*ANSWER*/.length(), server, port);
		DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);
		
		socket.setTimeout(timeout);
		
		socket.receive(recvData);
		socket.send(sentData);
		
		return this;
	}
	
	/**
	 * Function used to check the Certificate
	 * @return a reference to this object
	*/
	public boolean checkCert(){
		boolean toR; 
		
		return toR;
	}
	
	/**
	 * Function used to send a message that has been encrypted using the symetric key
	 * @return a reference to this object
	*/
	public abstract Client sendAES(String message);
	
	/**
	 * Function used to receive a message that has been encrypted using the symetric key
	 * @return a reference to this object
	*/
	public abstract Client receiveAES();

	
	
}
