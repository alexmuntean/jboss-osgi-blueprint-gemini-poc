package fi.eis.applications.jboss.poc.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

/**
 * @url http://localhost:8080/jboss-poc-webapp/HelloWorld
 */
@SuppressWarnings("serial")
@WebServlet("/HelloWorld")
public class HelloWorldServlet extends HttpServlet {

  private static Logger log = Logger.getLogger(HelloWorldServlet.class);

  @Resource
  private MessageService service;

  @Resource
  private BundleContext context;

  @Override
  public void init(final ServletConfig config) throws ServletException {
	  super.init(config);
	  log.debug("Initialized");
  }

  public void setMessageService(MessageService service) {
	  log.debug("Setting service: " + service);
	  this.service = service;
  }
  public MessageService getMessageService() {
	  return this.service;
  }  
  
  private static String PAGE_HEADER = "<html><head><title>helloworld</title><body>";
  private static String PAGE_FOOTER = "</body></html>";

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
	List<String> bundles = new ArrayList<String>();
	for (Bundle bundle : context.getBundles()) {
		bundles.add(bundle.getSymbolicName() + ":" + bundle.getVersion().toString());
	}
	Collections.sort(bundles);
	  
    resp.setContentType("text/html");
    PrintWriter writer = resp.getWriter();
    writer.println(PAGE_HEADER);
    writer.println("<h1>" + (service != null ? service.getMessage() : "null") + "</h1>");
    writer.println("Using BundleContext looked up in Servlet Context to list available OSGi Bundles:");
	writer.println("<ul>");
    for (String bundleInfo : bundles) {
    	writer.print("<li>");
        writer.print(bundleInfo);
    	writer.println("</li>");
    }
	writer.println("</ul>");
    writer.println(PAGE_FOOTER);
    writer.close();
  }

}
