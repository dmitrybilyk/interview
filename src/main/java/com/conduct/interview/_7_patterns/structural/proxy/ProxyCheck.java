package com.conduct.interview._7_patterns.structural.proxy;

public class ProxyCheck {
	public static void main(String[] args) {
		FetchInfo fetchInfo = new FetchInfoImplProxy("correctValue");
		InfoRequester infoRequester = new InfoRequester(fetchInfo);
		infoRequester.requestInfo();
	}
}

class InfoRequester {
	private FetchInfo fetchInfo;
	public InfoRequester(FetchInfo fetchInfo) {
		this.fetchInfo = fetchInfo;
	}
	public void requestInfo() {
		System.out.println(fetchInfo.fetchInformation());
	}
}

interface FetchInfo {
	String fetchInformation();
}

class FetchInfoImpl implements FetchInfo {
	public String fetchInformation() {
		return "Important information";
	}
}

class FetchInfoImplProxy implements FetchInfo {
	private String correctToken = "correctValue";
	private String token;
	private FetchInfo fetchInfo;
	public FetchInfoImplProxy(String token) {
		this.token = token;
		fetchInfo = new FetchInfoImpl();
	}
	public String fetchInformation() {
		if (correctToken.equals(token)) {
			return "Important information";
		} else {
			return "not allowed";
		}		
	}
}

