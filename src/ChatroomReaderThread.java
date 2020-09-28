import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatroomReaderThread implements Runnable
{
	Socket server;
	BufferedReader fromServer;
	public ChatroomReaderThread(Socket server) {
		this.server = server;
	}
    public void run() {
        try {
        	fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
			while (true) {

				String line = fromServer.readLine();
				if(line != null) {
					if(line.contains("JOIN")) {
						String[] header = line.split("\\|");
						String username = header[1];
						System.out.println(username + " has joined the chat");	
					}
					if(line.contains("BDMG")) {
						String[] header = line.split("\\|");
						String username = header[1];
						System.out.println(username + ": " +fromServer.readLine());
					}
					if(line.contains("PVMG")) {
						String[] header = line.split("\\|");
						String username = header[1];
						System.out.println(username + " (private message): " + fromServer.readLine());
					}
					if(line.contains("LEAV")) {
						String[] header = line.split("\\|");
						String username = header[1];
						System.out.println(username + " has left the chat");
						//break;
					}
					if(line.contains("STAT|421")) {
						System.out.println("Private message not sent invalid username");
					}
				}
			}
        }
        catch (java.io.IOException ioe) { }
    }
}
