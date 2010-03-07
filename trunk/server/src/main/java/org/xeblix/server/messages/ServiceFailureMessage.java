package org.xeblix.server.messages;

import org.apache.commons.lang.StringUtils;
import org.xeblix.server.util.MessagesEnum;

public class ServiceFailureMessage implements Message {

	private static final long serialVersionUID = -1911056955965409571L;

	private String failedService;
	
	public ServiceFailureMessage(String failedService){
		failedService = StringUtils.trimToNull(failedService);
		if(failedService == null){
			throw new IllegalArgumentException("This method does not " +
				"accept null parameters.");
		}
		this.failedService = failedService;
	}
	
	public MessagesEnum getType() {
		return MessagesEnum.SERVICE_FAILURE;
	}

	public String getFailedService() {
		return failedService;
	}
}
