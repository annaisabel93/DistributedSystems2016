package pt.upa.broker;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.upa.broker.BrokerApplication;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerApplication {

	public List<String> TransportStatus(){
		
		List<String> Status = new ArrayList<String>();
		Status.add("REQUESTED");
		Status.add("BUDGETED");
		Status.add("BOOKED");
		Status.add("FAILED");
		Status.add("HEADING");
		Status.add("ONGOING");
		Status.add("COMPLETED");
		
		
		return Status;
		
	}
	public static void main(String[] args) throws Exception {
		
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");

		if(args.length<3)
		{
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		
		boolean secundary = false;
		
		int instance = Integer.parseInt(args[3]);
		if(instance == 2){
			secundary = true;
		}

		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;try

		{
			BrokerPort port = new BrokerPort(uddiURL, name, url, secundary);
			endpoint = Endpoint.create(port);

			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			System.out.println(secundary);
			Thread.sleep(1000);
			boolean isAlive = true;
			boolean wasBorn = false;
			if (secundary == true){
				while(isAlive == true){
					Thread.sleep(5000);
						try{
							BrokerClient client = new BrokerClient("http://localhost:8080/broker-ws/endpoint");
							client.ping("teste");
							wasBorn = true;
						}
						catch( WebServiceException e){
							System.out.println("caught exception");
							if(wasBorn == true){
								isAlive = false;
							}
						}
				}
			}
			
			
			//REPLICAR O PRIMARIO EM LIGACAO------------teste1-------------------
			String teste = "http://localhost:8080/broker-ws/endpoint";
			/*if(secundary == true){
				
				System.out.println("Vai comecar a replicar o porto do primario");
				endpoint.stop();
				endpoint = Endpoint.create(teste);
				System.out.println("a meio da replicacao.....");
				System.out.printf("Starting %s%n", teste);
				endpoint.publish(teste);
			}*/
			
			
			// publish to UDDI
			Thread.sleep(1000);
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			//String teste = "http://localhost:8080/broker-ws/endpoint";
			uddiNaming.rebind("UpaBroker8", "http://localhost:8080/broker-ws/endpoint");
			System.out.println("Rebind name: "+ name+ " |url: "+ url);
			//System.out.println("url: " + teste);
			// wait
			
			//TESTING_---------------------------teste 2---------------------------------------------
			System.out.println("secundary? " + secundary);
			if(secundary == true){
				String endpointURL = "http://localhost:8080/broker-ws/endpoint";
				//BindingProvider bp = (BindingProvider)port;
				Endpoint.publish("http://localhost:8080/broker-ws/endpoint", port);
			}
			
			//---------------------------------------------------------------------------------
			
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			System.in.read();

		} catch(

				Exception e)

		{
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally

		{
			try {
				if (endpoint != null) {
					// stop endpoint
					endpoint.stop();
					System.out.printf("Stopped %s%n", url);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(name);
					uddiNaming.unbind("UpaBroker8");
					System.out.printf("Deleted '%s' from UDDI%n", name);
					System.exit(0);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}

	}



}
