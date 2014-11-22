package de.hotware.lucene.extension.util.tagging;

public class NextPositionTagImpl implements NextPositionTag {
	
	private final int start;
	private final String name;

	public NextPositionTagImpl(int start, String name) {
		super();
		this.start = start;
		this.name = name;
	}

	@Override
	public int getStart() {
		return this.start;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
