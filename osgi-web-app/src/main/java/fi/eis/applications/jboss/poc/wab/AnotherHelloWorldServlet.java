package fi.eis.applications.jboss.poc.wab;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

/**
 * To test, this should show up in an url like
 * http://localhost:8080/osgi-webapp-gemini/hi_to_osgi
 */
@SuppressWarnings("serial")
public class AnotherHelloWorldServlet extends HttpServlet implements Servlet {


  private static Logger log = Logger.getLogger(AnotherHelloWorldServlet.class);

  private MessageService service = null;
  
  private BundleContext bundleContext;
  
  public void setMessageService(MessageService service) {
	  log.debug("Setting service: " + service);
	  this.service = service;
  }
  
  public void unsetMessageService() {
	  log.info("unset message service");
	  this.service = null;
  }
  
  // TODO use blueprint for this
  @Override
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
      
      bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
      
      
      final HttpServlet servlet = this;
      
      
     log.infof("Registering with parameters %s, %s, %s", bundleContext, MessageService.class.getName(), null);
      
      ServiceTracker serviceTracker = new ServiceTracker(bundleContext, MessageService.class.getName(), null){
          @Override
          public Object addingService(final ServiceReference sref) {
            log.infof("Adding service: %s to %s", sref, servlet);
            service = (MessageService) super.addingService(sref);
            return service;
          }

          @Override
          public void removedService(final ServiceReference sref, final Object sinst) {
            super.removedService(sref, service);
            log.infof("Removing service: %s from %s", sref, servlet);
            service = null;
          }    	  
      };  
      serviceTracker.open(); 
      
  }  

  static String PAGE_HEADER = "<html><head><title>helloworld</title><body>";
  static String PAGE_FOOTER = "</body></html>";

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html");
    PrintWriter writer = resp.getWriter();
    writer.println(PAGE_HEADER);
    writer.println("<h1>" + (service != null ? service.getMessage() : "null") + "</h1>");

    if (bundleContext == null)
    {
	    writer.print("BundleContext null, cannot use BundleContext to list available OSGi Bundles");
    } else
    {
	    writer.print("Using BundleContext looked up in Servlet Context to list available OSGi Bundles:");
	    listBundlesWith(bundleContext, writer);
    }
    
    writer.println(PAGE_FOOTER);
    writer.close();
  }

	private static void listBundlesWith(BundleContext context, PrintWriter writer) {
		List<String> bundles = new ArrayList<String>();
		for (Bundle bundle : context.getBundles()) {
			bundles.add(bundle.getSymbolicName() + ":" + bundle.getVersion().toString());
		}
		Collections.sort(bundles);
		List<String> services = new ArrayList<String>();
		try {
			for (ServiceReference ref : context.getAllServiceReferences(null, null)) {
				services.add(context.getService(ref).toString());
				context.ungetService(ref);
			}
		} catch (InvalidSyntaxException e) {
			throw new IllegalStateException(e);
		}
		Collections.sort(services);
		writer.println("<ul>");
	    for (String bundleInfo : bundles) {
	    	writer.print("<li>");
	        writer.print(bundleInfo);
	    	writer.println("</li>");
	    }
		writer.println("</ul>");
		writer.println("<ul>");
	    for (String service : services) {
	    	writer.print("<li>");
	        writer.print(service);
	    	writer.println("</li>");
	    }
		writer.println("</ul>");
		
	}

}