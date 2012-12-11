package fi.eis.applications.jboss.poc.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fi.eis.applications.jboss.poc.compositeservice.gemini.api.InformationService;

/**
 * A simple stateless session bean.
 */
@Stateless
@LocalBean
public class SimpleStatelessSessionBean {

  static final Logger log = Logger.getLogger(SimpleStatelessSessionBean.class);

  @Resource
  BundleContext context;

  private InformationService service;

  @PostConstruct
  public void init() {

    final SimpleStatelessSessionBean bean = this;

    ServiceTracker tracker = new ServiceTracker(context,
        InformationService.class.getName(), null) {

      @Override
      public Object addingService(final ServiceReference sref) {
        log.infof("Adding service: %s to %s", sref, bean);
        service = (InformationService) super.addingService(sref);
        return service;
      }

      @Override
      public void removedService(final ServiceReference sref, final Object sinst) {
        super.removedService(sref, service);
        log.infof("Removing service: %s from %s", sref, bean);
        service = null;
      }
    };
    tracker.open();
  }

  public String getMessage() {

    if (service == null)
      throw new IllegalStateException("Service not available");

    return "EJB:" + service.getMessage();
  }
}