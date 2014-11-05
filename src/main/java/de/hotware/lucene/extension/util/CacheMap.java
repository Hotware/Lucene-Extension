package de.hotware.lucene.extension.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CacheMap<K, V> extends LinkedHashMap<K, V> {

	private final int size;

	public CacheMap(int size) {
		this.size = size;
	}

	private static final long serialVersionUID = 2690314341945452137L;

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return this.size() > this.size;
	}

	@Override
	public String toString() {
		return "CacheMap [size=" + size + "]";
	}

}