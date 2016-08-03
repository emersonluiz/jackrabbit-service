package br.com.emerson.service;

import java.io.InputStream;
import java.net.URI;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.emerson.repository.DefaultDocumentRepository;

public class DefaultDocumentService implements DocumentService {

    @Autowired
    DefaultDocumentRepository documentRepository;

    @Override
    public Response start() {
        try {
            String create  = documentRepository.createData();
            return Response.ok().entity(create).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getData() {
        try {
            String value  = documentRepository.getData();
            return Response.ok().entity(value).build();
        } catch (Exception e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response removeData() {
        try {
            documentRepository.removeData();
            return Response.ok().entity("removido com sucesso").build();
        } catch (Exception e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response createFile(UriInfo uriInfo, Attachment attachment) {
        try {
            DataHandler handler = attachment.getDataHandler();
            String contentType = attachment.getContentType().toString();
            InputStream inputStream = handler.getInputStream();
            String id = documentRepository.createFile(inputStream);
            URI uri = uriInfo.getRequestUriBuilder().path(id).build();
            return Response.created(uri).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getFile(String id) {
        try {
            InputStream inputStream = documentRepository.getFile(id);
            return Response.ok().entity(inputStream).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response removeFile(String id) {
        try {
            documentRepository.deleteFile(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
