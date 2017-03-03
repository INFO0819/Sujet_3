/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tamere;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Class used for logs
 * @author Chaest
 */
public class Log {
	
	public static PrintStream log = null;
	public static final boolean ERROR = true;
	public static final boolean LOG = true;
	
	public static void log(String text){
		if(log==null){
			try{
				log = new PrintStream(System.out, true, "utf-8");
			}catch(IOException e){
				System.exit(42);
			}
		}
		if(LOG)log.print(text);
	}
	
	public static void logln(String text){log(text+"\n");}
	public static void log(int text){log(text+"");}
	public static void logln(int text){log(text+"\n");}
	public static void log(long text){log(text+"");}
	public static void logln(long text){log(text+"\n");}
	public static void log(char text){log(text+"");}
	public static void logln(char text){log(text+"\n");}
	public static void log(float text){log(text+"");}
	public static void logln(float text){log(text+"\n");}
	public static void log(double text){log(text+"");}
	public static void logln(double text){log(text+"\n");}
	public static void log(short text){log(text+"");}
	public static void logln(short text){log(text+"\n");}
	
	public static void err(String type, String text){if(ERROR)logln("[ERROR] ["+type+"] : "+text);}
	public static void err(String type, char text){err(type, text+"");}
	public static void err(String type, short text){err(type, text+"");}
	public static void err(String type, int text){err(type, text+"");}
	public static void err(String type, long text){err(type, text+"");}
	public static void err(String type, float text){err(type, text+"");}
	public static void err(String type, double text){err(type, text+"");}
	
}
