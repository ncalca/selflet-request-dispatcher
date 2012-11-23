package it.polimi.elet.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Generic thread pool for jobs executed in the dispatcher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ThreadPool {

	private ThreadPool() {
		// private constructor
	}

	private static final int GENERIC_THREAD_POOL_SIZE = 10;

	private static final ExecutorService GENERIC_THREAD_POOL = Executors.newFixedThreadPool(GENERIC_THREAD_POOL_SIZE);

	/**
	 * Submits a thread to the generic thread pool
	 * */
	public static void submitGenericJob(Runnable job) {
		GENERIC_THREAD_POOL.execute(job);
	}

}