package com.chj.gr.josson.identities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.chj.gr.IdentityData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octomix.josson.Josson;

public class JossonIdentityDataResultsMain {

	private static ObjectMapper objectMapper = new ObjectMapper();
	public static void main(String[] args) {

		try {
			String json = Files.readString(
					Paths.get(System.getProperty("user.dir").concat("/src/main/resources/josson/identitydata-results.json")));
			String mapping = Files.readString(
					Paths.get(System.getProperty("user.dir").concat("/src/main/resources/josson/identities-results-mapping.josson")));
			
			mapAndTransform(json, mapping);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private static void mapAndTransform(String json, String mapping) throws JsonProcessingException {
		JsonNode transformedJson = Josson.fromJsonString(json).getNode(mapping);
		List<IdentityData> list = objectMapper.convertValue(transformedJson, new TypeReference<List<IdentityData>>() {});
		
		list.stream().forEach(System.out::println);
	}
}
