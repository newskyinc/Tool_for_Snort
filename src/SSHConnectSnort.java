package server;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.*;

import org.junit.experimental.theories.Theories;

/**
 * File to connect {snort engine} and previous {IoT Halo} java code:
 * 1. remotely read and download PCAP file via SSH - SFTP
 * 2. remotely run snort command for PCAP against snort engine rules
 * run snort: snort -r [PACKET] -c [RULE]
 * @author aaron
 */
public class SSHConnectSnort	{
	private final static String user 	= "ubuntu";  						
	private final static String password = "Mi123456123qwe"; 
	private final static String host 	= "vpn3.newskysecurity.com";
	private final static int    port     = 22;
	private static JSch jsch = null;
	private static Session session = null;
	private static ChannelSftp sftpChannel = null;
	private static String env = "> ubuntu@VPN3:~$ ";
	private static Boolean hited = false;
	public static String alert = "";
	
	public SSHConnectSnort() {
		jsch = null;
		session = null;
		sftpChannel = null;
		hited = false;
		alert = "";
	}

	public static void init() {
		System.out.println("---------------------------------------");
		System.out.println("ssh -Y " + user + "@" + host);
		System.out.println("password: **************");
		System.out.println("---------------------------------------\n");
		try {
			jsch = new JSch();
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			System.out.println("Establishing Connection...");
			session.connect();
			System.out.println("Connection established.");
			System.out.println("Creating SFTP Channel.");    	        
			sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
			System.out.println("SFTP Channel created. \n");    	        
			System.out.println("---------------------------------------");
			System.out.println(env);        		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//	+ "sudo -i;"	  
	//	+ "cd ../home/logs"
	public static String getAlertResult(String PCAP_FILE, String RULE_FILE,
									   String Absolute_Path1, String Absolute_Path2) {
		////////////////////////////////
		//// step 1: upload pcap, rule /
		////////////////////////////////
		uploadLocalFile(PCAP_FILE,  "/home/logs", Absolute_Path1);		 						
		uploadLocalFile(RULE_FILE,  "/home/logs", Absolute_Path2); 
		
		//////////////////////////////////
		// step 2: delete prev alert file/
		//////////////////////////////////
		sendCommand("cd /var/log/snort/ 	 && 		sudo ls;");
		sendCommand("cd /var/log/snort/   &&    	sudo rm -rf *;");

		//////////////////////////////////
		// step 3: check [1] alert not exist
		//               [2] pacp/rule exists 
		//               [3] run snort
		//////////////////////////////////
		
		System.out.println("=========== [ before snort, alert is ] =========== ");
		
		sendCommand("file /var/log/snort/alert");
		sendCommand("file /home/logs/" + PCAP_FILE);		
		sendCommand("file /home/logs/" + RULE_FILE);		

		sendCommand("cd /var/log/snort/ 	 &&		sudo ls;");  // check alert
		System.err.println("[should be none]");
		
		sendCommand("cd /home/logs  	  	 && 		sudo ls; " 	 // check PCAP, rule 						 // sudo ls -l
				  + "sudo snort -r " + PCAP_FILE + " -c " + RULE_FILE + "; "
				  + "");
		
		// cd /home/logs && 		ls 	&& 				snort -r 7254.pcap -c 20157254.rule 
		// file /home/logs/7254.pcap
		// file /home/logs/20157254.rule
		// snort -D -r /home/logs/7254.pcap -c /home/logs/20157254.rule 
		
		////////////////////////////////////
		// step 4: after snort, alert read /
		////////////////////////////////////
		System.out.println("=========== [ after snort, alert is ] =========== ");
		sendCommand("cd /var/log/snort/ 	 &&		sudo ls;");
		System.err.println("[should be alert here]");
		
		sendCommand("file /var/log/snort/alert");
		
		// vim  /var/log/snort/alert
		//  :wq      :q
																			//		sendCommand("vim /var/log/snort/alert");
		readAndDetectRemoteFile("/var/log/snort/alert");

		///////////////////////////////////
		// step 5: print hit res by above, return alert /
		///////////////////////////////////
		System.err.println(" --- Alert result --- "
						+ "\n for [" + PCAP_FILE + " against " + RULE_FILE
						+ "]: " + (hited ? "hited" : "not hited"));		
		System.err.println("\n- alert str: -----> return to JPanel \n" + alert);
		
		return alert;		
	}


	/* read and detect */
	private static void readAndDetectRemoteFile(String file) {
		System.out.println("\n --- read remote SSH file: " + file + " ---");
		try {
			
			ReadRuleDict rd = new ReadRuleDict();
			rd.initReadMapLocal("snortplus.txt");
			
			System.err.println("Test: get cooment by key - IOTExploit.Hikvision.ACBypass :");
			System.out.println(
				rd.getCommentByRule("IOTExploit.Hikvision.ACBypass")	
			);			
			
			
			InputStream out = null;
			out = sftpChannel.get(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(out));
			String line = null;
						
			String ruleKey = null;

			while ((line = br.readLine()) != null  &&  ruleKey == null) {
				
				System.err.println(line);
				
				////////////////////////////////
				
				//  alert += (line + "\n");   //
				
				////////////////////////////////
				/**				
				[**] [1:2106:0] Huawei.Router.CVE20157254 detected !!! [**]
						[Priority: 0] 
						02/08-19:34:14.934634 172.16.1.21:55506 -> 54.149.199.67:37215
						TCP TTL:64 TOS:0x0 ID:18150 IpLen:20 DgmLen:105 DF
						***AP*** Seq: 0xCDE4D523  Ack: 0x36D4E25B  Win: 0xE42  TcpLen: 32
						TCP Options (3) => NOP NOP TS: 2266099 739467288 
				 */
				
				if (line.contains("detect")) {
					hited = true;
				}
				
				for (String key : rd.getMap().keySet()) {
					if (line.contains(key)) {
						ruleKey = key;
						break;
					}
				}
			}
			
			
			System.out.println("hited " + hited);
			System.out.println("ruleKey " + ruleKey);
			
			
			if (!hited) {
				alert = " * Final Result: NOT HIT \n"
					  + " * Rule Name: N/A \n"
					  + " * Comment from NewSky: N/A "; 
			} else {
								
				alert = " * Final Result: HIT \n"
					  + " * Rule Name: " + (ruleKey == null ? "[Hit but comment NOT included in \"snortplus.txt\" yet]" 
							  								 : ruleKey) + "\n"
					  + " * Comment from NewSky: " 
					  					+  (ruleKey == null ? "[Not available yet]" 
					  					                    	 : rd.getCommentByRule(ruleKey)); 
			}
				
			br.close();
		} catch (SftpException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void readLocalFile(String file) throws FileNotFoundException {
		System.out.println("\n --- read local file: " + file + " ---");
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}   
			bufferedReader.close(); 
		} catch (Exception e) {
			System.err.print(e);
		}
	}

	private static void downloadRemoteFile(String dir, String oldFile, String newFile) {
		System.out.println("\n --- download file: " + dir + "/" + oldFile 
				+ " > " + newFile + " ---");
		try {
			sftpChannel.cd(dir);
			byte[] buffer = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(sftpChannel.get(oldFile));            
			File f = new File(newFile);
			OutputStream os = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				System.out.println("Writing.. ");
				bos.write(buffer, 0, readCount);
			}
			bis.close();
			bos.close();
			System.out.println("downloaded!");
		} catch (SftpException | IOException e) {
			e.printStackTrace();
		}		 
	}

	private static void uploadLocalFile(String file, String dir, String Absolute_Path)  {
		System.out.println("\n --- upload file: (local)" + Absolute_Path + "  to: " + "(ubuntu)" + dir + " ---");
		try {				
			sftpChannel.cd(dir);
			File f = new File(Absolute_Path);
			sftpChannel.put(new FileInputStream(f), f.getName());
			System.out.println("upload success!");
		} catch (SftpException | FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public static String sendCommand(String command) {
		if (command.toLowerCase().contains("sudo -i")) {
			env = "> root@VPN3:~# ";
		}
		System.out.println("\n --- run --- ");  				// run command
		System.out.println(env + command);
		StringBuilder outputBuffer = new StringBuilder();
		try {
			Channel channel = session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);
			InputStream commandOutput = channel.getInputStream();
			channel.connect();
			int readByte = commandOutput.read();
			while (readByte != 0xffffffff)
			{
				outputBuffer.append((char)readByte);
				readByte = commandOutput.read();
			}
			channel.disconnect();
		}
		catch(IOException | JSchException e) {
			e.printStackTrace();
		}
		System.out.println(outputBuffer);
		return outputBuffer.toString();
	} 
}






/*	
public static void main(String args[]) throws JSchException, FileNotFoundException {
	init(); 		 												 // initial SSH	
	getAlertResult("7254.pcap", "20157254.rule", "7254.pcap", "20157254.rule");
//	getAlertResult("1111.pcap", "20157254.rule", "/Users/aaron/Downloads/1111.pcap"
//												"/Users/aaron/Downloads/20157254.rule");	
}
*/



