/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
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
