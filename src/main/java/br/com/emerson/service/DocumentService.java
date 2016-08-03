package br.com.emerson.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

@Path("/")
public interface DocumentService {

    @GET
    @Path("/create")
    Response start();

    @GET
    @Path("/get")
    Response getData();

    @GET
    @Path("/remove")
    Response removeData();

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/createFile")
    Response createFile(@Context UriInfo uriInfo, Attachment attachemnt);

    @GET
    @Path("/getFile/{id}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    Response getFile(@PathParam("id") String id);

    @DELETE
    @Path("/removeFile/{id}")
    Response removeFile(@PathParam("id") String id);
}
