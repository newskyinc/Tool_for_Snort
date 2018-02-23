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
 *  
 * 1. remotely read and download PCAP file via SSH - SFTP
 * 2. remotely run snort command for PCAP against snort engine rules
 * 
 * run snort: snort -r [PACKET] -c [RULE]
 * 
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

	public static void main(String args[]) throws JSchException, FileNotFoundException {

		init();

		getAlertResult("11", "22", "User/Downlaod");
		
	}

	public static String getAlertResult(String PCAP_FILE, String RULE_FILE, String Abosult_Path) {

		////////////////////////////////
		//// step 1: user update pcap //
		////////////////////////////////

		uploadLocalFile(PCAP_FILE,  "/home/logs", Abosult_Path);		 						//	uploadLocalFile(RULE_FILE,  "/home/logs"); 



		//////////////////////////////////
		// step 2: run pcap against rule /
		//////////////////////////////////

		sendCommand("sudo ls");
		
		sendCommand("sudp cd /home/logs;"
				  + "sudo ls -l;"
						
//				+ "sudo -i;"	  
//				+ "cd ../home/logs"

				  + "sudo snort -r " + PCAP_FILE + " -c " + RULE_FILE + ";"
				  + "sudo ls;");

		////////////////////////////////////
		// step 3: server reads alert file /
		////////////////////////////////////

		sendCommand("file /var/log/snort/alert");
		readAndDetectRemoteFile("/var/log/snort/alert");


		///////////////////////////////////
		// step 4: user gets final result /
		///////////////////////////////////

		System.out.println(" --- Alert result --- "
				+ "\n for " + PCAP_FILE + " against " + RULE_FILE
				+ ": " + (hited ? "hited" : "not hited"));
		
		
		System.out.println("\n alert str: -----> return \n " + alert);
		
		return alert;

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

	/* read and detect */
	private static void readAndDetectRemoteFile(String file) {
		System.out.println("\n --- read remote SSH file: " + file + " ---");
		try {
			InputStream out = null;
			out = sftpChannel.get(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(out));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				
				alert += (line + "\n");
				
				if (line.contains("detect")) {
					hited = true;
				}
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
		System.out.println("\n --- run command --- ");
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








