import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.*;

//Group Router Class
public class GroupRouter extends Server {

	volatile String message;
	String prefix;
	String PING="PING";
	HashMap<String,Integer[]> chatServers= new HashMap<String, Integer[]>();
	int numberClients;
	String ACPT= "ACPT";
	String LEFT= "LEFT";
	String FWRD= "FWRD";
	String IDNT= "IDNT";
	String DENY= "DENY";
	String HELO= "HELO";
	String NULL= "NULL \n";
	private ArrayList<Socket> sockArray=new ArrayList<Socket>();
	
	@Override
	public String read(Socket readSock) throws UnsupportedEncodingException, IOException {
		// Wait for client connection, respond appropriately to message/request
		String csAddress;
		Integer[] keyArray=new Integer[2];
		InputStream stream = readSock.getInputStream();
		Scanner scan = new Scanner(stream, "US-ASCII");
		//read and parse message for prefix
		while (scan.hasNextLine()) {
			message = scan.nextLine();
			prefix=message.substring(0,4);
			//Keep track of all sockets connecting Chat Servers and Group Routers
			if(!sockArray.contains(readSock) && !prefix.equals(IDNT)) {
				sockArray.add(readSock);
			}
			//Ping indicates Chat Server is attempting to connect 
			if(prefix.equals(PING)) {
				csAddress=readSock.getInetAddress().getHostAddress().toString();
				String[] tokens = message.split("\\s+");
				//Initial number of clients on a Chat Server is 0
				int initialNumClients=0;
				keyArray[0]= Integer.parseInt(tokens[1]);
				keyArray[1]=initialNumClients;
				//Store information about Chat Servers on hash map
				chatServers.put(csAddress, keyArray);
				return NULL;
			}
			//IDNT indicates client is trying to connect to a Chat Server
			if(prefix.equals(IDNT)) {
				Set<String> chatIPs;	
				chatIPs=chatServers.keySet();
				int min=0;
				String[] chatServer=new String[2];
				//Loop will make sure client is placed on Chat Server with least amount of clients
				//Also makes sure max clients on each Chat Server is 10
				while(min<10) {
					for(String key: chatIPs) {
						numberClients=chatServers.get(key)[1];
						if (numberClients==min) {
							chatServer[0]=key; 
							chatServer[1]=chatServers.get(key)[0].toString();
							Integer[] temp = chatServers.get(key);
							numberClients++;
							temp[1] = numberClients;
							chatServers.replace(key, temp);
							//Sends message with info for Chat Server to connect to
							String messageTo= new String(ACPT + " " + chatServer[0] + " " + chatServer[1] + " " + System.getProperty("line.separator"));
							return messageTo;
						}
					}
					min++;
				}
				//DENY sent if all Chat Servers at max client capacity
				String messageTo=new String(DENY + " " + System.getProperty("line.separator"));
				return messageTo;
			}
			//Lets all Chat Servers/Clients know when user has left the chat
			if(prefix.equals(LEFT)) {
				numberClients--;
				message += " " + System.getProperty("line.separator");
				for(Socket sock:sockArray){
					this.write(sock, message);
				}
				return NULL;
			}
			//Lets all Chat Servers/Clients know when user has entered chat
			if(prefix.equals(HELO)) {
				message += " " + System.getProperty("line.separator");
				for(Socket sock:sockArray){
					this.write(sock, message);
				}
				return NULL;
			}
			//FWRD message needs to be sent to all Chat Servers/clients
			if(prefix.equals(FWRD)) {
				message += " " + System.getProperty("line.separator");
				for(Socket sock:sockArray){
					this.write(sock, message);
				}
			}
			//Null messages are handled 
			if(prefix.equals(NULL)) {
				return NULL;
			}
		}
		return NULL;
	}
		@Override
		//Write appropriate response for messages received
		public void write(Socket writeSock, String message) throws IOException {
			writeSock.getOutputStream().write(message.getBytes("US-ASCII"),0,message.length());	
		}

		public static void main(String[] args) throws IOException {
			GroupRouter gr=new GroupRouter();
			int port = Integer.parseInt(args[1]);
			String ipAddress= new String(args[0]);
			gr.listenConnect(ipAddress, 5, port);
		}

	}
