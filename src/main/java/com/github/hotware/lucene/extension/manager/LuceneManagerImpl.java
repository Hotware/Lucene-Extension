/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;

import com.github.hotware.lucene.extension.bean.converter.BeanConverter;
import com.github.hotware.lucene.extension.bean.converter.BeanConverterImpl;
import com.github.hotware.lucene.extension.bean.field.BeanInformationCache;
import com.github.hotware.lucene.extension.bean.field.BeanInformationCacheImpl;
import com.github.hotware.lucene.extension.util.LuceneVersion;

/**
 * Reference Implementation of a LuceneManager.
 * 
 * <br>
 * 
 * Refreshes the SearcherManager automatically in the provided time
 * 
 * @author Martin Braun
 */
public class LuceneManagerImpl implements LuceneManager {

	private static final Logger LOGGER = Logger
			.getLogger(LuceneManagerImpl.class.getName());

	private final Lock lock;
	private final BeanConverter beanConverter;
	private final Directory directory;
	private final BeanInformationCache beanInformationCache;
	private final ScheduledExecutorService searcherScheduler;
	private SearcherManager searcherManager;
	private boolean closed;

	public static Scheduling ONCE_EVERY_MINUTE = new Scheduling(1, 1,
			TimeUnit.MINUTES);

	public static IndexWriterConfig getDefaultIndexWriterConfig() {
		IndexWriterConfig defaultIndexWriterConfig = new IndexWriterConfig(LuceneVersion.VERSION,
				new StandardAnalyzer());
		defaultIndexWriterConfig.setCheckIntegrityAtMerge(true);
		return defaultIndexWriterConfig;
	}

	public LuceneManagerImpl(Directory directory, Scheduling scheduling) throws IOException {
		this.lock = new ReentrantLock();
		this.beanConverter = new BeanConverterImpl(
				this.beanInformationCache = new BeanInformationCacheImpl());
		this.directory = directory;
		try {
			this.searcherManager = new SearcherManager(this.directory,
					new SearcherFactory());
		} catch (IOException e) {
			this.close();
			throw e;
		}
		this.searcherScheduler = Executors.newScheduledThreadPool(1);
		this.searcherScheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					LuceneManagerImpl.this.searcherManager.maybeRefresh();
				} catch (IOException e) {
					LOGGER.warning(e.toString());
				}
			}

		}, scheduling.getInitialDelay(), scheduling.getPeriod(),
				scheduling.getUnit());
	}

	@Override
	public final BeanConverter getBeanConverter() {
		return this.beanConverter;
	}

	@Override
	public final IndexWriter getIndexWriter(IndexWriterConfig config) throws IOException {
		return new IndexWriter(this.directory, config);
	}

	@Override
	public final ReferenceManager<IndexSearcher> getIndexSearcherManager() {
		this.lock.lock();
		try {
			this.checkOpen();
			return this.searcherManager;
		} finally {
			this.lock.unlock();
		}
	}

	public final void close() throws IOException {
		this.lock.lock();
		try {
			this.closed = true;
			List<Exception> exceptions = new ArrayList<>();
			try {
				if (this.searcherManager != null) {
					this.searcherManager.close();
				}
			} catch (IOException e) {
				exceptions.add(e);
			}
			if (exceptions.size() > 0) {
				throw new IOException("IOException(s) while closing"
						+ exceptions);
			}
			if (this.searcherScheduler != null) {
				// as this is only running on searchers this is safe
				this.searcherScheduler.shutdownNow();
			}
		} finally {
			if (IndexWriter.isLocked(this.directory)) {
				IndexWriter.unlock(this.directory);
			}
			this.lock.unlock();
		}
	}

	@Override
	public final BeanInformationCache getBeanInformationCache() {
		return this.beanInformationCache;
	}

	private void checkOpen() {
		if (this.closed) {
			throw new IllegalStateException("has been closed");
		}
	}

}
