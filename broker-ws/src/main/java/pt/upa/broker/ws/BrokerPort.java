package pt.upa.broker.ws;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.TransporterClient;

@WebService(
    endpointInterface="pt.upa.broker.ws.BrokerPortType",
    wsdlLocation="broker.1_0.wsdl",
    name="BrokerWebService",
    portName="BrokerPort",
    targetNamespace="http://ws.broker.upa.pt/",
    serviceName="BrokerService"
)

public class BrokerPort implements BrokerPortType{
	
	
	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming;

	
	private String uddiUrl;
	private String name;
	private String url;
	
	public BrokerPort(String uddiURL1, String name1, String url1){
		this.uddiUrl = uddiURL1;
		this.name = name1;
		this.url = url1;
		try{
			this.uddiNaming = new UDDINaming(uddiURL1);
			this.uddiNaming.rebind(name, url);
		}
		catch (JAXRException e) {
			e.printStackTrace();
		}
	}
	

	/** Get UDDI Naming instance for contacting UDDI server */
	UDDINaming getUddiNaming() {
		return uddiNaming;
	}
	
	@Override
	public String ping(String name) {
		String pings = "";
		String ping = "";
		Collection<String> urls;
		try {
			urls = uddiNaming.list("UPATRANSPORT%");
			for(String url: urls){
				TransporterClient client = new TransporterClient(url);
				ping = client.ping(url);
				pings = pings + ping;
			}
			
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
