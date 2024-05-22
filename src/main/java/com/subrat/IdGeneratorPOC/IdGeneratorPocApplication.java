package com.subrat.IdGeneratorPOC;

import com.subrat.IdGeneratorPOC.amazon.AmazonIDGenerator;
import com.subrat.IdGeneratorPOC.amazon.Range;
import com.subrat.IdGeneratorPOC.databaseSnowflake.DatabaseSnowflakeIDGenerator;
import com.subrat.IdGeneratorPOC.flickr.FlickrIDGenerator;
import com.subrat.IdGeneratorPOC.snowflake.SnowFlakeIDGenerator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class IdGeneratorPocApplication {

	public static void main(String[] args) {
       // amazonIDGeneratorTest();
       // flickrIDGeneratorTest();
        snowFlakeIDGeneratorTest();
		//databaseFlakeIDGeneratorTest();

	}

	public static void amazonIDGeneratorTest() {
		System.out.println("amazon id generator test started ");
		AmazonIDGenerator idGenerator = new AmazonIDGenerator(getDataSourceOne(), 500);
		ExecutorService executorService = Executors.newFixedThreadPool(100);

		List<Future<Range>> ranges = new ArrayList<>();

		for (int i = 1; i<=100000; i++) {
			if(i%2==0) {
				ranges.add(executorService.submit(()-> idGenerator.getIDRange("order_service")));
			} else {
				ranges.add(executorService.submit(()-> idGenerator.getIDRange("payment_service")));
			}
		}

		for (int i = 1; i<=100000; i++) {
			try {
				Range range = ranges.get(i-1).get();
				System.out.println(range.getServiceName() + " : start value = " + range.getStartValue() + " end value = " + range.getEndValue());
			} catch (InterruptedException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			}

		}

		System.out.println("amazon id generator test ended ");
	}

	public static void flickrIDGeneratorTest() {
		System.out.println("flickr id generator test started ");
		FlickrIDGenerator idGenerator = new FlickrIDGenerator(0, getEvenDataSource(), getOddDataSource());
		ExecutorService executorService = Executors.newFixedThreadPool(100);

		List<Future<Integer>> ids = new ArrayList<>();

		for (int i = 1; i<=100000; i++) {
			ids.add(executorService.submit(idGenerator::getId));
		}

		for (int i = 1; i<=100000; i++) {
			try {
				Integer id = ids.get(i-1).get();
				System.out.println("ID : " + id);
			} catch (InterruptedException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			}

		}

		System.out.println("flickr id generator test ended ");
	}

	public static void snowFlakeIDGeneratorTest() {
		SnowFlakeIDGenerator idGeneratorOne = new SnowFlakeIDGenerator(1l);
		SnowFlakeIDGenerator idGeneratorTwo = new SnowFlakeIDGenerator(2l);
		SnowFlakeIDGenerator idGeneratorThree = new SnowFlakeIDGenerator(3l);

		new Thread(() -> singleSnowFlakeIDGenerator(idGeneratorOne)).start();

		new Thread(() -> singleSnowFlakeIDGenerator(idGeneratorTwo)).start();

		new Thread(() -> singleSnowFlakeIDGenerator(idGeneratorThree)).start();
	}

	private static void  singleSnowFlakeIDGenerator(SnowFlakeIDGenerator idGenerator) {
		System.out.println("Thread started : " + Thread.currentThread().getId());

		ExecutorService executorService = Executors.newFixedThreadPool(100);

		List<Future<Long>> ids = new ArrayList<>();

		for (int i = 1; i<=10000; i++) {
			ids.add(executorService.submit(idGenerator::getId));
		}

		for (int i = 1; i<=10000; i++) {
			try {
				Long id = ids.get(i-1).get();
				System.out.println("ID : " + id);
			} catch (InterruptedException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			}

		}
		System.out.println("Thread ended : " + Thread.currentThread().getId());
	}


	public static void databaseFlakeIDGeneratorTest() {
		DatabaseSnowflakeIDGenerator idGeneratorOne = new DatabaseSnowflakeIDGenerator(getDataSource(3309), 1l);
		DatabaseSnowflakeIDGenerator idGeneratorTwo = new DatabaseSnowflakeIDGenerator(getDataSource(3310), 2l);
		DatabaseSnowflakeIDGenerator idGeneratorThree = new DatabaseSnowflakeIDGenerator(getDataSource(3311), 3l);

		new Thread(() -> databaseSnowFlakeIDGenerator(idGeneratorOne)).start();

		new Thread(() -> databaseSnowFlakeIDGenerator(idGeneratorTwo)).start();

		new Thread(() -> databaseSnowFlakeIDGenerator(idGeneratorThree)).start();
	}

	private static void  databaseSnowFlakeIDGenerator(DatabaseSnowflakeIDGenerator idGenerator) {
		System.out.println("Thread started : " + Thread.currentThread().getId());

		ExecutorService executorService = Executors.newFixedThreadPool(100);

		List<Future<Long>> ids = new ArrayList<>();

		for (int i = 1; i<=10000; i++) {
			final Integer postNumber = i;
			ids.add(executorService.submit(() -> {
				String postContent = "Post : " + postNumber;
				return idGenerator.getId(postContent);
			}));
		}

		for (int i = 1; i<=10000; i++) {
			try {
				Long id = ids.get(i-1).get();
				System.out.println("ID : " + id);
			} catch (InterruptedException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("Error " + e.getMessage());
				e.printStackTrace();
			}

		}
		System.out.println("Thread ended : " + Thread.currentThread().getId());
	}


	public static DataSource getDataSourceOne() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://localhost:3306/amazon_id_store?allowPublicKeyRetrieval=true&useSSL=false");
		config.setUsername("root");
		config.setPassword("password");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(100);  // Set the maximum number of connections here
		return new HikariDataSource(config);
	}

	public static DataSource getEvenDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://localhost:3307/flickr_id_store?allowPublicKeyRetrieval=true&useSSL=false");
		config.setUsername("root");
		config.setPassword("password");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(100);  // Set the maximum number of connections here
		return new HikariDataSource(config);
	}

	public static DataSource getOddDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://localhost:3308/flickr_id_store?allowPublicKeyRetrieval=true&useSSL=false");
		config.setUsername("root");
		config.setPassword("password");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(100);  // Set the maximum number of connections here
		return new HikariDataSource(config);
	}

	public static DataSource getDataSource(int port) {
		HikariConfig config = new HikariConfig();
		String sqlUrl = "jdbc:mysql://localhost:" + port + "/snowflake_id_store?allowPublicKeyRetrieval=true&useSSL=false";
		config.setJdbcUrl(sqlUrl);
		config.setUsername("root");
		config.setPassword("password");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(100);  // Set the maximum number of connections here
		return new HikariDataSource(config);
	}

}
