package com.cardshifter.server.model;

import com.cardshifter.api.messages.Message;

public interface ClientServerInterface {

	void handleMessage(ClientIO clientIO, String message);

	void performIncoming(Message message, ClientIO clientIO);

	void onDisconnected(ClientIO clientIO);

}
