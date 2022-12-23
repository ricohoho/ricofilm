package ricohoho.themoviedb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testRegex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testRegex.test();
	}
	
	public static void  test(){
		String myString="Alice 02 - De l'autre cote du miroir 2021 [[23016]] TF 1080p.mp4";
		myString = "THE FRENCH DISPATCH 2021.MULTI.1080P.10BIT.BLURAY.HDLIGHT.HE-AAC.5.1.X265-FROSTIES[[542178]].mkv";
		
		// Extraction de l'annee
		Pattern p = Pattern.compile("(19)[0-9]+[0-9]|(20)[0-9]+[0-9]+");
		Matcher m = p.matcher(myString);
		System.out.println(p);
		while (m.find()) {
		    int n = Integer.parseInt(m.group());
		    System.out.println(n);
		    // append n to list
		}

		// Extraction de MovieDB iD
		p = Pattern.compile("\\[\\[[0-9]+\\]\\]");
		m = p.matcher(myString);
		System.out.println(p);
		while (m.find()) {
			String element =m.group();
		    int n = Integer.parseInt(element.replace("[","").replace("]", ""));
		    System.out.println(n);
		    // append n to list
		}
		
	}
	
	public void test0(){
		String filtre = "(avi|mkv|mp4)$";
		filtre = "(.)*.(avi|mkv|mp4)";

		
		String  s = "10eme Chambre, Instants d'Audience (2004) - Raymond Depardon.mp4";
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
	
	


