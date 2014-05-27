package com.lordofthejars.nosqlunit.couchbase;

import com.couchbase.client.CouchbaseClient;
import com.google.gson.JsonParseException;
import com.lordofthejars.nosqlunit.couchbase.model.Document;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
public class DataLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String DOCUMENTS_ROOT = "data";

    public static final String DESIGN_ROOT = "designDocs";// TODO

    private CouchbaseClient couchbaseClient;

    public void load(final InputStream dataScript) {
        final Map<String, Document> documentsIterator = getDocuments(dataScript);
        insertDocuments(documentsIterator);
    }

    @SneakyThrows({ExecutionException.class, InterruptedException.class})
    private void insertDocuments(final Map<String, Document> documentsIterator) {
        for (final Map.Entry<String, Document> documentEntry : documentsIterator.entrySet()) {
            final Document document = documentEntry.getValue();
            couchbaseClient.add(documentEntry.getKey(), document.calculateExpiration(), document).get();
        }
    }

    @SneakyThrows({JsonParseException.class, JsonMappingException.class, IOException.class})
    public static Map<String, Document> getDocuments(final InputStream dataScript) {

        MAPPER.readValue(dataScript, new HashMap<String, Map<String, Document>>());

        final Object dataElements = rootNode.get(DOCUMENTS_ROOT);

        if (dataElements instanceof Map) {
            return (Map<String, Document>) dataElements;
        } else {
            throw new IllegalArgumentException("Array of documents are required.");
        }
    }

}
