package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class BrokerClient implements BrokerPortType {

    BrokerService service;
    BrokerPortType port;

    public BrokerClient(String URL) {
            service = new BrokerService();
            port = service.getBrokerPort();

    		System.out.println("Setting endpoint address ...");
            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, URL);
    }
	
	
	@Override
	public String ping(String name) {
		return port.ping(name);
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		
		if (price < 0) {
			throw new InvalidPriceFault_Exception("Price not valid!", new InvalidPriceFault());
		}
		
		
		List<ArrayList<String>> Cities = new ArrayList<ArrayList<String>>();
		List<String> Norte = new ArrayList<String>();
		List<String> Centro = new ArrayList<String>();
		List<String> Sul = new ArrayList<String>();
		
		Norte.add("Porto");
		Norte.add("Viana do Castelo");
		Norte.add("Vila Real");
		Norte.add("Bragança");
		Cities.add((ArrayList<String>) Norte);
		
		Centro.add("Lisboa");
		Centro.add("Leiria");
		Centro.add("Santarém");
		Centro.add("Castelo Branco");
		Centro.add("Coimbra");
		Centro.add("Aveiro");
		Centro.add("Viseu");
		Centro.add("Guarda");
		Cities.add((ArrayList<String>) Centro);
		
		
		Sul.add("Setúbal");
		Sul.add("Évora");
		Sul.add("Portalegre");
		Sul.add("Beja");
		Sul.add("Faro");
		Cities.add((ArrayList<String>) Sul);
		
		
		if (Cities.contains(destination) & Cities.contains(origin)) {
			
		}
		else
			throw new UnknownLocationFault_Exception("Invalid Location", new UnknownLocationFault());
		
		System.out.println(Cities);
		
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
