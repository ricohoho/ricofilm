package ricohoho.themoviedb;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import ricohoho.tools.ReadPropertiesFile;

public class RicoFilm {

	public static void main(String[] args) {
		System.out.println("Extraction Information fichier films");
		
		
		//java.util.Properties config = new java.util.Properties(); 
		//config.put("StrictHostKeyChecking", "no");
		
		
		String pathFilm = "DEFAUT_PATH";
		List<String> listeFilm=null;
		boolean addDb=false;
		boolean downloadImagePoster = false;
		String serveurName="DEFAUT_SERVEUR_NAME";
		
		Properties prop=new Properties();
		try {
			prop = ReadPropertiesFile.readPropertiesFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TheMovieDb theMovieDb = new TheMovieDb();
		if (args.length >0) {
			pathFilm = args[0];
			serveurName=args[1];
		} else {
			
			addDb=true;
			
			
			/*
			//Config test 1
			serveurName ="POR80090940"; //NOS-RICO et DAVIC.MKDH.FR
			pathFilm = "C:\\tempo\\test\\";			
						
			// Config test 2
			serveurName ="NOS-RICO";
			pathFilm = "C:\\tempo\\nos-rico-test\\";
			//pathFilm = "\\\\NOS-RICO\\video\\Films\\2019\\201904\\";
			*/
			
			serveurName=prop.getProperty("SERVEUR_NAME");
			pathFilm=prop.getProperty("PATH_FILM");
			
		}
		
		downloadImagePoster=true;
		
		
		LogText logText = new LogText(pathFilm,"log.txt");
		logText.writeToFile("----------------------"+"\t"+new Date().toString()+"\t");
		
		theMovieDb.traiteDossierFilm(serveurName,pathFilm, addDb, downloadImagePoster);
		 
		 System.out.println("Fin :-)");

	}

}
