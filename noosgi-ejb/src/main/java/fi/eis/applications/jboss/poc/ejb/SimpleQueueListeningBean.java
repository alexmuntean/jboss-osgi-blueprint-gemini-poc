package fi.eis.applications.jboss.poc.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

/**
 * Message-Driven Bean implementation class for: SimpleQueueListeningBean
 *
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"
		),@ActivationConfigProperty(
				propertyName = "destination", propertyValue = "queue/test"
		) }, 
		mappedName = "queue/Test")
public class SimpleQueueListeningBean implements MessageListener {

	static final Logger log = Logger.getLogger(SimpleQueueListeningBean.class);
	
    /**
     * Default constructor. 
     */
    public SimpleQueueListeningBean() {
        // no-op
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        try {
			log.info("Got message" + ((TextMessage)message).getText());
		} catch (JMSException e) {
			log.error("Problem reading a message from " + message.toString(), e);
		}
    }

}
