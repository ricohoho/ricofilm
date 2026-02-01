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
	      Properties prop = new Properties();
	      try {
	         fis = new FileInputStream(fileName);
	         prop.load(fis);
	      } catch(FileNotFoundException fnfe) {
	         System.out.println("Warning: " + fileName + " not found. Using environment variables only.");
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      } finally {
	         if (fis != null) {
	            fis.close();
	         }
	      }
	      
	      // Override or add properties from Environment Variables
	      // This allows Docker/CI to set configuration like MONGODB_HOST, etc.
	      for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
	          prop.setProperty(entry.getKey(), entry.getValue());
	      }
	      
	      return prop;
	   }
	   
	   
}
