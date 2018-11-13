// Class that defines a GroupRouterID object containing a Group Router's encrypted IP address, port, and password

public class GroupRouterID {
	
	private String ipAddress;
	private String port;
	private String password;

	public GroupRouterID(String ipAddress, String port, String password) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.password = password;
	}

	public String getIPAddress() {
		return ipAddress;
	}

	public String getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}

}
