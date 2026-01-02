package com.chj.gr.josson.jsonarray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import com.chj.gr.IdentityData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octomix.josson.Josson;

public class JossonIdentityMapper {

	private static ObjectMapper objectMapper = new ObjectMapper();
	private final String expression;
	
	public JossonIdentityMapper(String jossonFilePath) throws IOException {
		
		this.expression = Files.readString(Paths.get(jossonFilePath)).trim();
	}
	
	public List<IdentityData> mapAll(String json) throws IOException {

		JsonNode result = Josson.fromJsonString(json).getNode(expression);
		if (result.isArray()) {
			return objectMapper.convertValue(result, new TypeReference<List<IdentityData>>() {});
		} else {
			IdentityData data = objectMapper.treeToValue(result, IdentityData.class);
			return Collections.singletonList(data);
		}
	}
	
}
