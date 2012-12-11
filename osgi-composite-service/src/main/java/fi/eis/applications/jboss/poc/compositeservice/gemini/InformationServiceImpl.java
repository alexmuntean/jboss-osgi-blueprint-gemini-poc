package fi.eis.applications.jboss.poc.compositeservice.gemini;

import fi.eis.applications.jboss.poc.compositeservice.gemini.api.InformationService;
import fi.eis.applications.jboss.poc.osgiservice.api.MessageService;

public class InformationServiceImpl implements InformationService {

	private MessageService messageService;
	
	public InformationServiceImpl(MessageService service) {
		messageService = service;
	}
	
	@Override
	public String getMessage() {
		return "Composite:" + messageService.getMessage();
	}

}
