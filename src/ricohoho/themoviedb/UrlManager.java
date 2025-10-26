package ricohoho.themoviedb;
//2024/10/24 Correction bloccage Git

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class UrlManager {

	/**
	 * AppelURL avec retour JSON 
	 * @param sURL
	 * @return
	 */
	public static String getUrl(String sURL) {
		Logger logger = LoggerFactory.getLogger(UrlManager.class);
		logger.debug( "getUrl : debut"); 
		logger.debug( "getUrl : sURL:"+sURL);
		String sReturn = "";						
			  try {

				URL url = new URL(sURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream()),"UTF8"));

				String output;
				logger.debug("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					//System.out.println(output);
					sReturn=sReturn+output+"\n";
				}
				conn.disconnect();
			  } catch (MalformedURLException e) {
				e.printStackTrace();
				logger.error("{}",e);
			  } catch (IOException e) {
				e.printStackTrace();
				logger.error("{}",e);
			  }

		return sReturn;
	}
	
	/* Post JSON to REST !!
	 *
	 */
	public static void sendJson(String url, JSONObject json ) {

		Logger logger = LoggerFactory.getLogger(UrlManager.class);
		logger.debug( "sendJson : debut"); 
		HttpClient httpClient = new DefaultHttpClient();


		try {			
		    HttpPost request = new HttpPost(url);
		    //StringEntity params =new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
		    StringEntity params = new StringEntity(json.toString());
		    request.addHeader("content-type", "application/json");
		    request.addHeader("Accept","application/json");
		    request.setEntity(params);
		    //HttpResponse response = httpClient.execute(request);
		    logger.debug("sendJson : fin");
		    // handle response here...
		} catch (Exception ex) {
		    // handle exception here
			logger.error("Exception:"+ex);			
		} finally {
		    httpClient.getConnectionManager().shutdown();
		}		
	}

	/**
	 * Variante avec envoi dans le BODY
	 * @param url
	 * @param json
	 */
	public static void sendJson2(String url, JSONObject json ) {

		Logger logger = LoggerFactory.getLogger(UrlManager.class);
		logger.debug( "sendJson : debut");
		HttpClient httpClient = new DefaultHttpClient();


		try {
			HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type", "application/json");
			request.addHeader("Accept","application/json");

			StringEntity params = new StringEntity(json.toString());
			request.setEntity(params);

			HttpResponse response = httpClient.execute(request);
			logger.debug("sendJson : fin");
			// handle response here...

			// Récupération de l'entité de la réponse
			HttpEntity entity = response.getEntity();

			// Affichage du statut de la réponse
			System.out.println("Statut de la réponse : " + response.getStatusLine());

			// Affichage du contenu de la réponse
			if (entity != null) {
				System.out.println("Contenu de la réponse : " + EntityUtils.toString(entity));
			}

		} catch (Exception ex) {
			// handle exception here
			logger.error("Exception:"+ex);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 
	 * @param imageUrl
	 * @param destinationFile
	 * @throws IOException
	 */
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
	    URL url = new URL(imageUrl);
	    InputStream is = url.openStream();
	    OutputStream os = new FileOutputStream(destinationFile);

	    byte[] b = new byte[2048];
	    int length;

	    while ((length = is.read(b)) != -1) {
	        os.write(b, 0, length);
	    }
	    is.close();
	    os.close();
	}
	
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(UrlManager.class);
		logger.debug( "main : debut"); 
		String sURL="https://www.google.fr";
		String sReturn= getUrl( sURL);
		logger.info("------------------------------");
		logger.info(""+sReturn);

		new UrlManager().testSetRequest();


	}

	// test de set REQUEST de meth via WebService
	public void testSetRequest() {
		//String url = "http://davic.mkdh.fr:3000/request/edit";
		String url="http://localhost:3000/request/edit";
		String _id="";
		String username="hoho";
		int  id=871547;//"481848";
		String title="L'appel de la forêt";
		String serveur_name="NOS-RICO";
		String path="/volume1/video/Films/2020/202003/" ;
		String file="xxericxx";
		String size="5912711500.0";
		String status="AFAIRE";
		Request request = new Request(_id,username,id,title,serveur_name,path,file,size,status);
		try {
			System.out.println("JSON request ="+request.getJson());
			this.sendJson2(url, request.getJson());
		} catch (Exception ex ) {
			System.err.println("Exception ;  :"+ex);
		}

	}
}
