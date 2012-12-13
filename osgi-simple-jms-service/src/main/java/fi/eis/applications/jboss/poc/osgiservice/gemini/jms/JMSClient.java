package fi.eis.applications.jboss.poc.osgiservice.gemini.jms;

import java.util.Date;

import org.jboss.logging.Logger;
import org.osgi.service.jndi.JNDIContextManager;

import fi.eis.applications.jboss.poc.osgiservice.api.ConvertService;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.NamingException;

public class JMSClient implements ConvertService {
    private static final Logger log = Logger.getLogger(JMSClient.class.getName());

    private JNDIContextManager jndiContextManager;

    public void bindJNDIService(final JNDIContextManager jndiContextManager) {
        this.jndiContextManager = jndiContextManager;
        log.info(jndiContextManager + " is linked");
    }

    public void unbindJNDIService(final JNDIContextManager jndiContextManager) {
        this.jndiContextManager = null;
        log.info("jndiContextManager is unlinked");
    }    
    // Set up all the default values
    private static final String DEFAULT_CONNECTION_FACTORY = "RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "queue/test";
    private static final String DEFAULT_USERNAME = "myguest";
    private static final String DEFAULT_PASSWORD = "guestpass";

	@Override
	public String convertMessage(String messageToConvert) {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        Destination destination = null;
        TextMessage message = null;
        Context context = null;

        try {
            // Set up the context for the JNDI lookup
            context = jndiContextManager.newInitialContext();

            // Perform the JNDI lookups
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            destination = (Destination) context.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");

            // Create the JMS connection, session, producer, and consumer
            connection = connectionFactory.createConnection(
            		System.getProperty("username", DEFAULT_USERNAME),
            		System.getProperty("password", DEFAULT_PASSWORD));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            connection.start();

            String content = messageToConvert;

            message = session.createTextMessage(content + new Date().toString());
            producer.send(message);

            return "Sent!";
            
        } catch (JMSException e) {
			log.error("exception with jms", e);
			return e.toString();
		} catch (NamingException e) {
			log.error("exception with lookup", e);
			return e.toString();
		}  finally {
            if (context != null) {
                try {
					context.close();
				} catch (NamingException e) {
					throw new IllegalStateException(e);
				}
            }

            // closing the connection takes care of the session, producer, and consumer
            if (connection != null) {
                try {
					connection.close();
				} catch (JMSException e) {
					throw new IllegalStateException(e);
				}
            }
        }
	}

}
