package de.hotware.lucene.extension.bean;

import org.apache.lucene.util.Version;

public final class LuceneVersion {
	
	private LuceneVersion() {
		throw new AssertionError("can't touch this!");
	}
	
	public static final Version VERSION = Version.LUCENE_4_9;

}
