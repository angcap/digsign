package digsign.pades;

public class CertificateVerificationException extends Exception {

	public CertificateVerificationException(String message) {
		super(message);
	}

	public CertificateVerificationException(String message, Throwable certPathEx) {
		super(message, certPathEx);
	}

}
