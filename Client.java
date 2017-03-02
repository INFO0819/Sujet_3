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
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class designed to handle the client connections and actions The class is
 * abstract and should not be instantiated
 *
 * @author Chaest
 */
public abstract class Client {

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

            certifCA = new String(Files.readAllBytes(Paths.get("certifCA"+name+".crt")), StandardCharsets.UTF_8);
            pubKey = new String(Files.readAllBytes(Paths.get(name + ".pub")), StandardCharsets.UTF_8);
            privKey = new String(Files.readAllBytes(Paths.get(name + ".priv")), StandardCharsets.UTF_8);

        } catch (FileNotFoundException ex) {
            System.out.println("Could not find the files");
        } catch (IOException e) {
            System.out.println("Problem while reading the files.");
        }
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
            System.out.println("Error while using the sockets.");
        }
        return this;
    }

    /**
     * Function used to receive the certificate
     *
     * @return a reference to this object
     */
    public Client receiveREQ() {
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
            
            switch(recvData.getData()[2]){
                case REQ_CERTIFICATE :
                    sendCert();
                break;
                default : break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

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
            System.out.println("Error while using the sockets.");
        }
        return this;
    }

    /**
     * Function used to receive the certificate
     *
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
     *
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

        //DatagramPacket sentData = new DatagramPacket(/*ANSWER*/.getBytes(), /*ANSWER*/.length(), server, PORT);
	DatagramPacket recvData = new DatagramPacket(new byte[LENR], LENR);

        try {
            socket.setSoTimeout(TIMEOUT);
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
                System.out.println(strcmd);
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
	
    public Client decrypt3DES(String nomFicMessage, String cle){
        try {
                Process p_cmd;
                String strcmd ="cat "+ nomFicMessage + " | openssl dec -des3 -pass pass:\"" + cle + "\"";
                Runtime runtime = Runtime.getRuntime();

                p_cmd = runtime.exec(new String[] { "bash", "-c",strcmd});
                BufferedReader std = new BufferedReader(new 
                             InputStreamReader(p_cmd.getInputStream()));

                String s = null;
                while ((s = std.readLine()) != null) {
                    System.out.println(s);
                }
        } catch (IOException e) {
                e.printStackTrace();
        }


        return this;		
    }
	
	
	
    public Client receive3DES(){
            return this;
    }
	
}
