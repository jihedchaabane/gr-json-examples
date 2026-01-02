package com.chj.gr.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateRowDatasArray {

	public static void main(String[] args) {
		Instant start = Instant.now();

		String rowdata = """
				{"source": "IN","type": "PRD","name": "ABC","active": false,"identities": [{"id": "11","type": "passport","number": "pass1","deliveryDate": "01-01-2000","expirationDate": "31-12-2050"}, {"id": "12","type": "cin","number": "123","deliveryDate": "01-01-2000","expirationDate": "31-12-2050"}, {"id": "13","type": "drive-licence","number": "drive-123","deliveryDate": "01-01-2000","expirationDate": "31-12-2050"}],"jobId": "job-1","chunkId": 1},""";
		
		int size = 1000;
		int batch = 10;
		
		try {
			final Path path = Paths.get(System.getProperty("user.dir").concat("/src/main/resources/rowdatas-array-"+size+".json"));
			Files.write(path, 
					Arrays.asList("["), 
					StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			
//			List<String> l = new ArrayList<>();
			for (int i = 0; i < size; i++) {
//				if (l.size() == batch) {
//					System.out.println("Write chunk");
					Files.write(path, 
							Arrays.asList(rowdata), 
							StandardCharsets.UTF_8, StandardOpenOption.APPEND);
//					l.clear();
//				} else {
//					l.add(rowdata);
//				}
			}
			
//			if (l.size() > 0) {
//				Files.write(path, l, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
//			}
			Files.write(path, 
					Arrays.asList("]"), 
					StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(JsonUtils.elapsedTime(start));
		}
	}
}
