import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("unused")
public class ButtonClient1 extends JButton implements MouseListener {

	private static final long serialVersionUID = 1L;
	private String name;
	private Image img;
	private JFrame fenetreAjout;
	private int identifiant;
	final static int port = 9632;
	final static int taille = 9000;
	static byte buffer[] = new byte[taille];


	public ButtonClient1(String name,int id) {
		super(name);
		this.name = name;
	    this.addMouseListener(this);
	    this.fenetreAjout = new JFrame();
	    this.identifiant=id;
	}


	public ButtonClient1(Action a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	public ButtonClient1(Icon icon) {
		super(icon);
		// TODO Auto-generated constructor stub
	}

	public ButtonClient1(String text, Icon icon) {
		super(text, icon);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		JFrame fenetreClient = new JFrame();
		fenetreClient.setTitle("Communication");
		fenetreClient.setSize(300,330);
		fenetreClient.setLocationRelativeTo(null);
		fenetreClient.setLayout(new FlowLayout());
		fenetreClient.setVisible(true); 

		
		switch(identifiant){	
			case 1 :
				Client1();
			
				break;
			case 2 :
				Client2();
				break;
			case 3 :
				Client3();
				break;
		
		}	
	}
	//ok pe
	public void Client1(){
		 try {
			  System.out.println("CLIENT 1 ");
			  String a_envoyer ="1,Bonjour test1";  
		      InetAddress serveur = InetAddress.getByName("localhost");
		      int length = a_envoyer.length();
		      byte buffer[] = a_envoyer.getBytes();
		      DatagramSocket socket = new DatagramSocket();
		      DatagramPacket donneesEmises = new DatagramPacket(buffer, length, serveur, port);
		      DatagramPacket donneesRecues = new DatagramPacket(new byte[taille], taille);

		      socket.setSoTimeout(30000);
		      socket.send(donneesEmises);	
		      System.out.println("On attend confirmation");
		      socket.receive(donneesRecues);

		      System.out.println("Message : " + new String(donneesRecues.getData(), 0, donneesRecues.getLength()));
		      System.out.println("de : " + donneesRecues.getAddress() + ":" + donneesRecues.getPort());
		     
		      // effectuer envoie apres confirmation pret 
		    
		      a_envoyer="1,Message de test transmission venant  de 1 ";
		      length = a_envoyer.length();
		      buffer = a_envoyer.getBytes();
		      donneesEmises = new DatagramPacket(buffer,length, serveur, port);
		      socket.send(donneesEmises);
			  /*On attend retour de 3 */
		      System.out.println("On attend le retour du 3 ");
		      donneesRecues = new DatagramPacket(new byte[taille], taille);  
		      socket.receive(donneesRecues);
		      
		      System.out.println("Message : " + new String(donneesRecues.getData(), 0, donneesRecues.getLength()));
		      System.out.println("de : " + donneesRecues.getAddress() + ":" + donneesRecues.getPort());
		      
		      System.out.println("Terminaison  ");   
				      
		    } catch (SocketTimeoutException ste) {
		      System.out.println("Le delai pour la reponse a expire");
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
	}
	
	
	
	public void Client2(){
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket(port);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    String donnees = "";
		    String message = "";
		    int taille = 0;
		    
		    String donnees2 = "";
		    String message2 = "";
		    int taille2 = 0;
		    
		    System.out.println("Lancement du serveur");
		    /*On attend les deux client */
		    DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
		    DatagramPacket paquet2 = new DatagramPacket(buffer, buffer.length);
		    DatagramPacket paquet3 = new DatagramPacket(buffer, buffer.length);
		    
		    DatagramPacket envoi = null;
		    try {
			  socket.receive(paquet);
		    } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		    }
		    // message du premier client 1 
		    System.out.println("\n"+paquet.getAddress());
		    taille = paquet.getLength();
		    donnees = new String(paquet.getData(),0, taille);
		    System.out.println("Donnees reçues = "+donnees);
		  
		  
		   try {
			 	socket.receive(paquet2);
		   }catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		   }
		   //message du deuxieme client
		   System.out.println("\n"+paquet2.getAddress());
		   taille2 = paquet2.getLength();
		   donnees2 = new String(paquet2.getData(),0, taille);
		   System.out.println("Donnees reçues = "+donnees2);
		   // On envoie la confirmation 
		   
		    String a_envoyer2 ="OK";  
		    envoi = new DatagramPacket(a_envoyer2.getBytes(), a_envoyer2.length(), paquet.getAddress(), paquet.getPort());
		    System.out.println("On envoie le OK 1 ");
			try {
				socket.send(envoi);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			 
		    envoi = new DatagramPacket(a_envoyer2.getBytes(), 
				  a_envoyer2.length(), paquet2.getAddress(), paquet2.getPort());
		    System.out.println("On envoie le ok 2 ");
			try {
				socket.send(envoi);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    while (true) {
		    	/*Boucle principale */
		    	System.out.println("On entre dans la boucle principale");
		    	/*Maintenant on va transmettre les donnes */
		    	String donnees3;
				
				int taille3 = 0;
				
				try {
					socket.receive(paquet3);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// message du premier client 1 
				System.out.println("\n"+paquet3.getAddress());
				taille3 = paquet3.getLength();
				donnees3 = new String(paquet3.getData(),0, taille3);
				System.out.println("Donnees reçues = "+donnees3);

		    	/*On evalue de qui vient le premier message que on recoit */
		    	 String regex="[,]";
		         String[] en_tete = donnees3.split(regex);
		         System.out.println("La chaine split");
		         
		         for(int k=0;k<en_tete.length;k++){
		        	 System.out.println("split "+k+" "+en_tete[k]);
		         }
		         System.out.println("En tete :"+en_tete[0]+"!=1");
		         
		         if(en_tete[0].equals("1")){
		        	 /*On a recu le paquet du client 1*/
		        	 //transmission du message au client 3
		        	 System.out.println("On doit envoyer au client 3 ");
		        	 envoi = new DatagramPacket(donnees3.getBytes(), 
		        			 donnees3.length(), paquet2.getAddress(), paquet2.getPort());
        		     try {
						socket.send(envoi);
					 }  catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
		         }else{
		        	 /*On a recu le paquet du client 3*/
		        	 System.out.println("On doit envoyer au client 1 ");
		        	 //transmission du message au client 1
		        	 envoi = new DatagramPacket(donnees3.getBytes(), 
		        			 donnees3.length(), paquet.getAddress(), paquet.getPort());
        		     try {
						socket.send(envoi);
					 } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
		         }
		    }
		  
	}
	
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}

	public JFrame getFenetreAjout() {
		return fenetreAjout;
	}

	public void setFenetreAjout(JFrame fenetreAjout) {
		this.fenetreAjout = fenetreAjout;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	 
}
