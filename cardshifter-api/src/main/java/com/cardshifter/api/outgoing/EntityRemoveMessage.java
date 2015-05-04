package com.cardshifter.api.outgoing;

import com.cardshifter.api.messages.Message;

public class EntityRemoveMessage extends Message {

	private int entity;

	public EntityRemoveMessage() {
		this(0);
	}

	public EntityRemoveMessage(int entity) {
		super("entityRemoved");
		this.entity = entity;
	}
	
	public int getEntity() {
		return entity;
	}

	@Override
	public String toString() {
		return "EntityRemoveMessage [entity=" + entity + "]";
	}
	
}
