package fi.eis.applications.jboss.poc.osgiservice.api;

public interface MessageDBStore {
	public Long persistMessage(String message);
	public String getMessage(Long id);
}
