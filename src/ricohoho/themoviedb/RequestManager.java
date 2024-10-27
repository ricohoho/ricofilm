package ricohoho.themoviedb;
//2024/10/24 Correction bloccage Git

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ricohoho.tools.FileTools;
import ricohoho.tools.ReadPropertiesFile;

public class RequestManager {

	public static void main(String[] args) {
		 
		Properties prop=new Properties();
		try {
			prop = ReadPropertiesFile.readPropertiesFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//1 : Appel des RESQUEST en cours pour le serveur concerne (dans laquel sont present les video)
		String serveurHost=prop.getProperty("REQUEST_HTTP_HOST");							//"localhost";
		String serveurPort=prop.getProperty("REQUEST_HTTP_PORT");							//"3000";
		String p_status=prop.getProperty("REQUEST_STATUS_AFAIRE");									//"AFAIRE";
		String p_status_fait=prop.getProperty("REQUEST_STATUS_FAIT");									//"AFAIRE";
		String p_serveur_name=prop.getProperty("SERVEUR_NAME");								//"NOS-RICO";
		List<Request>  requestList = getRequestFilm( serveurHost, serveurPort, p_status, p_serveur_name ) ;
		
		//2 : Envoi des fichiers sur le serveru davic (en SFTP)
		traitement(serveurHost,serveurPort,p_status_fait,requestList);		

	}

	/**
	 * Reccuperation des requete en attente 'AFAIRE'
	 * exemple : http://localhost:3000/request/list?status=AFAIRE&serveur_name=NOS-RICO
	 * @param serveurHost
	 * @param serveurPort
	 * @param p_status
	 * @param p_serveur_name
	 * @return
	 */
	static List<Request> getRequestFilm(String serveurHost,String serveurPort,String p_status,String p_serveur_name ) {

		
		Logger logger = LoggerFactory.getLogger(RequestManager.class);
		logger.debug( "getRequestFilm : debut"); 
		Request request = null;
		List<Request> requestList= new ArrayList<Request>();

		String sURL = "http://"+serveurHost+":"+serveurPort+"/request/list?status="+p_status+"&serveur_name="+p_serveur_name;
		logger.info("retour sURL :"+sURL);
		
		String sReturn= UrlManager.getUrl( sURL);
		logger.info("retour http :"+sReturn);
		//JSONArray ja = new JSONArray();

		 JSONParser parser = new JSONParser();
		 Object obj;
		 try {
			
			obj = parser.parse(sReturn);
			
			
			JSONArray requests = (JSONArray) obj;
			/*
			JSONObject jsonObject =null;// (JSONObject) obj;
			//System.out.println("testObjetJSONJSONObject=");
			//System.out.println(jsonObject);
			for(int i=0; i<ja.size(); i++){
					System.out.println("i"+i);
			}
			
			long total_results = (Long) jsonObject.get("total_results");
			//System.out.println("total_results="+total_results);
			
			// loop array
            JSONArray requests = (JSONArray) jsonObject.get("results");
            */
			
            GsonBuilder gsonBuilder=  new GsonBuilder();
        	gsonBuilder.setDateFormat("yyyy-MM-dd");
        	Gson gson =gsonBuilder.create();
        	
            for(int i=0; i<requests.size(); i++){
                JSONObject objRequest =(JSONObject)requests.get(i);// films.getJSONObject(i);
                //System.out.println("Film "+i);
                //String id =(String) objRequest.get("id");
				String id = objRequest.get("id").toString();
                String username = (String)objRequest.get("username");
                String title = (String)objRequest.get("title");
                String serveur_name=(String)objRequest.get("serveur_name");
                String   path= (String)objRequest.get("path");
                String file = (String) objRequest.get("file");
                String status = (String) objRequest.get("status");
            	
                	String a_jason_string=objRequest.toJSONString();  
                	//filmRico.setRelease_date(release_date);
                	request = gson.fromJson(a_jason_string, Request.class);
                	requestList.add(request);

                	logger.info("----- Request("+i+")");
                	logger.info("--"+ id);
                	logger.info("--"+username);                
                	logger.info("--"+serveur_name);
                	logger.info("--"+path+"/"+request.path);
                	logger.info("--"+file);
                	logger.info("--"+status);
                	logger.info("----- ");
                
            }  

            
	         
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("{}",e);
		}
	/*
		if (filmList.size()>0)
			return filmList.get(0);
		else
			return null;
		*/
		return requestList;
         
	}
	
	/**
	 * Execution des tratements en fct des requet en cours :STATUS = AFAIRE
	 * @param listRequest
	 */
	static void traitement(String serveurHost,String serveurPort,String p_status,List<Request> listRequest) {
		Logger logger = LoggerFactory.getLogger(RequestManager.class);
		logger.debug( "traitement : debut"); 
		Properties prop=new Properties();
		try {
			prop = ReadPropertiesFile.readPropertiesFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//Pour chaque demande
		//LAncer le traitement 
		//1) sftp de PAth + Film (\\nos-rico\....\xxx.avi  ====> davic.mkdh.fr -> /home/ok/ressuscite/
		String SFTPHOST = prop.getProperty("CENTRAL_SFTP_HOST");							//"davic.mkdh.fr";
	    int SFTPPORT =  Integer.parseInt(prop.getProperty("CENTRAL_SFTP_PORT"));	//4322;
	    String SFTPUSER =prop.getProperty("CENTRAL_SFTP_USER");						// "ricohoho";
	    String SFTPPASS = prop.getProperty("CENTRAL_SFTP_SFTP_PASS");				// "serveur$linux";
	    String SFTPWORKINGDIR =prop.getProperty("CENTRAL_SFTP_WORKINGDIR");			// "/home/ricohoho/test";
	    String fileName="";
	    
		FileTools fileTools = new FileTools(SFTPHOST,SFTPPORT,SFTPUSER,SFTPPASS,SFTPWORKINGDIR);
		
	    for (Request request : listRequest) {		    	
	    	fileName=request.getPath()+request.getFile();
	    	fileName=fileName.replace("NOS-RICO", "192.168.0.16");
	    	
	    	
	    	try {
	    		logger.info("Traitment du fichier :"+fileName);
	    		//	fileName = "D:\\tempo\\bog-fortalezza\\1.png";
	    		fileTools.sftpAvecConservationDate(fileName);
	    	
	    	
				//2) Update REQUEST.status='FAIT'
	    		logger.info("Mise a jour du status");
	    		String url = "http://"+serveurHost+":"+serveurPort+"/request/edit";
		    	//String url="http://localhost:3000/request/edit";
		    	request.setStatus(p_status);
		    	UrlManager.sendJson( url,request.getJson());
		    	} catch (Exception ex ) {
		    		logger.error("Excepiotn tratement fichier :"+ex);
		    	}
		    }					
	    
	
		}
	
}