package com.gieselaar.verzuimbeheer.verzuimweb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;

public class BackingBeanBase {
protected final static Logger logger = Logger.getLogger(BackingBeanBase.class);
@PostConstruct
	public void postconstructAction(){
		logger.debug("BackingBean created; " + this.getClass().toString());
	}
@PreDestroy
	public void predestructAction(){
		logger.debug("BackingBean destroyed; " + this.getClass().toString());
	}
}
