package com.chj.gr.josson.rowdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.chj.gr.RowData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octomix.josson.Josson;

public class JossonRowDataMain {

	private static ObjectMapper objectMapper = new ObjectMapper();
	public static void main(String[] args) {

		try {
			String json = Files.readString(
					Paths.get(System.getProperty("user.dir").concat("/src/main/resources/josson/rowdata.json")));
			String mapping = Files.readString(
					Paths.get(System.getProperty("user.dir").concat("/src/main/resources/josson/rowdata-mapping.josson")));
			
			mapAndTransform(json, mapping);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private static void mapAndTransform(String json, String mapping) throws JsonProcessingException {
		JsonNode transformedJson = Josson.fromJsonString(json).getNode(mapping);
		RowData data = objectMapper.treeToValue(transformedJson, RowData.class);
		System.out.println(data);
	}
}
