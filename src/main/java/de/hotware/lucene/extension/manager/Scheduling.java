package de.hotware.lucene.extension.manager;

import java.util.concurrent.TimeUnit;

public final class Scheduling {
	
	private final int initialDelay;
	private final int period;
	private final TimeUnit unit;
	
	public Scheduling(int initialDelay, int period, TimeUnit unit) {
		this.initialDelay = initialDelay;
		this.period = period;
		this.unit = unit;
	}

	public int getInitialDelay() {
		return initialDelay;
	}

	public int getPeriod() {
		return period;
	}

	public TimeUnit getUnit() {
		return unit;
	}
	
}