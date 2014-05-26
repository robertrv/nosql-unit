package com.lordofthejars.nosqlunit.couchbase.model;

import lombok.Value;

/**
 * Represents a document
 */
@Value
public class Document {

    // This is the content of the doc, in case is a json will be a map, otherwise a String
    private Object doc;

    private Integer expirationMSecs;

    public int calculateExpiration() {
        return expirationMSecs == null ?
                0 :
                (int) (System.currentTimeMillis() + expirationMSecs) / 1000;
    }

}
