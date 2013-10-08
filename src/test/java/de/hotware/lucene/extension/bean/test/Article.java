package de.hotware.lucene.extension.bean.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hotware.lucene.extension.bean.BeanField;
import de.hotware.lucene.extension.bean.BeanField.AnalyzerWrapper;
import de.hotware.lucene.extension.bean.BeanField.TypeWrapper;

/**
 * Arikel Bean zum Halten der Lucene Daten, nach denen gesucht werden kann, etc.
 * 
 * @author Martin Braun
 */
public final class Article implements Cloneable {

	@BeanField(store = true, index = true, type = TypeWrapper.INTEGER_STRING)
	protected Integer articleID;
	@BeanField(store = true, index = true, type = TypeWrapper.STRING)
	protected String searchText;
	@BeanField(store = true, index = true, type = TypeWrapper.BOOLEAN)
	protected Boolean showContactData;
	@BeanField(store = true, index = true, type = TypeWrapper.INTEGER)
	protected List<Integer> bestPlaced;
	@BeanField(store = true, index = true, type = TypeWrapper.DOUBLE)
	
	protected Double featureSearch1;
	@BeanField(store = true, index = false, type = TypeWrapper.STRING)
	protected String thumbnail;
	@BeanField(store = true, index = true, type = TypeWrapper.STRING, analyzer = AnalyzerWrapper.KEY_WORD_ANALYZER)
	protected String locale;
	@BeanField(store = true, index = true, type = TypeWrapper.INTEGER_STRING)
	protected List<Integer> country;
	@BeanField(type = TypeWrapper.SERIALIZED)
	protected Map<Integer, LocationInfo> locationInfo;

	public Article() {
		
	}

	/**
	 * @return the locale
	 */
	public final String getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	final void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the articleID
	 */
	public final Integer getArticleID() {
		return articleID;
	}

	/**
	 * @param articleID
	 *            the articleID to set
	 */
	final void setArticleID(Integer articleID) {
		this.articleID = articleID;
	}

	////////////////////////////////////////////////////////////////////////////////////
	//////////////////					Utilities					////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	public final LocationInfo getLocationInfo(Integer locationID) {
		LocationInfo ret = null;
		if(this.locationInfo != null) {
			ret = this.locationInfo.get(locationID);
		}
		return ret;
	}

	public final void addLocationInfo(Integer locationID,
			LocationInfo locationInfo) {
		if(this.locationInfo == null) {
			this.locationInfo = new HashMap<Integer, LocationInfo>();
		}
		this.locationInfo.put(locationID, locationInfo);
	}

	final void removeLocationInfo(Integer locationID) {
		if(this.locationInfo != null) {
			this.locationInfo.remove(locationID);
		}
	}

	public static final class LocationInfo implements Serializable {

		private static final long serialVersionUID = -1760061352492935816L;
		
		/**
		 * HACK-ALERT (not anymore)
		 */
		public static final char FANCY_SEPARATOR_CHAR = '\u1D20';

		public Integer autoin;
		public String stort;
		public String stprefix;
		public Integer countryID;
		public String stplz;
		public String regionShort;

		public LocationInfo(Integer autoin,
				String stort,
				String stprefix,
				Integer countryID,
				String stplz,
				String regionShort) {
			super();
			this.autoin = autoin;
			this.stort = stort;
			this.stprefix = stprefix;
			this.countryID = countryID;
			this.stplz = stplz;
			this.regionShort = regionShort;
		}

		@Override
		public String toString() {
			return String.format("%s%c" + "%s%c" + "%d%c" + "%s%c" + "%s",
					this.stort,
					FANCY_SEPARATOR_CHAR,
					this.stprefix,
					FANCY_SEPARATOR_CHAR,
					this.countryID,
					FANCY_SEPARATOR_CHAR,
					this.stplz,
					FANCY_SEPARATOR_CHAR,
					this.regionShort);
		}
		
	}

	public Article copy() {
		Article copy;
		try {
			copy = (Article) this.clone();
		} catch(CloneNotSupportedException e) {
			throw new AssertionError();
		}
		copy.locationInfo = new HashMap<Integer, LocationInfo>(this.locationInfo);
		return copy;
	}
}
