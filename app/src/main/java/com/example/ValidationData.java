package com.example;

public class ValidationData {
    public String userid;
    public String receipt;
    public String signature;
    public String url;

    public ValidationData(String _userid, String _receipt, String _signature, String _url) {
        this.userid = _userid;
        this.receipt = _receipt;
        this.signature = _signature;
        this.url = _url;
    }
}
