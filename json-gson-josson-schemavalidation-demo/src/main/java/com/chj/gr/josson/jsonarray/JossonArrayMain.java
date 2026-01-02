package com.chj.gr.josson.jsonarray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.chj.gr.IdentityData;

public class JossonArrayMain {

	public static void main(String[] args) {
		
		try {
			String json = Files.readString(
					Paths.get(System.getProperty("user.dir").concat("/src/main/resources/josson/identitydata-array.json")));
			String mapping = Files.readString(
					Paths.get(System.getProperty("user.dir").concat("/src/main/resources/josson/identitydata-array-mapping.josson")));
			
			JossonIdentityMapper jossonIdentityMapper = new JossonIdentityMapper(mapping);
			
			List<IdentityData> list = jossonIdentityMapper.mapAll(json);
			list.forEach(System.out::println);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
