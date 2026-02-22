package ricohoho.tools;
//2024/10/27 Correction  Git

import com.jcraft.jsch.*;

import ricohoho.themoviedb.RequestManager;

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

import org.slf4j.LoggerFactory;

public class FileTools {
	
	static {
		JSch.setLogger(new com.jcraft.jsch.Logger() {
			public boolean isEnabled(int level) {
				return true;
			}
			public void log(int level, String message) {
				LoggerFactory.getLogger(FileTools.class).info("JSCH-LOG: " + message);
			}
		});
	}

	//https://stackoverflow.com/questions/2405885/run-a-command-over-ssh-with-jsch
	
	String SFTPHOST = "host:IP";
    int SFTPPORT = 4322;
    String SFTPUSER = "username";
    String SFTPPASS = "password";
    String SFTPWORKINGDIR = "file/to/transfer";
    String CERTIFICATE_PATH = null;
    
    org.slf4j.Logger logger = null;
    
    /**
     * 
     * param SFTPHOST
     * param SFTPPORT
     * param SFTPUSER
     * param SFTPPASS
     * param SFTPWORKINGDIR
     */
    public FileTools(String _SFTPHOST, int _SFTPPORT ,String _SFTPUSER ,String _SFTPPASS ,String _SFTPWORKINGDIR ) {
    	logger = LoggerFactory.getLogger(FileTools.class);		
    	SFTPHOST = _SFTPHOST;
    	SFTPPORT =_SFTPPORT;
    	SFTPUSER =_SFTPUSER ;
    	SFTPPASS = _SFTPPASS ;
    	SFTPWORKINGDIR = _SFTPWORKINGDIR; 
    }

    public FileTools(String _SFTPHOST, int _SFTPPORT ,String _SFTPUSER ,String _SFTPWORKINGDIR, String _CERTIFICATE_PATH, boolean isCertif ) {
    	logger = LoggerFactory.getLogger(FileTools.class);	
			
    	SFTPHOST = _SFTPHOST;
    	SFTPPORT =_SFTPPORT;
    	SFTPUSER =_SFTPUSER ;
    	SFTPWORKINGDIR = _SFTPWORKINGDIR; 
    	if (isCertif) {
    	    CERTIFICATE_PATH = _CERTIFICATE_PATH;
    	}
		logger.info("FileTools SFTPHOST="+SFTPHOST);	
		logger.info("FileTools SFTPPORT="+SFTPPORT);	
		logger.info("FileTools SFTPUSER="+SFTPUSER);	
		logger.info("FileTools SFTPWORKINGDIR="+SFTPWORKINGDIR);	
		logger.info("FileTools CERTIFICATE_PATH="+CERTIFICATE_PATH);	
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
			fileTools.logger.error("{}",e);
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
			logger.info("fileName="+fileName);
			
			//1 recupperaion de la date local
			File file = new File(fileName);
			nomFile = file.getName();
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			FileTime ft = attr.creationTime();
			Date dateFichier = new Date(file.lastModified());
			//System.out.println("creationTime: " + ft);	
			Format formatter = new SimpleDateFormat("yyyyMMddHHmm");
			String sDateFormat = formatter.format(dateFichier);
			logger.info("creationTime: " + sDateFormat);
			
			
	
			//2 copie du fichier vers le serveur distant
			this.sftp( fileName);
			
			
			//3 Modification de la date du fichier distant
			String command=" touch -t "+sDateFormat+" "+SFTPWORKINGDIR+"/"+nomFile;
			logger.info("command="+command);
			this.execSSH(fileName, command);
			
		} catch (Exception  ex) {
			logger.error("sendAvecConservationDate Excepiton "+ex);
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
	    logger.info("---- sftp  debut");

	    try {
	        JSch jsch = new JSch();
	        if (CERTIFICATE_PATH != null && !CERTIFICATE_PATH.isEmpty()) {
				logger.info("---- CERTIFICATE_PATH : "+CERTIFICATE_PATH);
	            jsch.addIdentity(CERTIFICATE_PATH);
	        }
			logger.info("---- SFTPUSER : "+SFTPUSER);
			logger.info("---- SFTPHOST : "+SFTPHOST);
			logger.info("---- SFTPPORT : "+SFTPPORT);	
	        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
	        if (CERTIFICATE_PATH == null || CERTIFICATE_PATH.isEmpty()) {
				logger.info("---- CERTIFICATE_PATH est null");
	            session.setPassword(SFTPPASS);
	        }
	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        logger.info("Host connected.");
	        channel = session.openChannel("sftp");
	        channel.connect();
	        logger.info("sftp channel opened and connected.");
	        channelSftp = (ChannelSftp) channel;
	        channelSftp.cd(SFTPWORKINGDIR);
			logger.info("sftp channel cd : "+SFTPWORKINGDIR);
	        File f = new File(fileName);
			logger.info("sftp channel new File file : "+fileName);
			
			// Verification si le fichier existe et a la bonne taille
			boolean needUpload = true;
			try {
			    SftpATTRS attrs = channelSftp.lstat(f.getName());
			    if (attrs != null) {
			        long remoteSize = attrs.getSize();
			        long localSize = f.length();
			        if (remoteSize == localSize) {
			            logger.info("====> Le fichier " + f.getName() + " existe deja sur le serveur avec la meme taille (" + localSize + " octets). Ignoré !");
			            needUpload = false;
			        } else {
			             logger.info("Le fichier existe mais de taille differente (local: " + localSize + ", distant: " + remoteSize + "). Remplacement.");
			        }
			    }
			} catch (SftpException e) {
			    if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
			        logger.info("Le fichier n'existe pas encore sur le serveur.");
			    } else {
			        logger.warn("Impossible de verifier la taille du fichier distant : " + e.getMessage());
			    }
			}
			
			if (needUpload) {
			    channelSftp.put(new FileInputStream(f), f.getName());
			    logger.info("---- sftp transfert effectue");
			}
			
	        logger.info("---- sftp  Fin");
	    } catch (Exception ex) {
	    	logger.error("Exception found while tranfer the response."+ex);
	        throw ex;
	    } finally {
	        if (channelSftp != null) {
	            channelSftp.exit();
	            logger.debug("sftp Channel exited.");
	        }
	        if (channel != null) {
	            channel.disconnect();
	            logger.debug("Channel disconnected.");
	        }
	        if (session != null) {
	            session.disconnect();
	            logger.debug("Host Session disconnected.");
	        }
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
	    logger.info("---- execSSH  debut");

	    try {
	        JSch jsch = new JSch();
	        if (CERTIFICATE_PATH != null && !CERTIFICATE_PATH.isEmpty()) {
	            jsch.addIdentity(CERTIFICATE_PATH);
	        }
	        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
	        if (CERTIFICATE_PATH == null || CERTIFICATE_PATH.isEmpty()) {
	            session.setPassword(SFTPPASS);
	        }
	        java.util.Properties config = new java.util.Properties();
			logger.info("---- StrictHostKeyChecking : "+config.getProperty("StrictHostKeyChecking"));
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        logger.info("Host connected.");
	        
	        
	        
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
	            logger.debug(new String(tmp, 0, i));
	          }
	          if(channel.isClosed()){
	            if(in.available()>0) continue; 
	            logger.debug("exit-status: "+channel.getExitStatus());
	            break;
	          }
	          try{Thread.sleep(1000);}catch(Exception ee){}
	        }
	        //channel.disconnect();
	        //session.disconnect();
	        logger.info("---- execSSH fin");
	        
	    } catch (Exception ex) {
	        logger.error("Exception found while tranfer the response."+ex);
	        throw ex;
	    } finally {
	        //if (channelSftp != null) channelSftp.exit();
	        logger.debug("sftp Channel exited.");
	        if (channel != null) {
	            channel.disconnect();
	            logger.debug("Channel disconnected.");
	        }
	        if (session != null) {
	            session.disconnect();
	            logger.debug("Host Session disconnected.");
	        }
	    }
	}   
	
	
	
}
