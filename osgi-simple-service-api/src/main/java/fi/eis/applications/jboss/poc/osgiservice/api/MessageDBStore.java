package fi.eis.applications.jboss.poc.osgiservice.api;

import javax.sql.XAConnection;

public interface MessageDBStore {
	public XAConnection getXAConnection(); 
	public Long persistMessage(String message);
	public Long persistMessageXA(XAConnection conn, String message);
	public String getMessage(Long id);
}
