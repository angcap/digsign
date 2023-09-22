package digsign;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

	
	private KeyStoreManager km ;
	
	public PKCSGenerator() throws Exception {
		this.km = new KeyStoreManager();
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public PKCSGenerator(KeyStoreManager km ) throws Exception {
		this.km = km;
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public byte[] sign(byte [] data) throws Exception{
		
        Provider provider = Security.getProvider("BC");
        PrivateKey key = km.getPrivateKey();
        X509Certificate cert = km.getCertificate();

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
	public String sign(String file) throws Exception {
		return Files.write(Paths.get(file+".p7m"), this.sign(Files.readAllBytes(Paths.get(file))), StandardOpenOption.CREATE).toAbsolutePath().toUri().toString();
	}
}
