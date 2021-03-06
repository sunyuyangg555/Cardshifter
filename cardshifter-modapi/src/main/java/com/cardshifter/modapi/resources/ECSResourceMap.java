package com.cardshifter.modapi.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.cardshifter.modapi.base.Component;
import com.cardshifter.modapi.base.CopyableComponent;
import com.cardshifter.modapi.base.Entity;

public class ECSResourceMap extends Component implements CopyableComponent {

	private final Map<ECSResource, ECSResourceData> map = new HashMap<>();

	private ECSResourceMap() {
	}

	public ECSResourceMap set(ECSResource res, int value) {
		ECSResourceData data = getResource(res);
		data.set(value);
		return this;
	}

    public Optional<ECSResourceData> get(ECSResource res) {
        return Optional.ofNullable(map.get(res));
    }

    public ECSResourceData getResource(ECSResource res) {
		map.computeIfAbsent(res, r -> new ECSResourceData(getEntity(), r));
		return map.get(res);
	}

	public static ECSResourceMap createFor(Entity entity) {
		ECSResourceMap res = new ECSResourceMap();
		entity.addComponent(res);
		return res;
	}
	
	public static ECSResourceMap createOrGetFor(Entity entity) {
		ECSResourceMap res = entity.getComponent(ECSResourceMap.class);
		if (res == null) {
			res = new ECSResourceMap();
			entity.addComponent(res);
		}
		return res;
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public Stream<ECSResourceData> getResources() {
		return map.values().stream();
	}

	@Override
	public Component copy(Entity copyTo) {
		ECSResourceMap copy = new ECSResourceMap();
		for (ECSResourceData data : map.values()) {
			ECSResourceData copyData = data.copy(copyTo);
			copy.map.put(data.getResource(), copyData);
		}
		return copy;
	}
	
}
