package pt.upa.broker;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Endpoint;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.upa.broker.BrokerApplication;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerApplication {

	//FIXME not sure if here
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
							System.out.println("Vai fazer o try");
							BrokerClient client = new BrokerClient("http://localhost:8080/broker-ws/endpoint");
							client.ping("teste");
							wasBorn = true;
						}
						catch( Exception e){
							if(wasBorn == true){
								isAlive = false;
							}
						}
				}
			}
			
			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);

			// wait
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
					System.out.printf("Deleted '%s' from UDDI%n", name);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}

	}



}
