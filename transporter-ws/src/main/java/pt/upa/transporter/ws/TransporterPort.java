package pt.upa.transporter.ws;
import java.util.Date;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;




@WebService(
    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
    wsdlLocation="transporter.1_0.wsdl",
    name="TransporterWebService",
    portName="TransporterPort",
    targetNamespace="http://ws.transporter.upa.pt/",
    serviceName="TransporterService"
)

//@HandlerChain(file = "/handler-chain.xml")

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
		return this.jobs;
	}

	@Override
	public String ping(String name) {
		return "Transporter";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)throws BadLocationFault_Exception, BadPriceFault_Exception {
		
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
		job.setJobOrigin(origin);
		job.setJobDestination(destination);
		job.setJobState(JobStateView.PROPOSED);
		
		String id = origin+destination+price;
		for(JobView job1: this.jobs){
			if(job1.getJobIdentifier().equals(id)){ //percorre os jobs
				id=id+"1";
			}
		}
		job.setJobIdentifier(id);
		this.jobs.add(job); //caso nao haja erros, adiciona o job que iniciou ao arraylist de jobs
		Date datejob = new Date();
		creationDates.add(datejob);
		
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
		
		
		return job;
		
		
	}
	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		int x= 0;
		if(id == null){
			throw new BadJobFault_Exception("null id", null);
		}
		JobView toReturn = null;
		if(this.jobs.isEmpty()){
			return null;
		}
		boolean found = false;
		for(JobView job: this.jobs){
			if(job.getJobIdentifier().equals(id)){ //percorre os jobs
				found = true;
				if(job.getJobState() != null){
					if(job.getJobState() != JobStateView.PROPOSED){
						throw new BadJobFault_Exception("Already decided", null);
					}
				}
				if(accept){// se aceitou
					job.setJobState(JobStateView.ACCEPTED);
					toReturn = job;
					this.creationDates.set(this.jobs.indexOf(job), new Date());
					break;
				}
				else{ // se rejeitou
					job.setJobState(JobStateView.REJECTED);
					this.creationDates.set(this.jobs.indexOf(job), new Date());
					toReturn = job;
					break;
				}
			}
		x++;	
		}
		if(found == false){
			throw new BadJobFault_Exception("ID invalido", null);
		}
		if(toReturn == null && x == 0){// se o id estiver errado
			throw new BadJobFault_Exception("Wrong ID", new BadJobFault());
		}
		return toReturn;
	}

	@Override
	public JobView jobStatus(String id) {
		
	
		
		
		Date timer = null;
		Random r1 = new Random();
		Random r2 = new Random();
		Random r3 = new Random();
		
		int rTime1 = r1.nextInt((5000-0)+1 +0);
		int rTime2 = r2.nextInt((5000-0)+1 +0);
		int rTime3 = r3.nextInt((5000-0)+1 +0);
		if(this.jobs.isEmpty()){
			return null;
		}
		for(JobView job: this.jobs){
				if(job.getJobIdentifier().equals(id)){ //percorre os jobs
					if(job.getJobState() == JobStateView.ACCEPTED){
						timer = new Date();
						
						Date origin = this.creationDates.get(this.jobs.indexOf(job));
						if((timer.getTime() - origin.getTime()) > 3000 ){
							job.setJobState(JobStateView.HEADING);
							this.creationDates.set(this.jobs.indexOf(job), new Date());
						}
					}
					if(job.getJobState() == JobStateView.HEADING){
						timer = new Date();
						Date origin = creationDates.get(this.jobs.indexOf(job));
						if((timer.getTime() - origin.getTime()) > 3000 ){
							job.setJobState(JobStateView.ONGOING);
							this.creationDates.set(this.jobs.indexOf(job), new Date());
						}
					}
					if(job.getJobState() == JobStateView.ONGOING){
						timer = new Date();
						Date origin = creationDates.get(this.jobs.indexOf(job));
						if((timer.getTime() - origin.getTime()) > 3000 ){
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
		if(jobs.isEmpty() == false){
			return jobs;
		}
		return null;
	}

	@Override
	public void clearJobs() { //apaga todos os jobs que ja foram criados , da Lista
		jobs.clear();
		creationDates.clear();
		
	}


}
