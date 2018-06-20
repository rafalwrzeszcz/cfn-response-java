package com.sunrun.cfnresponse;

/**
 * Marshaling class for JSON response to the pre-signed S3 ResponseURL provided in a
 * Cfn lambda invocation payload
 */
public class CfnResponse<T> {

    private String StackId;
    private String RequestId;
    private String LogicalResourceId;
    private String PhysicalResourceId;
    private Status Status;
    private String Reason;
    private boolean NoEcho;
    private T Data;

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

    public Status getStatus() {
        return Status;
    }

    public void setStatus(final Status status) {
        Status = status;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(final String reason) {
        Reason = reason;
    }

    public T getData() {
        return Data;
    }

    public void setData(final T data) {
        Data = data;
    }

    public Boolean getNoEcho() {
        return NoEcho;
    }

    public void setNoEcho(final Boolean noEcho) {
        NoEcho = noEcho;
    }
}