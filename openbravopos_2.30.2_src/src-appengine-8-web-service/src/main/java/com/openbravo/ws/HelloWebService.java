package com.openbravo.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@Transactional(readOnly = true)
public class HelloWebService {
    
	private static final String DATA_FIELD = "data";
	private static final String ERROR_FIELD = "error";
	
	@Autowired
	private View jsonView_i;
	
	private String message = new String("Hello, ");

    public void HelloWebService() {}

    @RequestMapping(value = "/sayhello/{name}", method = RequestMethod.GET)
	public ModelAndView sayhello(@PathVariable("name") final String name){
    	
    	String retString = message + name;
		
		return new ModelAndView(jsonView_i, DATA_FIELD, retString);
	}
}