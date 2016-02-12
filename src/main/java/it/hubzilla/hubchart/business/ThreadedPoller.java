package it.hubzilla.hubchart.business;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.hubzilla.hubchart.AppConstants;

public class ThreadedPoller {
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean finished = false;
	private String result = null;
	
	public void launchPollingThread(/*Poller poller,*/ String url) throws
			ExecutionException, InterruptedException {
		finished = false;
		long end = System.currentTimeMillis() + AppConstants.POLL_TIMEOUT;
		Future<String> f = executor.submit(new TimedPollWrapper(/*poller,*/ url));
		try {
			result = f.get(end, TimeUnit.MILLISECONDS);
			finished = true;
		} catch (ExecutionException e) {
			finished = true;
			throw e;
		} catch (InterruptedException e) {
			finished = true;
			throw e;
		} catch (TimeoutException e) {
			finished = true;
			f.cancel(true);
		}
		finished = true;
	}
	
	public boolean isFinished() {
		return finished;
	}

	public String getResult() {
		return result;
	}


	
	//Inner Classes


	private class TimedPollWrapper implements Callable<String> {
		private Poller poller;
		private String url;

		public TimedPollWrapper(/*Poller poller,*/ String url) {
			this.poller = new Poller();
			this.url = url;
		}

		public String call() throws Exception {
			return poller.getJsonResponseFromUrl(url);
		}
	}

}