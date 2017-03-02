package tamere;

public class Client2 extends Client {

	/**
	 * Basic constructor
	 * The name given to the super constructor is "A1"
	 */
	public Client2(){
		super("A2");
		
		crypt3DES("toto", "tata", "123456789123456789.txt");
		
		/* FIRST STEP */
		/* Wait for A1 to send request */
		receiveREQ();
		
		/********************/
		/* RECV PUB KEY???? */
		/********************/
		
		/* SECOND STEP */
		/* Receive 3DES Key with RSA encryption from A1 */
		String dKEY = receiveRSA();
		
		
		/* THIRD STEP */
		/* Ask for A3 certificate */
		sendREQ(REQ_CERTIFICATE);
		
		
		/* FOURTH STEP */
		/* Receiving and checking certificate */
		if(receiveCert()){
			
			/********************/
			/* PUB KEY SEND ????*/
			/********************/
			
			/* FIFTH STEP */
			/* Send 3DES Key with RSA encryption */
			sendRSA(desKey);
			
			/* SIXTH STEP */
			/* Wait for A1 to ask for answer form A3 */
			receiveREQ();
			
			/* SEVENTH STEP */
			/* Wait for A3 to ask forwarding answer to A1 */
			receiveREQ();
			
		}else{ /* The certificate is incorrect */
			MessageBox.show("Error in certificate!", "The certificate A1 received was incorrect.\nShutting down simulation.");
		}
	}

}
