package pt.upa.broker.ws;
import java.util.List;

import javax.jws.WebService;

import org.apache.juddi.v3.client.transport.Transport;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

@WebService(
    endpointInterface="pt.upa.broker.ws.BrokerPortType",
    wsdlLocation="broker.1_0.wsdl",
    name="BrokerWebService",
    portName="BrokerPort",
    targetNamespace="http://ws.broker.upa.pt/",
    serviceName="BrokerService"
)

public class BrokerPort implements BrokerPortType{

	@Override
	public String ping(String name) {
//		List<String> list = UDDINaming.list("UPATRANSPORT%");
//		new Transport
		// uddi.list("upatransp%") - usa-se o método list e vamos buscar tudo o que começa por upatransporter
		//for (cada endereço recebido) 
		//	new transporter client(URL)
		//	client.ping
		//juntar pings numa string e fazer return do status
		return "Broker";
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransportView> listTransports() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearTransports() {
		// TODO Auto-generated method stub
		
	}

	// TODO

}
