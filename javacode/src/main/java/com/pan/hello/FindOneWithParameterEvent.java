package com.pan.hello;

import java.util.Map;

public class FindOneWithParameterEvent {

    private Map<String, String> queryParameters;
    private final String str = "";

    public FindOneWithParameterEvent(Map<String, String> queryParameters) {
        
        this.queryParameters = queryParameters;
    }

	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	public String getStr() {
		return str;
	}
    
}
