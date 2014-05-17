package com.lordofthejars.nosqlunit;

import com.lordofthejars.nosqlunit.couchbase.CouchbaseConfiguration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RemoteCouchbaseConfigurationBuilder {


    public static class Builder {

        private String name;
        private String password;
        private List<URI> uris;

        private Builder() {
            uris = new ArrayList<URI>();
        }

        public static Builder start() {
            return new Builder();
        }

        public Builder bucketName(String name) {
            this.name = name;
            return this;
        }

        public Builder bucketPassword(String pass) {
            this.password = pass;
            return this;
        }

        public Builder serverUri(String url) {
            this.uris.add(URI.create(url));
            return this;
        }

        public CouchbaseConfiguration build() {
            if (uris.isEmpty()) {
                uris.add(URI.create("http://localhost:8091"));
            }
            return new CouchbaseConfiguration(uris, password, name);
        }
    }
}
