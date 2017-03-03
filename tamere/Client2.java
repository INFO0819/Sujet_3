package tamere;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Client2 extends Client implements Runnable{

	/* Socket used by A2 as the server socket */
	private ServerSocket socketServer;

	/* Socket used by A2 to communicate with A3 */
	/* Socket from the Client class will be used A1-A2 communication */
	private Socket socket23;

	/* Corresponding input output */
	private InputStream in23;
	private OutputStream out23;

	/* Keys used for A2-A3 communications */
	private byte[] desKey23;
	private byte[] recipientPubKey23;

	/**
	 * Basic constructor
	 * The name given to the super constructor is "A1"
	 */
	public Client2(){
		super("A2");
	}

	@Override
	public void connect(){
		try {
			socketServer = new ServerSocket(PORT12);
		} catch(IOException e) {
			Log.err("CONNECT/A2", "Error while creating server socket.");
			System.exit(-1);
		}

		/* WAIT FOR A1 CONNECTION */
		try {
			socket = socketServer.accept();
			System.out.println("Connection 2<--1 established");
		} catch(IOException e) {
			Log.err("CONNECT/A2", "Error while waiting for connection.");
			System.exit(-1);
		}


		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e2) {
			Log.err("CONNECT/A2", "Error while instantiating flux.");
		}

		try {
			socket23 = new Socket("localhost", PORT23);
		} catch(IOException e) {
			Log.err("CONNECT/A2", "Error while creating 23 socket.");
			System.exit(-1);
		}

		try {
			in = socket23.getInputStream();
			out = socket23.getOutputStream();
			System.out.println("Connection 2-->3 established");
		} catch (IOException e) {
			Log.err("CONNECT/A2", "Error while instantiating flux.");
		}

	}

	@Override
	public void run(){
		connect();

        Log.logln("test");

		/* INITIALIZATION */
		/* Receive and check certificated from A1 */
		if(receiveCert(in,out)) {

			/* FIRST STEP */
			/* Sends certificate to A1 */
			sendCert(out);

			/* SECOND STEP */
			/* Waits for A1's validation */
			if(receive(in).equals("1")) {

				/* THIRD STEP */
				/* Sends certificate to A3 */
				sendCert(out23);

				/* FOURTH STEP */
				/* Waits for A3's validation */
				if(receive(in23).equals("1")) {

					/* FIFTH STEP */
					/* Receive and valide A3 certificate */
					if(receiveCert23(in23,out23)) {

						/* SIXTH STEP */
						/* Tell A1 it's ready */
						send("ready".getBytes(), out);

						/* SEVENTH STEP */
						/* Waits for A1's key */
						desKey = decryptRSA(receive(in),recipientPubKey);

						/* EIGTH STEP */
						/* Generate 3 DES Key and send it to A3 */
						sendRSA(generateString23(42).getBytes(),out);

						/* NINETH STEP */
						/* Tell A1 it's ready */
						send("ready".getBytes(), out);

						/* TENTH STEP */
						/* Receive A1's request */
						String request = decrypt3DES(receive(in),desKey);

						/* ELEVENTH STEP */
						/* Sends request to A3 encrypted with their 3DES key */
						send(encrypt3DES(request, desKey23),out23);

						/* TWELVETH STEP */
						/* Waits for A3's answer and decypher it */
						request = decrypt3DES(receive(in23),desKey23);

						/* THIRTEENTH STEP */
						/* Forwards anwser to A1 after encrypting it with their 3DES key */
						send(encrypt3DES(request, desKey),out);


					}

				}


			}


		}

	}

	/**
	 * Function used to receive the certificate
	 * @return true if the certificate comes from A2 and is correct
	 */
	public boolean receiveCert23(InputStream in, OutputStream out) {
		byte[] certif = receive(in);
		String toR = "";
		if(checkCert(certif)){
			Log.logln("A1 : I validate A2's certificate");
			toR = "1";
		}else{
			toR = "0";
			Log.logln("A1 : I invalidate A2's certificate");
		}
		recipientPubKey23 = extractPubKeyCert(certif);
		send(toR.getBytes(),out);
		return toR.equals("1");
	}



	/**
	 * Function used to generate random Strings
	 * @param length the length of the String to generate
	 * @return the generated String
	 */
	public String generateString23(int length) {
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < length) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		return (desKey23=salt.toString().getBytes()).toString();

	}

}


