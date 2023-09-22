package digsign;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class KeyStoreManager {

	

	public static final String CHANGEME = "changeme";
	private static final String KEY_ALIAS = "test";
	private KeyStore keystore;
	
	public KeyStoreManager() throws Exception {
		super();
		this.initKeyStore();
	}
	
	public char[] getPassword() {
		return CHANGEME.toCharArray();
	}
	
	public String getKeyAlias() {
		return KEY_ALIAS;
	}

	public KeyStore getKeyStore() {
		return this.keystore;
	}
	
	private void initKeyStore() throws Exception {
		this.keystore = KeyStore.getInstance("PKCS12");
		try (var keystoreContents = this.getClass().getClassLoader().getResourceAsStream("test.keystore")) {
			keystore.load(keystoreContents, getPassword());
		}
	}
	
	public  X509Certificate getCertificate() throws KeyStoreException {
		return (X509Certificate) getKeyStore().getCertificate(getKeyAlias());
	}
            

	public PrivateKey getPrivateKey() throws Exception {
		return (PrivateKey) getKeyStore().getKey(getKeyAlias(), getPassword());
	}

	
}
