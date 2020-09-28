/**
 * Handler class containing the logic for echoing results back
 * to the client. 
 *
 * @author Greg Gagne 
 */

import java.io.*;
import java.net.*;

public class ChatroomHandler 
{
	public static final int BUFFER_SIZE = 256;
	
	/**
	 * this method is invoked by a separate thread
	 */
	public void process(Socket client) throws java.io.IOException {
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			// read this request and parse it to obtain the origin host and resource.
			BufferedReader in = 
					new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out =
					new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

			String line = in.readLine();
			String host = "";
			String get = "";
			get = line.substring(0, line.indexOf("/"));
			String http = line.substring(line.indexOf("HTTP"));
			String temp = line.substring(line.indexOf("/")+1, line.indexOf("HTTP")-1);

			//if there is a resource there will be a slash in temp)
			if(temp.indexOf("/") != -1) {
				host = temp.substring(0, temp.indexOf("/"));
				temp = temp.substring(temp.indexOf("/"));
				get = get + " " + temp + " " + http;
			}
			else {
				host = temp;
				get = get + " / " + http;
			}
			//get = get + " " + line.substring(line.indexOf("HTTP"));
			//host = host.substring(0, host.indexOf("HTTP"));
			// host = host.substring(0, host.indexOf("/"));
			String hostString = "Host: " + host;
			String connectionString = "Connection: close";
			
			//Open a socket connection to the origin host. (port 80)
			
			// System.out.println(host);
			Socket originServer = new Socket(host, 80);
			//Make a HTTP 1.1 request to the origin host for the resource.
			
			BufferedReader inOriginServer = 
					new BufferedReader(new InputStreamReader(originServer.getInputStream()));
			PrintWriter outOriginServer =
					new PrintWriter(new OutputStreamWriter(originServer.getOutputStream()));
			outOriginServer.println(get);
			outOriginServer.println(hostString);
			outOriginServer.println( connectionString);
			outOriginServer.println();
			outOriginServer.flush();
			
			//Read the response from the origin host.
			//Write the response back to the requesting client.
			System.out.println("sent thing");
			String response;

			while((response = inOriginServer.readLine()) != null) {
				if (response == null) break;
				out.println(response);
			}
			
			out.println(response);
			out.println();
			out.flush();
			
			// Close the streams and socket, breaking the connection to the client
			out.close();
			in.close();
			client.close();
			outOriginServer.close();
			inOriginServer.close();
			originServer.close();
		} 
		catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
}
