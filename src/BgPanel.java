package server;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.omg.CORBA.portable.InputStream;

class BgPanel extends JPanel {
	/**
	 * < system path different >.
	 * 1. javac SwingFram.java
	 * 			 Working Directory = 
	 * 			/Users/aaron/eclipse-workspace/SnortEngine
	 * 2. java -jar snort.jar 
	 * 		     Working Directory = 
	 * 			/Users/aaron
	 * */
	private static final long serialVersionUID = 1L;
	public Image bg; 
	
	public BgPanel() throws IOException {	
		File file = new File("coding_bg_w_logo.jpg");;
		/**
		 * jar run will use path /User/aaron/..
		 * */	
		String current = new java.io.File( "." ).getCanonicalPath();
		
		if (file.exists()) {
			System.err.println("[1] Read success -- " + current + "/coding_bg_w_logo.jpg");
		} else {
			file = new File(current + "/support_file/coding_bg_w_logo.jpg");   // <-----------
			if (file.exists()) {
				System.err.println("[2] Read success -- " + current + "/support_file/coding_bg_w_logo.jpg");
			} 
			else {	
				String currentDir = System.getProperty("user.dir");
				file = new File(currentDir + "/support_file/coding_bg_w_logo.jpg");  // <-----------
				System.err.println("[3] Read -- " + currentDir + "/support_file/coding_bg_w_logo.jpg");
			}
		}
		bg = ImageIO.read(file);   
	}
	
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
}
