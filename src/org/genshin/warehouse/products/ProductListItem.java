package org.genshin.warehouse.products;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ProductListItem {
	private Drawable thumb;
	private String name;
	private String sku;
	private String link;
	private int id;
	private int count;
	private double price;

	// コンストラクタ
	public ProductListItem(Drawable thumb, String name, String sku, int count, String link, double price, int id) {
		super();
		this.id = id;
		this.thumb = thumb;
		this.name = name;
		this.sku = sku;
		this.count = count;
		this.link = link;
		this.price = price;
	}

	// 各種ゲッター
	public Drawable getThumb() {
		return this.thumb;
	}

	public String getName() {
		return this.name;
	}

	public String getSku() {
		return this.sku;
	}

	public String getLink() {
		return this.link;
	}

	public int getId() {
		return this.id;
	}

	public int getCount() {
		return this.count;
	}

	public double getPrice() {
		return this.price;
	}
}
