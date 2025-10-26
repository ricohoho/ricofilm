package ricohoho.themoviedb;
//2024/10/24 Correction bloccage Git

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
        logger.info( "RicoFilm Java : Lancement"); //
		
		String pathFilm = "DEFAUT_PATH";
		boolean addDb;
		boolean downloadImagePoster;
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
			if (args.length>1) {
				pathFilm=args[1];
			}
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
		if (pathFilm.equals("DEFAUT_PATH")) 
			pathFilm = 	prop.getProperty("PATH_FILM");			
		path_FILM_NIV_SSDOSSIER =Integer.parseInt( prop.getProperty("PATH_FILM_NIV_SSDOSSIER"));
		String serveurHost=prop.getProperty("REQUEST_HTTP_HOST");							//"localhost";
		String serveurPort=prop.getProperty("REQUEST_HTTP_PORT");							//"3000";
		
		logger.info( "serveurName="+serveurName); 
		logger.info( "pathFilm="+pathFilm); 
		logger.info( "path_FILM_NIV_SSDOSSIER="+path_FILM_NIV_SSDOSSIER); 
		
		// return int[0] => nombre total de fichier traite
		// return int[1] => nombre d'erreur
		int[] retourParse = new int[2];
		
		
		LogText logText = new LogText(pathFilm,"log.txt");
		logText.writeToFile("----------------------"+"\t"+new Date().toString()+"\t");
		
		String memoRicoFIlm="";
		if (action.equals("DOWNLOAD_IMAGE")) {
			memoRicoFIlm="NON";
			downloadImagePoster=true;
			retourParse = theMovieDb.traiteDossierFilm(serveurName,pathFilm, memoRicoFIlm, downloadImagePoster,path_FILM_NIV_SSDOSSIER);
			logText.writeToFile("----------------------"+"\t Nbre de fichier traite="+retourParse[0]+" /film pas trouve="+retourParse[1]+"/ image par dispo="+retourParse[2]+"  \t");
			logger.info("========================== RESULTATS ==========================");
			logger.info("----------------------Nbre de fichier traite="+retourParse[0]+" /film pas trouve="+retourParse[1]+"/ image par dispo="+retourParse[2]);
			logger.info("========================== RESULTATS ==========================");
		} else if (action.equals("AJOUT_FILM") || action.equals("AJOUT_FILM_SANS_IMAGE") || action.equals("MAJ_FILM") || action.equals("MAJ_FILM_SANS_IMAGE")   ) {
			if (action.equals("AJOUT_FILM") ||  action.equals("MAJ_FILM") ) 
				downloadImagePoster=true;
			else
				downloadImagePoster=false;
			
			memoRicoFIlm="BD"; // en attendant l'implemantation https web service REST
			retourParse = theMovieDb.traiteDossierFilm(serveurName,pathFilm, memoRicoFIlm, downloadImagePoster,path_FILM_NIV_SSDOSSIER);
			logText.writeToFile("----------------------"+"\t Nbre de fichier traite="+retourParse[0]+" /film pas trouve="+retourParse[1]+"/ image par dispo="+retourParse[2]+"  \t");
			logText.writeToFile("----------------------"+"\t BD : Nbre de films inseres="+retourParse[3]+" /fihcier insere="+retourParse[4]+"  \t");
			logger.info("========================== RESULTATS ==========================");
			logger.info("----------------------Nbre de fichier traite="+retourParse[0]+" /film pas trouve="+retourParse[1]+"/ image par dispo="+retourParse[2]);
			logger.info("----------------------BD : Nbre de films inseres="+retourParse[3]+" /fihcier insere="+retourParse[4]);
			logger.info("========================== RESULTATS ==========================");
			
			if ( action.equals("MAJ_FILM") || action.equals("MAJ_FILM_SANS_IMAGE") ) {
				addDb=true;
				boolean AcvecSsDossier=true;
				theMovieDb.traiterDossierSupprimeFilmDBFichierAbsent(serveurName, pathFilm, addDb,AcvecSsDossier);
			}
			//theMovieDb.closeMongoClient();
			
		} else if (action.equals("SUPPRIME_FILM")) {			
			addDb=true;
			boolean AcvecSsDossier=true;
			theMovieDb.traiterDossierSupprimeFilmDBFichierAbsent(serveurName, pathFilm, addDb,AcvecSsDossier);
		} else if (action.equals("REQUEST_FILM")) {									
			//1 : Appel des RESQUEST en cours pour le serveur concerne (dans laquel sont present les video)			
			String p_status=prop.getProperty("REQUEST_STATUS_AFAIRE");									//"AFAIRE";
			String p_serveur_name=prop.getProperty("SERVEUR_NAME");								//"NOS-RICO";						 
			List<Request>  requestList = RequestManager.getRequestFilm( serveurHost, serveurPort, p_status, p_serveur_name ) ;			
			//2 : Envoi des fichiers sur le serveru davic (en SFTP)
			String p_status_fait=prop.getProperty("REQUEST_STATUS_FAIT");									//"AFAIRE";
			RequestManager.traitement( serveurHost, serveurPort, p_status_fait,requestList);
		} else {
			if (action.equals("?")) {
				logger.info("Il manque le parametre action obligatoire :-( ");
			} else {
				logger.info("Le parametre action est errone :["+action +"]  :-( ");
			}	
			logger.info("commande a	 lancer > java -jar Ricofilm action");
			logger.info("ou 				> java -jar Ricofilm action path");
			logger.info("=========================================== INFO ===================================================");
			logger.info("Les parametres d'action possibles sont : ");
			logger.info("DOWNLOAD_IMAGE 		=> provoque le teechargment de l'image des fichier film dans le dossier :"+pathFilm);
			logger.info("AJOUT_FILM 			=> provoque l'ajout dans la base Ricofilms des fichiers du dossier :"+pathFilm);
			logger.info("AJOUT_FILM_SANS_IMAGE 	=> provoque l'ajout dans la base Ricofilms, sans l'image affiche,  des fichiers du dossier :"+pathFilm);			
			logger.info("SUPPRIME_FILM 			=> provoque la suppression dans le base des Ricofilms n'etant plus dans le dossier :"+pathFilm);
			logger.info("MAJ_FILM 				=> provoque la MAJ des films : ajout + supprime dans le base dans le dossier :"+pathFilm);
			logger.info("MAJ_FILM_SANS_IMAGE	=> provoque la MAJ des films : ajout + supprime dans le base dans le dossier :"+pathFilm);
			logger.info("REQUEST_FILM 			=> interoge la base ricofilms afin de savoir si un fichier de votre serveur a ete demande et si oui l'envoi !");
			logger.info("=========================================== PARAMETRE =============================================");
			logger.info("Pour information la configuration est dans init.properties");
			logger.info("le path du dossier est dans la  cle : pathFilm :["+pathFilm+"]");
			logger.info("Nbre de niveau niveau de ss dossier est dans la  cle : PATH_FILM_NIV_SSDOSSIER :["+path_FILM_NIV_SSDOSSIER+"]");			
			logger.info("le nom de votre serveur est dans la  cle : SERVEUR_NAME :["+serveurName+"]");
			logger.info("l'adresse du serveur + port  web Davic est dans la  cle : REQUEST_HTTP_HOST + serveurPort :["+serveurHost+"+"+serveurPort+"]");									
		}
		
		
		 
		logger.info( "Fin :-)");
		 

	}

}
