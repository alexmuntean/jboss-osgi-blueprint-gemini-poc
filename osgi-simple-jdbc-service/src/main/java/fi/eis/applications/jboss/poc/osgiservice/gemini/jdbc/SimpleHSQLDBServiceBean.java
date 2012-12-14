package fi.eis.applications.jboss.poc.osgiservice.gemini.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jboss.logging.Logger;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageDBStore;
import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

public class SimpleHSQLDBServiceBean extends AbstractJDBCServiceBean
	implements MessageService, MessageDBStore {

    private static Logger log = Logger.getLogger(SimpleHSQLDBServiceBean.class);

	private static final String JBOSS_DEFAULT_DATA_SOURCE_JNDI_NAME
		= "java:jboss/datasources/ExampleDS";

	private static final String JBOSS_DEFAULT_DATA_SOURCE_PASS
		= "sa";

	private static final String JBOSS_DEFAULT_DATA_SOURCE_USER
		= "sa";


	protected Connection getConnection(final DataSource ds) {
		try {
			return ds.getConnection(JBOSS_DEFAULT_DATA_SOURCE_USER, JBOSS_DEFAULT_DATA_SOURCE_PASS);
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
					.lookup(JBOSS_DEFAULT_DATA_SOURCE_JNDI_NAME);
		} catch (final NamingException e) {
			throw new IllegalStateException( "DS lookup failed", e);
		}
	}
	
	@Override
	protected String getCreateMessageTableSQL() {
		final String CREATE_TABLE = "CREATE TABLE message_data(id INT PRIMARY KEY, name VARCHAR)";
		return CREATE_TABLE;
	}


	@Override
	protected String getCreateSequenceSQL() {
		final String CREATE_SEQUENCE = "CREATE SEQUENCE message_data_seq";
		return CREATE_SEQUENCE;
	}


	@Override
	protected String getInsertMessageSQL() {
		final String INSERT_DATA = "INSERT INTO message_data VALUES(NEXT VALUE FOR test2_seq, ?)";
		return INSERT_DATA;
	}

}
