package com.cardshifter.api.outgoing;

import com.cardshifter.api.ArrayUtil;
import com.cardshifter.api.messages.Message;

import java.util.Arrays;

public class AvailableTargetsMessage extends Message {

	private int entity;
	private String action;
	private int min;
	private int max;
	private int[] targets;

	public AvailableTargetsMessage() {
		this(0, "", new int[0], 0, 0);
	}

	public AvailableTargetsMessage(int entity, String action, int[] targets, int min, int max) {
		super("targets");
		this.entity = entity;
		this.action = action;
		this.min = min;
		this.max = max;
		this.targets = ArrayUtil.copyOf(targets);
	}
	
	public int getMax() {
		return max;
	}
	
	public int getMin() {
		return min;
	}
	
	public int[] getTargets() {
		return ArrayUtil.copyOf(targets);
	}

	public String getAction() {
		return action;
	}
	
	public int getEntity() {
		return entity;
	}

	@Override
	public String toString() {
		return "AvailableTargetsMessage [entity=" + entity + ", action="
				+ action + ", min=" + min + ", max=" + max + ", targets="
				+ Arrays.toString(targets) + "]";
	}
	
}
