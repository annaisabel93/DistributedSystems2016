package pt.upa.broker;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.cli.BrokerClient;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class BrokerClientApplication {
	
	
	

	static BrokerClient client;
	
    public static void main(String[] args) throws Exception {
    	List<TransportView> transports = new ArrayList<TransportView>();
        System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
        client = new BrokerClient("http://localhost:8080/broker-ws/endpoint");
        //String result = client.ping("client");
      // System.out.println(result);
        String request = client.requestTransport("Lisboa", "Faro", 87);
        TransportView t = new TransportView();
        t.setDestination("Lisboa");
        t.setOrigin("Faro");
        t.setPrice(87);
        t.setId(request);
        //menu para testar apenas
        while(true){
        	//try{
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
	            	System.out.println("price?");
	            	String price_string = br.readLine();
	            	int price = Integer.parseInt(price_string);
	            	String id = client.requestTransport(origin, destiny, price);
	            	TransportView transport = new TransportView();
	                t.setDestination(origin);
	                t.setOrigin(destiny);
	                t.setPrice(price);
	                t.setId(id);
	                transports.add(transport);
	            }
	            if(s.equals("ping")){
	            	System.out.println("what do you want to send?");
	            	String ping_content = br.readLine();
	            	client.ping(ping_content);
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
	            	List<TransportView> transports1 = client.listTransports();
	            	System.out.println("size: " +client.listTransports().size());
	            	for(TransportView t1 : transports1){
	            		System.out.println("Origin: "+t1.getOrigin()+ "|Destination: "+t1.getDestination()+"|ID: "+t1.getPrice());
	            	}
	            }
	            if(s.equals("exit")){
	            	break;
	            }
        	//}
        	/*catch(Exception e){
        		System.out.println("exception");
        		client = new BrokerClient("http://localhost:8020/broker-ws/endpoint");
        	}*/
         }
       // System.out.println("correu tudo --------------------"+request);
      //  System.out.println("request 2: " + request2);
        }
      //  System.out.println("request 2: " + request2);
        
}

