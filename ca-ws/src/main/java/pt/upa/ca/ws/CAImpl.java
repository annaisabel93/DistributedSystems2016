package pt.upa.ca.ws;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA {	
	public byte[] getCertificate(){
		byte[] read = null;
		
		try {		
			read = Files.readAllBytes(Paths.get("src/resources/ca-certificate.pem.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(read);
		return read;
	}
}
