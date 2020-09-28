/**
 * This is the separate thread that services each
 * incoming echo client request.
 *
 * @author Greg Gagne 
 */

import java.net.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.*;
import java.io.*;

public class ChatroomConnection implements Runnable
{
	private Socket	client;
	private Map <String, Socket> socketConnections;
	private List <String> usernames = new ArrayList<String>();
	private String currentUsername;
	// private static ChatroomHandler handler = new ChatroomHandler();
	
	public ChatroomConnection(Socket client,  Map<String,Socket> socketConnections ) {
		this.client = client;
		this.socketConnections = socketConnections;
	}

    /**
     * This method runs in a separate thread.
     */	
	public void run() { 
		try {
			 BufferedReader in = null;
			 PrintWriter out = null; 
			 BufferedOutputStream dataOut = null;

				try {
		            // read what the client sent
		            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		            out = new PrintWriter(client.getOutputStream());
		            dataOut = new BufferedOutputStream(client.getOutputStream());
		            
		            String line;
		            while((line = in.readLine()) != null) {
		            	//System.out.println("inside while loop");
		            	//System.out.println("line " + line);
		            	if(line.length() > 0) {
		            		if(line.contains("JOIN")) {
		            			String header = line;
		            			String[] headerArray = header.split("\\|");
		            			String username = headerArray[1];
		            			
		            			//check that username is valid and then send stat and join message to respective people
		            			if(this.usernames.isEmpty() && !this.socketConnections.containsKey(username)) {
		            				this.usernames.add(username);
			            			this.currentUsername = username;
			            			this.socketConnections.put(username, client);
			            			//send stat message for now just send okay
			            			out.println("STAT|200");
			            			out.flush();
			            			//send join message to all clients
			            			for(Map.Entry<String,Socket> entry : socketConnections.entrySet()) {
			            				Socket c = entry.getValue();
			            				PrintWriter tempOut = new PrintWriter(c.getOutputStream());
			            				tempOut.println("JOIN|" + currentUsername + "|all|"+ Instant.now());
			            				tempOut.flush();
			            				//System.out.println("ended client for loop");
			            			}
		            			}
		            			else {
		            				out.println("STAT|420");
				            		out.flush();	
			            		}
		            		}
		            		else if(line.contains("BDMG")) {
		            			String header = line;
		            			String message = in.readLine();
		            			//send stat message
		            			out.println("STAT|200");
		            			out.flush();
		            			//send bdmg to all clients
		            			for(Map.Entry<String,Socket> entry : socketConnections.entrySet()) {
		            				Socket c = entry.getValue();
		            				PrintWriter tempOut = new PrintWriter(c.getOutputStream());
		            				tempOut.println("BDMG|" + currentUsername + "|all|"+ Instant.now());
		            				tempOut.println(message);
		            				tempOut.flush();
		            			}
		            		}
		            		else if(line.contains("PVMG")) {
		            			String header = line;
		            			String message = in.readLine();
		            			String[] headerParse = header.split("\\|");

		            			String toUser = headerParse[2];

		            			//send good stat if username is correct temp place holder for now

		            			if(socketConnections.containsKey(toUser)) {
			            			out.println("STAT|200");
			            			out.flush();
			            			//iterate over socket connections and send message to original sender and receiver 
			            			for(Map.Entry<String,Socket> entry : socketConnections.entrySet()) {
			            				Socket c = entry.getValue();
			            				String tempUser = entry.getKey();
			            				if(tempUser.equals(toUser) || tempUser.equals(currentUsername)){
				            				PrintWriter tempOut = new PrintWriter(c.getOutputStream());
				            				tempOut.println("PVMG|" + currentUsername + "|"+toUser+"|"+ Instant.now());
				            				tempOut.println(message);
				            				tempOut.flush();
			            				}
			            			}
		            			}
		            			else {
		            				out.println("STAT|421");
		            				out.flush();
		            			}
		            		}
		            		else if(line.contains("LEAV")) {
		            			String header = line;
		            			//String message = (currentUsername+ " has left the chat");
		            			//send left message to everyone
		            			for(Map.Entry<String,Socket> entry : socketConnections.entrySet()) {
		            				Socket c = entry.getValue();
		            				PrintWriter tempOut = new PrintWriter(c.getOutputStream());
		            				tempOut.println("LEAV|" + currentUsername + "|all|"+ Instant.now());
		            				//tempOut.println(message);
		            				tempOut.flush();
		            			}
		            			//actually leave the chat
		            			socketConnections.remove(currentUsername);
		            			usernames.remove(currentUsername);
		            			break;
		            		}
		            	}
		            }
		            
		   		}
				catch (IOException ioe) {
					System.err.println(ioe);
				}
				finally {
					// close streams and socket
		            System.out.println("closing socket");
		            out.close();
		            dataOut.close();
		            in.close();
		            client.close();
				}
		}
		catch (java.io.IOException ioe) {
			System.err.println(ioe);
		}
	}
}

