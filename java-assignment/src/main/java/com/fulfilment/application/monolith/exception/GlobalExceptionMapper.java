package com.fulfilment.application.monolith.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER =
            Logger.getLogger(GlobalExceptionMapper.class);

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {

        int status = Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (exception instanceof WebApplicationException webEx) {
            status = webEx.getResponse().getStatus();
        }

        if (status >= 500) {
            LOGGER.error("Request processing failed", exception);
        } else {
            LOGGER.warnf(
                    "Request failed. status=%d, message=%s",
                    status,
                    exception.getMessage());
        }

        ObjectNode error = objectMapper.createObjectNode();
        error.put("status", status);
        error.put("exceptionType", exception.getClass().getSimpleName());

        if (exception.getMessage() != null) {
            error.put("message", exception.getMessage());
        }

        return Response.status(status)
                .entity(error)
                .build();
    }
}
