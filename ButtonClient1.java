import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
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
	
	
	public ButtonClient1(String name) {
		super(name);
		this.name = name;
	    this.addMouseListener(this);
	    this.fenetreAjout = new JFrame();
	}

	public ButtonClient1() {
		super();
		// TODO Auto-generated constructor stub
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
		
		
		InetAddress LocaleAdresse ;
		InetAddress ServeurAdresse;

		try {

			LocaleAdresse = InetAddress.getLocalHost();
			System.out.println("L'adresse locale est : "+LocaleAdresse ); 
			
			
		
		} catch (UnknownHostException e) {
			
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