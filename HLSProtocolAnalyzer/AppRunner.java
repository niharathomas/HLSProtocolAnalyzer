package HLSProtocolAnalyzer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AppRunner {
	/**
	 * Main class to run the application
	 * Defines the UI via which the application is run
	 * Takes input URL and passes it to the ReadInputStream Class to read data from the file
	 * Checks tags in the input file against those defined in the PlaylistTagConstants class
	 */
	private static JFrame frame;
	private static JPanel panel;
	private static JLabel label;
	private static JTextField textField;
	private static JButton submitButton;
	private String inputURL;
	
	
	public AppRunner(){
		System.out.println("Initializing UI");
		initialize();
	}
	
	private void initialize(){
		setFrame(new JFrame());
		getFrame().setTitle("HLS Protocol Alalyzer App");
		frame.setSize(500, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		label = new JLabel();
		label.setText(" Input URL");
		
		textField = new JTextField(450);
		textField.setMinimumSize(new Dimension(300, 30));
		textField.setToolTipText("Enter the stream URL");
		
		submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				submitButtonActionPerformed(event);
            }
		});
		
		c.weightx=1.;
	    c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(label, c);
		panel.add(textField, c);
		panel.add(submitButton, c);
		
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		textField.requestFocusInWindow();
	}
	
	public void submitButtonActionPerformed(ActionEvent event){
		String inputURL = textField.getText();
		ReadInputStream urlReader = new ReadInputStream();
		ArrayList<String> masterFileList = urlReader.getMasterFileList(inputURL);
		urlReader.fileSeparator();
		
	}
	
	public void setFrame(JFrame newFrame) {
		frame = newFrame;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	public static void main(String []args){
		LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
		loggerWrapper.myLogger.info("Initializing...");
		
		// String checkLevel = args[0];
		loggerWrapper.myLogger.info("Creating User Interface...");
		AppRunner app = new AppRunner();
		
	}
	

}
