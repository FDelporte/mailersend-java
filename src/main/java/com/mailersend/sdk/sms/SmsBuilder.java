package com.mailersend.sdk.sms;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendApi;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.mailersend.sdk.util.JsonSerializationDeserializationStrategy;

public class SmsBuilder {

	private SmsBuilderBody builderBody;
	private MailerSend apiObjectReference;
	
	public SmsBuilder(MailerSend ref) {
		
		apiObjectReference = ref;
		builderBody = new SmsBuilderBody();
	}
	
	public SmsBuilder addPersonalization(String phoneNumber, String name, Object value) {
		
		SmsPersonalization entry = null;
		
		for (SmsPersonalization p : builderBody.personalization) {
			
			if (p.phoneNumber.equals(phoneNumber)) {
				
				entry = p;
				break;
			}
		}
		
		if (entry != null) {
			
			entry.data.put(name, value);
		} else {
			
			entry = new SmsPersonalization();
			entry.phoneNumber = phoneNumber;
			entry.data.put(name, value);
			builderBody.personalization.add(entry);
		}
		
		return this;
	}
	
	public SmsBuilder from(String from) {
		
		builderBody.from = from;
		return this;
	}
	
	public SmsBuilder text(String text) {
		builderBody.text = text;
		
		return this;
	}
	
	public SmsBuilder addRecipient(String phoneNumber) {
		
		builderBody.to.add(phoneNumber);
		
		return this;
	}
	
	public String send() throws MailerSendException {
		
		String endpoint = "/sms";
		
        MailerSendApi api = new MailerSendApi();
        api.setToken(apiObjectReference.getToken());
        
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new JsonSerializationDeserializationStrategy(false))
                .addDeserializationExclusionStrategy(new JsonSerializationDeserializationStrategy(true))
                .create();
        
        String json = gson.toJson(builderBody);
        
        builderBody = new SmsBuilderBody();
       
        MailerSendResponse response = api.postRequest(endpoint, json, MailerSendResponse.class);
        
        String messageId = null;
        
        for (Entry<String, List<String>> entry : response.headers.entrySet()) {
        	
        	if (entry.getKey().equals("x-sms-message-id")) {
        		
        		messageId = entry.getValue().get(0);
        		
        		break;
        	}
        }
        
        return messageId;
	}
}
