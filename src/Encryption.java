// Class that encrypts Group Router IP addresses, ports, and passwords and adds them to a text file

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Encryption {
	
	public static void usage() {
        System.err.println("Usage: java Encryption <group name> <group router address> <group router port> <new password>\n");
        System.exit(1);
    }
	
	// Encrypt and add password to password file
	private static void addToFile(String groupName, String ipAddress, String port, String password) throws IOException {
		Crypto crypt = new Crypto(7);
		File grFile = new File("GroupRouters.txt");
		
		if (!grFile.exists()) {
			grFile.createNewFile();
		}
		Boolean found = false;
		StringBuffer sbuf = new StringBuffer();
		Scanner scan = new Scanner(grFile);
		
		while (scan.hasNextLine()) {
			String currLine = scan.nextLine();
			String[] tokens = currLine.split(":");
			
			if (tokens[0].equals(groupName)) {
				ipAddress = crypt.encrypt(ipAddress);
				port = crypt.encrypt(port);
				password = crypt.encrypt(password);
				currLine = groupName + ":" + ipAddress + ":" + port + ":" + password;
		        found = true;
		        System.out.println("Added new encrypted password to corresponding Group Router in file.");
			}
            sbuf.append(currLine + '\n');
		}
		
		if (found == false) {
			System.err.println("ERROR: The group name you entered does not exist. "
					+ "Please add it to GroupRouters.txt with the correct IP address and port before trying again.");
			System.exit(1);
		}
		FileOutputStream fileOut = new FileOutputStream(grFile);
        fileOut.write(sbuf.toString().getBytes());
        fileOut.close();
	}
	
	public static void main(String[] args) {
		// Must have 2 arguments 
        if (args.length!=4) {
            usage();
            System.exit(1);
        }
		
		String groupName = args[0];
		String ipAddress = args[1];
		String port = args[2];
		String password = args[3];	
		
		try {
			addToFile(groupName, ipAddress, port, password);
		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

}
