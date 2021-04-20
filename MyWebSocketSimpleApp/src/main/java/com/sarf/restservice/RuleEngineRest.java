package com.sarf.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("ruleengine/")
@Produces(MediaType.APPLICATION_JSON)
public interface RuleEngineRest {

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("partial")
	public Response partialUpdate();

	

}