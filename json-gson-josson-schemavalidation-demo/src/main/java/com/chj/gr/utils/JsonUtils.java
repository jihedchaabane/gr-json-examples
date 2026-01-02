package com.chj.gr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.StreamSupport;

import com.chj.gr.RowData;
import com.chj.gr.schemavalidation.SchemaProvider;
import com.chj.gr.service.TheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.networknt.schema.ValidationMessage;
import com.octomix.josson.Josson;

public class JsonUtils {

	private JsonUtils() {}
	
	public static final String JSON_PATH;
	public static /*final*/ String JOSSON_MAPPING;
	public static final String JSON_ARRAY_PATH;
	public static final String JOB_ID;
	
	public static final int CHUNK_SIZE;
	public static final int THREAD_POOL_NBRE;
	
	private static final Gson GSON;
	private static final ObjectMapper MAPPER;
	private static final SchemaProvider SCHEMAPROVIDER_ROWDATA;
	
	static {
		
//		JSON_PATH = System.getProperty("user.dir").concat("/src/main/resources/rowdatas.json");
		JSON_PATH = System.getProperty("user.dir").concat("/src/main/resources/rowdatas-1000.json");
		
		JSON_ARRAY_PATH = System.getProperty("user.dir").concat("/src/main/resources/rowdatas-array-1000.json");
		
		try (InputStream inputStream = TheService.class.getResourceAsStream("/josson/rowdata-mapping.josson")) {
//			JOSSON_MAPPING = System.getProperty("user.dir").concat("/src/main/resources/rowdata-mapping.json");
			JOSSON_MAPPING = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		JOB_ID = "123456";
		CHUNK_SIZE = 100;
		THREAD_POOL_NBRE = 10;

		GSON = new GsonBuilder().create();
		MAPPER = new ObjectMapper();
		SCHEMAPROVIDER_ROWDATA = new SchemaProvider("/schemas/rowdata-schema.json");
	}
	
	public static Object toPojo(JsonReader jsonReader, Class<?> clazz) {
		return GSON.fromJson(JsonParser.parseReader(jsonReader).getAsJsonObject().toString(), clazz);
	}
	
	public static Object toPojo(JsonObject jsonObject, Class<?> clazz) {
		return GSON.fromJson(JsonParser.parseString(jsonObject.toString()), clazz);
	}
	
	public static void validateSchema(String body/*, String filePath*/) {
		try {
			JsonNode node = MAPPER.readTree(body);
			Set<ValidationMessage> errors = SCHEMAPROVIDER_ROWDATA.getJsonSchema().validate(node);
			if (!errors.isEmpty()) {
				errors.stream().map(ValidationMessage::getMessage).toList().forEach(System.err::println);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public static RowData jossonMapAndTransform(String json, String mapping) throws JsonProcessingException {
		JsonNode transformedJson = Josson.fromJsonString(json).getNode(mapping);
		RowData data = MAPPER.treeToValue(transformedJson, RowData.class);
		return data;
	}
	
	public static String elapsedTime(Instant start) {
		Duration duration = Duration.between(start, Instant.now());
		return "Program execution took :"
				+ duration.toHoursPart() + " hours, "
				+ duration.toMinutesPart() + " minutes, "
				+ duration.toSecondsPart() + " seconds, "
				+ duration.toMillisPart() + " millis.";
	}
	
	
	@SuppressWarnings("unused")
	private static JsonArray filterJsonArray(JsonReader jsonReader, String jobId) {
		final JsonArray rows = JsonParser.parseReader(jsonReader).getAsJsonArray();
		StreamSupport.stream(rows.spliterator(), false)
		.map(JsonElement::getAsJsonObject)
		.filter(jsonObject ->
				jsonObject.get("source").getAsString().equals("PRo")
				&& jsonObject.get("type").getAsString().equals("RPT")
				&& jsonObject.get("name").getAsString().equals("TEST")
		)
		.forEach(jsonObject -> {
			jsonObject.add("active", new JsonPrimitive(true));
			jsonObject.add("jobId", new JsonPrimitive(jobId));
		});
		return rows;
	}
}
