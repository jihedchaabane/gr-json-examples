package com.chj.gr.service;

import java.util.List;
import java.util.concurrent.Callable;

import com.chj.gr.RowData;

public class MyPersistenceCallable implements Callable<List<RowData>> {

	private List<RowData> rowDatas;
	
	public MyPersistenceCallable(List<RowData> rowDatas) {
		this.rowDatas = rowDatas;
	}

	@Override
	public List<RowData> call() throws Exception {
		/**
		 * Call database persistence or whatever type of treatments, etc ...
		 */
		Thread.sleep(2500);
		
		/**
		 * Do post call treatments here.
		 */
		System.out.println("Task " + Thread.currentThread().getName() + " -> DONE.");
		return rowDatas;
	}

	
}
