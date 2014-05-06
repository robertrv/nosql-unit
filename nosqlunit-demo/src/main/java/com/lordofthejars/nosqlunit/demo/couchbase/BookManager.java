package com.lordofthejars.nosqlunit.demo.couchbase;

import com.couchbase.client.CouchbaseClient;
import com.lordofthejars.nosqlunit.demo.model.Book;
import net.spy.memcached.internal.OperationCompletionListener;
import net.spy.memcached.internal.OperationFuture;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Created by rob on 4/22/14.
 */
public class BookManager {

    public static final ObjectMapper mapper = new ObjectMapper();

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private CouchbaseClient client;

    public BookManager(CouchbaseClient client) {
        this.client = client;
    }

    public OperationFuture<Boolean> create(final Book book) throws ExecutionException, InterruptedException {
        /*
        Best way to have search capabilities is by another document on couchbase, so we are adding it first and then
        the proper document.
          */
        final String key = nextKey();
        String value = toTitleKey(book.getTitle());
        OperationFuture<Boolean> future = client.set(value, key);
        return future.addListener(new OperationCompletionListener() {
            @Override
            public void onComplete(OperationFuture<?> operationFuture) throws Exception {
                client.set(key, mapper.writeValueAsBytes(book));
            }
        });
    }

    private String nextKey() {
        // TODO change this for a proper autonumerical done by couchbase incr, maybe get rid of it via nosqlunit.
        return "K::" + UUID.randomUUID().toString();
    }

    public Book findBookByTitle(String title) throws IOException {
        String key = (String) client.get(toTitleKey(title));

        String json = (String) client.get(key);
        return mapper.readValue(json, Book.class);
    }

    private String toTitleKey(String title) {
        return "Tittle::" + toSlug(title);
    }

    public static String toSlug(String input) {
        String nonwhites = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nonwhites, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

}
