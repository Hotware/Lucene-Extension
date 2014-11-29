package com.github.hotware.lucene.extension.util.tagging;

public class StartEndTagImpl implements StartEndTag {

	private final int start;
	private final int end;
	private final String name;

	public StartEndTagImpl(int start, int end, String name) {
		super();
		this.start = start;
		this.end = end;
		this.name = name;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public String getName() {
		return name;
	}

}
