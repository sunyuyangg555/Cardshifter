package net.zomis.cardshifter.ecs.cards;

import net.zomis.cardshifter.ecs.base.Entity;
import net.zomis.cardshifter.ecs.base.IEvent;

public class ZoneChangeEvent implements IEvent {

	private final ZoneComponent source;
	private final ZoneComponent destination;
	private final Entity card;

	public ZoneChangeEvent(ZoneComponent source, ZoneComponent destination, Entity card) {
		this.source = source;
		this.destination = destination;
		this.card = card;
	}
	
	public Entity getCard() {
		return card;
	}
	
	public ZoneComponent getDestination() {
		return destination;
	}
	
	public ZoneComponent getSource() {
		return source;
	}
	
}
