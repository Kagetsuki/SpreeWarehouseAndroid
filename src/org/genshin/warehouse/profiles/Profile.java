package org.genshin.warehouse.profiles;

import org.genshin.gsa.network.NetworkTask;

public class Profile {
	public long id;
	public String server;
	public int port;
	public String name;
	public String apiKey;
	public boolean useHTTPS;
	public boolean allowUnsigned;
	
	private boolean setHTTPSbyPort(int port) {
		return NetworkTask.isStandardHTTPSPort(port);
	}
	
	public Profile() {
		this.set(-1, "", "", 80, "", true, false);
	}
	
	public Profile(long id, String name, String server, String apiKey) {
		this.set(id, name, server, 80, apiKey, setHTTPSbyPort(80), false);
	}
	
	public Profile(long id, String name, String server, int port, String apiKey) {
		this.set(id, name, server, port, apiKey, setHTTPSbyPort(port), false);
	}
	
	public Profile(long id, String name, String server, int port, String apiKey, boolean useHTTPS, boolean allowUnsigned) {
		this.set(id, name, server, port, apiKey, useHTTPS, allowUnsigned);
	}
	public void set(long id, String name, String server, int port, String apiKey, boolean useHTTPS, boolean allowUnsigned) {
		this.id = id;
		this.name = name;
		this.server = server;
		this.port = port;
		this.apiKey = apiKey;
		this.useHTTPS = useHTTPS;
		this.allowUnsigned = allowUnsigned;
	}
}
