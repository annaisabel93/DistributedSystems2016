package ca.ws;

import javax.jws.WebService;

@WebService(endpointInterface = "ca.ws.CA")
public class CAImpl implements CA {	
	public byte[] getCertificate(String name){
		byte[] read = name.getBytes();
		return read;	
	}

}
