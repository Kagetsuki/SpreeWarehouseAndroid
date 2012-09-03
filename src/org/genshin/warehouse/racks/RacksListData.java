package org.genshin.warehouse.racks;

public class RacksListData {
	private String name;
	private String group;
	private String id;
	private String permalink;
	// アイコン表示用
	private boolean icon;

	// コンストラクタ
	public RacksListData() {
		this.name = null;
		this.group = null;
		this.id = null;
		this.permalink = null;
		this.icon = false;
	}

	// コンストラクタ
	public RacksListData(String name, String group, String id, String permalink) {
		this.name = name;
		this.group = group;
		this.id = id;
		this.permalink = permalink;
	}

	// 各種ゲッター、セッター
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return this.group;
	}
	public void setGroup(String group) {
		this.group = group;
	}

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getPermalink() {
		return this.permalink;
	}
	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public boolean getIcon() {
		return this.icon;
	}
	public void setIcon(boolean icon) {
		this.icon = icon;
	}
}