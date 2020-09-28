/**
 * An proxy server listening on port 8080. 
 *
 * This services each request in a separate thread.
 *
 * @author - Greg Gagne.
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class  ChatroomServer
{
	public static final int DEFAULT_PORT = 1337;
	//public static List <Socket> socketConnections = new ArrayList<Socket>();
	public static Map<String, Socket> connections = new HashMap<String, Socket>();

    // construct a thread pool for concurrency	
	private static final Executor exec = Executors.newCachedThreadPool();
	
	public static void main(String[] args) throws IOException {
		ServerSocket sock = null;
		//ChatroomScreen chatscreen = null;
		//Create a server that listens for connections, and stores each socket connection in an ArrayList
	
        System.out.println("Proudly serving at port 1337");
		
		try {
			// establish the socket
			sock = new ServerSocket(DEFAULT_PORT);
			//chatscreen = new ChatroomScreen();
			
			while (true) {
				/**
				 * now listen for connections
				 * and service the connection in a separate thread.
				 */
				Socket temp = sock.accept();
				//socketConnections.add(temp);
				Runnable task =  new ChatroomConnection(temp, connections);
				exec.execute(task);
			}
		}
		catch (IOException ioe) { }
		finally {
			if (sock != null)
				sock.close();
		}
	}
}
