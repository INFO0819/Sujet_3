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
	
	public void Client1(){
		 try {
			  String a_envoyer ="1,Bonjour test1";  
		      InetAddress serveur = InetAddress.getByName("localhost");
		      int length = a_envoyer.length();
		      byte buffer[] = a_envoyer.getBytes();
		      DatagramSocket socket = new DatagramSocket();
		      DatagramPacket donneesEmises = new DatagramPacket(buffer, length, serveur, port);
		      DatagramPacket donneesRecues = new DatagramPacket(new byte[taille], taille);

		      socket.setSoTimeout(30000);
		      socket.send(donneesEmises);
		      socket.receive(donneesRecues);

		      System.out.println("Message : " + new String(donneesRecues.getData(), 
		        0, donneesRecues.getLength()));
		      System.out.println("de : " + donneesRecues.getAddress() + ":" + 
		        donneesRecues.getPort());
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
		
		    System.out.println("Lancement du serveur");
		    while (true) {
		      DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
		      DatagramPacket envoi = null;
		      try {
				socket.receive(paquet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		      System.out.println("\n"+paquet.getAddress());
		      taille = paquet.getLength();
		      donnees = new String(paquet.getData(),0, taille);
		      System.out.println("Donnees reçues = "+donnees);
		
		      message = "Bonjour "+donnees;
		      System.out.println("Donnees envoyees = "+message);
		      envoi = new DatagramPacket(message.getBytes(), 
		        message.length(), paquet.getAddress(), paquet.getPort());
		      try {
				socket.send(envoi);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
	}
	
	
	
	public void Client3(){
		 try {
			 
			  String a_envoyer ="3,Bonjour test3";  
		      InetAddress serveur = InetAddress.getByName("localhost");
		      int length = a_envoyer.length();
		      byte buffer[] = a_envoyer.getBytes();
		      DatagramSocket socket = new DatagramSocket();
		      DatagramPacket donneesEmises = new DatagramPacket(buffer, length, serveur, port);
		      DatagramPacket donneesRecues = new DatagramPacket(new byte[taille], taille);
		      socket.setSoTimeout(30000);
		      socket.send(donneesEmises);
		      socket.receive(donneesRecues);

		      System.out.println("Message : " + new String(donneesRecues.getData(), 
		        0, donneesRecues.getLength()));
		      System.out.println("de : " + donneesRecues.getAddress() + ":" + 
		        donneesRecues.getPort());
		    } catch (SocketTimeoutException ste) {
		      System.out.println("Le delai pour la reponse a expire");
		    } catch (Exception e) {
		      e.printStackTrace();
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