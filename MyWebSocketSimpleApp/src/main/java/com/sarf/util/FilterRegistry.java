package com.sarf.util;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

public class FilterRegistry {

	public static Map<String, String> filterRegistry = new HashedMap();

	public static void registerFilter(String sessionId, String filter) {
		filterRegistry.put(sessionId, filter);
	}

	public static String getFilter(String sessionId) {
		return filterRegistry.get(sessionId);
	}

}
