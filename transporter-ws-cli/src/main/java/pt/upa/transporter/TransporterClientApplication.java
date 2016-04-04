package pt.upa.transporter;

import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

        public static void main(String[] args) throws Exception {
                System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
                TransporterClient client = new TransporterClient("http://localhost:8081/transporter-ws/endpoint");
                String result = client.ping("client");
                System.out.println(result);
        }
}
