package com.cloudant.storage.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.ektorp.CouchDbConnector;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChangesFeeder {
	static String url = System.getProperty("url", "https://jerryj3.cloudant.com");

	//	테스트해야 할 기능
	//	여러 attachment
	//	attachment를 여러 doc에 분산 저장
	//	replication api를 통한 동기화
	//	visualizer를 붙여 모니터링 : 의미???
	//	외부 파일 시스템의 데이터와 연결하는 방안
	
	public static void main(String args[]) {
		System.out.println("url = " + url);

		ChangesFeeder test = new ChangesFeeder();
		test.attachmentTest1();
	}
	
	public void attachmentTest1() {
		try {
			HttpClient httpClient = new StdHttpClient.Builder().url(url)
					.username("adm-jerryj")
					.password("Tjfdkr12")
					.connectionTimeout(5000)
					.socketTimeout(30000).build();

			StdCouchDbInstance dbInst = new StdCouchDbInstance(httpClient);
			
			System.out.println("*** Database connection is established");
			
			CouchDbConnector conn = dbInst.createConnector("a_user1", true);
			
			ObjectMapper om = new ObjectMapper();
			
			while(true) {
				JsonNode newDoc = om.readTree(new File("./fileMetaSample.json"));
				ObjectNode rootDoc = (ObjectNode)newDoc;
				ObjectNode fileNameDoc = (ObjectNode)newDoc.path("list").get(0);

				rootDoc.put("user", "user" + System.currentTimeMillis() % 1000000);
				fileNameDoc.put("dir", "dir_" + System.currentTimeMillis());
				conn.create(newDoc);
				System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(newDoc));
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
