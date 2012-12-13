package fi.eis.applications.jboss.poc.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

/**
 * Message-Driven Bean implementation class for: SimpleTopicListeningBean
 *
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Topic"
		), @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "topic/test"
		) }, 
		mappedName = "topic/Test")
public class SimpleTopicListeningBean implements MessageListener {
	static final Logger log = Logger.getLogger(SimpleTopicListeningBean.class);
    /**
     * Default constructor. 
     */
    public SimpleTopicListeningBean() {

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
