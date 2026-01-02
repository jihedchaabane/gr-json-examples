package com.chj.gr.schemavalidation;

import java.io.InputStream;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

public class SchemaProvider {

	private final JsonSchema jsonSchema;

	public SchemaProvider(String schemaFilePath) {
		try (InputStream inputStream = getClass().getResourceAsStream(schemaFilePath)) {
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
			this.jsonSchema = factory.getSchema(inputStream);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load schema file " + schemaFilePath, e);
		}
	}

	public JsonSchema getJsonSchema() {
		return jsonSchema;
	}

	private static void validate(String body, String filePath) {

		SchemaProvider schemaProvider = new SchemaProvider(filePath);
		ObjectMapper mapper = new ObjectMapper();
		Set<ValidationMessage> errors = null;
		try {
			JsonNode node = mapper.readTree(body);
			errors = schemaProvider.getJsonSchema().validate(node);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		if (!errors.isEmpty()) {
			errors.stream().map(ValidationMessage::getMessage).toList().forEach(System.err::println);
		}
	}

	public static void main(String[] args) {

		String body = """
					{
					"id": "11",
					"type": "passport",
					"number": "pass1",
					"deliveryDate": "11/11/2011"
					}
				""";
		validate(body, "/schemas/identitydata-schema.json");

		/**
		 * 
		 */
		body = """
				{
					"source": "PRo",
					"type": "RPT",
					"name": "TEST",
					"active": false,
					"identities": [
						{
							"id": "11",
							"type": "passport",
							"number": "pass1"
						}, {
							"id": "12",
							"type": "cin",
							"number": "123"
						}, {
							"id": "13",
							"type": "drive-licence",
							"number": "drive-123"
						}
					]
				}
				""";
		validate(body, "/schemas/rowdata-schema.json");
	}

}
