package digsign;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import digsign.pades.CreateSignature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileChooser extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton openButton;
	JFileChooser fc;

	private PKCSGenerator cadesSigner;
	private CreateSignature padesSigner;

	public FileChooser(PKCSGenerator signer, CreateSignature padesSigner) {
		super(new BorderLayout());
		this.cadesSigner = signer;
		this.padesSigner = padesSigner;
		
//Create a file chooser
		fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileFilter() {
		    public String getDescription() {
		        return "PDF Documents (*.pdf)";
		    }
		 
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return f.getName().toLowerCase().endsWith(".pdf");
		        }
		    }
		});
		openButton = new JButton("Open a File...", createImageIcon("images/Open16.gif"));
		openButton.addActionListener(this);

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
				log.info("Opening: " + file.getName() + ".");
				try {
					String cadesFile = this.cadesSigner.sign(file.getAbsolutePath());
					Path padesFile = Paths.get(file.getAbsolutePath()+".signed");
					this.padesSigner.signDetached(file, padesFile.toFile());
					JOptionPane.showMessageDialog(null,
							"File cades generated: " + cadesFile + System.lineSeparator() + 
							"File pades generated: " + padesFile.toAbsolutePath().toString());
				} catch (Exception  e1) {
					log.error(e1.getMessage(), e1);
				}
			} else {
				log.info("Open command cancelled by user.");
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
			// Create and set up the window.
			JFrame frame = new JFrame("Sign Document");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);

			// Add content to the window.
			frame.add(self);

			// Display the window.
			frame.pack();
			frame.setVisible(true);
		});

	}

}
