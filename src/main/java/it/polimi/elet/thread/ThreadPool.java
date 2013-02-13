package it.polimi.elet.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Generic thread pool for jobs executed in the dispatcher
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ThreadPool {

	private static final int GENERIC_THREAD_POOL_SIZE = 20;

	private static final ThreadPoolExecutor GENERIC_THREAD_POOL = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(GENERIC_THREAD_POOL_SIZE);

	private ThreadPool() {
		// private constructor
		GENERIC_THREAD_POOL.prestartAllCoreThreads();
	}

	/**
	 * Submits a thread to the generic thread pool
	 * */
	public static void submitGenericJob(Runnable job) {
		GENERIC_THREAD_POOL.execute(job);
	}

}