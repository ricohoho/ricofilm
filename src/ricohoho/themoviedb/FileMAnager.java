package ricohoho.themoviedb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMAnager {
	
	FileMAnager() {
		//
	}

	List<Fichier> listeFichiersFilm = new ArrayList<Fichier>();
	List<String> listeSsDossier = new ArrayList<String>();
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public List<Fichier> getPAthFile(String path) { 
		Logger logger = LoggerFactory.getLogger(FileMAnager.class);
		logger.debug( "getPAthFile : debut"); 
		logger.info("Path="+path);
		List<Fichier> listeFichiers = new ArrayList<Fichier>();
		String filtre = "(.)*.(avi|mkv|mp4)";
		try {
			Pattern p = Pattern.compile(filtre); 
			//String [] s = new File(path).list(); 
			File [] s = new File(path).listFiles();
			//System.out.println("s.length="+s.length);
			
			 
			for (int i=0; i<s.length;i++) {
				
				if (s[i].isFile()) {
				
				
					Matcher m = p.matcher(s[i].getName());
					//System.out.println("matcher");
					if ( m.matches()) { 
						//System.out.println("  ==> Match");
						logger.info("film="+s[i]);
						
						//File file =new File(path+s[i]);
						double bytes=0;
						Date dateFichier=null;
						if(s[i].exists()){
							bytes = s[i].length();
							
							long fileTime;
							
							try {
								 fileTime = s[i].lastModified();
								 dateFichier = new Date(fileTime);
							} catch (Exception e) {
							    System.err.println("Cannot get the last modified time - " + e);
							}
						}				
						
					
						
						listeFichiers.add(new Fichier(path,s[i].getName(),bytes,dateFichier)); 
					} else {
						//System.out.println("  ==> Not Match");
					}
				} else {
					listeSsDossier.add(s[i].getAbsolutePath());
				}
			}
		}  catch (Exception pse) { 
			logger.info("Le dossier : "+ path+" n'existe pas!!");
			//pse.printStackTrace(); 		 
		} 
		//List<String> wordList = Arrays.asList(words);  
		return listeFichiers ;
	}
	
	void initFilm(String path) {
		this.listeFichiersFilm = this.getPAthFile(path);
	}
	
}
