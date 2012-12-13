package fi.eis.applications.jboss.poc.osgiservice.api;

public interface JMSMessageSender {
	public String sendMessage(String message);
}
