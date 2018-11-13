// Thread which allows servers to read and write from and to their listening sockets

import java.io.IOException;
import java.net.Socket;

public class Connection extends Thread {

	private Socket sock;
	private Server serv;
	
	public Connection(Server serv, Socket sock) {
		this.serv = serv;
		this.sock = sock;
	}

	public void run() {
		while (!sock.isClosed()) {
			try {
				String message = serv.read(sock);
				serv.write(sock, message);
				}
			catch (IOException e) {
				try {
					sock.close();
					continue;
				} catch (IOException e1) {
					System.err.println(e1);
					e1.printStackTrace();
				}
				System.err.println(e);
				e.printStackTrace();
			}

		}

	}
}
