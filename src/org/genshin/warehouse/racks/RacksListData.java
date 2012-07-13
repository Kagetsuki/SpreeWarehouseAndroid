package org.genshin.warehouse.racks;

import java.util.ArrayList;
import java.util.HashMap;

public class RacksListData {
	
	HashMap<String, String> data;
	String id;

	public RacksListData() {
		this.data = new HashMap<String, String>();
		this.id = null;
	}

	public RacksListData(HashMap<String, String> data, String id) {
		this.data = data;
		this.id = id;
	}

}
