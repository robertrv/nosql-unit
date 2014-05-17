package com.lordofthejars.nosqlunit.couchbase;

import com.lordofthejars.nosqlunit.core.AbstractJsr330Configuration;

import java.net.URI;
import java.util.List;

public class CouchbaseConfiguration extends AbstractJsr330Configuration {

    private final List<URI> urlList;

    private final String bucketPassword;

    private final String bucketName;

    public CouchbaseConfiguration(List<URI> urlList, String bucketPassword, String bucketName) {
        this.urlList = urlList;
        this.bucketPassword = bucketPassword;
        this.bucketName = bucketName;
    }

    public boolean isPasswordSet() {
        return bucketPassword != null;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getBucketPassword() {
        return bucketPassword;
    }

    public List<URI> getUrlList() {
        return urlList;
    }

}
