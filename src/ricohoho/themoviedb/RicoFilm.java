package ricohoho.themoviedb;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import ricohoho.tools.LogTest;
import ricohoho.tools.ReadPropertiesFile;
import java.util.logging.Level; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.LoggerFactory;

public class RicoFilm {

	public static void main(String[] args) {		
		
		
		//Create a Logger JAVA 
		Logger logger = LoggerFactory.getLogger(RicoFilm.class);
		
		
        
        //Log messages using log(Level level, String msg) 
        logger.info( "RicoFilm Java : Lancement"); 
		
		String pathFilm = "DEFAUT_PATH";
		boolean addDb=false;
		boolean downloadImagePoster = false;
		String serveurName="DEFAUT_SERVEUR_NAME";
		int path_FILM_NIV_SSDOSSIER=0;
		String dbMongoHost="";
		int dbMongoPort=0;
		String dbMongoName="";
		String dbUSerName="";
		String dbPAssword="";
		String action="AJOUT_FILM";
		
		Properties prop=new Properties();
		try {
			prop = ReadPropertiesFile.readPropertiesFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("{}", e);
		}
		
		
		dbMongoHost=prop.getProperty("MONGODB_HOST");
		dbMongoPort	=Integer.parseInt( prop.getProperty("MONGODB_PORT"));
		dbMongoName	=prop.getProperty("MONGODB_NAME");		
		dbUSerName=prop.getProperty("MONGODB_USER");
		dbPAssword=prop.getProperty("MONGODB_PWD");
		
		TheMovieDb theMovieDb = new TheMovieDb(dbMongoHost,dbMongoPort,dbMongoName,dbUSerName,dbPAssword);
		//Determine l'action a engager !
		if (args.length >0) {
			action = args[0];
		} else {
			action ="?";//""AJOUT_FILM";
		}
		logger.info("action :["+action +"]");
		
		
		/*
		//Config test 1
		serveurName ="POR80090940"; //NOS-RICO et DAVIC.MKDH.FR
		pathFilm = "C:\\tempo\\test\\";			
					
		// Config test 2
		serveurName ="NOS-RICO";
		pathFilm = "C:\\tempo\\nos-rico-test\\";
		//pathFilm = "\\\\NOS-RICO\\video\\Films\\2019\\201904\\";
		*/	
		serveurName =prop.getProperty("SERVEUR_NAME");
		pathFilm = 	prop.getProperty("PATH_FILM");			
		path_FILM_NIV_SSDOSSIER =Integer.parseInt( prop.getProperty("PATH_FILM_NIV_SSDOSSIER"));
		String serveurHost=prop.getProperty("REQUEST_HTTP_HOST");							//"localhost";
		String serveurPort=prop.getProperty("REQUEST_HTTP_PORT");							//"3000";
		
		logger.info( "serveurName="+serveurName); 
		logger.info( "pathFilm="+pathFilm); 
		logger.info( "path_FILM_NIV_SSDOSSIER="+path_FILM_NIV_SSDOSSIER); 
		
		
		
		
		LogText logText = new LogText(pathFilm,"log.txt");
		logText.writeToFile("----------------------"+"\t"+new Date().toString()+"\t");
		
		String memoRicoFIlm="";
		if (action.equals("DOWNLOAD_IMAGE")) {
			memoRicoFIlm="NON";
			downloadImagePoster=true;
			theMovieDb.traiteDossierFilm(serveurName,pathFilm, memoRicoFIlm, downloadImagePoster,path_FILM_NIV_SSDOSSIER);	
		} else if (action.equals("AJOUT_FILM")) {
			downloadImagePoster=true;
			memoRicoFIlm="BD"; // en attendant l'implemantation https web service REST
			theMovieDb.traiteDossierFilm(serveurName,pathFilm, memoRicoFIlm, downloadImagePoster,path_FILM_NIV_SSDOSSIER);	
		} else if (action.equals("SUPPRIME_FILM")) {			
			addDb=true;
			theMovieDb.traiterDossierSupprimeFilmDBFichierAbsent(serveurName, pathFilm, addDb);
		} else if (action.equals("REQUEST_FILM")) {									
			//1 : Appel des RESQUEST en cours pour le serveur concerné (dans laquel sont present les vidéo)			
			String p_status=prop.getProperty("REQUEST_STATUS");									//"AFAIRE";
			String p_serveur_name=prop.getProperty("SERVEUR_NAME");								//"NOS-RICO";
			
			 
			List<Request>  requestList = RequestManager.getRequestFilm( serveurHost, serveurPort, p_status, p_serveur_name ) ;			
			//2 : Envoi des fichiers sur le serveru davic (en SFTP)
			RequestManager.traitement(requestList);
		} else {
			if (action.equals("?")) {
				logger.info("Il manque le paramètre action obligatoire :-( ");
			} else {
				logger.info("Le paramètre action est erroné :["+action +"]  :-( ");
			}	
			logger.info("commande à	 lancer > java -jar Ricofilm action");
			logger.info("=========================================== INFO ===================================================");
			logger.info("Les paramètres d'action possibles sont : ");
			logger.info("DOWNLOAD_IMAGE => provoque le tééchargment de l'image des fichier film dans le dossier :"+pathFilm);
			logger.info("AJOUT_FILM 	=> provoque l'ajout dans la base Ricofilms des fichiers du dossier :"+pathFilm);
			logger.info("SUPPRIME_FILM 	=> provoque la suppression dans le base des Ricofilms n'etant plus dans le dossier :"+pathFilm);
			logger.info("REQUEST_FILM 	=> intéroge la base ricofilms afin de savoir si un fichier de votre serveur a été demandé et si oui l'envoi !");
			logger.info("=========================================== PARAMETRE =============================================");
			logger.info("Pour information la configuration est dans init.properties");
			logger.info("le path du dossier est dans la  clé : pathFilm :["+pathFilm+"]");
			logger.info("Nbre de niveau niveau de ss dossier est dans la  clé : PATH_FILM_NIV_SSDOSSIER :["+path_FILM_NIV_SSDOSSIER+"]");			
			logger.info("le nom de votre serveur est dans la  clé : SERVEUR_NAME :["+serveurName+"]");
			logger.info("l'adresse du serveur + port  web Davic est dans la  clé : REQUEST_HTTP_HOST + serveurPort :["+serveurHost+"+"+serveurPort+"]");									
		}
		
		
		 
		logger.info( "Fin :-)");
		 

	}

}
