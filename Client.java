import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.length;

/**
 * Class designed to handle the client connections and actions The class is
 * abstract and should not be instantiated
 *
 * @author Chaest
 */
public abstract class Client {

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
     * Client main constructor Loads the client keys and the certificate of the
     * CA thanks to its name Therefor the only names you should use are A1, A2,
     * A3 Will show a warning if another name is given
     *
     * @param name the name of the client
     */
    public Client(String name) throws UnknownHostException {
        this.name = name;

        if (name.compareTo("A1") != 0 && name.compareTo("A2") != 0 && name.compareTo("A3") != 0) {
            /* compare the name to "A1", "A2" and "A3" */
            System.out.println("The name you gave to the Client isn't a predefined name, you should reconsider using it for it may cause bugs.");
        }

        this.server = InetAddress.getByName("localhost");

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
			String strcmd ="openssl genpkey -algorithm RSA -out " + this.name + ".priv -pkeyopt rsa_keygen_bits:2048 && "
					+ "openssl rsa -pubout -in " + this.name + ".priv -out " + this.name + ".pub";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return this;
    }
    
    

    /**
     * Function used to send the Certificate
     *
     * @return a reference to this object
     */
    public Client sendCert() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String toSend = name.charAt(1)+"," + certifCA;
        DatagramPacket sentData = new DatagramPacket(toSend.getBytes(), toSend.length(), server, port);
        DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);
        try {
            socket.setSoTimeout(timeout);
            socket.send(sentData);
            socket.receive(recvData);
        } catch (IOException e) {
            System.out.println("Error while using the sockets.");
        }
        return this;
    }

    /**
     * Function used to receive the certificate
     *
     * @return a reference to this object
     */
    public boolean receiveCert() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);

        try {
            socket.setSoTimeout(timeout);
            socket.receive(recvData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return checkCert(new String(recvData.getData()));
    }

    /**
     * Function used to send a message encrypted using the private key
     *
     * @return a reference to this object
     */
    public Client sendRSA(String message) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String toSend = name.charAt(1)+"," + message;
        DatagramPacket sentData = new DatagramPacket(toSend.getBytes(), toSend.length(), server, port);
        DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);

        try {
            socket.setSoTimeout(timeout);
            socket.send(sentData);
            socket.receive(recvData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Function used to receive a message that has been encrypted using a
     * private key and to decrypt it
     *
     * @return a reference to this object
     */
    public Client receiveRSA() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //DatagramPacket sentData = new DatagramPacket(/*ANSWER*/.getBytes(), /*ANSWER*/.length(), server, port);
	DatagramPacket recvData = new DatagramPacket(new byte[lenR], lenR);

        try {
            socket.setSoTimeout(timeout);
            socket.receive(recvData);
            //socket.send(sentData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Function used to check the Certificate
     *
     * @return a reference to this object
     */
    public boolean checkCert(String cert) {
        boolean toR = false;

        return toR;
    }

    /**
     * Function used to send a message that has been encrypted using the
     * symetric key
     *
     * @return a reference to this object
     */
    public abstract Client sendDES(String message);

    /**
     * Function used to receive a message that has been encrypted using the
     * symetric key
     *
     * @return a reference to this object
     */
    public abstract Client receiveDES();

	public Client crypt3DES(String message, String cle, String nomFic){
		try {
			Process p_cmd;
			String strcmd ="echo \""+ message + "\" | openssl enc -des3 -pass pass:" + cle + " -out $(pwd)/" + nomFic;
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
	
	public Client decrypt3DES(String nomFicMessage, String cle){
		try {
			Process p_cmd;
			String strcmd ="cat "+ nomFicMessage + " | openssl dec -des3 -pass pass:\"" + cle + "\"";
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
	
	
	
	public Client receive3DES(){
		return this;
	}
	
}
