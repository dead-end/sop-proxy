package de.dead.end.sop.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author dead-end
 *
 */
public class ProxyTarget {

	private final String id;

	private final String url;

	private final Map<String, String> data = new HashMap<>();

	/**
	 * Every proxy target needs at least an id (context) and an url.
	 */
	public ProxyTarget(final String id, final String url) {
		super();

		this.id = id;
		this.url = url;
	}

	/**
	 * The method returns the id for the proxy target, which is used as a context.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The method returns the url of the proxy target.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * The method adds a key / value pair to the data of the proxy target. It can be
	 * used for filtering the proxy targets in the browser.
	 */
	public void add(final String key, final String value) {
		this.data.put(key, value);
	}

	/**
	 * The method returns a JSONObject which represents this object.
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {

		final JSONObject jsonObject = new JSONObject();

		jsonObject.put(Constants.JSON_ID, id);
		jsonObject.put(Constants.JSON_URL, url);

		final JSONArray dataArray = new JSONArray();

		for (final Entry<String, String> mapEntry : this.data.entrySet()) {

			final JSONObject jsonEntry = new JSONObject();
			jsonEntry.put(mapEntry.getKey(), mapEntry.getValue());
			dataArray.add(jsonEntry);
		}

		jsonObject.put(Constants.JSON_DATA, dataArray);

		return jsonObject;
	}
}
