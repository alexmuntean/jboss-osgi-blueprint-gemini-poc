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

public class SimpleJTAService implements ConvertService {
	
	private UserTransaction userTransactionService;
	private TransactionManager transactionManager;

	public void unbindTransactionManager(final TransactionManager transactionManager) {
		this.transactionManager = null;
	}

	public void bindTransactionManager(final TransactionManager transactionManager) {
		log.info("Registered transaction manager");
		this.transactionManager = transactionManager;
	}

	public void bindUserTransactionService(final UserTransaction userTransactionService) {
		log.info("Registered user transaction service");
		this.userTransactionService = userTransactionService;
	}
	public void unbindUserTransactionService(final UserTransaction userTransactionService) {
		this.userTransactionService = null;
	}
	
	
	private Logger log = Logger.getLogger(SimpleJTAService.class);

	/**
	 * Example from
	 * @url https://github.com/jbosgi/jbosgi/blob/master/testsuite/example/src/test/java/org/jboss/test/osgi/example/jta/TransactionTestCase.java
	 */
	@Override
	public String convertMessage(String message) {
		
		TransactionalMessage txObj = new TransactionalMessage();

		try {
			userTransactionService.begin();

			Transaction tx = transactionManager.getTransaction();
			
			log.debug("tx=" + tx);

			tx.registerSynchronization(txObj);

			txObj.setMessage(message + " - pls donate $1.000.000");
			log.debug("message=" + txObj.getMessage());

			userTransactionService.commit();
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (HeuristicMixedException e) {
			throw new IllegalStateException(e);
		} catch (HeuristicRollbackException e) {
			throw new IllegalStateException(e);
		} catch (NotSupportedException e) {
			throw new IllegalStateException(e);
		} catch (SystemException e) {
			throw new IllegalStateException(e);
		} catch (RollbackException e) {
			throw new IllegalStateException(e);
		}

		return txObj.getMessage();
	}

}
