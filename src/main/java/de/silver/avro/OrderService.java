package de.silver.avro;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Jan on 30.04.2016.
 */
public class OrderService {
    public static class OrderImpl implements OrderProtocol {
        public String storeOrder(Order order) throws AvroRemoteException {
            System.out.println("Sending order: " + order.toString());

            if (!existsProduct(order.getProductId())) {
                throw new StoreException("Product doesn't exist!");
            }
            return "Sending order with productID: " + order.getProductId();
        }

        public boolean existsProduct(CharSequence productId) throws AvroRemoteException {
            if (productId.length() == 3)
                return false;
            return true;
        }
    }

    private static Server server;

    public static void startServer() throws IOException {
        server = new NettyServer(new SpecificResponder(OrderProtocol.class, new OrderImpl()), new InetSocketAddress(65111));
    }

    public static void closeServer() {
        server.close();
    }

    // Use the JUnit tests in src/test as they are dynamic and much better than this method
    // Call this method with: mvn -e exec:java -Dexec.mainClass=de.silver.avro.OrderService
    @Deprecated
    public static void main(String[] args) throws IOException {
        startServer();

        NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(65111));
        OrderProtocol proxy = SpecificRequestor.getClient(OrderProtocol.class, client);

        try {
            System.out.println("### Test case start ###");

            Order invalidOrder = new Order("123", "321", 3, "Test");
            Order validOrder = new Order("123", "3212", 3, "Test");
            // Change invalidOrder <-> validOrder to see test fail or not
            System.out.println("Result: " + proxy.storeOrder(validOrder));

            System.out.println("### Test case end ###");
        } catch(Exception ex) {
        } finally {
            client.close();
            server.close();
        }
    }
}
