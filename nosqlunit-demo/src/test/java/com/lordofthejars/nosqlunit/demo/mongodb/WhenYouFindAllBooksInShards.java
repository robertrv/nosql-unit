package com.lordofthejars.nosqlunit.demo.mongodb;

import static com.lordofthejars.nosqlunit.mongodb.ManagedMongoDbLifecycleManagerBuilder.newManagedMongoDbLifecycle;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.replicaset.ReplicationMongoDbConfigurationBuilder.replicationMongoDbConfiguration;
import static com.lordofthejars.nosqlunit.mongodb.shard.ManagedMongosLifecycleManagerBuilder.newManagedMongosLifecycle;
import static com.lordofthejars.nosqlunit.mongodb.shard.ShardedGroupBuilder.shardedGroup;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.demo.model.Book;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.lordofthejars.nosqlunit.mongodb.shard.ShardedManagedMongoDb;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class WhenYouFindAllBooksInShards {

	static {
		System.setProperty("MONGO_HOME", "/opt/mongo");
	}

	@ClassRule
	public static ShardedManagedMongoDb shardedManagedMongoDb = shardedGroup()
																	.shard(newManagedMongoDbLifecycle().port(27018).dbRelativePath("rs-1").logRelativePath("log-1").get())
																	.shard(newManagedMongoDbLifecycle().port(27019).dbRelativePath("rs-2").logRelativePath("log-2").get())
																	.config(newManagedMongoDbLifecycle().port(27020).dbRelativePath("rs-3").logRelativePath("log-3").get())
																	.mongos(newManagedMongosLifecycle().configServer(27020).get())
																	.get();
	
	@Rule
	public MongoDbRule mongoDbRule = newMongoDbRule().configure(
			replicationMongoDbConfiguration().databaseName("test")
											 .enableSharding()
											 .seed("localhost", 27017)
											 .configure())
										.build(); 
	
	@Inject
	private Mongo mongo;
	
	@Test
	@UsingDataSet(locations="InitialDataShard.json", loadStrategy=LoadStrategyEnum.CLEAN_INSERT)
	public void manager_should_return_all_inserted_books() {
		
		BookManager bookManager = new BookManager(bookCollection());
		List<Book> books = bookManager.findAll();

		Book expectedBook = new Book("The Hobbit", 293);

		assertThat(books, hasSize(1));
		assertThat(expectedBook, isIn(books));
		
	}
	
	private DBCollection bookCollection() {
		return mongo.getDB("test").getCollection(Book.class.getSimpleName());
	}
	
}
