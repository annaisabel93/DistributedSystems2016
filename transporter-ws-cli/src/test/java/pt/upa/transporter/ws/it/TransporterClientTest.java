package pt.upa.transporter.ws.it;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class TransporterClientTest {



	// static members


	// one-time initialization and clean-up


	/** mocked web service endpoint address */

	private static String wsURL = "http://localhost:9090";

	@BeforeClass
	public static void oneTimeSetUp() {

	}

	@AfterClass
	public static void oneTimeTearDown() {

	}



	// members


	// initialization and clean-up for each test

	// members



	/** used for the BindingProvider request context */

	Map<String,Object> contextMap = null;

	// initialization and clean-up for each test



	@Before

	public void setUp() {

		contextMap = new HashMap<String,Object>();

	}



	@After

	public void tearDown() {

		contextMap = null;

	}

}

