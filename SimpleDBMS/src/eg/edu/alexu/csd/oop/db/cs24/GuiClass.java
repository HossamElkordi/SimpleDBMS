package eg.edu.alexu.csd.oop.db.cs24;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class GuiClass {

	private JFrame frame;
	private JTable table;
	
	private String command = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			GuiClass window = new GuiClass();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	/**
	 * Create the application.
	 */
	public GuiClass() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Database Management System");
		frame.setBounds(100, 100, 632, 459);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setBounds(260, 11, 346, 399);
		frame.getContentPane().add(tableScrollPane);
		
		table = new JTable();
		tableScrollPane.setViewportView(table);
		
		setCommand();
	}

	private void setCommand() {
		JScrollPane commandScrollPane = new JScrollPane();
		commandScrollPane.setBounds(10, 61, 239, 159);
		frame.getContentPane().add(commandScrollPane);
		
		JTextArea commandArea = new JTextArea();
		commandScrollPane.setViewportView(commandArea);
		commandArea.setTabSize(5);
		commandArea.setLineWrap(true);
		commandArea.setFont(new Font("Courier New", Font.PLAIN, 13));
		commandArea.setWrapStyleWord(true);
		commandArea.getDocument().addDocumentListener(new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				try {
					command += e.getDocument().getText(e.getOffset(), e.getLength());
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}				
			}

			public void removeUpdate(DocumentEvent e) {
				command = command.substring(0, command.length() - e.getLength());				
			}

			public void changedUpdate(DocumentEvent e) {				
			}
			
		});
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(command);
				command = "";
			}
		});
		btnEnter.setBounds(80, 235, 89, 23);
		frame.getContentPane().add(btnEnter);
	}
}
