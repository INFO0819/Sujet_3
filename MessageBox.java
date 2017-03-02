import javax.swing.JOptionPane;


public class MessageBox{

	/**
	 * Will pop up a message displaying the given String.
	 * @param infoMessage the String to display.
	 */
	public static void show(String infoMessage){
        JOptionPane.showMessageDialog(null, infoMessage, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

	/**
	 * Will pop up a message displaying the given String.
	 * @param title the title of the poped up window.
	 * @param infoMessage the String to display.
	 */
	public static void show(String title, String infoMessage){
        JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
    }
	
}