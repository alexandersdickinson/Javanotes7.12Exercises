package netgame.chat;

import java.io.*;
import netgame.common.*;
import java.util.HashMap;

/**
 * This class contains just a small main class that creates a Hub
 * and starts it listening on port 37829.  This port is used
 * by the ChatRoomWindow application.  This program should be run
 * on the computer that "hosts" the chat room.  See the ChatRoomWindow
 * class for more details.  Once the server starts listening, it
 * will listen for connection requests from clients until the
 * ChatRoomServer program is terminated (for example by a 
 * Control-C).
 * <p>Note that the ChatRoom application uses a basic, generic
 * Hub, which simply forwards any message that it received from
 * a client to all connected clients (including the one that
 * sent it), wrapped in an object of type ForwardedMessage.
 */
public class ChatRoomServer extends Hub{

    private final static int PORT = 37829;
	private static HashMap<String, Integer> usernames;//contains the usernames being used in the chat and the number of duplicate names.
    
    public static void main(String[] args) {
        try {
            new ChatRoomServer(PORT);
        }
        catch (IOException e) {
            System.out.println("Can't create listening socket.  Shutting down.");
        }
    }
	
	public ChatRoomServer(int port) throws IOException{
        super(port);
	}
	
	protected void ExtraHandShake(int playerID, ObjectInputStream in, ObjectOutputStream out) throws IOException{
		
		System.out.println("Extra handshake.");//debug
		try{
			
			String username = (String)in.readObject();
			synchronized(usernames){
				if(usernames.size() == 0 || !usernames.containsKey(username)){
					usernames.put(username, 0);
				}
				else{
					usernames.put(username, usernames.get(username) + 1);
				}
			}
			username = username + "(" + usernames.get(username) + ")";
			out.writeObject(username);
			out.flush();
			
		}
		catch(Exception e){
			System.out.println("An error occurred while attempting to connect to the hub.");
			e.printStackTrace();
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
		}
		
	}
    
}