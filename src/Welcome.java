import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.event.KeyEvent;


//this GUI will be displayed initially to the client so they can
//input their user name and the group name and password of the major group chat they are trying to enter
public class Welcome extends Thread{
	JFrame frame;
	JTextField usernameInput;
	JTextField groupnameInput;
	JTextField passwordInput;
	String[] info = new String[3];
	volatile Boolean finished;


	//takes in a boolean that will be used to know when the client has finished inputing the information 
	public Welcome(boolean isDone) throws InterruptedException {
		finished = isDone;
		frame = new JFrame();
		frame.setSize(800, 600);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setBackground(Color.BLACK);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getGUI();
		//button used to listen for the enter key
		Button welcomeButton = new Button(usernameInput, groupnameInput, passwordInput);
		frame.setVisible(true);
	}
	
	//creates the GUI, mostly done with a eclipse accessory and is just making the GUI
	//look engaging and providing three text fields that users will input the various
	//information we are looking to get from the client
	public void getGUI() {
		JLabel lblWelcomeToCchat = new JLabel("Welcome to CChat!");
		lblWelcomeToCchat.setVisible(true);
		lblWelcomeToCchat.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcomeToCchat.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 30));
		lblWelcomeToCchat.setForeground(Color.YELLOW);
		lblWelcomeToCchat.setBounds(184, 16, 367, 54);
		frame.getContentPane().add(lblWelcomeToCchat);
		
		JLabel lblNewLabel_1 = new JLabel("Name of the Group Chat:");
		lblNewLabel_1.setVisible(true);
		lblNewLabel_1.setForeground(Color.YELLOW);
		lblNewLabel_1.setBackground(Color.WHITE);
		lblNewLabel_1.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 22));
		lblNewLabel_1.setBounds(41, 194, 331, 39);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Password for the Group Chat:");
		lblNewLabel_2.setVisible(true);
		lblNewLabel_2.setForeground(Color.YELLOW);
		lblNewLabel_2.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 22));
		lblNewLabel_2.setBounds(41, 287, 343, 39);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblIdentificationForGroup = new JLabel("Identification for Group Chat:");
		lblIdentificationForGroup.setVisible(true);
		lblIdentificationForGroup.setForeground(Color.YELLOW);
		lblIdentificationForGroup.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 22));
		lblIdentificationForGroup.setBounds(41, 98, 331, 39);
		frame.getContentPane().add(lblIdentificationForGroup);
		
		usernameInput = new JTextField();
		usernameInput.setVisible(true);
		usernameInput.setFont(new Font("DejaVu Sans Condensed", Font.PLAIN, 20));
		usernameInput.setForeground(new Color(0, 0, 0));
		usernameInput.setBounds(399, 101, 257, 39);
		usernameInput.setColumns(10);
		frame.getContentPane().add(usernameInput);
		
		groupnameInput = new JTextField();
		groupnameInput.setVisible(true);
		groupnameInput.setFont(new Font("DejaVu Sans Condensed", Font.PLAIN, 20));
		groupnameInput.setForeground(Color.BLACK);
		groupnameInput.setColumns(10);
		groupnameInput.setBounds(399, 196, 257, 39);
		frame.getContentPane().add(groupnameInput);
		
		passwordInput = new JTextField();
		passwordInput.setVisible(true);
		passwordInput.setFont(new Font("DejaVu Sans Condensed", Font.PLAIN, 20));
		passwordInput.setForeground(Color.BLACK);
		passwordInput.setColumns(10);
		passwordInput.setBounds(399, 290, 257, 39);
		frame.getContentPane().add(passwordInput);

		
		//if the user presses the continue button we want to update the information into 
		//an array that our client file can get later to send to the central server
		JButton btnContinue = new JButton("CONTINUE");
		btnContinue.setBackground(Color.YELLOW);
		btnContinue.setForeground(Color.BLACK);
		btnContinue.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 18));
		btnContinue.setBounds(294, 378, 139, 54);
		frame.getContentPane().add(btnContinue);
		btnContinue.addActionListener(new ActionListener() {
			private WrongInfo usernameError;
			private WrongInfo groupnameError;
			private WrongInfo passwordError;

			public void actionPerformed(ActionEvent e) {
				//break up the user to make sure they only put a single word for their user name
				String[] tokens = null;	
				tokens = usernameInput.getText().split("\\s+");
				
				//check that they inputed something for this text box
				if (usernameInput.getText().isEmpty()) {
					usernameError = new WrongInfo("No username was inputed");
				}
				else if(tokens.length > 1) {
					WrongInfo username = new WrongInfo("You can only have a single word for your username.");
				}
				//check that they inputed something for this text box
				else if (groupnameInput.getText().isEmpty()) {
					groupnameError = new WrongInfo("No group name was inputed");
				}
				//check that they inputed something for this text box
				else if (passwordInput.getText().isEmpty()) {
					passwordError = new WrongInfo("No password was inputed");
				}
				//if they inputed correct things then update the info array so the client can get this information
				else {
					info[0] = usernameInput.getText();
					info[1] = groupnameInput.getText();
					info[2] = passwordInput.getText();
					//update the boolean so the client knows we are finished
					finished = true;
					frame.dispose();
				}
			}
		});
	}
	
	//return the info array that contains the text the user entered
	public String[] getInfo() {
		return info;
	}
	
	//return the boolean that will say whether the client is finished updating the GUI
	public boolean done() {
		return finished;
	}

	
	public class Button implements KeyListener{
		
		private WrongInfo usernameError;
		private WrongInfo groupnameError;
		private WrongInfo passwordError;
		private JTextField username;
		private JTextField groupname;
		private JTextField password;
		
		//listen for a button to be pressed and take what is in the text fields the user
		//can write the various inputs we require
		public Button(JTextField usernameInfo, JTextField groupnameInfo, JTextField passwordInfo) {
			usernameInfo.addKeyListener(this);
			groupnameInfo.addKeyListener(this);
			passwordInfo.addKeyListener(this);
			username = usernameInfo;
			groupname = groupnameInfo;
			password = passwordInfo;
		}
		
		
		public void keyTyped(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		//if the enter button is pressed read in the text field to get the informations from the text field
		public void keyPressed(java.awt.event.KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){

				//break up the user to make sure they only put a single word for their user name
				String[] tokens = null;	
				tokens = usernameInput.getText().split("\\s+");
				
				//check that they inputed something for this text box
				if (usernameInput.getText().isEmpty()) {
					usernameError = new WrongInfo("No username was inputed");
				}
				else if(tokens.length > 1) {
					WrongInfo username = new WrongInfo("You can only have a single word for your username.");
				}
				//check that they inputed something for this text box
				else if (groupnameInput.getText().isEmpty()) {
					groupnameError = new WrongInfo("No group name was inputed");
				}
				//check that they inputed something for this text box
				else if (passwordInput.getText().isEmpty()) {
					passwordError = new WrongInfo("No password was inputed");
				}
				//if they inputed correct things then update the info array so the client can get this information
				else {
					info[0] = usernameInput.getText();
					info[1] = groupnameInput.getText();
					info[2] = passwordInput.getText();
					//update the boolean so the client knows we are finished
					finished = true;
					frame.dispose();
				}
			
			}
			
		}

		@Override
		public void keyReleased(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
}
