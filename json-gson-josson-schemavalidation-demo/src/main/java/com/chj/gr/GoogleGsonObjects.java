package com.chj.gr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.chj.gr.service.TheService;
import com.chj.gr.utils.JsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class GoogleGsonObjects {

	private static TheService theService = new TheService();
	public static void main(String[] args) {
		Instant start = Instant.now();
		AtomicReference<List<RowData>> rowDatas = new AtomicReference<>(new ArrayList<RowData>());
		try (JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(JsonUtils.JSON_PATH), StandardCharsets.UTF_8))) {
			jsonReader.setLenient(true);
			while (jsonReader.hasNext()) {
				JsonObject jsonObject = JsonParser.parseReader(jsonReader).getAsJsonObject();
				JsonUtils.validateSchema(jsonObject.toString());
				
				jsonObject = theService.filterJsonObjectIfNeeded(jsonObject, JsonUtils.JOB_ID);
				theService.process(rowDatas, jsonObject);
			}
			if (rowDatas.get().size() > 0) {
				theService.cloneClearDispatch(rowDatas);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			theService.waitForAllDone();
		}
		
		System.out.println(JsonUtils.elapsedTime(start));
	}

}
