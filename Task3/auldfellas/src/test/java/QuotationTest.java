import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import service.core.ClientInfo;
import service.message.ClientMessage;
import service.message.QuotationMessage;

import javax.jms.*;

import static org.junit.Assert.assertEquals;

public class QuotationTest {

    @Test
    public void testService() throws Exception {
        // Start the main application
        Main.main(new String[0]);
        // Set up ActiveMQ connection
        ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.setClientID("test");

        System.out.println("DEBUG: Set up ActiveMQ connection ");

        // Create session, queue, and topic
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Queue queue = session.createQueue("QUOTATIONS");
        Topic topic = session.createTopic("APPLICATIONS");

        System.out.println("DEBUG: Created session, queue, and topic");

        // Create consumer and producer
        MessageConsumer consumer = session.createConsumer(queue);
        MessageProducer producer = session.createProducer(topic);

        System.out.println("DEBUG: Created the consumer and producer");

        // Start the connection
        connection.start();

        System.out.println("DEBUG: Started the connection");

        // Send a message
        producer.send(session.createObjectMessage(
                new ClientMessage(1L, new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false))
        ));

        System.out.println("DEBUG: Message sent");

        // Receive and process the message
        Message message = consumer.receive();
        System.out.println("DEBUG: Received and processed the message");

        QuotationMessage quotationMessage = (QuotationMessage) ((ObjectMessage) message).getObject();

        System.out.println("token: " + quotationMessage.getToken());
        System.out.println("quotation: " + quotationMessage.getQuotation());

        // Acknowledge and assert
        message.acknowledge();
        assertEquals(1L, quotationMessage.getToken());
    }
}
