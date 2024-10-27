package com.example.gwpractice.reactive;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureEx {
	interface SucessCallback {
		void onSucess(String result);
	}

	interface ExceptionCallback {
		void onException(Throwable t);
	}

	public static class CallBackFutureTask extends FutureTask<String> {
		SucessCallback sc;
		ExceptionCallback ec;

		public CallBackFutureTask(Callable<String> callable, SucessCallback sc, ExceptionCallback ec) {
			super(callable);
			this.sc = Objects.requireNonNull(sc);
			this.ec = Objects.requireNonNull(ec);
		}

		@Override
		protected void done() {
			try {
				sc.onSucess(get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				ec.onException(e.getCause());
			}
		}
	}

	public static void main(String[] args) {
		ExecutorService es = Executors.newCachedThreadPool();

		FutureTask<String> f = new FutureTask<String>(() -> {
			Thread.sleep(2000);
			log.info("Async");
			return "hello";
		}) {
			@Override
			protected void done() {
				try {
					log.info(get());
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		};
		///////////////////////////////////////////
		CallBackFutureTask ff = new CallBackFutureTask(() -> {
			Thread.sleep(2000);
			log.info("Async");
			if(1==1) throw new RuntimeException("Asyn Error!!");
			return "hello";
		}, res -> System.out.println(res),
			e -> System.out.println(e.getMessage()));

		es.execute(ff);

		es.shutdown();

	}
}
