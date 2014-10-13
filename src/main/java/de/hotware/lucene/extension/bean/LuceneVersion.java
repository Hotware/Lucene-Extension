package de.hotware.lucene.extension.bean;

import org.apache.lucene.util.Version;

/**
 * This Extension should only operate with one Version constant. This is wrapped
 * in here
 * 
 * @author Martin Braun
 */
public final class LuceneVersion {

	private LuceneVersion() {
		throw new AssertionError("can't touch this!");
	}

	public static final Version VERSION = Version.LUCENE_4_9;

}
