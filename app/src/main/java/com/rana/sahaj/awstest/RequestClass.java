package com.rana.sahaj.awstest;

public class RequestClass {
    String mName,mClass,mCase;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmClass() {
        return mClass;
    }

    public void setmClass(String mClass) {
        this.mClass = mClass;
    }

    public String getmCase() {
        return mCase;
    }

    public void setmCase(String mCase) {
        this.mCase = mCase;
    }

    public RequestClass(String mName, String mClass, String mCase) {
        this.mName = mName;
        this.mClass = mClass;
        this.mCase = mCase;
    }

    public RequestClass() {
    }
}
