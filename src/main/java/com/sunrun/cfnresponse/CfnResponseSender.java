package com.sunrun.cfnresponse;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Manages sending REST call to S3 endpoint to communicate with CFN service
 *
 */
public class CfnResponseSender {

    private final ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
    private final HttpClient httpClient;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CfnResponseSender.class);
    
    public CfnResponseSender() {
        httpClient = HttpClientBuilder.create().build();
    }
    
    /**
     * Visible for testing
     * @param httpClient mock for testing
     */
    CfnResponseSender(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    /**
     * Synchronously sends response to Cloudformation S3 endpoint
     * 
     * @param event cloudformation event
     * @param status SUCCESS or FAILURE
     * @param context AWS lambda context
     * @param reason custom reason to report to cfn (optional)
     * @param data user data return object (optional) 
     * @param physicalResourceId custom physical resource ID (optional)
     * @param <T> type of custom user data to send in response
     * 
     * @return true if success, false if there was an error (check logs)
     */
    public <T> boolean send(@Nonnull final CfnRequest<?> event, @Nonnull final Status status, @Nonnull final Context context,
            @Nullable final String reason, @Nullable final T data, @Nullable final String physicalResourceId) {
        // Sanitize inputs
        checkNotNull(event, "event");
        checkNotNull(status, "status");
        checkNotNull(context, "context");
        
        // Compose response
        final CfnResponse<T> response = new CfnResponse<>();
        response.setData(data);
        response.setStatus(status);
        response.setLogicalResourceId(event.getLogicalResourceId());
        response.setPhysicalResourceId(physicalResourceId == null ? context.getLogStreamName() : physicalResourceId);
        response.setReason(reason == null ? "See the details in CloudWatch Log Stream: " + context.getLogStreamName() : reason);
        response.setRequestId(event.getRequestId());
        response.setStackId(event.getStackId());

        // Send response
        final HttpPut put = new HttpPut(event.getResponseURL());
        try {
            final String body = mapper.writeValueAsString(response);
            LOGGER.info(body);
            put.setEntity(new StringEntity(body));
            put.setHeader("Content-Type", "");
            httpClient.execute(put, new BasicResponseHandler());
        } catch (final IOException e) {
            LOGGER.error("Could not send response to " + event.getResponseURL(), e);
            return false;
        }
        return true;
    }
    
    private <T> void checkNotNull(final Object param, final String name) {
        if (param == null) {
            throw new IllegalArgumentException("param " + name + " cannot be null.");
        }
    }
}
