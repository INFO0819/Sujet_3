package tamere;


public class Client3 extends Client{

	/**
	 * Basic constructor
	 * The name given to the super constructor is "A1"
	 */
	public Client3(){
		super("A3");
		
		
		/* FIRST STEP */
		/* Wait for A2 to send request */
		receiveREQ();
		
		/********************/
		/* RECV PUB KEY???? */
		/********************/
		
		/* THIRD STEP */
		/* Ask for A2 certificate */
		sendREQ(REQ_CERTIFICATE);
		
		
		/* FOURTH STEP */
		/* Receiving and checking certificate */
		if(receiveCert()){
			
			/********************/
			/* PUB KEY SEND ????*/
			/********************/
			
			/* FIFTH STEP */
			/* Receive 3DES Key with RSA encryption from A2 */
			String dKEY = receiveRSA();
			
			/* SIXTH STEP */
			/* Wait for A1 to ask for answer*/
			receiveREQ();
			
		}else{ /* The certificate is incorrect */
			MessageBox.show("Error in certificate!", "The certificate A1 received was incorrect.\nShutting down simulation.");
		}
	}

}
