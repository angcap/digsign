package digsign;



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileChooser extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton openButton;
	JFileChooser fc;

	private PKCSGenerator signer;
	
	public FileChooser(PKCSGenerator signer) {
		super(new BorderLayout());
		this.signer = signer;


//Create a file chooser
		fc = new JFileChooser();

//Uncomment one of the following lines to try a different
//file selection mode.  The first allows just directories
//to be selected (and, at least in the Java look and feel,
//shown).  The second allows both files and directories
//to be selected.  If you leave these lines commented out,
//then the default mode (FILES_ONLY) will be used.
//
//fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

//Create the open button.  We use the image from the JLF
//Graphics Repository (but we extracted it from the jar).
		openButton = new JButton("Open a File...", createImageIcon("images/Open16.gif"));
		openButton.addActionListener(this);

//Create the save button.  We use the image from the JLF
//Graphics Repository (but we extracted it from the jar).

//For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(new JLabel("Choose the file to sign"));
		buttonPanel.add(openButton);

		
//Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		

	}

	public void actionPerformed(ActionEvent e) {

//Handle open button action.
		if (e.getSource() == openButton) {
			int returnVal = fc.showOpenDialog(FileChooser.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
//This is where a real application would open the file.
				log.info("Opening: " + file.getName() + "." );
				try {
					JOptionPane.showMessageDialog(null, "File signed generated: "+ this.signer.sign(file.getAbsolutePath()));
				} catch (UnrecoverableKeyException | OperatorCreationException | KeyStoreException
						| NoSuchAlgorithmException | CertificateException | IOException | CMSException e1) {
					log.error(e1.getMessage(),e1);
				}
			} else {
				log.info("Open command cancelled by user." );
			}

		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = FileChooser.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			log.error("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event dispatch thread.
	 */
	public void createAndShowGUI() {
		FileChooser self = this;
		SwingUtilities.invokeLater(() -> {
//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				//Create and set up the window.
				JFrame frame = new JFrame("Sign Document");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null);

		//Add content to the window.
				frame.add(self);

		//Display the window.
				frame.pack();
				frame.setVisible(true);
		});

	}

}
