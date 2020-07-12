package ricohoho.themoviedb;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;



public class UrlManager {

	/**
	 * AppelURL avec retour JSON
	 * @param sURL
	 * @return
	 */
	public static String getUrl(String sURL) {
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
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					//System.out.println(output);
					sReturn=sReturn+output+"\n";
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();

			  }

			

		
		
		return sReturn;
	}
	
	/* Post JSON to REST !!
	 *
	 */
	public static void sendJson(String url, JSONObject json ) {

		
		HttpClient httpClient = new DefaultHttpClient();


		try {
			System.out.println("sendJson : debut");
		    HttpPost request = new HttpPost(url);
		    //StringEntity params =new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
		    StringEntity params = new StringEntity(json.toString());
		    request.addHeader("content-type", "application/json");
		    request.addHeader("Accept","application/json");
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    System.out.println("sendJson : fin");
		    // handle response here...
		}catch (Exception ex) {
		    // handle exception here
			System.out.println("Exception:"+ex);
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
		String sURL="https://www.google.fr";
		String sReturn= getUrl( sURL);
		System.out.println("------------------------------");
		System.out.println(""+sReturn);
	}
	
	
}
