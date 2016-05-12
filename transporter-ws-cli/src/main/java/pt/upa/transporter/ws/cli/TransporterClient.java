package pt.upa.transporter.ws.cli;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;

import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;


public class TransporterClient implements TransporterPortType{

        TransporterService service;
        TransporterPortType port;

        public TransporterClient(String URL) {
                service = new TransporterService();
                port = service.getTransporterPort();

                System.out.println("Setting endpoint address ...");
                BindingProvider bindingProvider = (BindingProvider) port;
                Map<String, Object> requestContext = bindingProvider.getRequestContext();
                requestContext.put(ENDPOINT_ADDRESS_PROPERTY, URL);
        }

        
//        private List<TransporterClient> listTransporterClients() throws JAXRException {
//    		Collection<String> urlList = listTransporters();
//    		List<TransporterClient> clientList =  new ArrayList<TransporterClient>();
//    		for(String url: urlList){
//    			clientList.add(new TransporterClient(url));
//    		}
//    		return clientList;
//    	}
//    	
//    	private Collection<String> listTransporters() throws JAXRException {
//    		Collection<String> urls;
//    		urls = uddiNaming.list("UpaTransporter%");
//    		return urls;
//    	}
    	
        
        @Override
        public String ping(String name) {
                return port.ping(name);
        }
        

        @Override
        public JobView requestJob(String origin, String destination, int price)
                        throws BadLocationFault_Exception, BadPriceFault_Exception {
               return port.requestJob(origin, destination, price);
        }

        @Override
        public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
                return port.decideJob(id, accept);
        }

        @Override
        public JobView jobStatus(String id) {
                return port.jobStatus(id);
        }

        @Override
        public List<JobView> listJobs() {
                return port.listJobs();
        }

        @Override
        public void clearJobs() {
               port.clearJobs();

        }
        // TODO

}
