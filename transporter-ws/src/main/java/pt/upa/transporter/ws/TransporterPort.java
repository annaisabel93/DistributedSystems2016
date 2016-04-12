package pt.upa.transporter.ws;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import pt.upa.transporter.ws.BadPriceFault;


@WebService(
    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
    wsdlLocation="transporter.1_0.wsdl",
    name="TransporterWebService",
    portName="TransporterPort",
    targetNamespace="http://ws.transporter.upa.pt/",
    serviceName="TransporterService"
)
public class TransporterPort implements TransporterPortType{
	
	
	private List<ArrayList<String>> Cities = new ArrayList<ArrayList<String>>();
	private List<String> Norte = new ArrayList<String>();
	private List<String> Centro = new ArrayList<String>();
	private List<String> Sul = new ArrayList<String>();

	@Override
	public String ping(String name) {
		return "Transporter";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
	
	if (price < 0) {
		throw new BadPriceFault_Exception("Price not valid!", new BadPriceFault());
	}
	

	
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
		throw new BadLocationFault_Exception("Invalid Location", new BadLocationFault());
	
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
