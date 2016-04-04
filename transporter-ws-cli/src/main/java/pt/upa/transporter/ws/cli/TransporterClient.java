package pt.upa.transporter.ws.cli;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

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

        @Override
        public String ping(String name) {
                return port.ping(name);
        }
        

@Override
        public JobView requestJob(String origin, String destination, int price)
                        throws BadLocationFault_Exception, BadPriceFault_Exception {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public JobView jobStatus(String id) {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public List<JobView> listJobs() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public void clearJobs() {
                // TODO Auto-generated method stub

        }
        // TODO

}
