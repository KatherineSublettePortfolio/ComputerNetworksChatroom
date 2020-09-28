import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;

public class ChatroomClient {
	// the default port
	public static final int PORT = 1337;
	// this could be replaced with an IP address or IP name
	public static final String host = "localhost";

	public static void main(String[] args) throws java.io.IOException {


		BufferedReader networkBin = null;	// the reader from the network
		PrintWriter networkPout = null;		// the writer to the network
		BufferedReader localBin = null;		// the reader from the local keyboard
		Socket sock = null;			// the socket
		DataOutputStream toConnection = null;
		String username = null;

		try {
			sock = new Socket(host, PORT);

			// set up the necessary communication channels
			networkBin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			localBin = new BufferedReader(new InputStreamReader(System.in));

			/**
			 * a PrintWriter allows us to use println() with ordinary
			 * socket I/O. "true" indicates automatic flushing of the stream.
			 * The stream is flushed with an invocation of println()
			 */
			networkPout = new PrintWriter(sock.getOutputStream(),true);

			/**
			 * get username
			 */
			System.out.println("Enter username");
			boolean done = false;
			username = localBin.readLine();
			String header = "JOIN|" + username + "|all|"+ Instant.now();
			networkPout.println(header);
			String response = networkBin.readLine();
			if(response.equals("STAT|420")) {
				System.out.println("Bad username disconnecting");
				networkBin.close();
				localBin.close();
				networkPout.close();
				sock.close();
			}
			
			Thread ChatroomReaderThread = new Thread(new ChatroomReaderThread(sock));
			ChatroomReaderThread.start();
			String message;
			while((message = localBin.readLine()) != null) {
				if(message.contains("|")) {
					String toUser = message.substring(0,  message.indexOf("|"));
					message = message.substring(message.indexOf("|")+2);
					header = "PVMG|"+ username + "|" +toUser+"|"+Instant.now();
					networkPout.println(header);
					networkPout.println(message);
				}
				else if(message.equals("LEAVE")) {
					header ="LEAV|"+ username + "|all|"+Instant.now();
					//send to server
					networkPout.println(header);

				}
				else {
					header = "BDMG|"+ username + "|all|"+Instant.now();
					networkPout.println(header);
					networkPout.println(message);
				}
			}
			
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		finally {
			if (networkBin != null)
				networkBin.close();
			if (localBin != null)
				localBin.close();
			if (networkPout != null)
				networkPout.close();
			if (sock != null)
				sock.close();
		}
	}
}
