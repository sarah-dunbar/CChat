import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.Font;
import java.util.Scanner;

import javax.swing.SwingConstants;

//import com.sun.glass.events.KeyEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.Label;
import javax.swing.JButton;


//this GUI will display the chat and text box that the client will
//see as the chat is running
public class ChatRoomGUI {
	private static JTextField textField;
	private static String chatroom;
	private static JFrame frame;
	private volatile static String messageTyped;
	private static JTextArea chat;
	private volatile static boolean left;
	
	//takes in the group name of the chat the user is connecting
	//to, this way the label can be dynamic and display a different 
	//title based upon the group and takes in the boolean that 
	//will be used to know when we have left the group chat
	public ChatRoomGUI(String groupname, boolean info) {
		left = info;
		chatroom = groupname;
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setLayout(null);
		frame.setSize(790,670);
		//update the boolean when we close the GUI so we can tell the Client to send 
		//the left message to the chat server that this client has left the group chat
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		    	left = true;
		        e.getWindow().dispose();

		    }
		});
		createGUI();
		//create a button that will listen for our text field input
		Button enterButton = new Button(textField);
		frame.setVisible(true);
	}
	
	//this builds the GUI most was done with an eclipse accessory that helped
	public void createGUI() {
		JLabel label = new JLabel("");
		label.setForeground(Color.YELLOW);
		label.setBackground(Color.BLACK);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("DejaVu Serif Condensed", Font.BOLD, 28));
		label.setBounds(15, 25, 738, 85);
		frame.getContentPane().add(label);

		label.setText(chatroom + " Major " + "Chatroom");
		
		textField = new JTextField();
		textField.setBounds(35, 491, 697, 54);
		textField.setFont(new Font("DejaVu Serif Condensed", Font.BOLD, 20));
		frame.getContentPane().add(textField);
		
		JButton btnSend = new JButton("SEND");
		btnSend.setVerticalAlignment(SwingConstants.BOTTOM);
		btnSend.setBounds(617, 561, 115, 29);
		frame.getContentPane().add(btnSend);
		
		chat = new JTextArea();
		chat.setEditable(false);
		chat.setLineWrap(true);
		chat.setFont(new Font("DejaVu Serif Condensed", Font.BOLD, 18));
		chat.setBounds(35, 102, 697, 373);
		
		JScrollPane scrollPane = new JScrollPane(chat);
		scrollPane.setSize(697, 345);
		scrollPane.setLocation(35, 111);
		frame.getContentPane().add(scrollPane);
		
		
		//listens for when the button is pressed 
		btnSend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String statement = textField.getText();
					//if the statement is not zero characters or over 250 then it can be sent
					//on as a message to the chat room, thus it updates the messageTyped with the statment
					if (statement.length() < 250 && !(statement.equals(""))) {
						messageTyped = statement;
						textField.setText("");
					}
					//if the message is no characters give an error
					else if(statement.equals("")) {
						WrongInfo characterLimit = new WrongInfo("You didn't send any text in that message.");
					}
					//if the message is too many characters give an error
					else {
						int overlimit = statement.length() - 250;
						WrongInfo characterLimit = new WrongInfo("Your text had " + Integer.toString(overlimit) + " characters over the limit.");
					}
				}
			});
		
		frame.setVisible(true);
		
	}
	
	//after the message has been sent we need to update the message with null so we don't keep sending the same
	//message
	public void editMessage() {
		messageTyped = null;
	}
	
	//if we have received a text this method will be called to display who has sent the 
	//message and what it sent
	public void addChat(String username, String message) {
		chat.append(username +": " + message + System.getProperty("line.separator"));
	}
	
	//this will be called if a user has joined the chat and will append to the text area
	public void addUser(String username) {
		chat.append(username + System.getProperty("line.separator"));
	}
	
	//if a user has left the message will append this to the text area
	public void removeUser(String username) {
		chat.append(username + System.getProperty("line.separator"));
	}
		
	//if a message has been sent it will update it and allow the client to grab it so it can send it
	public String getMessage() {
		return messageTyped;
	}
	
	//see if the we have closed the GUI and we have left
	//left will be updated if we close the GUI
	public boolean left() {
		return left;
	}
	

	public class Button implements KeyListener{
		
		//listen for a button to be pressed and take what is in the text field the user
		//can write their messages in
		public Button(JTextField text) {
			text.addKeyListener(this);
		}
		
		
		public void keyTyped(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		//if the enter button is pressed read in the text field to get the message the client wants to send
		public void keyPressed(java.awt.event.KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				String statement = textField.getText();
				//if the statement is not zero characters or over 250 then it can be sent
				//on as a message to the chat room, thus it updates the messageTyped with the statement
				if (statement.length() < 250 && !(statement.equals(""))) {
					messageTyped = statement;
					textField.setText("");
				}
				//if the message is no characters give an error
				else if(statement.equals("")) {
					WrongInfo characterLimit = new WrongInfo("You didn't send any text in that message.");
				}
				//if the message is too many characters give an error
				else {
					int overlimit = statement.length() - 250;
					WrongInfo characterLimit = new WrongInfo("Your text had " + Integer.toString(overlimit) + " characters over the limit.");
				}
			}
		}

		@Override
		public void keyReleased(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
}
