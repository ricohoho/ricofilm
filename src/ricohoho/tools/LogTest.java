package ricohoho.tools;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
	 private static final Logger logger = LoggerFactory.getLogger(LogTest.class);

	    public static void main(String[] args) {

	        logger.debug("Hello from Logback");
	        logger.info("Info");

	        logger.debug("getNumber() : {}", getNumber());
	        String sep = File.separator ;
	        //System.out.println("File.separator = "+File.separator);
	        
	        String paramName_Valeur="HOHO/HIHI";
	        String paramNames[]=paramName_Valeur.split("/");
	        System.out.println("paramName="+paramNames[0]);
	        String pathFilm="/volume1/video/Films/2020/202004";
	        pathFilm="C:\\tempo\\nos-rico-test\\";
	        System.out.println(pathFilm.substring(pathFilm.length()-1));
			if (!pathFilm.substring(pathFilm.length()-1).equals(File.separator)) {
				System.out.println("=>["+File.separator+"]");
				pathFilm = pathFilm + File.separator;
			}
			System.out.println(pathFilm);
	    }

	    static int getNumber() {
	        return 5;
	    }

}
