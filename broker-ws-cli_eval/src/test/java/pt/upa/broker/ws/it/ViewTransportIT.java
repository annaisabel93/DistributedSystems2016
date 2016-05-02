package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class ViewTransportIT extends AbstractIT {

	// public TransportView viewTransport(String id)
	// throws UnknownTransportFault_Exception

	@Test
	public void testTransportStateTransition() throws Exception {
		List<TransportStateView> transportStates = new ArrayList<>();
		transportStates.add(TransportStateView.BOOKED);
		transportStates.add(TransportStateView.HEADING);
		transportStates.add(TransportStateView.ONGOING);
		transportStates.add(TransportStateView.COMPLETED);

		String transportId = CLIENT.requestTransport(CENTER_1, SOUTH_1, PRICE_SMALLEST_LIMIT);
		TransportView transportView = CLIENT.viewTransport(transportId);

		for (int t = DELAY_LOWER; t <= 3 * DELAY_UPPER; t = t + DELAY_LOWER) {
			Thread.sleep(DELAY_LOWER);
			transportView = CLIENT.viewTransport(transportId);
			if (transportStates.contains(transportView.getState()))
				transportStates.remove(transportView.getState());
		}
		assertEquals(0, transportStates.size());
	}
	
	@Test(expected = UnknownTransportFault_Exception.class)
	public void testViewInvalidTransport() throws Exception {
		CLIENT.viewTransport(null);
	}
	
	@Test(expected = UnknownTransportFault_Exception.class)
	public void testViewNullTransport() throws Exception {
		CLIENT.viewTransport(EMPTY_STRING);
	}

}
