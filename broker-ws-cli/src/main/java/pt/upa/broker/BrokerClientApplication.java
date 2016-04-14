package pt.upa.broker;

import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

    public static void main(String[] args) throws Exception {
        System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
        BrokerClient client = new BrokerClient("http://localhost:8080/broker-ws/endpoint");
        String result = client.ping("client");
        System.out.println(result);
        String request = client.requestTransport("Lisboa", "Faro", 100);
       // String request2 = client.requestTransport("Lisboa", "Faro", 100);
        System.out.println("correu tudo --------------------"+request);
      //  System.out.println("request 2: " + request2);
        
        
//        String request2 = client.requestTransport("Porto", "Lisboa", 100);
//        System.out.println(request);
//        System.out.println(request2);
}

}
