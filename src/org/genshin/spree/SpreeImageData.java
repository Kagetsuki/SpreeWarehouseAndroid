package org.genshin.spree;

import android.graphics.drawable.Drawable;

public class SpreeImageData {
	private String name;
	private Integer id;
	private Drawable data;

	public SpreeImageData() {
		this.name = "";
		this.id = -1;
		this.data = null;
	}

	public SpreeImageData(String name, Integer id) {
		this.name = name;
		this.id = id;
		this.data = null;
	}

	public SpreeImageData(String name, Integer id, Drawable data) {
		this.name = name;
		this.id = id;
		this.data = data;
	}

	// 各種ゲッター、セッター
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Drawable getData() {
		return this.data;
	}
	public void setData(Drawable data) {
		this.data = data;
	}
}