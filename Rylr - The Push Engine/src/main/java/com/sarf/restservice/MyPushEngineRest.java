package com.sarf.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("pushengine/")
@Produces(MediaType.APPLICATION_JSON)
public interface MyPushEngineRest {

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("push/{id}")
	public Response push(@QueryParam("id") String clientId, String message);

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response push(String message);

	@POST
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Path("process/")
	public Response process(String message);

}