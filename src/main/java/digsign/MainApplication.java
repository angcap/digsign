package digsign;

import digsign.pades.CreateSignature;

public class MainApplication {
	
	
	public static void main(String[] args) throws Exception {
		KeyStoreManager km = new KeyStoreManager();
		FileChooser chooser = new FileChooser(new PKCSGenerator(km),new CreateSignature(km, km.getKeyStore(), km.getPassword()));
		chooser.createAndShowGUI();
	}

}
