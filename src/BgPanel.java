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














// ------- logs -------------------
/* == run from SwingFrame.java. OK

// -- run jar file               from    /external_src
// 	File pathToFile = new File(BgPanel.class.getClassLoader().getResource("coding_bg_w_logo.jpg").getFile());
// 	bg = ImageIO.read(pathToFile);
// == run from SwingFrame.java. OK
// == run from java -jar nort_v4.jar  Error
	Exception in thread "main" javax.imageio.IIOException: Can't read input file!
	at javax.imageio.ImageIO.read(ImageIO.java:1301)
	at server.BgPanel.<init>(BgPanel.java:51)
	at server.SwingFrame.prepareGUI(SwingFrame.java:107)
	at server.SwingFrame.<init>(SwingFrame.java:54)
	at server.SwingFrame.main(SwingFrame.java:458)
*/




// ===== test load file from external_src classpath work or not ========
//System.err.println("Read: /external_src/snortplus.txt  (combined in JAR file)  works");
//
//java.io.InputStream stream = BgPanel.class.getClassLoader().getResourceAsStream("snortplus.txt");
//System.out.println(stream != null);
//System.err.println(stream);

//  java.io.InputStream is = stream;
//  int i;
//  char c;  
//  try {
//     // new input stream created
//     System.out.println("Characters printed:");
//     // reads till the end of the stream
//     while((i = is.read())!=-1) {
//        // converts integer to character
//        c = (char)i;
//        // prints character
//        System.err.print(c);
//     }
//  } catch(Exception e) {
//     // if any I/O error occurs
//     e.printStackTrace();
//  } finally {
//     // releases system resources associated with this stream
//     if(is!=null)
//        is.close();
//  }
// =========================================
//public Image bg;
//
//public BgPanel() throws IOException {
//	// TODO Auto-generated constructor stub
////	try {
////		
////		URL url = new URL("http://github.com/newskyinc/Tool_for_Snort/blob/master/src/img/coding_bg_w_logo.jpg");
////		bg = ImageIO.read(url);
////		
////	} catch (MalformedURLException e) {
////		e.printStackTrace();
////	}
//		
//	Image bg = new ImageIcon("coding_bg_w_logo.jpg").getImage();   
////	  - local file: /Users/aaron/eclipse-workspace/SnortEngine/coding_bg_w_logo.jpg
//}
