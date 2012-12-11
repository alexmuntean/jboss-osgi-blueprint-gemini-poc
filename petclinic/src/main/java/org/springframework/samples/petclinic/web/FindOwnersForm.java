
package org.springframework.samples.petclinic.web;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

/**
 * JavaBean Form controller that is used to search for <code>Owner</code>s by
 * last name.
 * 
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author eis
 */
@Controller
public class FindOwnersForm {

	private final Clinic clinic;

	// not final - cannot be, as services come and go at runtime
	private MessageService service;

	@Autowired
	public FindOwnersForm(Clinic clinic) {
		this.clinic = clinic;
		
		// TODO is this possible with just Blueprint XML configuration?
		BundleContext bundleContext = FrameworkUtil.getBundle(MessageService.class).getBundleContext();
		
		ServiceTracker serviceTracker = new ServiceTracker(bundleContext, MessageService.class.getName(), null){
	           @Override
	           public Object addingService(final ServiceReference sref) {
	             service = (MessageService) super.addingService(sref);
	             return service;
	           }

	           @Override
	           public void removedService(final ServiceReference sref, final Object sinst) {
	             super.removedService(sref, service);
	             service = null;
	           }    	  
	       };  
	    serviceTracker.open(); 
		
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@RequestMapping(value = "/owners/search", method = RequestMethod.GET)
	public String setupForm(Model model) {
		model.addAttribute("owner", new Owner());
		return "owners/search";
	}

	@RequestMapping(value = "/owners", method = RequestMethod.GET)
	public String processSubmit(Owner owner, BindingResult result, Model model) {

		// allow parameterless GET request for /owners to return all records
		if (owner.getLastName() == null) {
			owner.setLastName(""); // empty string signifies broadest possible search
		}
		
		if (owner.getLastName().equalsIgnoreCase(service.getMessage())) {
			// search string is not allowed
			result.rejectValue("lastName", "notAllowed", "not allowed");
			return "owners/search";
		}

		// find owners by last name
		Collection<Owner> results = this.clinic.findOwners(owner.getLastName());
		if (results.size() < 1) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/search";
		}
		if (results.size() > 1) {
			// multiple owners found
			model.addAttribute("selections", results);
			return "owners/list";
		}
		else {
			// 1 owner found
			owner = results.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
	}

}
