import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Menu {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame fenetre = new JFrame();
		
		
	    fenetre.setTitle("INFO0819");
	    fenetre.setSize(140, 150);
	    fenetre.setResizable(false);
	    fenetre.setLocationRelativeTo(null);
	    fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   
	   
	    
	    fenetre.setContentPane(new Panneau()); 
	    JOptionPane jop = new JOptionPane();            


	    JOptionPane jop1 = new JOptionPane(), jop2 = new JOptionPane();

	    JButton bL=new JButton("Client1");
	    JButton bW=new JButton("Client2");
	    JButton bA=new JButton("Client3");
	    JButton bD= new JButton("CA");
	    
	    
	    fenetre.setLayout(new FlowLayout());
	    fenetre.getContentPane().add(bL,BorderLayout.EAST);
	    fenetre.getContentPane().add(bW,BorderLayout.EAST);
	    fenetre.getContentPane().add(bA,BorderLayout.EAST);
	    fenetre.getContentPane().add(bD,BorderLayout.EAST);
	   
	    fenetre.setVisible(true);
	}

}
