package com.sunrun.cfnresponse;

import com.amazonaws.services.lambda.runtime.Context;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CfnResponseSenderTest {

    // Mocks
    @Mock
    private HttpClient httpClient;

    @Mock
    private Context context;

    // Captors
    @Captor
    private ArgumentCaptor<HttpPut> httpPutCaptor;

    // Test data
    private CfnRequest<MyRequestProperties> event;
    private Status status;
    private String reason;
    private MyData data;
    private String physicalResourceId;

    // Under test
    private CfnResponseSender sender;

    @Before
    public void setUp() {
        sender = new CfnResponseSender(httpClient);

        // Set up test data
        event = new CfnRequest<MyRequestProperties>();
        event.setResponseURL("http://pre-signed-S3-url-for-response");
        event.setLogicalResourceId("logical-id-123");
        event.setStackId("stack-id-123");
        event.setRequestId("request-id-123");

        status = Status.SUCCESS;

        data = new MyData("value one", "value two");

        // Configure mocks
        when(context.getLogStreamName()).thenReturn("log-stream-name-123");
    }

    /**
     * Test standard use case for correct formatted JSON body and header
     *
     * @throws IOException
     * @throws JSONException
     */
    @Test
    public void testSend() throws IOException, JSONException {
        final boolean result = sender.send(event, status, context, reason, data, physicalResourceId);
        assertTrue(result);
        testHelper("entity.json");
    }

    /**
     * Test standard use case with FAILURE status.
     * Sender should still return true, however, data sent to CloudFormation
     * should have FAILURE as the value of status
     *
     * @throws IOException
     * @throws JSONException
     */
    @Test
    public void testSendFailureStatus() throws IOException, JSONException {
        final boolean result = sender.send(event, Status.FAILED, context, reason, data, physicalResourceId);
        assertTrue(result);
        testHelper("entity-failure.json");
    }

    /**
     * reason and physicalId are overridable. Test they can be overriden properly
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test
    public void testSendOptionalParams() throws IOException, JSONException {
        reason = "my custom reason";
        physicalResourceId = "custom-physical-resource-id-123";
        final boolean result = sender.send(event, status, context, reason, data, physicalResourceId, true);
        assertTrue(result);
        testHelper("entity-with-optional-params.json");
    }

    /**
     * reason and noEcho are overridable. Test they can be overriden properly
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test
    public void testSendOptionalParamsNullPhysicalResourceId() throws IOException, JSONException {
        reason = "my custom reason2";
        final boolean result = sender.send(event, status, context, reason, data, null, true);
        assertTrue(result);
        testHelper("entity-with-optional-params-null-res-id.json");
    }

    private void testHelper(final String testFixture) throws IOException, JSONException {
        verify(httpClient).execute(httpPutCaptor.capture(), any(BasicResponseHandler.class));

        final HttpPut httpPut = httpPutCaptor.getValue();

        final String entity = EntityUtils.toString(httpPut.getEntity());

        JSONAssert.assertEquals(IOUtils.toString(getClass().getResourceAsStream(testFixture)), entity, true);

        assertEquals("", httpPut.getFirstHeader("Content-Type").getValue());
    }

    /**
     * Test we return false in case of an unexpected exception where our message possibly failed to send
     * or we encountered an unexpected response from the S3 endpoint
     *
     * @throws ClientProtocolException
     * @throws IOException
     */
    @Test
    public void testSendError() throws ClientProtocolException, IOException {
        when(httpClient.execute(any(HttpPut.class), any(BasicResponseHandler.class))).thenThrow(new IOException("something went wrong"));
        final boolean result = sender.send(event, status, context, reason, data, physicalResourceId);
        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEventNonnull() {
        event = null;
        sender.send(event, status, context, reason, data, physicalResourceId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStatusNonnull() {
        status = null;
        sender.send(event, status, context, reason, data, physicalResourceId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContextNonnull() {
        context = null;
        sender.send(event, status, context, reason, data, physicalResourceId);
    }

    public static class MyRequestProperties {
    }

    public static class MyData {
        private final String fieldOne;
        private final String fieldTwo;

        public MyData(final String fieldOne, final String fieldTwo) {
            this.fieldOne = fieldOne;
            this.fieldTwo = fieldTwo;
        }

        public String getFieldOne() {
            return fieldOne;
        }

        public String getFieldTwo() {
            return fieldTwo;
        }
    }
}
