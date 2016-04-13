package pt.upa.broker.ws;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
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

	private List<TransportView> transports = new ArrayList<TransportView>();
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
	
	private List<TransporterClient> listTransporterClients() throws JAXRException {
		Collection<String> urlList = listTransporters();
		List<TransporterClient> clientList =  new ArrayList<TransporterClient>();
		for(String url: urlList){
			clientList.add(new TransporterClient(url));
		}
		return clientList;
	}
	
	private Collection<String> listTransporters() throws JAXRException {
		Collection<String> urls;
		urls = uddiNaming.list("UpaTransporter%");
		return urls;
	}
	
	@Override
	public String ping(String name){
		Collection<String> urls = null;;
		List<TransporterClient> clientList = null;
		String pings = "Broker responding to: " + name;
		System.out.println(pings);
		try {
			clientList = listTransporterClients();
			urls = listTransporters();
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String ping = "";
		//try {
			//urls = uddiNaming.list("UpaTransporter%");
			pings += "\nFound " + urls.size() + "\n";
			for(String url: urls){
				TransporterClient client = new TransporterClient(url);
				ping = client.ping("broker");
				System.out.println("ping-->"+ping);
				pings = pings + "\n" + url + " " + ping;
			}
		/*	
		} catch (JAXRException e) {
			pings += "Failed to contact UDDI: " + e;
		}*/
		// uddi.list("upatransp%") - usa-se o método list e vamos buscar tudo o que começa por upatransporter
		//for (cada endereço recebido) 
		//	new transporter client(URL)
		//	client.ping
		//juntar pings numa string e fazer return do status
		return pings;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		TransportView transport = new TransportView();
		transport.setDestination(destination);
		transport.setOrigin(origin);
		transport.setPrice(price);
		List<TransporterClient> list;
		try {
			list = listTransporterClients();
			for(TransporterClient t : listTransporterClients()){
				int x = list.indexOf(t);
				Integer test = (Integer) x;
				origin = origin +"/" + test.toString();
			try {
				t.requestJob(origin, destination, price);
			} catch (BadLocationFault_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPriceFault_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int x = 0;
		
		transports.add(transport);
		
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
