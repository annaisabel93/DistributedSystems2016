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
import pt.upa.broker.ws.cli.BrokerClient;

@WebService(
    endpointInterface="pt.upa.broker.ws.BrokerPortType",
    wsdlLocation="broker.1_0.wsdl",
    name="BrokerWebService",
    portName="BrokerPort",
    targetNamespace="http://ws.broker.upa.pt/",
    serviceName="BrokerService"
)

//@HandlerChain(file = "/handler-chain.xml")

public class BrokerPort implements BrokerPortType{
	
	
	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming;

	private List<TransportView> transports = new ArrayList<TransportView>();
	private List<TransportView> proposes = new ArrayList<TransportView>();
	private List<ArrayList<String>> Cities = new ArrayList<ArrayList<String>>();
	private String uddiUrl;
	private String name;
	private String url;
	private boolean isSecundary = false;
	private List<String> Norte = new ArrayList<String>();
	private List<String> Centro = new ArrayList<String>();
	private List<String> Sul = new ArrayList<String>();
	private BrokerClient secondary = null;
	
	public BrokerPort(String uddiURL1, String name1, String url1, boolean Is_secundary){
		this.uddiUrl = uddiURL1;
		this.name = name1;
		this.url = url1;
		this.isSecundary = Is_secundary;
		Norte.add("Porto");
		Norte.add("Viana do Castelo");
		Norte.add("Vila Real");
		Norte.add("Bragança");
		Norte.add("Braga");
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
			System.out.println("vai ver se e primario");
			if(isSecundary == false){
				System.out.println("e primario, vai fazer set secundary");
				setSecundary();
			}
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
	
	private void setSecundary() throws JAXRException { //vai buscar o url do secundario
				this.secondary = new BrokerClient("http://localhost:8020/broker-ws/endpoint");
				System.out.println("ja fez o teste");
				return;
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
	
	
	private Collection<String> listBrokers() throws JAXRException {
		Collection<String> urls;
		
		urls = uddiNaming.list("UpaBroker%");
		return urls;
	}
	
	@Override
	public String ping(String name){
		if(name.equals("exit")){
			System.exit(0);
		}
		Collection<String> urls = null;
		List<TransporterClient> clientList = null;
		String pings = "Broker responding to: " + name;
		//System.out.println(pings);
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
		int bestPrice = price;
		int companyIndex = 0;
		try {
			boolean gotResponse = false;
			boolean betterPrice = false;
			List<TransporterClient> transporters = listTransporterClients();
			for(TransporterClient client : transporters){
				try {
					JobView job1 = client.requestJob(origin, destination, price);
					if(job1 != null){
						gotResponse = true;
						if(bestPrice > job1.getJobPrice()){
							betterPrice = true;
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
			if(betterPrice == false){
				throw new UnavailableTransportPriceFault_Exception("Oferta acima do pretendido", null);
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
		if(isSecundary == false){
			secondary.addTransportView(origin+destination+price, origin, destination, bestPrice, company, TransportStateView.BOOKED);
		}
		
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
		boolean match = false;
		if(id == null){
			throw new UnknownTransportFault_Exception("id null", null);
		}
		if(transports.isEmpty()){
			throw new UnknownTransportFault_Exception("Lista vazia!", null);
		}
		t =  transports.get(0);
		for (TransportView transport : transports) {
			if (transport.getId().equals(id)) {
				match = true;
				t = transport;
			}		
		}
		if(match == false){
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
			if (job1.getJobState() == JobStateView.PROPOSED){
				return t;
			}
			
			if(job1.getJobState() == JobStateView.ACCEPTED){
				t.setState(state.BOOKED);
				if(isSecundary == false){
					secondary.updateStatus(id, TransportStateView.BOOKED);
				}
				return t;
			}
				
			if(job1.getJobState() == JobStateView.REJECTED){
				t.setState(state.FAILED);
				if(isSecundary == false){
					secondary.updateStatus(id, TransportStateView.FAILED);
				}
				return t;
			}
			
			if (job1.getJobState() == JobStateView.HEADING) {
				t.setState(state.HEADING);
				if(isSecundary == false){
					secondary.updateStatus(id, TransportStateView.HEADING);
				}
				return t;
			}
			
			if (job1.getJobState() == JobStateView.ONGOING) {
				t.setState(state.ONGOING);
				if(isSecundary == false){
					secondary.updateStatus(id, TransportStateView.ONGOING);
				}
				return t;
			}
			
			if (job1.getJobState() == JobStateView.COMPLETED) {
				t.setState(state.COMPLETED);
				if(isSecundary == false){
					secondary.updateStatus(id, TransportStateView.COMPLETED);
				}
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
		if(isSecundary == false){
			secondary.updateClear("do");
		}
		List<TransporterClient> clients;
		try {
			clients = listTransporterClients();
			for (TransporterClient c : 	clients)
				c.clearJobs();
		} catch (JAXRException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addTransportView(String id, String origin, String destination, Integer price, String transporterCompany, TransportStateView state){
		
		TransportView t = new TransportView();
		t.setId(id);
		t.setDestination(destination);
		t.setOrigin(origin);
		t.setPrice(price);
		t.setTransporterCompany(transporterCompany);
		t.setState(state);
		this.transports.add(t);
		System.out.println("added TransportView. List:");
		for(TransportView transport : this.transports){
			System.out.println("Origin: "+ transport.getOrigin() + "| destiny: "+ transport.getDestination() + " |price: "+ transport.getPrice());
		}
	}
	@Override
	public void updateClear(String error){ //should not receive string, but I have no ideia where to change
		System.out.println("clearing transports!");
		transports.clear();
		System.out.println("Transports clear? " + transports.isEmpty());
	}
	
	@Override
	public String updateStatus(String id, TransportStateView state){
		System.out.println("Going to update status with id/state: " + id +"/" + state);
		TransportView t = null;
		for (TransportView transport : this.transports) {
			if (transport.getId().equals(id)) {
				t = transport;
				t.setState(state);
				System.out.println("updated it with " + t.getState());
			}		
		}
		return "done";
	}
	
	

}
