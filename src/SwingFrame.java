package server;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import server.ShowMessageDialogWithScrollpane.ShowDialogListener;

public class SwingFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JFrame frame;
	private JPanel panel;
	
	private JTextField textField, textField2;
	
	private JButton btnBrowse, btnBrowse2;
	private JButton btnExit;
	private JButton btnSend;

	private JLabel label1, label2, label3, label;

	private JTextArea textArea1;
	
	private String Absolute_Path1 = null;
	private String Absolute_Path2 = null;

	private String PCAP_FILE = null; 	
	private String RULE_FILE = "20157254.rule"; 
	
	private String alertResult = "[alert result hasn't initialized]";			

	
	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
	}

	public SwingFrame(){
		
		Absolute_Path1 = null;
		Absolute_Path2 = null;
		PCAP_FILE = null; 	
		RULE_FILE = "20157254.rule"; 
		alertResult = "[alert result hasn't initialized]";	
		prepareGUI();
		
	}
	
	private void prepareGUI(){
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		System.err.println("--- last update " + dtf.format(localDate) + " --- \n");
	
		frame = new JFrame("[Snort Engine v3.0]" + "  --- last updated " + dtf.format(localDate));
		frame.setSize(600,500);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}	
		});

		
		panel = new JPanel();
		panel.setBounds(0, 0, 580, 500);  // x, y, w, h
		panel.setLayout(null);
		panel.setVisible(true);
		
		
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(panel);
		frame.setVisible(true);  
		
		
		// =======================================================================	
		// =======================================================================	

		label1 = new JLabel("PCAP File:");
		label1.setVisible(true);
		label1.setBounds(10, 5, 305, 31);           // (int x, int y, int width, int height) 
		label1.setFont(new Font("", Font.PLAIN, 13));
		panel.add(label1);
		
		// --------------------------------------------------------------------
		textField = new JTextField();
		textField.setBounds(80, 5, 295, 31);
		textField.setColumns(100);
		panel.add(textField);

		// --------------------------------------------------------------------
				
		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(378, 5, 105, 31);
		btnBrowse.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser filedilg = new JFileChooser();
				filedilg.showOpenDialog(filedilg);
				Absolute_Path1 = filedilg.getSelectedFile().getAbsolutePath();
					
				
				textField.setText(Absolute_Path1);

				File file1 = new File(Absolute_Path1);
				PCAP_FILE = file1.getName();    // <---      
				
				
			    try { Thread.sleep(400); } catch (InterruptedException e1) { e1.printStackTrace(); }
			    
			    
				if (PCAP_FILE == null || !PCAP_FILE.toLowerCase().contains("cap")) {
					label2.setText("<html><font color='red'>This is NOT a pcap file. Please browse again!</font></html>");
				} else {
					label2.setText("<html><font color='orange'>[Ready] PCAP file you selected is: <b>" + PCAP_FILE + "</b></font></html>");	
					label3.setVisible(true);
					label3.setText("<html><font color='red'>* [Next] <b>'Browse'</b> to select another rule file. Or use default rule. Click 'Send'</font></html>");
					
					textField2.setText("(default): /remote/20157254.rule");
				}
						
				System.out.println("\n=========== [ 1. browse PCAP finish ] ================= ");
				System.out.println("PCAP_FILE selected: <b>" + PCAP_FILE + "</b>");
				System.out.println("Absolute Path1: " 		     + Absolute_Path1);
				System.out.println("=======================================================\n ");
			}
		});
		
		btnBrowse.setFont(new Font("", Font.PLAIN, 13));
		panel.add(btnBrowse);

		
		
		
		
		// =======================================================================
		// =======================================================================	
		label = new JLabel("Rule File:");
		label.setVisible(true);
		label.setBounds(10, 35, 305, 31);           // (int x, int y, int width, int height) 
		label.setFont(new Font("", Font.PLAIN, 13));
		panel.add(label);
		
		// --------------------------------------------------------------------
		textField2 = new JTextField();
		textField2.setBounds(80, 35, 295, 31);
		textField2.setColumns(100);
		panel.add(textField2);

		// --------------------------------------------------------------------

		btnBrowse2 = new JButton("Browse");
		btnBrowse2.setBounds(378, 35, 105, 31);
		btnBrowse2.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser filedilg = new JFileChooser();
				filedilg.showOpenDialog(filedilg);
				Absolute_Path2 = filedilg.getSelectedFile().getAbsolutePath();
					
				
				textField2.setText(Absolute_Path2);

				File file2 = new File(Absolute_Path2);
				RULE_FILE = file2.getName();    // <---      
				
				
			    try { Thread.sleep(400); } catch (InterruptedException e1) { e1.printStackTrace(); }
			    
				if (RULE_FILE == null || !RULE_FILE.toLowerCase().contains("rule")) {
					label2.setText("<html><font color='red'>This is NOT a valid rule file. Please browse again!</font></html>");
				} else {
					label2.setText("<html><font color='orange'>Ready! RULE file you selected is: <b>" + RULE_FILE + "</b></font></html>");
					label3.setVisible(true);
					label3.setText("<html><font color='red'>* Final Step: Click <b>'Send'</b></font></html>");
				}
						
				System.out.println("\n=========== [ 2. browse RULE finish ] ================= ");
				System.out.println("RULE_FILE selected: " + RULE_FILE);
				System.out.println("Absolute Path2: " 		     + Absolute_Path2);
				System.out.println("=======================================================\n ");
			}
		});
		
		btnBrowse2.setFont(new Font("", Font.PLAIN, 13));
		panel.add(btnBrowse2);
		

		
		
		// =======================================================================	
		// =======================================================================	
				
		btnExit = new JButton("Exit");
		btnExit.setBounds(182, 75, 90, 31);
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0); 
			}
		});
		panel.add(btnExit);
		
		// =======================================================================	
		label2 = new JLabel("<html><font color='orange'>* Step 1: Click <b>'Browse'</b> to select PCAP or CAP file.</font></html>");
		label2.setVisible(true);
		label2.setBounds(10, 128, 500, 31);           // (int x, int y, int width, int height) 
		label2.setFont(new Font("", Font.PLAIN, 13));
		panel.add(label2);

		
		// =======================================================================
		label3 = new JLabel(" -- "); //Alert Result:   
		label3.setVisible(false);
		label3.setBounds(10, 158, 500, 31);           // (int x, int y, int width, int "height") 
		label3.setFont(new Font("", Font.PLAIN, 13));
		panel.add(label3);
		
		
		// -----------------------------------------------------------------------
		

		
		
		
		
		// ====================== SSH(), * init() * before browser ======================
		
		
		
		SSHConnectSnort ssh = new SSHConnectSnort();
		ssh.init();
		
		
		
		
		// ============================== * ====== * =======================================
		
		btnSend = new JButton("Send");  // run
		btnSend.setBounds(75, 75, 90, 31);
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// run only when entire below .... finished
								
				if (PCAP_FILE == null || RULE_FILE == null) {
					label2.setText("INVALID file! Please browse again!");
					return;
				} else {
					label2.setText("<html><font color='orange'>Processing...</font></html>");
					label2.setVisible(true);
					
					label3.setText(PCAP_FILE + " against " + RULE_FILE);
					label3.setVisible(true);
				}

				label2.setVisible(true);
				
				
				try {
					java.lang.Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				///////////////////////////				
				//       //
				///////////////////////////
				// ===================== * getAlertResult() * ==============================


				alertResult = ssh.getAlertResult(PCAP_FILE, RULE_FILE, Absolute_Path1, Absolute_Path2);  //
				

				// ===================== * ================ * ===============================

				///// display alert /////
					
				label3.setText("<html><font color='green'><b>Success!</b> Please see result at right side ....</font></html>");

				// create a JTextArea
				JTextArea textArea = new JTextArea(30, 40);
				textArea.setText(alertResult);
				textArea.setEditable(false);

				// wrap a scrollpane around it
				JScrollPane scrollPane = new JScrollPane(textArea);

				// display them in a message dialog
				//			      JOptionPane.showMessageDialog(frame, scrollPane, "Alert Result", 2);
				//			      int input = JOptionPane.showOptionDialog(null, "Hello World", "The title", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

				int input = JOptionPane.showOptionDialog(frame, scrollPane, "Alert Result for " + PCAP_FILE + " against " + RULE_FILE + " via Snort" , JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

				if (input == JOptionPane.OK_OPTION) {
					
					//////////////////////////////////				
					// when click 'OK' go back - reset
					
					SwingFrame sf = new SwingFrame();
					
					//////////////////////////////////			

				}

				// display the jframe
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}});

		panel.add(btnSend);
		
	}


	public static void main(String[] args){
		SwingFrame sf = new SwingFrame();      
	}
	
	
	
	
	
    /*
	
	alertResult = "[**] [1:527:8] BAD-TRAFFIC same SRC/DST [**]\n" + 
			"[Classification: Potentially Bad Traffic] [Priority: 2]\n" + 
			"10/21-13:48:11.139566 :: -> ff02::1:ff76:6bd6\n" + 
			"IPV6-ICMP TTL:255 TOS:0x0 ID:0 IpLen:40 DgmLen:64\n" + 
			"[Xref => http://www.cert.org/advisories/CA-1997-28.html][Xref =>\n" + 
			"http://cve.mitre.org/cgi-bin/cvename.cgi?name=1999-0016][Xref =>\n" + 
			"http://www.securityfocus.com/bid/2666]\n" + 
			"\n" + 
			"[**] [1:527:8] BAD-TRAFFIC same SRC/DST [**]\n" + 
			"[Classification: Potentially Bad Traffic] [Priority: 2]\n" + 
			"10/21-15:15:25.378277 :: -> ff02::16\n" + 
			"IPV6-ICMP TTL:1 TOS:0x0 ID:256 IpLen:40 DgmLen:76\n" + 
			"[Xref => http://www.cert.org/advisories/CA-1997-28.html]\n" + 
			"\n" + 
			"[**] [1:384:5] ICMP PING [**]\n" + 
			"[Classification: Misc activity] [Priority: 3]\n" + 
			"10/21-15:15:25.378323 192.168.192.254 -> 192.168.192.128\n" + 
			"ICMP TTL:16 TOS:0x10 ID:0 IpLen:20 DgmLen:48\n" + 
			"Type:8 Code:0 ID:13312 Seq:0 ECHO\n" + 
			"\n" + 
			"[**] [1:384:5] ICMP PING [**]\n" + 
			"[Classification: Misc activity] [Priority: 3]\n" + 
			"10/21-15:15:25.378323 192.168.192.254 -> 192.168.192.128\n" + 
			"ICMP TTL:16 TOS:0x10 ID:0 IpLen:20 DgmLen:48\n" + 
			"Type:8 Code:0 ID:13312 Seq:0 ECHO\n" + 
			"\n" + 
			"[**] [1:384:5] ICMP PING [**]\n" + 
			"[Classification: Misc activity] [Priority: 3]\n" + 
			"10/21-15:15:25.378323 192.168.192.254 -> 192.168.192.128\n" + 
			"ICMP TTL:16 TOS:0x10 ID:0 IpLen:20 DgmLen:48\n" + 
			"Type:8 Code:0 ID:13312 Seq:0 ECHO\n" + 
			"\n" + 
			"[**] [1:384:5] ICMP PING [**]\n" + 
			"[Classification: Misc activity] [Priority: 3]\n" + 
			"10/21-15:15:25.378323 192.168.192.254 -> 192.168.192.128\n" + 
			"ICMP TTL:16 TOS:0x10 ID:0 IpLen:20 DgmLen:48\n" + 
			"Type:8 Code:0 ID:13312 Seq:0 ECHO\n" + 
			"\n" + 
			"[**] [1:384:5] ICMP PING [**]\n" + 
			"[Classification: Misc activity] [Priority: 3]\n" + 
			"10/21-15:15:25.378323 192.168.192.254 -> 192.168.192.128\n" + 
			"ICMP TTL:16 TOS:0x10 ID:0 IpLen:20 DgmLen:48\n" + 
			"Type:8 Code:0 ID:13312 Seq:0 ECHO\n" + 
			"\n" + 
			"[**] [1:527:8] BAD-TRAFFIC same SRC/DST [**]\n" + 
			"[Classification: Potentially Bad Traffic] [Priority: 2]\n" + 
			"10/21-15:15:26.195015 :: -> ff02::16\n" + 
			"IPV6-ICMP TTL:1 TOS:0x0 ID:256 IpLen:40 DgmLen:76";
	
	*/

}   






// =======================================================================
//progressBar = new JProgressBar(0, 100);
//progressBar.setBounds(62, 168, 146, 31);
//progressBar.setValue(0);
//progressBar.setBorderPainted(true);
//
//
//
//
//progressBar.setStringPainted(true);
//progressBar.setVisible(true);
//
//panel.add(progressBar);
//
//
//for (int i = 0; i <= 100; i++) {
//    final int currentValue = i;
//    try {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                progressBar.setValue(currentValue);
//            }
//        });
//        java.lang.Thread.sleep(100);
//    } catch (InterruptedException e1) {
//        JOptionPane.showMessageDialog(frame, e1.getMessage());
//    }
//}
