package pt.upa.broker;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.cli.BrokerClient;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class BrokerClientApplication {

    public static void main(String[] args) throws Exception {
        System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
        BrokerClient client = new BrokerClient("http://localhost:8080/broker-ws/endpoint");
        String result = client.ping("client");
        System.out.println(result);
        String request = client.requestTransport("Lisboa", "Faro", 100);
        //menu para testar apenas
        while(true){
        	System.out.println("Acao a realizar: ");
        	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            if(s.equals("request")){
            	String origin;
            	String destiny;
            	System.out.println("origin?");
            	origin = br.readLine();
            	System.out.println("destiny?");
            	destiny = br.readLine();
            	client.requestTransport(origin, destiny, 80);
            }
            if(s.equals("ping")){
            	client.ping("client");
            }
            if(s.equals("status")){
            	System.out.println("id do job?");
            		String id = br.readLine();
            		TransportView job  = null;
            		System.out.println("done");
            		job = client.viewTransport(id);
            		if(job == null){
            			System.out.println("Nada com esse id....");
            			continue;
            		}
            		System.out.println("job:");
            		System.out.println("origin: "+ job.getOrigin());
            		System.out.println("destiny: " + job.getDestination());
            		System.out.println(" price: " + job.getPrice());
            		System.out.println(" status: "+ job.getState());
            	}
            if(s.equals("clear")){
            	client.clearTransports();
            }
            if(s.equals("list")){
            	System.out.println(client.listTransports().size());
            }
            if(s.equals("exit")){
            	break;
            }
         }
       // System.out.println("correu tudo --------------------"+request);
      //  System.out.println("request 2: " + request2);
        }
      //  System.out.println("request 2: " + request2);
        
}

