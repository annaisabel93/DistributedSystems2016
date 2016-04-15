package pt.upa.broker.ws;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BrokerPortTest {

	private BrokerPort localPort;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		localPort = new BrokerPort("http://localhost:9090", "UpaBroker", "http://localhost:8080/broker-ws/endpoint");
	}

	@After
	public void tearDown() throws Exception {
		localPort = null;
	}

//	@Test
//	public void testRequestTransport() {
//		try{
//			localPort.requestTransport("origin", "destination", 10);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		assertNotNull(localPort.getTransports());
//		TransportView transport = localPort.getTransports().get(0);
//		assertEquals("origin", transport.getOrigin());
//		assertEquals("destination", transport.getDestination());
//		assertEquals(Integer.valueOf(10), transport.getPrice());
//	}
	
//	@Test
//	public void testViewTransport() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testListTransports() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testClearTransports() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testPing() {
//		fail("Not yet implemented");
//	}
//	
}
