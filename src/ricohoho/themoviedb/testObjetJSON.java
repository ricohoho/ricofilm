package ricohoho.themoviedb;

import java.io.File;
import java.util.Iterator;

//import org.apache.commons.io.FileUtils;
//import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ricohoho.themoviedb.User;

public class testObjetJSON {

	/*
	{
		  "name" : { "first" : "Joe", "last" : "Sixpack" },
		  "gender" : "MALE",
		  "verified" : false,
		  "userImage" : "Rm9vYmFyIQ=="
		}
	*/
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub 
		System.out.println("testObjetJSON	");
//		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
//		System.out.println(""+User.class);
//		File ff = new File("user.json");
//		User user = mapper.eadValue(new File("user.json"), User.class);
		
		testObjetJSON.getURLJSON();
		
		
	}
	
	
	static void  getURLJSON() {
		String sURL="https://api.themoviedb.org/3/movie/603-the-matrix?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US";
		sURL = "https://api.themoviedb.org/3/search/movie?api_key=bd5b73151b4a5a2ac5b34aca8bfe555a&language=en-US&query=Matrix&page=1&include_adult=false";
		String sReturn= UrlManager.getUrl( sURL);
		System.out.println("retour http :"+sReturn);
		//JSONArray ja = new JSONArray();
		
		 JSONParser parser = new JSONParser();
		 Object obj;
		try {
			
			obj = parser.parse(sReturn);
			
			JSONObject jsonObject = (JSONObject) obj;
			System.out.println("testObjetJSONJSONObject=");
			System.out.println(jsonObject);
			
			//URL 1
			/*
			 String original_language = (String) jsonObject.get("original_language");
	         System.out.println("original_language="+original_language);
			
	         boolean  adult = (Boolean) jsonObject.get("adult");
	         System.out.println("adult="+adult);
	         */
			System.out.println("debut:1");
			long total_results = (Long) jsonObject.get("total_results");
			System.out.println("total_results="+total_results);
			
			// loop array
            JSONArray films = (JSONArray) jsonObject.get("results");
            for(int i=0; i<films.size(); i++){
                JSONObject objFilm =(JSONObject)films.get(i);// films.getJSONObject(i);
                System.out.println("Film "+i);
                long id = (Long)objFilm.get("id");
                String original_title = (String)objFilm.get("original_title");
                String title = (String)objFilm.get("title");

                System.out.println("----- Film("+i+") :"+ id);
                System.out.println(original_title);
                System.out.println(title);
                System.out.println("----- ");
            }  

            
	         
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
         
	}
	
}
