package org.genshin.warehouse.products;

import java.util.ArrayList;

import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.Warehouse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProductSearcher extends SpreeConnector {
	protected ArrayList<Product> collection;
	private ArrayList<Product> list;
	private JSONObject productContainer;
	protected String code;
	protected String format;
	private int count;
	protected String mode;
	private Context context;

	// コンストラクタ
	public ProductSearcher(Context ctx, String format, String code) {
		super(ctx);
		collection = new ArrayList<Product>();
		this.code = code;
		this.format = format;
		this.mode = "LIST";
		this.context = ctx;
	}

	// コンストラクタ
	public ProductSearcher(Context ctx, String format,  String code, String mode) {
		super(ctx);
		collection = new ArrayList<Product>();
		this.code = code;
		this.format = format;
		this.mode = mode;
		this.context = ctx;
	}

	// JSONObjectを格納
	private ArrayList<Product> processProductContainer(JSONObject productContainer) {
		ArrayList<Product> collection = new ArrayList<Product>();

		if (productContainer == null)
			return null;

		//Pick apart JSON object
		try {
			this.count = productContainer.getInt("count");
			JSONArray products = productContainer.getJSONArray("products");
			for (int i = 0; i < products.length(); i++) {
				JSONObject productJSON = products.getJSONObject(i).getJSONObject("product");

				//TODO put this in variant stuff
				String sku = "";
				try {
					sku = productJSON.getString("sku");
				} catch (JSONException e) {
					//No SKU
					sku = "";
				}

				Product product = new Product(productJSON);
				collection.add(product);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		list = collection;;
		return collection;
	}

	@Override
	public void process() {
		productContainer = getJSONObject("api/products/search.json?q[variants_including_master_visual_code_eq]=" + code);
		collection = processProductContainer(productContainer);
	}

	@Override
	public void complete() {
		Warehouse.Products().setList(collection);

		//if we have one hit that's the product we want, so go to it
    	if (Warehouse.Products().getList().size() == 1) {
    		if (mode.equals("SELECT")) {
    			//int selectMode = Warehouse.ResultCodes.STOCK_PRODUCT.ordinal();
    			//ProductsMenuActivity.showProductDetails(context, Warehouse.Products().list.get(0), selectMode);
    		} else if (mode.equals("LIST")) {
    			int selectMode = Warehouse.ResultCodes.NORMAL.ordinal();
    			ProductsMenuActivity.showProductDetails(context, Warehouse.Products().getList().get(0), selectMode);
    		}
    	} else if (Warehouse.Products().getList().size() == 0) {
    		//New product?
    		Warehouse.Products().unregisteredBarcode(context, code);
    	} else {
    		if (mode.equals("SELECT")) {
    			ProductsMenuActivity.selectProductActivity(this.context, format, code);
    		}else if (mode.equals("LIST")) {
    			ProductsMenuActivity.listProductsActivity(this.context);
    		}
    	}
	}
}