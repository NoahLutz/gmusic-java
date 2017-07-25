package com.charlesnlutz.gmusic.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.net.URI;

/**
 * Created by nlutz on 7/9/17.
 */
public class Utils {

    private static Logger logger;

    public static ObjectMapper mapper = new ObjectMapper();

    /**
     * Singleton pattern for logger
     * Adds a <code>ConsoleAppender</code> on initialization
     * @return <code>Logger</code>
     */
    public static Logger getLogger() {
        if(logger == null) {
            logger = Logger.getRootLogger();
            ConsoleAppender appender = new ConsoleAppender();

            appender.setLayout(new PatternLayout("%d{HH:mm:ss.SSS} [%t] %-5p - %m %n"));
            appender.setThreshold(Level.ALL);
            appender.activateOptions();
            logger.addAppender(appender);
        }
        return logger;
    }

    /**
     * Compares to URI's in depth.
     * Checks host, path and individual URI params
     * @param uri1
     * @param uri2
     * @return true if URI's have the same host, path and params, false otherwise
     */
    public static boolean compareURI(URI uri1, URI uri2) {
        //Check if host is different
        if(!uri1.getHost().equals(uri2.getHost())) {
            return false;
        }
        //check is path is different
        if(!uri1.getPath().equals(uri2.getPath())) {
            return false;
        }

        //Get both uri query strings and split them
        String[] uri1Query = uri1.getRawQuery().split("&");
        String[] uri2Query = uri2.getRawQuery().split("&");

        //If different lenghts, return false
        if(uri1Query.length != uri2Query.length) {
            return false;
        }

        //Make sure they both have the same params
        for(String uri1Param : uri1Query) {
            boolean containsParam = false;
            for(String uri2Param : uri2Query) {
                if(uri1Param.equals(uri2Param)) {
                    containsParam = true;
                    break;
                }
            }
            if(!containsParam) {
                return false;
            }
        }
        return true;
    }
}
