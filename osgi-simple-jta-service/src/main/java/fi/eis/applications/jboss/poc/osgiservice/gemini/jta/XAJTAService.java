package fi.eis.applications.jboss.poc.osgiservice.gemini.jta;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

import fi.eis.applications.jboss.poc.osgiservice.api.ConvertService;
import fi.eis.applications.jboss.poc.osgiservice.api.JMSMessageSender;
import fi.eis.applications.jboss.poc.osgiservice.api.MessageDBStore;

public class XAJTAService implements ConvertService {
	private static final String FAIL_MESSAGE = "fail";
	
	private MessageDBStore dbStore;
	private JMSMessageSender jmsSender;
	private UserTransaction userTransactionService;
	private TransactionManager transactionManager;

	public void unbindTransactionManager(final TransactionManager transactionManager) {
		this.transactionManager = null;
	}

	public void bindTransactionManager(final TransactionManager transactionManager) {
		log.info("Registered transaction manager");
		this.transactionManager = transactionManager;
	}

	public void bindJDBCService(final MessageDBStore newStore) {
		log.info("Registered jdbc service");
		this.dbStore = newStore;
	}
	
	public void unbindJDBCService(final MessageDBStore newStore) {
		this.dbStore = null;
	}
	
	public void bindJMSService(final JMSMessageSender jmsSender) {
		log.info("Registered jms sender");
		this.jmsSender = jmsSender;
	}
	
	public void unbindJMSService(final JMSMessageSender jmsSender) {
		this.jmsSender = null;
	}
	
	public void bindUserTransactionService(final UserTransaction userTransactionService) {
		log.info("Registered user transaction service");
		this.userTransactionService = userTransactionService;
	}
	public void unbindUserTransactionService(final UserTransaction userTransactionService) {
		this.userTransactionService = null;
	}
	
	private Logger log = Logger.getLogger(this.getClass());

	@Override
	public String convertMessage(String message) {

		TransactionalMessage txObj = new TransactionalMessage();

		try {
			userTransactionService.begin();

			Transaction tx = transactionManager.getTransaction();
			
			log.debug("tx=" + tx);
			
			tx.registerSynchronization(txObj);

			String ourMessage = message + "XA! - pls donate $1.000.000";
			txObj.setMessage(ourMessage);
			log.info("message before commit=" + txObj.getMessage());
			
			// no need for explicit xa datasource here, JBoss does that:
			// http://www.coderanch.com/t/463211/JBoss/org-jboss-resource-adapter-jdbc
			dbStore.persistMessage(ourMessage);
			jmsSender.sendMessage(ourMessage);
			
			if (FAIL_MESSAGE.equals(message)) {
				log.info("fail message detected, rolling back");
				userTransactionService.rollback();				
				log.info("rolled back, message=" + txObj.getMessage());
			} else {
				userTransactionService.commit();
				log.info("message after commit=" + txObj.getMessage());
			}
			
		} catch (SecurityException e) {
			rollBack(userTransactionService);
			throw new IllegalStateException(e);
		} catch (HeuristicMixedException e) {
			rollBack(userTransactionService);
			throw new IllegalStateException(e);
		} catch (HeuristicRollbackException e) {
			rollBack(userTransactionService);
			throw new IllegalStateException(e);
		} catch (NotSupportedException e) {
			rollBack(userTransactionService);
			throw new IllegalStateException(e);
		} catch (SystemException e) {
			rollBack(userTransactionService);
			throw new IllegalStateException(e);
		} catch (RollbackException e) {
			rollBack(userTransactionService);
			throw new IllegalStateException(e);
		}

		return txObj.getMessage();

	}

	private void rollBack(UserTransaction userTransaction) {
		try {
			userTransaction.rollback();
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Exception rolling back", e);
		} catch (SecurityException e) {
			throw new IllegalStateException("Exception rolling back", e);
		} catch (SystemException e) {
			throw new IllegalStateException("Exception rolling back", e);
		}
		
	}
}
