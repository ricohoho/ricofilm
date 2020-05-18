package ricohoho.tools;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
public class FileTools {

	//https://stackoverflow.com/questions/2405885/run-a-command-over-ssh-with-jsch
	
	String SFTPHOST = "host:IP";
    int SFTPPORT = 4322;
    String SFTPUSER = "username";
    String SFTPPASS = "password";
    String SFTPWORKINGDIR = "file/to/transfer";
    
    /**
     * 
     * @param SFTPHOST
     * @param SFTPPORT
     * @param SFTPUSER
     * @param SFTPPASS
     * @param SFTPWORKINGDIR
     */
    public FileTools(String _SFTPHOST, int _SFTPPORT ,String _SFTPUSER ,String _SFTPPASS ,String _SFTPWORKINGDIR ) {
    	SFTPHOST = _SFTPHOST;
    	SFTPPORT =_SFTPPORT;
    	SFTPUSER =_SFTPUSER ;
    	SFTPPASS = _SFTPPASS ;
    	SFTPWORKINGDIR = _SFTPWORKINGDIR; 
    }
    
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		String SFTPHOST = "davic.mkdh.fr";
	    int SFTPPORT = 4322;
	    String SFTPUSER = "ricohoho";
	    String SFTPPASS = "serveur$linux";
	    String SFTPWORKINGDIR = "/home/ricohoho/test";
		
		// TODO Auto-generated method stub
		FileTools fileTools = new FileTools(SFTPHOST,SFTPPORT,SFTPUSER,SFTPPASS,SFTPWORKINGDIR);
		String fileName = "D:\\tempo\\bog-fortalezza\\1.png";
		try {
			//fileTools.send(fileName);
			String command="ls";
			command =" touch -t 201901211111 test/A.I.Rising.2018.FRENCH.720p.WEB.H264-FRATERNiTY.jpg";
			//fileTools.exec(fileName, command);
			fileTools.sftpAvecConservationDate(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Copie dun fichier local vers un serveur SSH distant en conserant la date du fichier local
	 * @param fileName
	 * @throws Exception
	 */
	public  void sftpAvecConservationDate (String fileName) throws Exception {
		String nomFile="";
		try  {
			System.out.println("fileName="+fileName);
			
			//1 recupperaion de la date local
			File file = new File(fileName);
			nomFile = file.getName();
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			FileTime ft = attr.creationTime();
			Date dateFichier = new Date(file.lastModified());
			//System.out.println("creationTime: " + ft);	
			Format formatter = new SimpleDateFormat("yyyyMMddHHmm");
			String sDateFormat = formatter.format(dateFichier);
			System.out.println("creationTime: " + sDateFormat);
			
			
	
			//2 copie du fichier vers le serveur distant
			this.sftp( fileName);
			
			
			//3 Modification de la date du fichier distant
			String command=" touch -t "+sDateFormat+" "+SFTPWORKINGDIR+"/"+nomFile;
			System.out.println("command="+command);
			this.execSSH(fileName, command);
			
		} catch (Exception  ex) {
			System.out.println("sendAvecConservationDate Excepiton "+ex);
			throw ex;
		}
	}
	
/**
 * 
 * @param fileName : copue de fichier du local vers serveur SSH
 * https://stackoverflow.com/questions/14830146/how-to-transfer-a-file-through-sftp-in-java
 */
	public void sftp (String fileName) throws Exception {	   
		fileName = fileName.replace("\\", "/");
	    Session session = null;
	    Channel channel = null;
	    ChannelSftp channelSftp = null;
	    System.out.println("---- sftp  debut");

	    try {
	        JSch jsch = new JSch();
	        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
	        session.setPassword(SFTPPASS);
	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        System.out.println("Host connected.");
	        channel = session.openChannel("sftp");
	        channel.connect();
	        System.out.println("sftp channel opened and connected.");
	        channelSftp = (ChannelSftp) channel;
	        channelSftp.cd(SFTPWORKINGDIR);
	        File f = new File(fileName);
	        channelSftp.put(new FileInputStream(f), f.getName());
	        //Log.info("File transfered successfully to host.");
	        System.out.println("---- sftp  Fin");
	    } catch (Exception ex) {
	        System.out.println("Exception found while tranfer the response."+ex);
	        throw ex;
	    } finally {
	        channelSftp.exit();
	        System.out.println("sftp Channel exited.");
	        channel.disconnect();
	        System.out.println("Channel disconnected.");
	        session.disconnect();
	        System.out.println("Host Session disconnected.");
	    }
	}   
	
	
	
	/**
	 * Execution d'une comamnde linux a distace
	 * http://www.jcraft.com/jsch/examples/Exec.java.html
	 * @param fileName
	 * @param command
	 * @throws Exception
	 */
	public void execSSH (String fileName, String command) throws Exception {	   
		fileName = fileName.replace("\\", "/");
	    Session session = null;
	    Channel channel = null;
	    ChannelSftp channelSftp = null;
	    System.out.println("---- execSSH  debut");

	    try {
	        JSch jsch = new JSch();
	        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
	        session.setPassword(SFTPPASS);
	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        System.out.println("Host connected.");
	        
	        
	        
	        channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(command);
	        
	        channel.setInputStream(null);
	        ((ChannelExec)channel).setErrStream(System.err);

	        InputStream in=channel.getInputStream();	        
	        
	        //Autre .... FTP ---
	        //channel = session.openChannel("sftp");
	        channel.connect();

	        
	        byte[] tmp=new byte[1024];
	        while(true){
	          while(in.available()>0){
	            int i=in.read(tmp, 0, 1024);
	            if(i<0)break;
	            System.out.print(new String(tmp, 0, i));
	          }
	          if(channel.isClosed()){
	            if(in.available()>0) continue; 
	            System.out.println("exit-status: "+channel.getExitStatus());
	            break;
	          }
	          try{Thread.sleep(1000);}catch(Exception ee){}
	        }
	        //channel.disconnect();
	        //session.disconnect();
	        System.out.println("---- execSSH fin");
	        
	    } catch (Exception ex) {
	        System.out.println("Exception found while tranfer the response."+ex);
	        throw ex;
	    } finally {
	        //channelSftp.exit();
	        System.out.println("sftp Channel exited.");
	        channel.disconnect();
	        System.out.println("Channel disconnected.");
	        session.disconnect();
	        System.out.println("Host Session disconnected.");
	    }
	}   
	
	
	
}
