package ricohoho.themoviedb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testRegex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testRegex.test();
	}
	
	public static void  test(){
		String myString="Alice 02 - De l'autre cote du miroir [ATG 2016] TF 1080p.mp4";
		Pattern p = Pattern.compile("[19|20][0-9][0-9]+");
		Matcher m = p.matcher(myString);
		while (m.find()) {
		    int n = Integer.parseInt(m.group());
		    System.out.println(n);
		    // append n to list
		}
	}
	
	public void test0(){
		String filtre = "(avi|mkv|mp4)$";
		filtre = "(.)*.(avi|mkv|mp4)";

		
		String  s = "10ème Chambre, Instants d'Audience (2004) - Raymond Depardon.mp4"; 
		s="A";
		
			Pattern p = Pattern.compile(filtre); 
			Matcher m = p.matcher(s); 
			if ( m.matches()) { 
			//if ( s.endsWith("mp4")) {
				System.out.println("===> Match");
				//System.out.println("===> Match:"+m.group(0)); 					
			} else {
				System.out.println("  ==> Not Match");
			} 
		}

	}
	
	


