import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

//this class will display GUIs if something else goes wrong in
//what the client inputs and 
public class WrongInfo {
	
	JButton btnOkay;
	JFrame helpFrame; 
	
	//wrong info takes in a string that will display
	//the specific issue that occurred so the user knows what is happen in this particular case
	public WrongInfo(String comment) {
		//Constructor is mostly creating the window and the sending the method create GUI
		//with the specific command that needs to be displayed for this window at this time
		helpFrame = new JFrame();
		helpFrame.setSize(861,557);
		helpFrame.getContentPane().setBackground(Color.BLACK);
		helpFrame.getContentPane().setForeground(Color.YELLOW);
		helpFrame.getContentPane().setLayout(null);
		helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createGUI(comment);
		helpFrame.setVisible(true);
	}
	
	//in here we create a new button that states okay for the client to press once they
	//have acknowledged the JLabel which contains the error message for the specific issue
	public void createGUI(String comment) {
		
		btnOkay = new JButton("OKAY");
		btnOkay.setForeground(new Color(0, 0, 0));
		btnOkay.setBackground(Color.YELLOW);
		btnOkay.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 26));
		btnOkay.setBounds(341, 329, 148, 69);
		btnOkay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpFrame.dispose();
			}
		});
		helpFrame.getContentPane().add(btnOkay);
		
		JLabel lblNewLabel = new JLabel(comment);
		lblNewLabel.setBackground(Color.YELLOW);
		lblNewLabel.setFont(new Font("DejaVu Serif Condensed", Font.BOLD, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(Color.YELLOW);
		lblNewLabel.setBounds(15, 94, 809, 138);
		helpFrame.getContentPane().add(lblNewLabel);
	}

}
