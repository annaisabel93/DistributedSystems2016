package pt.upa.transporter.ws;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.upa.transporter.ws.TransporterPort;

public class TransporterPortTest {

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRequestJob() {
		try {
			
			// Trabalhos com preco maior que 100 deviam devolver null
			TransporterPort localPort1 = new TransporterPort(true);
			assertNull(localPort1.requestJob("Porto", "Lisboa", 1000));
			assertNull(localPort1.getJobs());
			
			// Trabalhos com isPar==true e Faro(sul) como destino ou origem deviam ser null
			TransporterPort localPort2 = new TransporterPort(true);
			assertNull(localPort2.requestJob("Faro", "Porto", 10));
			assertNull(localPort2.getJobs());
			
			// Trabalhos com isPar==false e Porto(norte) como destino ou origem deviam ser null
			TransporterPort localPort3 = new TransporterPort(false);
			assertNull(localPort3.requestJob("Porto", "Faro", 10));
			assertNull(localPort3.getJobs());
			
			// Trabalho com preco par deve oferecer menos que o preco dado
			TransporterPort localPort4 = new TransporterPort(true);
			assertNotNull(localPort4.requestJob("Porto", "Lisboa", 10));
			assertNotNull(localPort4.getJobs());
			assertTrue(localPort4.getJobs().get(0).getJobPrice() <= 10);
			
			// Trabalho com preco impar deve oferecer mais que o preco dado
			TransporterPort localPort5 = new TransporterPort(true);
			assertNotNull(localPort5.requestJob("Porto", "Lisboa", 11));
			assertNotNull(localPort5.getJobs());
			assertTrue(localPort5.getJobs().get(0).getJobPrice() >= 11);
			
		} catch (BadLocationFault_Exception e) {
			System.out.println("Invalid location");
		} catch (BadPriceFault_Exception e) {
			System.out.println("Invalid price");
		}
		
	}
	
//	@Test
//	public void testDecideJob() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testJobStatus() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testListJobs() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testClearJobs() {
//		fail("Not yet implemented");
//	}

	
}
