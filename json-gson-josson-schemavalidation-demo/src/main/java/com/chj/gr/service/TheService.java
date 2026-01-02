package com.chj.gr.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.SerializationUtils;

import com.chj.gr.RowData;
import com.chj.gr.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TheService {

	private ExecutorService executorService;
	private List<Future<List<RowData>>> futures = new ArrayList<>();
	private AtomicInteger atomicInteger = new AtomicInteger(1);
	private final ReentrantLock reentrantLock = new ReentrantLock();
	 
	public TheService() {
		ThreadFactory namedThreadFactory =
				new ThreadFactoryBuilder().setNameFormat("my-jihed-thread-%d").build();
		executorService = Executors.newFixedThreadPool(JsonUtils.THREAD_POOL_NBRE, namedThreadFactory);
	}

	public JsonObject filterJsonObjectIfNeeded(JsonObject jsonObject, String jobId) {
//		if (jsonObject.get("source").getAsString().equals("PRo")
//				&& jsonObject.get("type").getAsString().equals("RPT")
//				&& jsonObject.get("name").getAsString().equals("TEST")) {
			/**
			 * Add ab brand new properties.
			 */
			jsonObject.add("jobId", new JsonPrimitive(jobId));
			jsonObject.add("chunkId", new JsonPrimitive(atomicInteger.intValue()));
			/**
			 * Update an already existing property.
			 */
			jsonObject.add("active", new JsonPrimitive(true));
			return jsonObject;
//		}
//		return null;
	}
	
	public void process(AtomicReference<List<RowData>> rowDatas, JsonObject jsonObject) {
		if (jsonObject != null && jsonObject.has("jobId") && jsonObject.has("chunkId")) {
//			RowData mapped = (RowData) JsonUtils.toPojo(jsonObject.getAsJsonObject(), RowData.class);
			/**
			 * START JOSSON.
			 */
			try {
				RowData mapped = JsonUtils.jossonMapAndTransform(jsonObject.toString(), JsonUtils.JOSSON_MAPPING);
				/**
				 * END JOSSON.
				 */
				if (mapped.getIdentities() != null) {
					mapped.getIdentities().stream().forEach(t -> {
						RowData row = SerializationUtils.clone(mapped);
						row.setIdentities(Arrays.asList(t));
						rowDatas.get().add(row);
						if (rowDatas.get().size() == JsonUtils.CHUNK_SIZE) {
							this.cloneClearDispatch(rowDatas);
						}
					});
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	public void cloneClearDispatch(AtomicReference<List<RowData>> rowDatas) {
		AtomicReference<List<RowData>> o = SerializationUtils.clone(rowDatas);
		rowDatas.get().clear();
		this.dispatch(o);
	}

	private void dispatch(AtomicReference<List<RowData>> o) {
		this.futures.add(
					executorService.submit(new MyPersistenceCallable(o.get())));
		this.increment();
	}

	private synchronized void increment() {
		reentrantLock.lock();  // Acquire lock
		atomicInteger.getAndIncrement();
		reentrantLock.unlock();  // Release lock
	}
	
	private long howMuchProcessed;
	public void waitForAllDone() {
		/**
		 * Wait for the result of all Future(s).
		 */
		this.futures.stream().forEach(f -> {
			try {
				List<RowData> rowDatas = f.get();
				howMuchProcessed += rowDatas.size();
//				rowDatas.forEach(System.out::println);
				System.out.println("Computation result: " + rowDatas.size() + " of chuck " + rowDatas.get(0).getChunkId());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				this.executorService.shutdown();
			}
		});
		System.out.println(atomicInteger.get()-1 + " chunks processed with total of " + howMuchProcessed + " rows !!");
	}
}
