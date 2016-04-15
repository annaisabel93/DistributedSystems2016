package pt.upa.broker.ws;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
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
	
	public List<TransportView> getTransports() {
		return transports;
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
		Collection<String> urls = null;
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
			pings += "\n Found " + urls.size() + "\n";
			for(String url: urls){
				TransporterClient client = new TransporterClient(url);
				ping = client.ping("broker");
				System.out.println("ping-->"+ping);
				pings = pings + "\n" + url + " " + ping;
			}
		return pings;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		
		System.out.println("origin: "+origin + "- destiny: " + destination + "price: " +  price);
		
		
		
		TransportView transport = new TransportView();
	
		List<TransporterClient> list;
		try {
			List<TransporterClient> transporters = listTransporterClients();
			for(TransporterClient client : transporters){
			try {
				client.requestJob(origin, destination, price);
			} catch (BadLocationFault_Exception e) {
				e.printStackTrace();
			} catch (BadPriceFault_Exception e) {
				e.printStackTrace();
			}
		}
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int x = 0;
		
		transport.setDestination(destination);
		transport.setOrigin(origin);
		transport.setPrice(price);
		transport.setId(origin+destination+price);
		transports.add(transport);
		
		return "Transport Requested by Broker";
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		
		TransportView t = null;
		t =  transports.get(0);
		for (TransportView transport : transports) {
			if (transport.getId().equals(id)) {
				t = transport;
			}		
		}
		
		List<TransporterClient> clients = null;
		//TransporterClient client;
		TransportStateView state = t.getState();
		try {
			clients = listTransporterClients();
		} catch (JAXRException e) {
			e.printStackTrace();
		}
		for(TransporterClient client: clients){
			 JobView job1 = client.jobStatus(id);
			 if(job1 == null){
				 continue;
			 }
			JobStateView job =	job1.getJobState();
			if(job == null){
				System.out.println("null......");
				continue;
			}
			if (job.PROPOSED != null)
				return t;
			
			if(job.ACCEPTED != null){
				t.setState(state.BOOKED);
				return t;
			}
				
			if(job.REJECTED != null){
				t.setState(state.FAILED);
				return t;
			}
			
			if (job.HEADING != null) {
				t.setState(state.HEADING);
				return t;
			}
			
			if (job.ONGOING != null) {
				t.setState(state.ONGOING);
				return t;
			}
			
			if (job.COMPLETED != null) {
				t.setState(state.COMPLETED);
				return t;
			}
		}
		;
			
		
		
		System.out.println("vai retornar o job com o id pedido");
		
		return t;
	}

	@Override
	public List<TransportView> listTransports() { //estava a criar um novo, nao estava a usar o global
		
		System.out.println("vai listar tudo");
		return transports;
	}

	@Override
	public void clearTransports() {
		
		System.out.println("vai apagar tudo");
		
		//estavas a chamar a funcao list, podes logo fazer clear
		transports.clear();
		List<TransporterClient> clients;
		try {
			clients = listTransporterClients();
			for (TransporterClient c : 	clients)
				c.clearJobs();
		} catch (JAXRException e) {
			e.printStackTrace();
		}
	}

}
