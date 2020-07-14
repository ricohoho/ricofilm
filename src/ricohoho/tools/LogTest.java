package ricohoho.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
	 private static final Logger logger = LoggerFactory.getLogger(LogTest.class);

	    public static void main(String[] args) {

	        logger.debug("Hello from Logback");
	        logger.info("Info");

	        logger.debug("getNumber() : {}", getNumber());

	    }

	    static int getNumber() {
	        return 5;
	    }

}
