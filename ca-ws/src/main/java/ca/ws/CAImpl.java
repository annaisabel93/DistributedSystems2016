package ca.ws;

import javax.jws.WebService;

@WebService(endpointInterface = "ca.ws.CA")
public class CAImpl implements CA {

	public String sayHello(String name) {
		return "Hello " + name + "!";
	}

}
