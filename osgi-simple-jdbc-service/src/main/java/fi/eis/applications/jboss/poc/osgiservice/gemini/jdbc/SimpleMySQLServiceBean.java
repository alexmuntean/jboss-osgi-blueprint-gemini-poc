package fi.eis.applications.jboss.poc.osgiservice.gemini.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.jboss.logging.Logger;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageDBStore;
import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

public class SimpleMySQLServiceBean extends AbstractJDBCServiceBean
	implements MessageService, MessageDBStore {

    private static Logger log = Logger.getLogger(SimpleMySQLServiceBean.class);

	private static final String MYSQL_DATA_SOURCE_JNDI_NAME
		= "java:jboss/datasources/MySqlXADS";

	private static final String MYSQL_USER
	    = "root";
	private static final String MYSQL_PASS
		= "mysqlpass";

	protected Connection getConnection(final DataSource ds) {
		try {
			return ds.getConnection(MYSQL_USER, MYSQL_PASS);
		} catch (final SQLException e) {
			throw new IllegalStateException("Getting a connection failed", e);
		}
	}


	protected DataSource getDataSource() {

		log.debug("jndi context manager is " + jndiContextManager);

		// create a context with the default environment setup
		Context initialContext = null;
		try {
			initialContext = jndiContextManager.newInitialContext();
		} catch (final NamingException e) {
			throw new IllegalStateException("JNDI lookup failed", e);
		}

		log.debug("jndi initial context is " + initialContext);

		try {
			return (DataSource) initialContext
					.lookup(MYSQL_DATA_SOURCE_JNDI_NAME);
		} catch (final NamingException e) {
			throw new IllegalStateException( "DS lookup failed", e);
		}
	}
	protected XADataSource getXADataSource() {

		log.debug("jndi context manager is " + jndiContextManager);

		// create a context with the default environment setup
		Context initialContext = null;
		try {
			initialContext = jndiContextManager.newInitialContext();
		} catch (final NamingException e) {
			throw new IllegalStateException("JNDI lookup failed", e);
		}

		log.debug("jndi initial context is " + initialContext);

		try {
			return (XADataSource) initialContext
					.lookup(MYSQL_DATA_SOURCE_JNDI_NAME);
		} catch (final NamingException e) {
			throw new IllegalStateException( "DS lookup failed", e);
		}
	}
	
	@Override
	public XAConnection getXAConnection() {
		try {
			XADataSource ds = getXADataSource();
			return ds.getXAConnection(MYSQL_USER, MYSQL_PASS);
		} catch (final SQLException e) {
			throw new IllegalStateException("Getting an XA connection failed", e);
		}
    }

	@Override
	public Long persistMessageXA(XAConnection xa, String message) {
		Connection conn = null;
		try {
			conn = xa.getConnection();
		} catch (SQLException e) {
			throw new IllegalStateException("Couldn't get a connection from XA resource", e);
		}
		return addMessage(conn, message);
	}


	@Override
	protected String getCreateMessageTableSQL() {
		// cannot create tables in MySQL within transaction
		// temporary tables can be created, but not rolled back, so they're of no use here
		// either
		
		// http://dev.mysql.com/doc/refman/5.1/en/implicit-commit.html
		
		//final String CREATE_TABLE = "CREATE TABLE message_data(id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), time TIMESTAMP default CURRENT_TIMESTAMP)";
		final String CREATE_TABLE = "";
		return CREATE_TABLE;
	}


	@Override
	protected String getCreateSequenceSQL() {
		final String CREATE_SEQUENCE = ""; // no sequences in mysql
		return CREATE_SEQUENCE;
	}


	@Override
	protected String getInsertMessageSQL() {
		final String INSERT_DATA = "INSERT INTO message_data(name) VALUES(?)";
		return INSERT_DATA;
	}

}
