package pt.upa.broker.ws;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadJobFault_Exception;
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

@HandlerChain(file = "/handler-chain.xml")

public class BrokerPort implements BrokerPortType{
	
	
	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming;

	private List<TransportView> transports = new ArrayList<TransportView>();
	private List<TransportView> proposes = new ArrayList<TransportView>();
	private List<ArrayList<String>> Cities = new ArrayList<ArrayList<String>>();
	private String uddiUrl;
	private String name;
	private String url;
	private List<String> Norte = new ArrayList<String>();
	private List<String> Centro = new ArrayList<String>();
	private List<String> Sul = new ArrayList<String>();
	
	public BrokerPort(String uddiURL1, String name1, String url1){
		this.uddiUrl = uddiURL1;
		this.name = name1;
		this.url = url1;
		Norte.add("Porto");
		Norte.add("Viana do Castelo");
		Norte.add("Vila Real");
		Norte.add("Bragança");
		Cities.add((ArrayList<String>) Norte);
		
		Centro.add("Lisboa");
		Centro.add("Leiria");
		Centro.add("Santarem");
		Centro.add("Castelo Branco");
		Centro.add("Coimbra");
		Centro.add("Aveiro");
		Centro.add("Viseu");
		Centro.add("Guarda");
		Cities.add((ArrayList<String>) Centro);
		
		
		Sul.add("Setubal");
		Sul.add("Evora");
		Sul.add("Portalegre");
		Sul.add("Beja");
		Sul.add("Faro");
		Cities.add((ArrayList<String>) Sul);
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
			System.out.println("url->" + url);
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
				pings = pings + "\n" + url + " " + ping;
			}
		return pings;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		
		if(price > 100){
			throw new UnavailableTransportFault_Exception("Invalid Price! high", null);
		}
		if(price < 0){
			throw new InvalidPriceFault_Exception("invalid price! low", null);
		}
		if(this.Sul.contains(origin) || this.Norte.contains(origin) || this.Centro.contains(origin)){
			if(this.Sul.contains(destination) || this.Norte.contains(destination) || this.Centro.contains(destination)){
				//ok
			}
			else{
				throw new UnknownLocationFault_Exception("invalid destination", null);
			}
		}
		else{
			throw new UnknownLocationFault_Exception("invalid origin", null);
		}
		
		TransportView transport = new TransportView();
		List<TransporterClient> list;
		JobView job = null;
		int bestPrice = 1000;
		int companyIndex = 0;
		try {
			boolean gotResponse = false;
			List<TransporterClient> transporters = listTransporterClients();
			for(TransporterClient client : transporters){
				try {
					JobView job1 = client.requestJob(origin, destination, price);
					if(job1 != null){
						gotResponse = true;
						if(bestPrice > job1.getJobPrice()){
							bestPrice = job1.getJobPrice();
							companyIndex = transporters.indexOf(client);						
						}
					}
				}
				catch (BadLocationFault_Exception e) {
					e.printStackTrace();
				}
				catch (BadPriceFault_Exception e) {
					e.printStackTrace();
				}
			}
			if(gotResponse == false){
				throw new UnavailableTransportFault_Exception("did not get an offer", null);
			}
		}
		catch (JAXRException e) {
			e.printStackTrace();
		}
		String company = "";
		company = company + companyIndex;
		transport.setDestination(destination);
		transport.setOrigin(origin);
		transport.setPrice(bestPrice);
		transport.setTransporterCompany(company);
		transport.setId(origin+destination+price);
		transport.setState(TransportStateView.BOOKED);
		transports.add(transport);
		
			List<TransporterClient> transporters = null;
			try {
				transporters = listTransporterClients();
			} catch (JAXRException e1) {
				e1.printStackTrace();
			}
			for(TransporterClient client : transporters){
				try {
					if(transporters.indexOf(client) == companyIndex){
						client.decideJob(origin+destination+price, true);						
					}
					else{
						client.decideJob(origin+destination+price, false);
					}
				}
			
				catch (BadJobFault_Exception e) {
					e.printStackTrace();
				} 
			}
		
		return origin+destination+price;
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
		if(t == null){
			throw new UnknownTransportFault_Exception("ID inexistente", null);
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
			
		
		
		
		return t;
	}

	@Override
	public List<TransportView> listTransports() { //estava a criar um novo, nao estava a usar o global
		
		return transports;
	}

	@Override
	public void clearTransports() {
		
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
