package fi.eis.applications.jboss.poc.osgiservice.gemini.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.osgi.service.jndi.JNDIContextManager;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageDBStore;
import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

public abstract class AbstractJDBCServiceBean implements MessageService, MessageDBStore {
    protected JNDIContextManager jndiContextManager;

    public void bindJNDIService(final JNDIContextManager jndiContextManager) {
        this.jndiContextManager = jndiContextManager;
        log.info(jndiContextManager + " is linked");
    }

    public void unbindJNDIService(final JNDIContextManager jndiContextManager) {
        this.jndiContextManager = null;
        log.info("jndiContextManager is unlinked");
    }
    protected abstract DataSource getDataSource();
    protected abstract Connection getConnection(final DataSource ds);

    protected static Logger log = Logger.getLogger(AbstractJDBCServiceBean.class);
    
	@Override
	public Long persistMessage(String message) {
		DataSource ds = null;
		Connection conn = null;

		try {
			ds = getDataSource();
			conn = getConnection(ds);
			return addMessage(conn, message);
		} finally {
			close(conn);
		}
	}
	
	protected abstract String getCreateMessageTableSQL();
	protected abstract String getCreateSequenceSQL();
	protected abstract String getInsertMessageSQL();

	protected Long addMessage(Connection conn, String message) {
		final String CREATE_TABLE = getCreateMessageTableSQL();
		final String CREATE_SEQUENCE = getCreateSequenceSQL();
		final String INSERT_DATA = getInsertMessageSQL();

		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			try {
				if (CREATE_TABLE != null && (CREATE_TABLE.length() > 0))
					st.execute(CREATE_TABLE);
			} catch (SQLException ex) {
				log.info("exception creating a table, we don't really care: " + ex.getMessage(), ex);
			}
			try {
				if (CREATE_SEQUENCE != null && (CREATE_SEQUENCE.length() > 0)) 
					st.execute(CREATE_SEQUENCE);
			} catch (SQLException ex) {
				log.info("exception creating a sequence, we don't really care: " + ex.getMessage());
			}
			ps = conn.prepareStatement(INSERT_DATA, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, message);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys(); // or SELECT IDENTITY() for HSQLDB
			if (rs.next()) {
				return rs.getLong(1);
			}
			throw new IllegalStateException("Couldn't get result id");
		} catch (final SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(rs);
			close(ps);
			close(st);
		}
	}

	@Override
	public String getMessage(Long id) {
		DataSource ds = null;
		Connection conn = null;

		try {
			ds = getDataSource();
			conn = getConnection(ds);
			return getMessage(conn, id);
		} finally {
			close(conn);
		}
	}	
	
	private String getMessage(Connection conn, Long id) {
		final String GET_NAME =
			"SELECT name FROM message_data WHERE id = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(GET_NAME);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (final SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(rs);
			close(ps);
		}
		return "";
	}

	@Override
	public String getMessage() {

		DataSource ds = null;
		Connection conn = null;

		try {
			ds = getDataSource();
			conn = getConnection(ds);
			addData(conn);
			return getResultFrom(conn, "Hello%");
		} catch (final IllegalStateException ex) {
			log.error("error working with data source", ex);
			return "Error working with data source: " + ex.getMessage();
		} finally {
			close(conn);
		}
	}



	/**
	 * For the sake of testing, we do a roundtrip to database here.
	 *
	 * @param conn connection to the database containing data
	 * @param searchWord what word do we want to search with
	 * @return what we were able to find
	 */
	private String getResultFrom(final Connection conn, final String searchWord) {
		final String GET_NAME =
			"SELECT name FROM static_test_data WHERE name LIKE ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(GET_NAME);
			ps.setString(1, searchWord);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (final SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(rs);
			close(ps);
		}
		return "";
	}

	private void addData(final Connection conn) {
		//final String DROP_PREV_TABLE = "DROP TABLE static_test_data";
		//final String CREATE_TABLE = "CREATE TABLE static_test_data(id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))";
		final String INSERT_DATA = "INSERT INTO static_test_data(name) VALUES('Hello_Worrld')";

		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(INSERT_DATA);
		} catch (final SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			close(st);
		}

	}

	private static void close(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (final SQLException e) {
				throw new IllegalStateException("Couldn't close", e);
			}
		}
	}

	private static void close(final Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (final SQLException e) {
				throw new IllegalStateException("Couldn't close", e);
			}
		}
	}

	private static void close(final PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (final SQLException e) {
				throw new IllegalStateException("Couldn't close", e);
			}
		}
	}
	private static void close(final Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (final SQLException e) {
				throw new IllegalStateException("Couldn't close", e);
			}
		}
	}
	

}
