package pt.upa.transporter.ws.handler;

//provides helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.bind.DatatypeConverter;

//TODO falta fazer: quando é chamado ao enviar/receber/wtv -> se a mensagem for outbound vai ler, buscar bytes, resumir, e assinar - deve ter que ir buscar certificados!
/*
 * #2 The client handler receives data from the client (via message context). #3
 * The client handler passes data to the server handler (via outbound SOAP
 * message header).
 *
 *
 * #10 The client handler receives data from the server handler (via inbound
 * SOAP message header). #11 The client handler passes data to the client (via
 * message context).
 *
 * *** GO BACK TO client to see what happens next! ***
 */

public class SignatureHandler implements SOAPHandler<SOAPMessageContext>{

	public static final String REQUEST_PROPERTY = "my.request.property";
	public static final String RESPONSE_PROPERTY = "my.response.property";

	public static final String REQUEST_HEADER = "myRequestHeader";
	public static final String REQUEST_NS = "urn:example";

	public static final String RESPONSE_HEADER = "myResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CLASS_NAME = SignatureHandler.class.getSimpleName();
	public static final String TOKEN = "client-handler";

	private static byte[] SOAPMessageToByteArray(SOAPMessage msg) throws Exception {

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        byte[] msgByteArray = null;

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        Source source = msg.getSOAPPart().getContent();
        Result result = new StreamResult(byteOutStream);
        transformer.transform(source, result);

        msgByteArray = byteOutStream.toByteArray();
        return msgByteArray;
    }

	private String SOAPMessageToString(SOAPMessage msg) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			msg.writeTo(stream);
		} catch (SOAPException | IOException e) {
			e.printStackTrace();
		}
		try {
			return new String(stream.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {

			// outbound message
			// *** #2 ***
			// get token from request context
			String propertyValue = (String) smc.get(REQUEST_PROPERTY);
			System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue);

			SOAPMessage soapMsg = smc.getMessage();
			SOAPBody el;
			try {
				el = soapMsg.getSOAPBody();
				DOMSource source = new DOMSource(el);
				StringWriter stringResult = new StringWriter();
				TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
				String message = stringResult.toString();
				DatatypeConverter.parseBase64Binary(message);
			} catch (SOAPException | TransformerException | TransformerFactoryConfigurationError e1) {
				e1.printStackTrace();
			} 
			
			
			
//			MessageContext message = new XMLMessage();
//			message.processReceivedData(request);
//			
//			MessageContext clientMessage = new EncryptedXMLMessage(csKey);
//			clientMessage.processReceivedData(DataConvertor.getBytesFromBase64String(message.getElement(TOKEN)));
			
			// put token in request SOAP header
			try {
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);

				// *** #3 ***
				// add header element value
				String newValue = propertyValue + "," + TOKEN;
				element.addTextNode(newValue);

				System.out.printf("%s put token '%s' on request message header%n", CLASS_NAME, newValue);

			} catch (SOAPException e) {

				System.out.printf("Failed to add SOAP header because of %s%n", e);

			}

		} else {
			// inbound message
			// get token from response SOAP header

			try {

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}

				// get first header element
				Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.printf("Header element %s not found.%n", RESPONSE_HEADER);
					return true;
				}

				SOAPElement element = (SOAPElement) it.next();

				// *** #10 ***
				// get header element value
				String headerValue = element.getValue();

				System.out.printf("%s got '%s'%n", CLASS_NAME, headerValue);

				// *** #11 ***
				// put token in response context

				String newValue = headerValue + "," + TOKEN;
				System.out.printf("%s put token '%s' on response context%n", CLASS_NAME, TOKEN);
				smc.put(RESPONSE_PROPERTY, newValue);

				// set property scope to application so that client class can
				// access property
				smc.setScope(RESPONSE_PROPERTY, Scope.APPLICATION);
			} catch (SOAPException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			}
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext messageContext) {
	}

	public void digest(String[] args) throws NoSuchAlgorithmException {
		// check args and get plaintext

		if (args.length != 1) {
			System.err.println("args: (text)");
			return;
		}

		final String plainText = args[0];
		final byte[] plainBytes = plainText.getBytes();

		System.out.println("Text:");
		System.out.println(plainText);
		System.out.println("Bytes:");
		System.out.println(printHexBinary(plainBytes));

		// get a message digest object using the specified algorithm
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		System.out.println(messageDigest.getProvider().getInfo());

		System.out.println("Computing digest ...");
		messageDigest.update(plainBytes);
		byte[] digest = messageDigest.digest();

		System.out.println("Digest:");
		System.out.println(printHexBinary(digest));
	}

	final static String CERTIFICATE_FILE = "example.cer";
	final static String KEYSTORE_FILE = "keystore.jks";
	final static String KEYSTORE_PASSWORD = "1nsecure";
	final static String KEY_ALIAS = "example";
	final static String KEY_PASSWORD = "ins3cur3";

	public static void sign(String[] args) throws Exception {
		// check arguments and get plain-text

		if (args.length != 1) {

			System.err.println("args: (text)");
			return;
		}

		final String plainText = args[0];
		final byte[] plainBytes = plainText.getBytes();

		System.out.println("Text:");
		System.out.println(plainText);

		System.out.println("Bytes:");
		System.out.println(printHexBinary(plainBytes));

		// make digital signature
		System.out.println("Signing ...");

		byte[] digitalSignature = makeDigitalSignature(plainBytes, getPrivateKeyFromKeystore(KEYSTORE_FILE,

				KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray()));

		System.out.println("Signature Bytes:");
		System.out.println(printHexBinary(digitalSignature));

		Certificate certificate = readCertificateFile(CERTIFICATE_FILE);
		PublicKey publicKey = certificate.getPublicKey();

		// verify the signature
		System.out.println("Verifying ...");
		boolean isValid = verifyDigitalSignature(digitalSignature, plainBytes, publicKey);

		if (isValid) {
			System.out.println("The digital signature is valid");
		} else {
			System.out.println("The digital signature is NOT valid");
		}

		// data modification ...
		plainBytes[3] = 12;
		System.out.println("Tampered bytes: (look closely around the 7th hex character)");
		System.out.println(printHexBinary(plainBytes));

		// again verify the signature
		System.out.println("Verifying again ...");
		isValid = verifyDigitalSignature(digitalSignature, plainBytes, publicKey);

		if (isValid) {
			System.out.println("The digital signature is valid");

		} else {
			System.out.println("The digital signature is NOT valid");
		}
	}

	/**
	 * Returns the public key from a certificate
	 * 
	 * @param certificate
	 * @return
	 */

	public static PublicKey getPublicKeyFromCertificate(Certificate certificate) {
		return certificate.getPublicKey();
	}

	/**
	 * Reads a certificate from a file
	 * 
	 * @return
	 * @throws Exception
	 */

	public static Certificate readCertificateFile(String certificateFilePath) throws Exception {

		FileInputStream fis;

		try {
			fis = new FileInputStream(certificateFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Certificate file <" + certificateFilePath + "> not fount.");
			return null;
		}

		BufferedInputStream bis = new BufferedInputStream(fis);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		
		if (bis.available() > 0) {
			Certificate cert = cf.generateCertificate(bis);
			return cert;
			// It is possible to print the content of the certificate file:
			// System.out.println(cert.toString());
		}

		bis.close();
		fis.close();

		return null;
	}
	
	/**
	 * Reads a collections of certificates from a file
	 * 
	 * @return
	 * @throws Exception
	 */

	public static Collection<Certificate> readCertificateList(String certificateFilePath) throws Exception {
		FileInputStream fis;

		try {
			fis = new FileInputStream(certificateFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Certificate file <" + certificateFilePath + "> not fount.");
			return null;
		}

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		@SuppressWarnings("unchecked")
		Collection<Certificate> c = (Collection<Certificate>) cf.generateCertificates(fis);
		fis.close();
		return c;
	}

	/**
	 * Reads a PrivateKey from a key-store
	 * 
	 * @return The PrivateKey
	 * @throws Exception
	 */

	public static PrivateKey getPrivateKeyFromKeystore(String keyStoreFilePath, char[] keyStorePassword,

			String keyAlias, char[] keyPassword) throws Exception {

		KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword);
		PrivateKey key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);

		return key;
	}



	/**
	 * Reads a KeyStore from a file
	 * 
	 * @return The read KeyStore
	 * @throws Exception
	 */

	public static KeyStore readKeystoreFile(String keyStoreFilePath, char[] keyStorePassword) throws Exception {

		FileInputStream fis;

		try {
			fis = new FileInputStream(keyStoreFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Keystore file <" + keyStoreFilePath + "> not fount.");
			return null;
		}

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(fis, keyStorePassword);
		return keystore;
	}



	/** auxiliary method to calculate digest from text and cipher it */

	public static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privateKey) throws Exception {
		// get a signature object using the SHA-1 and RSA combo

		// and sign the plain-text with the private key

		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privateKey);
		sig.update(bytes);
		byte[] signature = sig.sign();

		return signature;
	}



	/**
	 * auxiliary method to calculate new digest from text and compare it to the
	 * to deciphered digest
	 */

	public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey)

			throws Exception {
		// verify the signature with the public key

		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initVerify(publicKey);
		sig.update(bytes);

		try {
			return sig.verify(cipherDigest);
		} catch (SignatureException se) {
			System.err.println("Caught exception while verifying signature " + se);
			return false;
		}
	}
}