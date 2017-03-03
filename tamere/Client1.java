package tamere;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class containing the actions of the Client A1
 * @author Alex
 */
public class Client1 extends Client implements Runnable{

	/**
	 * Basic constructor
	 * The name given to the super constructor is "A1"
	 */
	public Client1(){
		super("A1");
	}

	@Override
	public void connect(){
		try {
			socket = new Socket("localhost", PORT12);
			in = socket.getInputStream();
			out = socket.getOutputStream();
			Log.logln("Connection 1->2 established.");
		} catch(UnknownHostException e) {
			Log.err("HOST", "Host error");
			System.exit(-1);
		} catch(IOException e) {
			Log.err("SOCKET","Socket creation impossible in " + name);
			System.exit(-1);
		}
	}

	@Override
	public void run(){
		connect();


		/* INITIALIZATION */
		/* Sends certificate to A2 */
		sendCert(out);

		Log.logln("before wait for a2 validation");
		/* Waits for A2's validation */
		if(receive(in).equals("1")) {

            Log.logln("after wait for a2 validation");
			/* FIRST STEP */
			/* Receiving and checking certificate */
			if (receiveCert(in,out)) {
				Log.logln("A1 : my certificate has been validated by A2.");

				/* SECOND STEP */
				/* Waiting for A2 to check certification with A3 */
				receive(in); // no need to read the message, just waiting

                /* THIRD STEP */
                /* Generate 3DES Key and send it to A2 */
                sendRSA(generateString(42).getBytes(),out);

				/* FOURTH STEP */
				/* WAit for A2-A3 communication */
				receive(in); // no need to read the message, just waiting

				/* FIFTH STEP */
				/* Send request through symetric encryption */
				send(encrypt3DES("REQUEST",desKey),out);

				/* SIXTH STEP */
				/* Receive answer through symetric encryption */
				MessageBox.show("A1 received : " + decrypt3DES(receive(in),desKey));


			} else { /* The certificate is incorrect */
				MessageBox.show("Error in certificate!", "The certificate A1 received was incorrect.\nShutting down simulation.");
			}

		}else {
			Log.logln("A1 : my certificate has been invalidated by A2.");
		}

	}
}
