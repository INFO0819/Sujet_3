package tamere;


import java.io.IOException;
import java.net.ServerSocket;

public class Client3 extends Client implements Runnable{

	/* Socket used by A2 as the server socket */
	ServerSocket socketServer;

	/**
	 * Basic constructor
	 * The name given to the super constructor is "A1"
	 */
	public Client3(){
		super("A3");
	}

	@Override
	public void connect(){


		try {
			socketServer = new ServerSocket(PORT23);
		} catch(IOException e) {
			Log.err("CONNECT/A3", "Error while creating server socket.");
			System.exit(-1);
		}

		/* WAIT FOR A2 CONNECTION */
		try {
			socket = socketServer.accept();
		} catch(IOException e) {
			Log.err("CONNECT/A3", "Error while waiting for connection.");
			System.exit(-1);
		}


		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			System.out.println("Connection 3<--2 established");
		} catch (IOException e2) {
			Log.err("CONNECT/A3", "Error while instantiating flux.");
		}

	}


	@Override
	public void run() {
		connect();

		/* INITIALIZATION */
		/* Receive and check certificated from A2 */
		if(receiveCert(in,out)){

			/* FIRST STEP */
			/* Sends certificate to A2 */
			sendCert(out);

			/* SECOND STEP */
			/* Waits for A2's validation */
			if(receive(in).equals("1")) {

				/* THIRD STEP */
				/* Receive 3 DES Key from A2 */
				desKey = decryptRSA(receive(in),desKey);

				/* FOURTH STEP */
				/* Wait for A2 to forward A1's request */
				String request = decrypt3DES(receive(in),desKey);

				/* FIFTH STEP */
				/* Generate answer */
				request += " / I READ IT, SIGNED A3.";

				/* SIXTH STEP */
				/* Encrypt and send answer */
				send(encrypt3DES(request,desKey),out);


			}

		}
	}

}
