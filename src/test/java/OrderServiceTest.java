import de.silver.avro.Order;
import de.silver.avro.OrderProtocol;
import de.silver.avro.StoreException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

import static de.silver.avro.OrderService.closeServer;
import static de.silver.avro.OrderService.startServer;

/**
 * Created by Jan on 30.04.2016.
 */
public class OrderServiceTest {
    public static NettyTransceiver client;
    public static OrderProtocol proxy;

    @Before
    public void setUp() throws IOException {
        startServer();

        client = new NettyTransceiver(new InetSocketAddress(65111));
        proxy = SpecificRequestor.getClient(OrderProtocol.class, client);
    }

    @Test(expected = StoreException.class)
    public void storeOrderShouldFail() throws IOException {
        Order invalidOrder = new Order("123", "321", 3, "Test");
        System.out.println("Result: " + proxy.storeOrder(invalidOrder));

    }

    @Test
    public void storeOrderShouldNotFail() throws IOException {
        Order validOrder = new Order("123", "3212", 3, "Test");
        System.out.println("Result: " + proxy.storeOrder(validOrder));
    }

    @After
    public void tearDown() {
        client.close();
        closeServer();
    }
}
