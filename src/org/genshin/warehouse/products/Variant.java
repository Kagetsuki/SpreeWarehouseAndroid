package org.genshin.warehouse.products;

public class Variant {
	private int		id;
	private String	name;
	private int		countOnHand;
	private String	visualCode;
	private String	sku;
	private double	price;
	private double	weight;
	private double	height;
	private double	width;
	private double	depth;
	private Boolean	isMaster;
	private double	costPrice;
	private String	permalink;
	//TODO options

	//dummy init
	public Variant () {
		this.id = -1;
		this.name = "";
		this.countOnHand = 0;
		this.visualCode = "";
		this.sku = "";
		this.price = 0.0;
		this.weight = 0.0;
		this.height = 0.0;
		this.width = 0.0;
		this.depth = 0.0;
		this.isMaster = false;
		this.costPrice = 0.0;
		this.permalink = "";
	}


	public Variant(int id, String name, int countOnHand, String visualCode,
			String sku, double price, double weight, double height, double width,
			double depth, Boolean isMaster, double costPrice, String permalink) {

			this.id = id;
			this.name = name;
			this.countOnHand = countOnHand;
			this.visualCode = visualCode;
			this.sku = sku;
			this.price = price;
			this.weight = weight;
			this.height = height;
			this.width = width;
			this.depth = depth;
			this.isMaster = isMaster;
			this.costPrice = costPrice;
			this.permalink = permalink;
	}

	// 各種ゲッター
	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getCountOnHand() {
		return this.countOnHand;
	}

	public String getVisualCode() {
		return this.visualCode;
	}

	public String getSku() {
		return this.sku;
	}

	public double getPrice() {
		return this.price;
	}

	public double getWeight() {
		return this.weight;
	}

	public double getHeight() {
		return this.height;
	}

	public double getWidth() {
		return this.width;
	}

	public double getDepth() {
		return this.depth;
	}

	public Boolean getIsMaster() {
		return this.isMaster;
	}

	public double getCostPrice() {
		return this.costPrice;
	}

	public String getPermalink() {
		return this.permalink;
	}
}
