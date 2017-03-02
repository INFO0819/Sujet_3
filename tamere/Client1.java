package tamere;


/**
 * Class containing the actions of the Client A1
 * @author Alex
 */
public class Client1 extends Client {

	/**
	 * Basic constructor
	 * The name given to the super constructor is "A1"
	 */
	public Client1(){
		super("A1");
		
		crypt3DES("toto", "tata", "123456789123456789.txt");
		
		/* FIRST STEP */
		/* Ask for A2 certificate */
		sendREQ(REQ_CERTIFICATE);
		
		/* SECOND STEP */
		/* Receiving and checking certificate */
		if(receiveCert()){
			
			/********************/
			/* PUB KEY SEND ????*/
			/********************/
			
			/* FOURTH STEP */
			/* Send 3DES Key with RSA encryption */
			sendRSA(desKey);
			
			/* FIFTH STEP */
			/* Send request through symetric encryption */
			sendDES(name.charAt(1)+","+REQ_ANSWER);
			
			/* SIXTH STEP */
			/* Receive answer through symetric encryption */
			receiveDES();
			
			
		}else{ /* The certificate is incorrect */
			MessageBox.show("Error in certificate!", "The certificate A1 received was incorrect.\nShutting down simulation.");
		}
	}
	
}
