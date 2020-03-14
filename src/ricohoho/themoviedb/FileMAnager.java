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

public class FileMAnager {
	
	FileMAnager() {
		//
	}

	List<Fichier> listeFichiersFilm = new ArrayList<Fichier>();
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public List<Fichier> getPAthFile(String path) { 
		System.out.println("Path="+path);
		List<Fichier> listeFichiers = new ArrayList<Fichier>();
		String filtre = "(.)*.(avi|mkv|mp4)";
		try {
			Pattern p = Pattern.compile(filtre); 
			String [] s = new File(path).list(); 
			//System.out.println("s.length="+s.length);
			
			 
			for (int i=0; i<s.length;i++) {
				
				Matcher m = p.matcher(s[i]);
				//System.out.println("matcher");
				if ( m.matches()) { 
					//System.out.println("  ==> Match");
					System.out.println("film="+s[i]);
					
					File file =new File(path+s[i]);
					double bytes=0;
					Date dateFichier=null;
					if(file.exists()){
						bytes = file.length();
						
						long fileTime;
						
						try {
							 fileTime = file.lastModified();
							 dateFichier = new Date(fileTime);
						} catch (Exception e) {
						    System.err.println("Cannot get the last modified time - " + e);
						}
					}				
					
				
					
					listeFichiers.add(new Fichier(path,s[i],bytes,dateFichier)); 
				} else {
					//System.out.println("  ==> Not Match");
				}
			}
		}  catch (Exception pse) { 
			System.out.println("Le dossier : "+ path+" n'existe pas!!");
			//pse.printStackTrace(); 		 
		} 
		//List<String> wordList = Arrays.asList(words);  
		return listeFichiers ;
	}
	
	void initFilm(String path) {
		this.listeFichiersFilm = this.getPAthFile(path);
	}
	
}
