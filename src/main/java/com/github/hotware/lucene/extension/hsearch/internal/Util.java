package com.github.hotware.lucene.extension.hsearch.internal;

import com.github.hotware.lucene.extension.bean.converter.BeanConverter;
import com.github.hotware.lucene.extension.bean.converter.BeanConverterImpl;
import com.github.hotware.lucene.extension.bean.field.BeanInformationCacheImpl;
import com.github.hotware.lucene.extension.util.CacheMap;

public final class Util {

	private Util() {
		throw new AssertionError("can't touch this!");
	}

	// TODO: cache everything and don't let go. maybe make this configurable via
	// settings?
	// or maybe just hook to the GarbageCollector and clean this up if the
	// GarbageCollection
	// wants to clear up
	public static final BeanConverter BEAN_CONVERTER = new BeanConverterImpl(
			new BeanInformationCacheImpl(Integer.MAX_VALUE), Integer.MAX_VALUE);
	public static final CacheMap<String, Class<?>> CLASSES_FOR_NAME = new CacheMap<>(
			Integer.MAX_VALUE);

}
