package org.genshin.warehouse.products;

import java.io.InputStream;
import java.util.ArrayList;

import org.genshin.spree.SpreeImageData;
import org.genshin.warehouse.Warehouse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class Product {
	private int id;
	private String name;
	private String sku;
	private double price;
	private String availableOn;
	private int countOnHand;
	private String description;
	private String metaDescription;
	private String permalink;
	private String visualCode;
	private SpreeImageData thumbnail;
	private ArrayList<SpreeImageData> images;
	private ArrayList<String> smallImagePath;
	private ArrayList<String> imagePath;
	private int primaryVarientIndex;
	private ArrayList<Variant> variants;

	// images初期化
	private void init() {
		this.images = new ArrayList<SpreeImageData>();
	}

	// コンストラクタ
	public Product() {
		init();

		this.id = -1;
		this.name = "";
		this.sku = "";
		this.price = 0.0;
		this.countOnHand = 0;
		this.description = "";
		this.permalink = "";
		this.visualCode = "";
		this.variants = new ArrayList<Variant>();
	}

	// コンストラクタ
	public Product(int id, String name, String sku, double price,
												int countOnHand, String description, String permalink) {
		init();

		this.id = id;
		this.name = name;
		this.sku = sku;
		this.price = price;
		this.countOnHand = countOnHand;
		this.description = description;
		this.permalink = permalink;
		this.variants = new ArrayList<Variant>();
	}

	// コンストラクタ　JSONObjectを格納
	public Product(JSONObject productJSON) {
		init();
		this.variants = new ArrayList<Variant>();

		parseProductJSON(productJSON);

		obtainImagesInfo(productJSON);
		obtainVariants(productJSON);
		obtainThumbnail();
		setImagePath();
	}

	// JSONObjectを分解、格納
	private void parseProductJSON(JSONObject productJSON) {
		try {
			this.id = productJSON.getInt("id");
			this.name = productJSON.getString("name");

			this.sku = "";
			this.price = productJSON.getDouble("price");
			this.countOnHand = productJSON.getInt("count_on_hand");
			this.description = productJSON.getString("description");
			if (this.description == null)
				this.description = "";
			this.permalink = productJSON.getString("permalink");
			this.visualCode = productJSON.getString("visual_code");
			if (this.visualCode == null)
				this.visualCode = ""; // This should be added by master variant
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// variant ゲッター、セッター
	public Variant variant() {
		if (variants.size() == 0) // no variants
			return new Variant(); // return dummy

		return variants.get(primaryVarientIndex);
	}
	public Variant variant(int idx) {
		return variants.get(idx);
	}

	// variant格納
	public void addVariant(int id, String name, int countOnHand, // basics
			String visualCode, String sku, double price, // extended identifying information
			double weight, double height, double width, double depth, //physical specifications
			Boolean isMaster, double costPrice,	String permalink) { // extended data informationß
		variants.add(new Variant(id, name, countOnHand, visualCode, sku,
										price, weight, height, width, depth, isMaster, costPrice, permalink));

		if (isMaster) {
			this.visualCode = visualCode;
			this.sku = sku;
			this.primaryVarientIndex = variants.size() - 1; // set as last added variant
		}
	}

	// サムネイル格納
	private void obtainThumbnail() {
		if (images.size() > 0) {
			this.thumbnail = images.get(0);
			this.thumbnail.setName(this.thumbnail.getName());
			this.thumbnail = getThumbnailData(this.thumbnail);
		} else
			this.thumbnail = null;
	}

	// JSONObjectからSpreeImageDataのファイル名、id格納
	private void obtainImagesInfo(JSONObject productJSON) {
		try {
			JSONArray imageInfoArray = productJSON.getJSONArray("images");
			for (int i = 0; i < imageInfoArray.length(); i++) {
				JSONObject imageInfo = imageInfoArray.getJSONObject(i).getJSONObject("image");
				images.add(new SpreeImageData
										(imageInfo.getString("attachment_file_name"), imageInfo.getInt("id")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 商品リストに表示されるため最初のサムネイルだけあらかじめ格納
	private SpreeImageData getThumbnailData(SpreeImageData image) {
		String path = "spree/products/" + image.getId() + "/small/" + image.getName();

		InputStream is = Warehouse.Spree().connector.getStream(path);

		if (is != null) {
			Drawable imageData = Drawable.createFromStream(is, image.getName());
			image.setData(imageData);
		}

		return image;
	}

	// とりあえず画像のパスだけ保存
	private void setImagePath() {
		String path;
		smallImagePath = new ArrayList<String>();
		imagePath = new ArrayList<String>();

		for (int i = 0; i < images.size(); i++) {
			path = "spree/products/" + images.get(i).getId() + "/small/" + images.get(i).getName();
			smallImagePath.add(path);
			path = "spree/products/" + images.get(i).getId() + "/product/" + images.get(i).getName();
			imagePath.add(path);
		}
	}

	// 画像格納　（処理が重くなるため現在未使用）
	private void obtainImages(JSONObject productJSON) {
		if (images.size() <= 0)
			obtainImagesInfo(productJSON);

		for (int i = 0; i < this.images.size(); i++)
			getImageData(this.images.get(i));
	}

	// 画像格納　（処理が重くなるため現在未使用）
	private SpreeImageData getImageData(SpreeImageData image) {
		String path = "spree/products/" + image.getId() + "/product/" + image.getName();

		InputStream is = Warehouse.Spree().connector.getStream(path);
		if (is != null) {
			Drawable imageData = Drawable.createFromStream(is, image.getName());
			image.setData (imageData);
		}

		return image;
	}

	// JSONObjectを分解、格納
	private void processVariantJSON(JSONObject v) {
		//pre-build object
		boolean isMaster = false;
		try {
			isMaster = v.getBoolean("is_master");
		} catch (JSONException e) {
			isMaster = false;
		}

		int id = this.id;
		try {
			id = v.getInt("id");
		} catch (JSONException e) {
			//no unique ID, so set to product ID
			id = this.id;
		}

		String name = this.name;
		try {
			name = v.getString("name");
		} catch (JSONException e) {
			name = this.name;
		}

		int countOnHand = this.countOnHand;
		try {
			countOnHand = v.getInt("count_on_hand");
		} catch (JSONException e) {
			countOnHand = this.countOnHand;
		}

		String visualCode = this.visualCode;
		try {
			visualCode = v.getString("visual_code");
		} catch (JSONException e) {
			visualCode = this.visualCode;
		}

		String sku = this.sku;
		try {
			sku = v.getString("sku");
		} catch (JSONException e) {
			sku = this.sku;
		}

		double price = this.price;
		try {
			price = v.getDouble("price");
		} catch (JSONException e) {
			price = this.price;
		}

		double weight = 0.0;
		try {
			weight = v.getDouble("weight");
		} catch (JSONException e) {
			//weight = this.variant().getWeight();
		}

		double height = 0.0;
		try {
			height = v.getDouble("height");
		} catch (JSONException e) {
			//height = this.variant().getHeight();
		}

		double width = 0.0;
		try {
			width = v.getDouble("width");
		} catch (JSONException e) {
			//width = this.variant().getWidth();
		}

		double depth = 0.0;
		try {
			depth = v.getDouble("depth");
		} catch (JSONException e) {
			//depth = this.variant().getDepth();
		}

		double costPrice = 0.0;
		try {
			costPrice = v.getDouble("cost_price");
		} catch (JSONException e) {

		}

		String permalink = this.permalink;
		try {
			permalink = v.getString("permalink");
		} catch (JSONException e) {

		}

		addVariant(id, name, countOnHand,
			visualCode,	sku, price,
			weight, height, width, depth,
			isMaster, costPrice, permalink);

	}

	// JSONObjectからvariantsを格納
	private void obtainVariants(JSONObject productJSON) {
		JSONArray variantArray = new JSONArray();

		try {
			variantArray = productJSON.getJSONArray("variants");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//Log.d("VARIANTS", "Length: " + variantArray.length());

		// get master first
		for (int i = 0; i < variantArray.length(); i++) {
			JSONObject v = new JSONObject();
			try {
				v = variantArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				// something broken? skip this one
				continue;
			}

			boolean isMaster = false;
			try {
				v = v.getJSONObject("variant");
				isMaster = v.getBoolean("is_master");
			} catch (JSONException e) {
				isMaster = false;
			}
			if (isMaster) {
				processVariantJSON(v);
				break;
			}
		}

		/*for (int i = 0; i < variantArray.length(); i++) {
			JSONObject v = new JSONObject();
			try {
				v = variantArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				// something broken? skip this one
				continue;
			}

			boolean isMaster = false;
			try {
				isMaster = v.getBoolean("is_master");
			} catch (JSONException e) {
				isMaster = false;
			}

			if (!isMaster) {
				processVariantJSON(v);
			}
		}*/

	}

	// JSONObject取得
	public void refresh() {
		JSONObject productJSON = Warehouse.Spree().connector.getJSONObject("api/products/" + permalink + ".json");
		parseProductJSON(productJSON);
	}

	// 各種ゲッター
	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSku() {
		return this.sku;
	}

	public double getPrice() {
		return this.price;
	}

	public String getAvailableOn() {
		return this.availableOn;
	}

	public int getCountOnHand() {
		return this.countOnHand;
	}

	public String getDescription() {
		return this.description;
	}

	public String getMetaDescription() {
		return this.metaDescription;
	}

	public String getPermalink() {
		return this.permalink;
	}

	public String getVisualCode() {
		return this.visualCode;
	}

	public SpreeImageData getThumbnail() {
		return this.thumbnail;
	}

	public ArrayList<SpreeImageData> getImages() {
		return this.images;
	}

	public ArrayList<String> getSmallImagePath() {
		return this.smallImagePath;
	}

	public ArrayList<String> getImagePath() {
		return this.imagePath;
	}

	public int getPrimaryVarientIndex() {
		return this.primaryVarientIndex;
	}

	public ArrayList<Variant> getVariants() {
		return this.variants;
	}
}
