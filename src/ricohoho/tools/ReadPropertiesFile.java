package ricohoho.tools;
//2024/10/27 Correction  Git
import java.io.*;
import java.util.*;

public class ReadPropertiesFile {
	
	static String fileName="init.properties";

	 public static void main(String args[]) throws IOException {
	      Properties prop = readPropertiesFile();
	      //System.out.println("SFTPHOST: "+ prop.getProperty("SFTPHOST"));
	      //System.out.println("SFTPPORT: "+ prop.getProperty("SFTPPORT"));
	   }
	 /**
	  * 
	  * @param fileName
	  * @return
	  * @throws IOException
	  */
	   public static Properties readPropertiesFile() throws IOException {
	      FileInputStream fis = null;
	      Properties prop = null;
	      try {
	         fis = new FileInputStream(fileName);
	         prop = new Properties();
	         prop.load(fis);
	      } catch(FileNotFoundException fnfe) {
	         fnfe.printStackTrace();
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      } finally {
	         fis.close();
	      }
	      return prop;
	   }
	   
	   
}
