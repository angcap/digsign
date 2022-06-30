package digsign;

public class MainApplication {
	
	
	public static void main(String[] args) {
		FileChooser chooser = new FileChooser(new PKCSGenerator());
		chooser.createAndShowGUI();
	}

}
