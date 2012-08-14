package org.genshin.warehouse.racks;

public class RacksListData {
	
	String name;
	String group;
	String id;
	String permalink;
	boolean icon;		// アイコン表示用

	public RacksListData() {
		this.name = null;
		this.group = null;
		this.id = null;
		this.permalink = null;
		this.icon = false;
	}

	public RacksListData(String name, String group, String id, String permalink) {
		this.name = name;
		this.group = group;
		this.id = id;
		this.permalink = permalink;
	}
}
