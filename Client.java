import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class designed to handle the client connections and actions
 * This class is astract
 * The class shouldn't be instantiated, please use Client1, Client2 or Client3
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
			certifCA = Files.readAllBytes(Paths.get(name + ".cert.pem"));
		} catch (IOException e) {

			try {
				this.generateCert();
				try {
					certifCA = Files.readAllBytes(Paths.get(name + ".cert.pem"));
				} catch (IOException e1) {
					System.out.println(e1.getMessage());
				}
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
				System.exit(1);
			}
		}

	}

	/**
	 * Function used to generated the keys if the keys couldn't be read
     * @param name use to name of the client
	 * @return a reference to this object
	 */
	public static void generateKeyPair(String name){
		try {
			Process p_cmd;
			String strcmd ="openssl genpkey -algorithm RSA -out " + name + ".priv -pkeyopt rsa_keygen_bits:2048 && "
					+ "openssl rsa -pubout -in " + name + ".priv -out " + name + ".pub";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			int a = p_cmd.waitFor();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Function used to generate random Strings
	 * @param length the length of the String to generate
	 * @return the generated String
	 */
	public String generateString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    /**
     * Function used to generate new certificate
     * @return a reference to this object
     */
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
     * @param request is the request to send
     * @param out is the flux to use
	 * @return a reference to this object
	 */
	public Client sendREQ(String request, OutputStream out) {
		DataOutputStream dOut = new DataOutputStream(out);

		try {
			dOut.writeInt(request.getBytes().length);
			dOut.write(request.getBytes(), 0, request.getBytes().length);
			dOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // write length of the message 
		return this;
	}

    /**
     * Function used to send Request
     * @param request is the request to send
     * @param out is the flux to use
     * @return a reference to this object
     */
	public Client sendREQ(byte[] request, OutputStream out) {
		DataOutputStream dOut = new DataOutputStream(out);

		try {
			dOut.writeInt(request.length);
			dOut.write(request, 0, request.length);
			dOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // write length of the message 
		return this;
	}


    /**
     * Function used to receive messages
     * @param is the flux to us
     * @return the received message
     */
	public byte[] receive(InputStream is) {
		DataInputStream dIn = new DataInputStream(is);
		byte[] message = null;

		int length;
		try {
			length = dIn.readInt();
			if(length>0) {
			    message = new byte[length];
			    dIn.readFully(message, 0, message.length); // read the message
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                    // read length of incoming message
		
		
		return message;
	}
	

	/**
	 * Function used to send the Certificate
     * @param out is the flux to use
	 * @return a reference to this object
	 */
	public Client sendCert(OutputStream out) {
		try {
			DataOutputStream dOut = new DataOutputStream(out);
			
			dOut.writeInt(this.certifCA.length);
			dOut.write(this.certifCA, 0, certifCA.length);;
			dOut.flush(); // Send off the data
		} catch (IOException e) {
			System.out.println("Error while using the sockets.");
		}
		return this;
	}

	/**
	 * Function used to send a message encrypted using the private key
     * @param message is the message to send
     * @param recipientPubKey is the key used to encrypt
     * @param out is the flux to use	 *
	 * @return a reference to this object
	 */
	public Client sendRSA(String message, byte[] recipientPubKey, OutputStream out) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(name + "recipientPubKey"));
			fos.write(recipientPubKey);
			fos.close();
			
			fos = new FileOutputStream(new File(name + "message"));
			fos.write(message.getBytes());
			fos.close();
			
			Process p_cmd;
			String strcmd = "openssl  smime  -encrypt  -in " + name + "message -binary -outform DEM " + name + "recipientPubKey";
			
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			p_cmd.waitFor();

			DataInputStream std = new DataInputStream(p_cmd.getInputStream());	
			byte[] tab = new byte[std.available()];
			std.read(tab, 0, std.available());
			
			DataOutputStream dOut = new DataOutputStream(out);
			dOut.writeInt(tab.length);
			dOut.write(tab, 0, tab.length);;
			dOut.flush();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return this;
	}

    /**
     * Function used to decrypt RSA messages
     * @param data the data to decrypt
     * @param ficPrivKey the file of the key used to decrypt
     * @return the decyphered message
     */
	public byte[] decryptRSA(byte[] data, String ficPrivKey) {
		try {
			
			FileOutputStream fos = new FileOutputStream(new File(name + "data"));
			fos.write(data);
			fos.close();
			
			Process p_cmd;
			String strcmd = "openssl smime -decrypt  -in " + name + "data" + " -binary -inform DEM -inkey " +  ficPrivKey;
			
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			p_cmd.waitFor();

			DataInputStream std = new DataInputStream(p_cmd.getInputStream());
			
			byte[] tab = new byte[std.available()];
			std.read(tab, 0, std.available());
			
			return tab;

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return new byte[]{};
	}

	/**
	 * Function used to check the Certificate
     * @param cert certificate to check
	 *
	 * @return a reference to this object
	 */
	public boolean checkCert(byte[] cert) {
		try {
			FileOutputStream fos = new FileOutputStream(name + "certiftmp");
			fos.write(cert);
			fos.close();

			Process p_cmd;
			String strcmd ="openssl verify -verbose -CAfile "+ certifCAFileName + " " + name + "certiftmp";
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
     * Function used to extract the public key from the certificate
     * @param cert the certificate from which the key must be extracted
     * @return public key
     */
	public byte[] extractPubKeyCert(byte[] cert){
		try {
			FileOutputStream fos = new FileOutputStream(name + "certiftmp");
			fos.write(cert);
			fos.close();

			Process p_cmd;
			String strcmd ="openssl x509 -pubkey -noout -in " + name + "certiftmp";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(strcmd);
			
			
			p_cmd.waitFor();
			BufferedReader std = new BufferedReader(new InputStreamReader(p_cmd.getInputStream()));
			String s = "", temp = null;
			while ((temp = std.readLine()) != null) {
				s+= temp;
			}
			
			return s.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return new byte[]{};
	}

    /**
     * Function used to encrypt using 3DES
     * @param message the message to encrypt
     * @param cle the key used to encrypt
     * @return the encrypted message as an array of byte
     */
	public byte[] crypt3DES(String message, byte[] cle){
		try {
			FileOutputStream fos = new FileOutputStream(name + "crypt3DES");
			fos.write(message.getBytes());
			fos.close();
			
			Process p_cmd;
			String strcmd ="openssl enc -des3 -pass pass:" + new String(cle) + " -in " + name + "crypt3DES";
			Runtime runtime = Runtime.getRuntime();
			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			int a = p_cmd.waitFor();
			
			DataInputStream std = new DataInputStream(p_cmd.getInputStream());
			
			byte[] tab = new byte[std.available()];
			std.read(tab, 0, std.available());

			return tab;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return new byte[]{};		
	}

    /**
     * Decrypt message using 3DES
     * @param message the message to decrypt
     * @param cle the key used to decrypt
     * @return the decyphered message
     */
	public String decrypt3DES(byte[] message, byte[] cle){
		try {
			FileOutputStream fos = new FileOutputStream(name + "decrypt3DES");
			fos.write(message);
			fos.close();
						
			Process p_cmd;
			String strcmd ="openssl enc -d -des3 -pass pass:\"" + new String(cle) + "\" -in " + name + "decrypt3DES";
			Runtime runtime = Runtime.getRuntime();

			p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
			BufferedReader std = new BufferedReader(new InputStreamReader(p_cmd.getInputStream()));

			int a = p_cmd.waitFor();

			String s = "", temp = null;
			while ((temp = std.readLine()) != null) {
				s+= temp;
			}

			return s;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}

}
