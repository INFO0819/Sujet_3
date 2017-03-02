import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.length;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

/**
 * Class designed to handle the client connections and actions The class is
 * abstract and should not be instantiated
 *
 * @author Chaest
 */
public abstract class Client {
	/* The authority path. launch scriptCA.sh before using */
	public static final String caPath = "/tmp/ca/";

    /* The certificate of the client */
    public byte[] certifCA;

    /* The public key of the client */
    public byte[] pubKey;

    /* The private key of the client */
    public byte[] privKey;

    /* The name of the file of the certificate of the client */
    public static String certifCAFileName = "/tmp/ca/certs/ca.cert.pem";

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
    public int portEcoute;

    /* The length of the received data */
    public final static int LENR = 9000;

    /* The time before timeout */
    public final static int TIMEOUT = 30000;

    /* REQUESTS VALUES */
    public final static char REQ_CERTIFICATE = '1';
    
    

    /**
     * Client main constructor Loads the client keys and the certificate of the
     * CA thanks to its name Therefor the only names you should use are A1, A2,
     * A3 Will show a warning if another name is given
     * @param name the name of the client
     * @param port of the client
     */
    public Client(String name, int port){
        this.name = name;
        this.portEcoute = port;

        if (name.compareTo("A1") != 0 && name.compareTo("A2") != 0 && name.compareTo("A3") != 0) { /* compare the name to "A1", "A2" and "A3" */
            System.out.println("The name you gave to the Client isn't a predefined name, you should reconsider using it for it may cause bugs.");
        }

        try {
            this.server = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            privKey = Files.readAllBytes(Paths.get(name + ".priv"));
            pubKey = Files.readAllBytes(Paths.get(name + ".pub"));
            
        } catch (IOException ex) {
        	// Si la paire de clé n'est pas trouvable, on la crée
        	Client.generateKeyPair(this.name);
        	try {
                privKey = Files.readAllBytes(Paths.get(name + ".priv"));
                pubKey = Files.readAllBytes(Paths.get(name + ".pub"));
			} catch (Exception e) {
	            System.out.println("Problem while reading the files. (" + name + "generating key)");
	            System.exit(1);
			}
            
        }
        
        privKeyFileName = name + ".priv";
        pubKeyFileName = name + ".pub";
        
        try {
        	certifCA = Files.readAllBytes(Paths.get("certifCA"+ name + ".crt"));
        } catch (IOException e) {
        	
            try {
				this.generateCert();
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
				System.exit(1);
			}
        }
        
    }
    
    
    public static void generateKeyPair(String name){
    	try {
			Process p_cmd;
			String strcmd ="openssl genpkey -algorithm RSA -out " + name + ".priv -pkeyopt rsa_keygen_bits:2048 && "
					+ "openssl rsa -pubout -in " + name + ".priv -out " + name + ".pub";
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
    	
    }
    
    public Client generateCert() throws Exception{
    	try {
			Process p_cmd;
			String strcmd ="openssl req -config " + Client.caPath + "openssl.cnf -key " + privKeyFileName+ 
					" -new -sha256 -passin pass:\"foobar\" -subj \"/C=FR/ST=France/L=Reims/O=urca/OU=IT/CN=" + name + ".example.com\" "
					+ "-out " + name + ".csr.pem";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			int code = p_cmd.waitFor();
			
			if(code == 0){
				strcmd = "openssl ca -config openssl.cnf -extensions server_cert -days 365 -notext -md sha256 "
						+ "-passin pass:\"foobar\" -in " + name + ".csr.pem -out " + name + ".cert.pem -batch";
				
				runtime = Runtime.getRuntime();
				p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
				code = p_cmd.waitFor();
				if(code != 0)
					throw new Exception("Erreur de la génération de certificat");
			}else{
				throw new Exception("Erreur de génération de la demande de certificat");
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
    
    
    
    /**
     * Function used to send the Certificate
     * @return a reference to this object
     */
    public Client sendREQ(String request, Socket socket) {
        try {
            String toSend = name.charAt(1)+"," + request;
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
	         dOut.writeUTF(toSend);
	         dOut.flush(); // Send off the data
        } catch (IOException e) {
            System.out.println("Error while using the sockets.");
        }
        return this;
    }

    /**
     * Function used to send the Certificate
     * @return a reference to this object
     */
    public Client sendCert(String request, Socket socket) {
        try {
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
	         dOut.write(this.certifCA, 0, certifCA.length);;
	         dOut.flush(); // Send off the data
        } catch (IOException e) {
            System.out.println("Error while using the sockets.");
        }
        return this;
    }

    /**
     * Function used to send a message encrypted using the private key
     *
     * @return a reference to this object
     */
    public Client sendRSA(String message, byte[] recipientPubKey, Socket socket) {
        try {
            PrintWriter writer = new PrintWriter(new File("toEncrypt.txt"));
            writer.write(message);
            Process p_cmd;
            String strcmd ="echo \"" + new String(recipientPubKey) + "\" > " + name + "temp";
            Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			int a = p_cmd.waitFor();
			
			
			strcmd = "echo \"" + message + "\"| openssl rsautl -encrypt -inkey " + name + "temp -pubin";
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			
			
			p_cmd.waitFor();
			BufferedReader std = new BufferedReader(new InputStreamReader(p_cmd.getInputStream()));
			String s = "", temp = null;
			while ((temp = std.readLine()) != null) {
				s+= temp;
			}
			
            File f = new File ("outencrypted.txt");
            
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
	        dOut.write(this.certifCA, 0, certifCA.length);;
	        dOut.flush();
            
        } catch (IOException | InterruptedException e) {
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
     * Function used to send a message that has been encrypted using the
     * symetric key
     *
     * @return a reference to this object
     */
    public Client sendDES(String message){
    	
    	return this;
    }

    /**
     * Function used to receive a message that has been encrypted using the
     * symetric key
     *
     * @return a reference to this object
     */
    public Client receiveDES(){
    	
    	return this;
    }

	public Client crypt3DES(String message, String cle, String nomFic){
		try {
			Process p_cmd;
			String strcmd ="echo \""+ message + "\" | openssl enc -des3 -pass pass:" + cle + " -out " + nomFic;
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
			String strcmd ="openssl enc -d -des3 -pass pass:\"" + cle + "\" -in " + nomFicMessage;
			Runtime runtime = Runtime.getRuntime();
			
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			BufferedReader std = new BufferedReader(new InputStreamReader(p_cmd.getInputStream()));
			

			int a = p_cmd.waitFor();
			
			String s = "", temp = null;
			while ((temp = std.readLine()) != null) {
				s+= temp;
			}
			
			System.out.println(s);
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
