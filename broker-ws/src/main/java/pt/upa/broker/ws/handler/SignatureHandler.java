package pt.upa.broker.ws.handler;

//provides helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.security.KeyStore;
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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Document;

import pt.upa.ca.ws.CA;
import pt.upa.ca.ws.cli.CAClient;
import pt.upa.ca.ws.CAImplService;
import pt.upa.ca.ws.*;

public class SignatureHandler implements SOAPHandler<SOAPMessageContext>{
	
	public static final String CONTEXT_PROPERTY = "my.property";
	final static String CERTIFICATE_FILE = "src/main/resources/UpaTransporter1.cer";
	final static String KEYSTORE_FILE = "src/main/resources/UpaTransporter1.jks";
	final static String KEYSTORE_PASSWORD = "ins3cur3";
	final static String KEY_ALIAS = "UpaTransporter1";
	final static String KEY_PASSWORD = "1nsecure";
	
	//
	// Handler interface methods
	//
	public Set<QName> getHeaders() {
		return null;
	}


	public boolean handleMessage(SOAPMessageContext smc) {

		System.out.println("AddHeaderHandler: Handling message.");
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {

			if (outboundElement.booleanValue()) {
				System.out.println("Writing header in outbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				//*****************************get SOAP body***************************************************
				SOAPBody body = se.getBody();
				msg.writeTo(System.out);
				String resultBody = parseSOAPBodyToString(body);
				System.out.println("RESULT: " + resultBody);

				byte[] bodyBytes = resultBody.getBytes();
				System.out.println("Bytes:");
				System.out.println(printHexBinary(bodyBytes));


				// make digital signature
				System.out.println("Signing ...");
				byte[] digitalSignature = makeDigitalSignature(bodyBytes, getPrivateKeyFromKeystore(KEYSTORE_FILE, KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray()));


				System.out.println("Signature Bytes:");
				System.out.println(printHexBinary(digitalSignature));

				Certificate certificate = readCertificateFile(CERTIFICATE_FILE);
				PublicKey publicKey = certificate.getPublicKey();

				// verify the signature
				System.out.println("Verifying ...");
				boolean isValid = verifyDigitalSignature(digitalSignature, bodyBytes, publicKey);


				if (isValid) {
					System.out.println("The digital signature is valid");

				} else {
					System.out.println("The digital signature is NOT valid");
				}
				
				// data modification ...
				bodyBytes[3] = 12;

				System.out.println("Tampered bytes: (look closely around the 7th hex character)");
				System.out.println(printHexBinary(bodyBytes));


				// again verify the signature
				System.out.println("Verifying again ...");
				isValid = verifyDigitalSignature(digitalSignature, bodyBytes, publicKey);


				if (isValid) {
					System.out.println("The digital signature is valid");
				} else {
					System.out.println("The digital signature is NOT valid");

				}

				//*********************************************************************************************

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name name = se.createName("myHeader", "d", "http://demo");
				SOAPHeaderElement element = sh.addHeaderElement(name);

				// add header element value
				int value = 22;
				String valueString = Integer.toString(value);
				element.addTextNode(valueString);



			} else {

				System.out.println("Reading header in inbound SOAP message...");

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
				Name name = se.createName("myHeader", "d", "http://demo");
				Iterator it = sh.getChildElements(name);

				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}

				SOAPElement element = (SOAPElement) it.next();

				// get header element value
				String valueString = element.getValue();
				int value = Integer.parseInt(valueString);
				
				// print received header
				System.out.println("Header value is " + value);
				// put header in a property context
				smc.put(CONTEXT_PROPERTY, value);
				// set property scope to application client/server class can access it
				smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);
			}

		} catch (Exception e) {

			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}
		return true;
	}


	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("Ignoring fault message...");
		return true;
	}



	public void close(MessageContext messageContext) {
	}



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



	public String parseSOAPBodyToString(SOAPBody body){

		String strBody=null;
		try {

			Document doc = body.extractContentAsDocument();
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			strBody=stringWriter.getBuffer().toString();

		} catch (SOAPException e) {

			//log.error("SOAPException while parsing soap body to string: " + e.getMessage());

			//log.debug("Stack Trace: ", e);

		} catch (TransformerConfigurationException e) {

			// log.error("TransformerConfigurationException while parsing soap body to string: " + e.getMessage());

			// log.debug("Stack Trace: ", e);

		} catch (TransformerException e) {

			// log.error("TransformerException while parsing soap body to string: " + e.getMessage());

			// log.debug("Stack Trace: ", e);

		}

		return strBody;

	}

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


	public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey) throws Exception {

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