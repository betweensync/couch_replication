package com.cloudant.storage.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ektorp.CouchDbConnector;
import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.ChangesFeed;
import org.ektorp.changes.DocumentChange;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChangesEventListener {
	static String url = System.getProperty("url", "https://jerryj3.cloudant.com");
	
	public static void main(String args[]) {
		ChangesEventListener test = new ChangesEventListener();
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
			
			CouchDbConnector conn = dbInst.createConnector("a_event", true);
			CouchDbConnector connSeq = dbInst.createConnector("a_seq", true);
			
			JsonNode a_event_seq = (JsonNode)connSeq.find(JsonNode.class, "a_event");
			ChangesCommand cmd = null;
			if(a_event_seq != null) {
				System.out.println("Since: " + a_event_seq.get("seq").textValue());
				cmd = new ChangesCommand.Builder().since(a_event_seq.get("seq").textValue()).includeDocs(true).build();
			}
			else {
				cmd = new ChangesCommand.Builder().includeDocs(true).build();
			}
			
			int type = 2;
			
			if(type == 1) {
				//	한꺼번에 수신
				List<DocumentChange> changes = conn.changes(cmd);
				System.out.println(cmd);
				
				for(DocumentChange change : changes) {
					System.out.println(change.getId() + ", " + change.getSequence());
				}
			}
			else if(type ==2) {
				//	Event 형식 수신
				ChangesFeed feed = conn.changesFeed(cmd);
				while(feed.isAlive()) {
					DocumentChange change = null;
					try {
						System.out.println("Waiting...");
						
//						change = feed.next();
						change = feed.next(5, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(change != null) {
						//	데이터가 있는 동안은 한 번에 처리
						DocumentChange newChange = null;
						do {
							String docId = change.getId();
							JsonNode changedDoc = change.getDocAsNode();
							System.out.println(changedDoc + ", seq=" + change.getStringSequence());
							try {
								newChange = feed.poll();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						while(newChange != null);

						//	추출한 데이터의 최종 seq 번호를 업데이트
						if(a_event_seq == null) {
							ObjectMapper om = new ObjectMapper();
							a_event_seq = om.createObjectNode();
							((ObjectNode)a_event_seq).put("_id", "a_event");
							((ObjectNode)a_event_seq).put("seq", change.getStringSequence());
							connSeq.create(a_event_seq);
						}
						else {
							((ObjectNode)a_event_seq).put("seq", change.getStringSequence());
							connSeq.update(a_event_seq);
						}
					}
				}
				//	현재는 여기까지 도달하지 않음
				feed.cancel();
			}
			else if(type ==3) {
				ChangesFeed feed = conn.changesFeed(cmd);
				System.out.println("Waiting...");
				while(feed.isAlive()) {
				}
			}
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
