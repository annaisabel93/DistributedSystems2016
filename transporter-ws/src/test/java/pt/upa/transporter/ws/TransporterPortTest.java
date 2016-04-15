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
			System.out.println("[TestRequestJob] Invalid location");
		} catch (BadPriceFault_Exception e) {
			System.out.println("[TestRequestJob] Invalid price");
		}
		
	}
	
	@Test
	public void testDecideJob() {
		try {
			
			TransporterPort localPort1 = new TransporterPort(true);
			JobView job1 = localPort1.requestJob("Porto", "Lisboa", 11);
			String id1 = job1.getJobIdentifier();
			JobView decidedJob1 = localPort1.decideJob(id1, true);
			assertEquals(JobStateView.ACCEPTED, decidedJob1.getJobState());
			
			TransporterPort localPort2 = new TransporterPort(true);
			JobView job2 = localPort2.requestJob("Porto", "Lisboa", 11);
			String id2 = job2.getJobIdentifier();
			JobView decidedJob2 = localPort2.decideJob(id2, false);
			assertEquals(JobStateView.REJECTED, decidedJob2.getJobState());
			
		} catch (BadLocationFault_Exception e) {
			System.out.println("[TestDecideJob] Invalid location!");
		} catch (BadPriceFault_Exception e) {
			System.out.println("[TestDecideJob] Invalid price!");
		} catch (BadJobFault_Exception e) {
			System.out.println("[TestDecideJob] Invalid job!");
		}
	}
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
