package tamere;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
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

    /* The socket used to send and receive data */
    public Socket socket;

    /* Intput stream for data sending */
    InputStream in;

    /* Output stream for data receiving */
    OutputStream out;

    /* The certificate of the client */
    public byte[] certifCA;

    /* The public key of the client */
    public byte[] pubKey;

    /* The private key of the client */
    public byte[] privKey;

    /* The public key received in the certificate */
    public byte[] recipientPubKey;

    /* The authority path. launch scriptCA.sh before using */
    public static final String caPath = "/tmp/ca/";

    /* The name of the file of the certificate of the client */
    public static final String certifCAFileName = "/tmp/ca/certs/ca.cert.pem";

    /* The name of the file of the public key of the client */
    public String pubKeyFileName;

    /* The name of the file of the private key of the client */
    public String privKeyFileName;

    /* The 3DES key of the client */
    public byte [] desKey;

    /* The name of the client (A1, A2, A3) */
    public String name;

    /* The server the client will use */
    public InetAddress server;

    /* The port that will be used for the communications between A1 and A2*/
    public final static int PORT12 = 10002;


    /* The port that will be used for the communications between A2 and A3*/
    public final static int PORT23 = 10001;

    /* The length of the received data */
    public final static int LENR = 9000;

    /* The time before timeout */
    public final static int TIMEOUT = 30000;

    /* The String used to generate random Strings */
    public static final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

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

            pubKey = Files.readAllBytes(Paths.get(name + ".pub"));
            privKey = Files.readAllBytes(Paths.get(name + ".priv"));
        } catch (FileNotFoundException ex) {
            Log.logln("Could not find the files");
        } catch (IOException e) {
            Log.logln("Problem while reading the files.");
            Log.logln("Generating keypair.");
            generateKeyPair();
            try {
                privKey = Files.readAllBytes(Paths.get(name + ".priv"));
                pubKey = Files.readAllBytes(Paths.get(name + ".pub"));
            } catch (Exception ex) {
                Log.err("Constructor/"+name,"Problem while reading the files. (" + name + "generating key)");
                System.exit(1);
            }
        }

        privKeyFileName = name + ".priv";
        pubKeyFileName = name + ".pub";

        try {
            certifCA = Files.readAllBytes(Paths.get(name + ".cert.pem"));
        } catch (IOException e) {

            Log.logln("Problem while reading the certificate file.");
            Log.logln("Generating certificate.");
            try {
                generateCert();
                try {
                    certifCA = Files.readAllBytes(Paths.get(name + ".cert.pem"));
                } catch (IOException e1) {
                    Log.err("Constructor/"+name,e1.getMessage());
                }
            } catch (Exception e1) {
                Log.err("Constructor/"+name,e1.getMessage());
                System.exit(1);
            }
        }
    }






    /*****************************************************************************************/
    /****************************************GENERATION***************************************/
    /*****************************************************************************************/








    /**
     * Function used to generated the keys if the keys couldn't be read
     * @return a reference to this object
     */
    public Client generateKeyPair(){
        try {
            Process p_cmd;
            String strcmd ="openssl genpkey -algorithm RSA -out " + name + ".priv -pkeyopt rsa_keygen_bits:2048 && openssl rsa -pubout -in " + name + ".priv -out " + name + ".pub";
            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
            int a = p_cmd.waitFor();


        } catch (IOException | InterruptedException  e) {
            Log.err("KEYGEN/"+name, "Problem with key generation.");
        }
    	return this;
    }

    /**
     * Function used to generate new certificate
     * @return a reference to this object
     */
    public Client generateCert(){
        try {
            Process p_cmd;
            String strcmd ="openssl req -config " + caPath + "openssl.cnf -key " + privKeyFileName+" -new -sha256 -passin pass:\"foobar\" -subj \"/C=FR/ST=France/L=Reims/O=urca/OU=IT/CN=" + name +
                    ".example.com\" -out " + name + ".csr.pem";
            Runtime runtime = Runtime.getRuntime();
            Log.logln(strcmd);
            p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});

            int code = p_cmd.waitFor();

            if(code == 0){
                strcmd = "openssl ca -config " + caPath + "openssl.cnf -extensions server_cert -days 365 -notext -md sha256 -passin pass:\"foobar\" -in " + name + ".csr.pem -out " + name + ".cert.pem -batch";

                runtime = Runtime.getRuntime();
                Log.logln(strcmd);
                p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
                code = p_cmd.waitFor();
                if(code != 0)
                    Log.err("CERTIFICATE", "Error while creating the certificate1.");
            }else{
                Log.err("CERTIFICATE", "Error while creating the certificate2.");
            }

        } catch (IOException | InterruptedException e) {
            Log.err("CERTIFICATE/"+name, "Error while creating the certificate3.");
        }

        return this;
    }

    /**
     * Function used to generate random Strings
     * @param length the length of the String to generate
     * @return the generated String
     */
    public String generateString(int length) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return (desKey=salt.toString().getBytes()).toString();

    }





    /*****************************************************************************************/
    /****************************************RECEIVING****************************************/
    /*****************************************************************************************/









    /**
     * Function used to receive messages
     * @return the received message
     */
    public byte[] receive(InputStream in) {
        DataInputStream dIn = new DataInputStream(in);
        try {
            byte[] message = new byte[dIn.readInt()];
            if(message.length>0)
               dIn.readFully(message, 0, message.length); // read the message
            return message;
        } catch (IOException e) {
            Log.err("RECEPTION/"+name, "Error while reading a message");
        }
        return null;
    }

    /**
     * Function used to receive the certificate
     * @return true if the certificate comes from A2 and is correct
     */
    public boolean receiveCert(InputStream in, OutputStream out) {
        byte[] certif = receive(in);
        String toR = "";
        if(checkCert(certif)){
            Log.logln("A1 : I validate A2's certificate");
            toR = "1";
        }else{
            toR = "0";
            Log.logln("A1 : I invalidate A2's certificate");
        }
        recipientPubKey = extractPubKeyCert(certif);
        send(toR.getBytes(),out);
        return toR.equals("1");
    }









    /*****************************************************************************************/
    /******************************************SENDING****************************************/
    /*****************************************************************************************/










    /**
     * Function used to send messages
     * @param message the message to send
     * @return a reference to this object
     */
    public Client send(byte[] message, OutputStream out) {
        DataOutputStream dOut = new DataOutputStream(out);
        try {
            dOut.writeInt(message.length);
            dOut.write(message, 0, message.length);
            dOut.flush();
        } catch (IOException e) {
            Log.err("SEND/"+name, "Couldn't send data.");
        }
        return this;
    }




    /**
     * Function used to send the Certificate
     * @return a reference to this object
     */
    public Client sendCert(OutputStream out) {
        try {
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.writeInt(certifCA.length);
            dOut.write(certifCA, 0, certifCA.length);;
            dOut.flush(); // Send off the data
        } catch (IOException e) {
            Log.err("SENDCERT/"+name, "Error while using the sockets.");
        }
        return this;
    }



    /**
     * Function used to send a message encrypted using the private key
     * @return a reference to this object
     */
    public Client sendRSA(byte[] message, OutputStream out) {
        try {

            /* LOADING PUBKEY IN FILE */
            FileOutputStream fos = new FileOutputStream(new File(name + "recipientPubKey"));
            fos.write(recipientPubKey);
            fos.close();

            /* LOADING MESSAGE IN FILE */
            fos = new FileOutputStream(new File(name + "message"));
            fos.write(message);
            fos.close();

            /* MESSAGE ENCRYPTION USING RSA (PUBKEY) */
            Process p_cmd;
            String strcmd = "openssl  smime  -encrypt  -in " + name + "message -binary -outform DEM " + name + "recipientPubKey";
            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
            p_cmd.waitFor();

            /* RECEIVING RESULT */
            DataInputStream std = new DataInputStream(p_cmd.getInputStream());
            byte[] tab = new byte[std.available()];
            std.read(tab, 0, std.available());


            /* SENDING RESULT */
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.writeInt(tab.length);
            dOut.write(tab, 0, tab.length);;
            dOut.flush();

        } catch (IOException | InterruptedException e) {
            Log.err("SENDRSA/"+name, "Error while sending via RSA.");
        }

        return this;
    }







    /*****************************************************************************************/
    /************************************CRYPTING PART****************************************/
    /*****************************************************************************************/




    /**
     * Function used to encrypt using 3DES
     * @param message the message to encrypt
     * @param key the key used to encrypt
     * @return the encrypted message as an array of byte
     */
	public byte[] encrypt3DES(String message, byte [] key){
        try {
            FileOutputStream fos = new FileOutputStream(name + "crypt3DES");
            fos.write(message.getBytes());
            fos.close();

            Process p_cmd;
            String strcmd ="openssl enc -des3 -pass pass:" + new String(key) + " -in " + name + "crypt3DES";
            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
            int a = p_cmd.waitFor();

            DataInputStream std = new DataInputStream(p_cmd.getInputStream());

            byte[] tab = new byte[std.available()];
            std.read(tab, 0, std.available());

            return tab;

        } catch (IOException | InterruptedException e) {
            Log.err("CRYPT/"+name, "Problem while encrypting using DES3.");
        }


        return null;
	}

    /**
     * Decrypt message using 3DES
     * @param message the message to decrypt
     * @param key the key used to decrypt
     * @return the decyphered message
     */
	public String decrypt3DES(byte[] message, byte [] key){
        try {
            FileOutputStream fos = new FileOutputStream(name + "decrypt3DES");
            fos.write(message);
            fos.close();

            Process p_cmd;
            String strcmd ="openssl enc -d -des3 -pass pass:\"" + new String(key) + "\" -in " + name + "decrypt3DES";
            Runtime runtime = Runtime.getRuntime();

            p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
            BufferedReader std = new BufferedReader(new InputStreamReader(p_cmd.getInputStream()));

            int a = p_cmd.waitFor();

            String s = "", temp = null;
            while ((temp = std.readLine()) != null) {
                s+= temp;
            }

            return s;
        } catch (IOException | InterruptedException e) {
            Log.err("DECRYPT/"+name, "Problem while encrypting using DES3.");
        }
        return null;
	}


    /**
     * Function used to decrypt RSA messages
     * @param data the data to decrypt
     * @param pk the key used to decrypt
     * @return the decyphered message
     */
    public byte[] decryptRSA(byte[] data, byte [] pk) {
        try {

            FileOutputStream fos = new FileOutputStream(new File(name + "data"));
            fos.write(data);
            fos.close();

            Process p_cmd;
            String strcmd = "openssl smime -decrypt  -in " + name + "data" + " -binary -inform DEM -inkey " +  pk;

            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
            p_cmd.waitFor();

            DataInputStream std = new DataInputStream(p_cmd.getInputStream());

            byte[] tab = new byte[std.available()];
            std.read(tab, 0, std.available());

            return tab;

        } catch (IOException | InterruptedException e) {
            Log.err("DECRYPTRSA/"+name, "Problem while decrypting using RSA.");
        }

        return null;
    }





    /**
     * Function used to check the Certificate
     * @return a reference to this object
     */
    public boolean checkCert(byte [] cert) {
        try {
            FileOutputStream fos = new FileOutputStream(name + "certiftmp");
            fos.write(cert);
            fos.close();
            Process p_cmd;
            String strcmd ="openssl verify -verbose -CAfile "+ certifCAFileName + " " + name + "certiftmp";
            Runtime runtime = Runtime.getRuntime();
            p_cmd = runtime.exec(strcmd);
            return p_cmd.waitFor() == 0;
        } catch (IOException | InterruptedException ex) {
            Log.err("CHECKCERT/"+name, "Problem while checking certificate.");
        }
        return false;
    }


    /**
     * Function used to extract the public key from the certificate
     * @param cert the certificate from which the key must be extracted
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

        } catch (IOException | InterruptedException ex) {
            Log.err("EXTRACT/"+name, "Problem while extracting public key from certificate.");
        }
        return null;

    }



    /**
     * Function used to start each client's connection
     * Should be implemented in every Client subclasses
     */
    public abstract void connect();
	
	
}
