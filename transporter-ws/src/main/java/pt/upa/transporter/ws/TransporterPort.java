package pt.upa.transporter.ws;
import java.util.Date;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;




@WebService(
    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
    wsdlLocation="transporter.1_0.wsdl",
    name="TransporterWebService",
    portName="TransporterPort",
    targetNamespace="http://ws.transporter.upa.pt/",
    serviceName="TransporterService"
)
public class TransporterPort implements TransporterPortType{
	
	
	
	
	
	private List<JobView> jobs = new ArrayList<JobView>();
	private List<ArrayList<String>> Cities = new ArrayList<ArrayList<String>>();
	private List<String> Norte = new ArrayList<String>();
	private List<String> Centro = new ArrayList<String>();
	private List<String> Sul = new ArrayList<String>();
	private List<Date> creationDates = new ArrayList<Date>();
	
	private boolean isPar;
	
	public TransporterPort(boolean isPar){
		this.isPar = isPar;
	}
	
	public List<JobView> getJobs() {
		return jobs;
	}

	@Override
	public String ping(String name) {
		return "Transporter";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)throws BadLocationFault_Exception, BadPriceFault_Exception {
		
		System.out.println("Recebeu: "+origin +" " +destination );
		JobView job = new JobView();
	
	
		if (price < 0) {
			throw new BadPriceFault_Exception("Price not valid!", new BadPriceFault());
		}
	
		if(price > 100){
			return null;
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
		
		if ((Norte.contains(destination) || Centro.contains(destination) || Sul.contains(destination) )&( Norte.contains(origin) || Centro.contains(origin) || Sul.contains(origin))) {
			if(isPar){//se for uma instancia par
				if(Sul.contains(destination) || Sul.contains(origin)){ //caso seja par, nao pode ter nada do sul
					return null;
				}
			}
			else{ //se for uma instancia impar
				if(Norte.contains(destination) || Norte.contains(origin)){ //caso seja impar, nao pode ter nada do norte
					return null;
				}
			}
		}
		else{
			throw new BadLocationFault_Exception("Invalid Location", new BadLocationFault());
			//return null;
		}
		
		jobs.add(job); //caso nao haja erros, adiciona o job que iniciou ao arraylist de jobs
		
		//array to keep creation date
		
		if(price <= 10){
			job.setJobPrice(price-1);
		}
		else{
			if(isPar){ // se for uma instancia par
				if(price%2 == 0){ //se o preco for par, vai oferecer menos do que recebido
					int min = 1;
					int max = price-1;
					int offer = min + (int)(Math.random() * ((max - min) + 1));
					job.setJobPrice(offer);
				}
				else{ // se o preco nao for par vai oferecer mais do que recebido
					int min = price+1;
					int max = 101;
					int offer = min + (int)(Math.random() * ((max - min) + 1));
					job.setJobPrice(offer);
				}
			}
			else{
				if(price%2 == 1){ //se o preco for impar, vai oferecer menos do que recebe
					int min = 1;
					int max = price -1;
					int offer = min + (int)(Math.random() * ((max - min) + 1));
					job.setJobPrice(offer);
				}
				else{ //se o preco nao for impar, vai oferecer mais do que recebido
					int min = price+1;
					int max = 101;
					int offer = min + (int)(Math.random() * ((max - min) + 1));
					job.setJobPrice(offer);
					
				}
			}
		}
		
		String id = destination+"/"+origin+"/"+price;
		job.setJobIdentifier(id);
		
		return job;
		
		
	}
	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		JobView toReturn = null;
		for(JobView job: jobs){
			if(job.getJobIdentifier().equals(id)){ //percorre os jobs
				if(accept){// se aceitou
					job.setJobState(JobStateView.ACCEPTED);
					toReturn = job;
					Date datejob = new Date();
					
					creationDates.add(datejob);
					break;
				}
				else{ // se rejeitou
					job.setJobState(JobStateView.REJECTED);
					Date datejob = new Date();
					
					creationDates.add(datejob);
					toReturn = job;
					break;
				}
			}
		}
		if(toReturn == null){// se o id estiver errado
			throw new BadJobFault_Exception("Wrong ID", new BadJobFault());
		}
		return toReturn;
	}

	@Override
	public JobView jobStatus(String id) {
		
		
		System.out.println("vai ver o status");
		List<JobView> jobList = listJobs();
		
		
		Date timer = null;
		Random r1 = new Random();
		Random r2 = new Random();
		Random r3 = new Random();
		
		int rTime1 = r1.nextInt((5000-0)+1 +0);
		int rTime2 = r2.nextInt((5000-0)+1 +0);
		int rTime3 = r3.nextInt((5000-0)+1 +0);
		System.out.println("chega auq");

		for(JobView job: jobs){
			for(Date date : creationDates)
				if(job.getJobIdentifier().equals(id)){ //percorre os jobs
					if(job.getJobState() == JobStateView.ACCEPTED){
						timer = new Date();
						job.setJobState(JobStateView.HEADING);
						if(timer.getTime() - date.getTime() > (rTime1 + rTime2)){
							job.setJobState(JobStateView.ONGOING);
							if(timer.getTime() - date.getTime() > (rTime1 + rTime2 + rTime3))
								job.setJobState(JobStateView.COMPLETED);
						}
					}
				return job;
			}
		}
		return null;
	}

	@Override
	public List<JobView> listJobs() { //retorna a lista de jobs 
		System.out.println("vai fazer o list");
		if(jobs.isEmpty() == false){
			return jobs;
		}
		return null;
	}

	@Override
	public void clearJobs() { //apaga todos os jobs que ja foram criados , da Lista
		System.out.println(jobs.size());
		jobs.clear();
		System.out.println(jobs.size());
		
	}


}
