package ricohoho.themoviedb;
//2024/10/24 Correction bloccage Git

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class LogText {

	String pathFichier=null;
	String nomFichier=null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public LogText(String path,String nom ){
		this.pathFichier=path;
		this.nomFichier=nom;
	}
	
	private static final String newLine = System.getProperty("line.separator");
	
	public synchronized void writeToFile(String msg)  {
		
	    String fileName = pathFichier+ "\\" + nomFichier;
	    PrintWriter printWriter = null;
	    File file = new File(fileName);
	    try {
	        if (!file.exists()) file.createNewFile();
	        printWriter = new PrintWriter(new FileOutputStream(fileName, true));
	        printWriter.write(newLine + msg);
	    } catch (IOException ioex) {
	        //ioex.printStackTrace();
	    } finally {
	        if (printWriter != null) {
	            printWriter.flush();
	            printWriter.close();
	        }
	    }
	}
	
}
