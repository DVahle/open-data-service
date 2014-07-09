package org.jvalue.ods.notifications.sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jvalue.ods.data.DataSource;
import org.jvalue.ods.data.generic.GenericEntity;
import org.jvalue.ods.logger.Logging;
import org.jvalue.ods.notifications.ApiKey;
import org.jvalue.ods.notifications.Client;
import org.jvalue.ods.notifications.NotificationException;
import org.jvalue.ods.notifications.NotificationManager;
import org.jvalue.ods.notifications.NotificationSender;
import org.jvalue.ods.notifications.clients.GcmClient;
import org.jvalue.ods.utils.Assert;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


final class GcmSender implements NotificationSender<GcmClient> {
	
	static final String 
		DATA_KEY_SOURCE = "source",
		DATA_KEY_DEBUG = "debug";

	
	private final Sender sender;
	private final NotificationManager notificationManager;

	GcmSender(ApiKey key) {
		Assert.assertNotNull(key);
		this.sender = new Sender(key.toString());
		this.notificationManager = NotificationManager.getInstance();
	}
	
	
	@Override
	public void notifySourceChanged(
			GcmClient client, 
			DataSource source, 
			GenericEntity data) throws NotificationException {

		// gather data
		Map<String,String> payload = new HashMap<String,String>();
		payload.put(DATA_KEY_SOURCE, source.getId());
		payload.put(DATA_KEY_DEBUG, Boolean.TRUE.toString());

		String collapseKey = source.getId();

		final List<String> devices = new ArrayList<String>();
		devices.add(client.getId());

		// send
		Message.Builder builder = new Message.Builder().collapseKey(collapseKey);
		for (Map.Entry<String, String> e : payload.entrySet()) {
			builder.addData(e.getKey(), e.getValue());
		}

		MulticastResult multicastResult;
		try {
			multicastResult = sender.send(builder.build(), devices, 5);
		} catch (IOException e) {
			Logging.error(NotificationSender.class, "Error posting messages");
			return;
		}

		// analyze the results
		List<Result> results = multicastResult.getResults();
		for (int i = 0; i < devices.size(); i++) {
			String regId = devices.get(i);
			Result result = results.get(i);
			String messageId = result.getMessageId();
			if (messageId != null) {
				Logging.info(NotificationSender.class, "Succesfully sent message to device: " 
					+ regId + "; messageId = " + messageId);
				String canonicalRegId = result.getCanonicalRegistrationId();
				if (canonicalRegId != null) {
					// same device has more than on registration id: update it
					updateClientId(regId, canonicalRegId);
				}
			} else {
				String error = result.getErrorCodeName();
				if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
					// application has been removed from device - unregister it
					unregisterClientId(regId);
				} else {
					throw new NotificationException("Error sending message to " + regId + ": " + error);
				}
			}
		}
	}


	private void unregisterClientId(String id) {
		Logging.info(NotificationSender.class, "Unregistered device: " + id);
		for (Client client : notificationManager.getAllClients()) {
			if (client.getId().equals(id))
				notificationManager.unregisterClient(client);
		}
	}


	private void updateClientId(String oldId, String newId) {
		Logging.info(NotificationSender.class, "Updating client id to " + newId);
		for (Client client : notificationManager.getAllClients()) {
			if (client.getId().equals(oldId)) {
				notificationManager.unregisterClient(client);
				notificationManager.registerClient(new GcmClient(newId, client.getSource()));
			}
		}
	}

}
