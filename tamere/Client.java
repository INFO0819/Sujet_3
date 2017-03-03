package tamere;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class designed to handle the client connections and actions
 * The class shouldn't be instantiated, please use Client1, Client2 or Client3
 * @author Chaest
 */
public class Client {

    /* The certificate of the client */
    public String certifCA;

    /* The public key of the client */
    public String pubKey;

    /* The private key of the client */
    public String privKey;

    /* The name of the file of the certificate of the client */
    public String certifCAFileName;

    /* The name of the file of the public key of the client */
    public String pubKeyFileName;

    /* The name of the file of the private key of the client */
    public String privKeyFileName;

    /* The 3DES key of the client */
    public String desKey;

    /* The name of the client (A1, A2, A3) */
    public String name;

    /* The server the client will use */
    public InetAddress server;

    /* The PORT that will be used for the communications */
    public final static int PORT = 9632;

    /* The length of the received data */
    public final static int LENR = 9000;

    /* The time before timeout */
    public final static int TIMEOUT = 30000;

    /* REQUESTS VALUES */
    public final static char REQ_CERTIFICATE = '1';
    public final static char REQ_ANSWER = '2';
    public final static char REQ_FORWARD = '3';
    

    /**
     * Client main constructor Loads the client keys and the certificate of the
     * CA thanks to its name Therefor the only names you should use are A1, A2,
     * A3 Will show a warning if another name is given
     * @param name the name of the client
     */
    public Client(String name){
        this.name = name;

        if (name.compareTo("A1") != 0 && name.compareTo("A2") != 0 && name.compareTo("A3") != 0) { /* compare the name to "A1", "A2" and "A3" */
            System.out.println("The name you gave to the Client isn't a predefined name, you should reconsider using it for it may cause bugs.");
        }

        try {
            this.server = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            certifCA = new String(Files.readAllBytes(Paths.get("certifCA"+ name + ".crt")), StandardCharsets.UTF_8);
            pubKey = new String(Files.readAllBytes(Paths.get(name + ".pub")), StandardCharsets.UTF_8);
            privKey = new String(Files.readAllBytes(Paths.get(name + ".priv")), StandardCharsets.UTF_8);
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find the files");
        } catch (IOException e) {
            System.out.println("Problem while reading the files.");
        }
    }
    
    
    public Client generateKeyPair(){
    	try {
			Process p_cmd;
			String strcmd ="openssl genpkey -algorithm RSA -out " + this.name + ".priv -pkeyopt rsa_keygen_bits:2048 && openssl rsa -pubout -in " + this.name + ".priv -out " + this.name + ".pub";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			int a = p_cmd.waitFor();
			
			System.out.println(a);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return this;
    }
    
    public Client generateCert(){
    	//openssl req -new -key 2/c2.key > c2.csr
    	//openssl x509 -req -in c2.csr -out 2/c2.crt -CA ca.crt -CAkey ca.key -CAcreateserial -CAserial ca.srl
    	try {
			Process p_cmd;
			String strcmd ="openssl x509 -req -in  -out " + name + ".priv -CA ca.crt -CAkey ca.key -CAcreateserial -CAserial ca.srl"
					+ "openssl rsa -pubout -in " + this.name + ".priv -out " + this.name + ".pub";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			int a = p_cmd.waitFor();
			
			System.out.println(a);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	return this;
    }
    
    
    
    /**
     * Function used to send the Certificate
     * @return a reference to this object
     */
    public Client sendREQ(char request) {
        try {
            DatagramSocket socket = new DatagramSocket();
            String toSend = name.charAt(1)+"," + request;
            socket.setSoTimeout(TIMEOUT);
            socket.send(new DatagramPacket(toSend.getBytes(), toSend.length(), server, PORT));
        } catch (IOException e) {
            Log.logln("Error while using the sockets.");
        }
        return this;
    }

    /**
     * Function used to receive the certificate
	 * @param des tell wether the message received has to be decyphered
     * @return a reference to this object
     */
    public Client receiveREQ(boolean des) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket recvData = new DatagramPacket(new byte[LENR], LENR);
		
        try {
            socket.setSoTimeout(TIMEOUT);
            socket.receive(recvData);
			
			String data = new String(recvData.getData());
            if(des)decrypt3DES(data, desKey);
			
            switch(data.charAt(2)){
                case REQ_CERTIFICATE :
                    sendCert();
                break;
				case REQ_ANSWER : 
					if(name.charAt(1)=='3')
						sendDES(name+","+REQ_FORWARD);
					else
						sendDES(name+","+REQ_ANSWER);
				break;
				case REQ_FORWARD :
					if(name.charAt(1) == '1')
						Log.logln("Answer received!");
					else
						sendDES(name+","+REQ_FORWARD);
                default : break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
	/** PREV DEF **/
	public Client receiveREQ(){return receiveREQ(false);}

    /**
     * Function used to send the Certificate
     * @return a reference to this object
     */
    public Client sendCert() {
        try {
            DatagramSocket socket = new DatagramSocket();
            String toSend = name.charAt(1)+"," + certifCA;
            socket.setSoTimeout(TIMEOUT);
            socket.send(new DatagramPacket(toSend.getBytes(), toSend.length(), server, PORT));
            socket.receive(new DatagramPacket(new byte[LENR], LENR));
        } catch (IOException e) {
            Log.logln("Error while using the sockets.");
        }
        return this;
    }

    /**
     * Function used to receive the certificate
     * @return true if the certificate comes from A2 and is correct
     */
    public boolean receiveCert() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket recvData = new DatagramPacket(new byte[LENR], LENR);

        try {
            socket.setSoTimeout(TIMEOUT);
            socket.receive(recvData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(recvData.getData()[0] == '2')
            return checkCert(new String(recvData.getData()));
        else
            return false;
    }

    /**
     * Function used to send a message encrypted using the private key
     * @return a reference to this object
     */
    public Client sendRSA(String message) {
        try {
            PrintWriter writer = new PrintWriter(new File("toEncrypt.txt"));
            writer.write(message);
            Process p_cmd;
            String strcmd ="openssl rsautl -encrypt -in toEncrypt.txt -inkey " + privKeyFileName + " -out outencrypted.txt";
            File f = new File ("outencrypted.txt");
            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(strcmd);
            
            DatagramSocket socket = new DatagramSocket();
            String toSend=name.charAt(1)+","+new String(Files.readAllBytes(Paths.get("outencrypted.txt")), StandardCharsets.UTF_8);
            socket.setSoTimeout(TIMEOUT);
            socket.send(new DatagramPacket(toSend.getBytes(), toSend.length(), server, PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Function used to receive a message that has been encrypted using a private key and to decrypt it
     * @param filePubKDecrypt : public key file of a message sender
     * @return if decryption work : the String fileName of the decrypted datap, else the filePubKDecrypt
     */
    public String receiveRSA(String filePubKDecrypt) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
		
		DatagramPacket recvData = new DatagramPacket(new byte[LENR], LENR);

        try {
            socket.setSoTimeout(TIMEOUT);
            socket.receive(recvData);
            String data = new String(recvData.getData());
            PrintWriter encryptMsg = new PrintWriter(new File ("encryptMsg.txt"));
            encryptMsg.write(data);

            Process p_cmd;
            String strcmd ="openssl rsautl -decrypt -in encryptMsg.txt -inkey " + filePubKDecrypt + " -out outdecrypted.txt";
            File f = new File ("outencrypted.txt");
            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(strcmd);
            return "outdecrypted.txt";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePubKDecrypt;
    }

    /**
     * Function used to check the Certificate
     * @return a reference to this object
     */
    public boolean checkCert(String cert) {
        try {
            PrintWriter writer = new PrintWriter(new File("certif.tmp"));
            writer.write(cert);
            Process p_cmd;
            String strcmd ="openssl verify -verbose -CAfile "+certifCAFileName+" certif.tmp";
            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(strcmd);
            return p_cmd.waitFor() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return false;
    }

    /**
     * Function used to send a message that has been encrypted using the symetric key
     * @return a reference to this object
     */
    public Client sendDES(String message){
        try {
            DatagramSocket socket = new DatagramSocket();
            String toSend = crypt3DES(name.charAt(1)+"," + message,desKey);
            socket.setSoTimeout(TIMEOUT);
            socket.send(new DatagramPacket(toSend.getBytes(), toSend.length(), server, PORT));
        } catch (IOException e) {
            Log.logln("Error while using the sockets.");
        }
		return this;
	}

    /**
     * Function used to receive a message that has been encrypted using the symetric key
     * @return a reference to this object
     */
    public Client receiveDES(){
		receiveREQ(true);
		return this;
	}

	public String crypt3DES(String message, String key){
		try {
			Process p_cmd;
			String strcmd ="echo \""+ message + "\" | openssl enc -des3 -pass pass:" + key + " -out $(pwd)/encrypted.des";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			int a = p_cmd.waitFor();
			
			System.out.println(a);
			
			return new String(Files.readAllBytes(Paths.get("encrypted.des")), StandardCharsets.UTF_8);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Client decrypt3DES(String filename, String key){
		try {
			Process p_cmd;
			String strcmd ="cat "+ filename + " | openssl dec -des3 -pass pass:\"" + key + "\"";
			Runtime runtime = Runtime.getRuntime();
			
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			BufferedReader std = new BufferedReader(new InputStreamReader(p_cmd.getInputStream()));
			

			int a = p_cmd.waitFor();
			
			String s = null;
			while ((s = std.readLine()) != null) {
			    System.out.println(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return this;		
	}
	
	
	
	
}
