package digsign;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

public class PKCSGenerator {

	private static final String CHANGEME = "changeme";
	private static final String KEY_ALIAS = "test";

	
	public byte[] sign(byte [] data) throws IOException, OperatorCreationException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, CMSException{
		Security.addProvider(new BouncyCastleProvider());
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (var keystoreContents = this.getClass().getClassLoader().getResourceAsStream("test.keystore")) {
            keystore.load(keystoreContents, CHANGEME.toCharArray());
        }
        Provider provider = Security.getProvider("BC");
        PrivateKey key = (PrivateKey) keystore.getKey(KEY_ALIAS,
        		CHANGEME.toCharArray());
        X509Certificate cert = (X509Certificate) keystore
                .getCertificate(KEY_ALIAS);

        // Create the signature
        CMSTypedData msg = new CMSProcessableByteArray(data);
        CMSSignedDataGenerator signedDataGen = new CMSSignedDataGenerator();
        X509CertificateHolder signCert = new X509CertificateHolder(
                cert.getEncoded());
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(provider).build(key);
        signedDataGen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder().setProvider(provider)
                        .build()).build(signer, signCert));
                
        // add the signing cert to the signature
        Store<X509Certificate> certs = new JcaCertStore(Arrays.asList(cert)); 
        signedDataGen.addCertificates(certs);

        // false = create detached signature
        CMSSignedData signedData = signedDataGen.generate(msg, true);
        return signedData.getEncoded();
	}
	
	/**
	 * Generate the signed file with original file name + .p7m (in the same location) 
	 * 
	 * @param file
	 * @return 
	 * @throws UnrecoverableKeyException
	 * @throws OperatorCreationException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws CMSException
	 */
	public String sign(String file) throws UnrecoverableKeyException, OperatorCreationException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, CMSException {
		return Files.write(Paths.get(file+".p7m"), this.sign(Files.readAllBytes(Paths.get(file))), StandardOpenOption.CREATE).toAbsolutePath().toUri().toString();
	}
}
