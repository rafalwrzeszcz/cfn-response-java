package com.sunrun.cfnresponse;

/**
 * Cfn request class
 *
 */
public class CfnRequest<T> {
    public T ResourceProperties;
    public String RequestType;
    public String ResponseURL;
    public String StackId;
    public String RequestId;
    public String LogicalResourceId;
    public String PhysicalResourceId;
    public String ResourceType;

    public T getResourceProperties() {
        return ResourceProperties;
    }
    public void setResourceProperties(final T ResourceProperties) {
        this.ResourceProperties = ResourceProperties;
    }
    public String getRequestType() {
        return RequestType;
    }
    public void setRequestType(final String RequestType) {
        this.RequestType = RequestType;
    }
    public String getResponseURL() {
        return ResponseURL;
    }
    public void setResponseURL(final String responseURL) {
        ResponseURL = responseURL;
    }
    public String getStackId() {
        return StackId;
    }
    public void setStackId(final String stackId) {
        StackId = stackId;
    }
    public String getRequestId() {
        return RequestId;
    }
    public void setRequestId(final String requestId) {
        RequestId = requestId;
    }
    public String getLogicalResourceId() {
        return LogicalResourceId;
    }
    public void setLogicalResourceId(final String logicalResourceId) {
        LogicalResourceId = logicalResourceId;
    }
    public String getPhysicalResourceId() {
        return PhysicalResourceId;
    }
    public void setPhysicalResourceId(final String physicalResourceId) {
        PhysicalResourceId = physicalResourceId;
    }
    public String getResourceType() {
        return ResourceType;
    }
    public void setResourceType(final String resourceType) {
        ResourceType = resourceType;
    }
}
