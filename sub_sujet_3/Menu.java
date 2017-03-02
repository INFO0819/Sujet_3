package sujet;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Menu {

public static PrintStream log = null;
public static void main(String[] args) {

   try {
  log = new PrintStream(System.out, true, "UTF-8");
   }catch(IOException e){
  System.exit(42);
   }

	JFrame fenetre = new JFrame();
		
		
	fenetre.setTitle("INFO0819");
	fenetre.setSize(140, 150);
	fenetre.setResizable(false);
	fenetre.setLocationRelativeTo(null);
	fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   
	   
	
	fenetre.setContentPane(new Panneau()); 
	JOptionPane jop = new JOptionPane();  


	JOptionPane jop1 = new JOptionPane(), jop2 = new JOptionPane();

	ButtonClient1 bL=new ButtonClient1("Client1",1);
	ButtonClient1 bW=new ButtonClient1("Client2",2);
	ButtonClient1 bA=new ButtonClient1("Client3",3);
	JButton bD= new JButton("CA");
	
	
	fenetre.setLayout(new FlowLayout());
	fenetre.getContentPane().add(bL,BorderLayout.EAST);
	fenetre.getContentPane().add(bW,BorderLayout.EAST);
	fenetre.getContentPane().add(bA,BorderLayout.EAST);
	fenetre.getContentPane().add(bD,BorderLayout.EAST);
	   
	fenetre.setVisible(true);
}

}
