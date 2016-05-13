package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class BrokerClient implements BrokerPortType {

    BrokerService service = new BrokerService();
    BrokerPortType port = service.getBrokerPort();

    public BrokerClient(String URL) {

    	BindingProvider bindingProvider = (BindingProvider) port;
    	Map<String, Object> requestContext = bindingProvider.getRequestContext();
    	requestContext.put(ENDPOINT_ADDRESS_PROPERTY, URL);
    }

	
	@Override
	public String ping(String name) {
		try{
			return port.ping(name);
		}
		catch(WebServiceException e){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				return port.ping(name);
		}
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		try{
			return port.requestTransport(origin, destination, price);
		}
		catch(WebServiceException e){
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				return port.requestTransport(origin, destination, price);
		}
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		try{
			return port.viewTransport(id);
		}
		catch(WebServiceException e){
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				return port.viewTransport(id);
		}
	}

	@Override
	public List<TransportView> listTransports() {
		try{
			return port.listTransports();
		}
		catch(WebServiceException e){
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				return port.listTransports();
		}	
	}

	@Override
	public void clearTransports() {
		try{
			port.clearTransports();	
		}
		catch(WebServiceException e){
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				port.clearTransports();
		}
	}

	@Override
	public String updateStatus(String id, TransportStateView booked) {
		try{
			return port.updateStatus(id, booked);
		}
		catch(WebServiceException e){
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				return port.updateStatus(id, booked);
		}
		
	}

	@Override
	public void addTransportView(String id, String origin, String destination, Integer price, String transporterCompany, TransportStateView state) {
		port.addTransportView(id, origin, destination, price, transporterCompany, state);
		
	}

	@Override
	public void updateClear(String update) {
		port.updateClear(update);
	}


}
