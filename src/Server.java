// Abstract class that defines a method, utilized by all servers, for opening a listening socket 
// and creating a read/write thread for each new connection,
// and declares read and write methods implemented by each server subclass and called by the thread in listenConnect

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server {

	// Listen for new incoming connections 
	// and create thread for each new connection which reads from and writes to the listening socket
	public void listenConnect(String ipAddress, int allowedConnections, int port) throws IOException {
		ServerSocket srvSock;
        InetAddress serverAddress;
    
        serverAddress = InetAddress.getByName(ipAddress);
		srvSock = new ServerSocket(port, allowedConnections, serverAddress);
		
        // Read and handle connections forever
        while(true) {
			Socket listeningSock = srvSock.accept();
			Connection conn = new Connection(this, listeningSock);
			conn.start();	
        }	
	}
	
	// Read message from socket, parse, and compile new message to be sent
	public abstract String read(Socket readSock) throws UnsupportedEncodingException, IOException;
	
	// Write message to socket
	public abstract void write(Socket writeSock, String message) throws IOException;
	
}
