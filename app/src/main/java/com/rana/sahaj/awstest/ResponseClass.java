package com.rana.sahaj.awstest;

public class ResponseClass {
    String mNameResponse,statusCode,body;


    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getmNameResponse() {
        return mNameResponse;
    }

    public void setmNameResponse(String mNameResponse) {
        this.mNameResponse = mNameResponse;
    }

    public ResponseClass(String mNameResponse, String statusCode, String body) {
        this.mNameResponse = mNameResponse;
        this.statusCode = statusCode;
        this.body = body;
    }

    public ResponseClass() {
    }
}
