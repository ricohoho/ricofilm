package ricohoho.themoviedb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DBObject;



public class FilmRestManager {
	
	public String serveurHost="localhost";
	public String serveurPort="3000";

	
	public FilmRestManager(String serveurHost,String serveurPort) {
		this.serveurHost=serveurHost;
		this.serveurPort=serveurPort;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//1 : Appel des RESQUEST en cours pour le serveur concern� (dans laquel sont present les vid�o)
		
		String p_name = "id:125742";		
		FilmRestManager _FilmRestManager= new FilmRestManager("localhost","3000");
		
		int nbCount =  _FilmRestManager.getFilmsCount(  p_name ) ;
		System.out.println("Nb Film:"+nbCount);
		
		List<Film>  filmList = _FilmRestManager.getFilms( p_name ) ;
		System.out.println("filmList size:"+filmList.size());
		Film film = filmList.get(0);
		System.out.println("title:"+film.getOriginal_title());
		/*
		//2 : Envoi des fichiers sur le serveru davic (en SFTP)
		traitement(requestList);
		*/

	}
	
	
	/**
	 * 
	 * @param json
	 */
	public void addFilm(JSONObject json ) {
		System.out.println("addFilm Debut");
		//json.put("someKey", "someValue");    

		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
		    HttpPost request = new HttpPost("http://"+this.serveurHost+":"+this.serveurPort+"/films/add");
		    StringEntity params = new StringEntity(json.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    response = client.execute(request);
		    
		    /*Checking response */
            if (response != null) {
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
            }
		    // handle response here...
		} catch (Exception ex) {
		    // handle exception here
			ex.printStackTrace();
            System.out.println( "Cannot Estabilish Connection");
		}
		System.out.println("addFilm Fin");
	}
	
	/**
	 * Eenvoi le nd de flm qui match
	 * @param serveurHost
	 * @param serveurPort
	 * @param p_name
	 * @param p_serveur_name
	 * @return
	 */
	public int getFilmsCount(String p_name ) {
		String sURL = "http://"+this.serveurHost+":"+this.serveurPort+"/films/list?filmname="+p_name+"&infocount=O";
		System.out.println("retour sURL :"+sURL);
		int count=0;
		String sReturn= UrlManager.getUrl( sURL);
		System.out.println("retour http :"+sReturn);
		 JSONParser parser = new JSONParser();
		 Object obj;
		 try {			
			 obj = parser.parse(sReturn);		
			 JSONObject objRequest =(JSONObject)obj;
			 count = Integer.parseInt(objRequest.get("count").toString());
			 //String title = (String)objRequest.get("count");
			 System.out.println("");
			 
		 } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }		
		return count;
	}
	
	/**
	 * Eenvoi la list des flm qui match 
	 * @param serveurHost
	 * @param serveurPort
	 * @param p_name
	 * @param p_serveur_name
	 * @return
	 */
	public List<Film> getFilms(String p_name ) {

		Film film= null;
		List<Film> filmList= new ArrayList<Film>();

		String sURL = "http://"+this.serveurHost+":"+this.serveurPort+"/films/list?filmname="+p_name;
		System.out.println("retour sURL :"+sURL);
		
		String sReturn= UrlManager.getUrl( sURL);
		System.out.println("retour http :"+sReturn);
		//JSONArray ja = new JSONArray();
		
		 JSONParser parser = new JSONParser();
		 Object obj;
		 try {
			
			obj = parser.parse(sReturn);
			
			
			JSONArray films = (JSONArray) obj;
			
            GsonBuilder gsonBuilder=  new GsonBuilder();
        	gsonBuilder.setDateFormat("yyyy-MM-dd");
        	Gson gson =gsonBuilder.create();
        	
            for(int i=0; i<films.size(); i++){
                JSONObject objRequest =(JSONObject)films.get(i);// films.getJSONObject(i);
                //System.out.println("Film "+i);
                int id = Integer.parseInt(objRequest.get("id").toString());
                String original_title = (String)objRequest.get("original_title");
               	
            	String a_jason_string=objRequest.toJSONString();  
            	//filmRico.setRelease_date(release_date);
            	film = gson.fromJson(a_jason_string, Film.class);
            	filmList.add(film);

                System.out.println("----- Request("+i+")");
                System.out.println("--"+ id);
                System.out.println("--"+original_title);    
    
            }  

            
	         
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return filmList;
         
	}

}
