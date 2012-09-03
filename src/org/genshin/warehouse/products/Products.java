package org.genshin.warehouse.products;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class Products {
	private Context ctx;
	private Product selected;
	private ArrayList<Product> list;
	private int count;

	// コンストラクタ
	public Products(Context ctx) {
		this.ctx = ctx;
		this.selected = null;
		this.list = new ArrayList<Product>();
		count = 0;
	}

	// ゲッター、セッター
	public Product getSelected() {
		return selected;
	}
	public void setSelect(Product product) {
		selected = product;
	}

	// リストを初期化
	public void clear() {
		this.list = new ArrayList<Product>();
		count = 0;
	}

	// JSONデータを取得
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

		list = collection;
		return collection;
	}

	// 最新の（limit）件数を取得…現在は１ページ表示
	public ArrayList<Product> getNewestProducts(int limit) {
		ArrayList<Product> collection = new ArrayList<Product>();
		JSONObject productContainer =
				Warehouse.Spree().connector.getJSONObject("api/products.json?page=1");
		collection = processProductContainer(productContainer);

		return collection;
	}

	// テキスト検索
	public ArrayList<Product> textSearch(String query) {
		ArrayList<Product> collection = new ArrayList<Product>();
		String escapedQuery = query;
		try {
			escapedQuery = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// WTF unsupported encoding? fine, just take it raw
			escapedQuery = query;
		}
		JSONObject productContainer =
				Warehouse.Spree().connector.getJSONObject("api/products/search.json?q[name_cont]=" + escapedQuery);
		collection = processProductContainer(productContainer);

		return collection;
	}

	// バーコードが登録されていない場合
	public static void unregisteredBarcode(Context ctxt, final String code) {
		final Context ctx = ctxt;
		AlertDialog.Builder question = new AlertDialog.Builder(ctx);

		// 新規商品登録
		question.setTitle(ctx.getString(R.string.unregistered_barcode_title));
		question.setMessage(ctx.getString(R.string.unregistered_barcode_new_product));
		question.setIcon(R.drawable.newproduct);
		question.setPositiveButton(ctx.getString(R.string.register_to_new_product), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(ctx, ProductEditActivity.class);
				intent.putExtra("IS_NEW", true);
				intent.putExtra("BARCODE", code);
	            ctx.startActivity(intent);
			}
		});
		// 登録済商品に追加
		question.setNeutralButton(ctx.getString(R.string.register_to_existing_product), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(ctx, ProductsMenuActivity.class);
				intent.putExtra("MODE", "UPDATE_PRODUCT_BARCODE");
				intent.putExtra("BARCODE", code);
				ctx.startActivity(intent);
			}
		});

		question.show();
	}

	// ゲッター、セッター
	public ArrayList<Product> getList() {
		return this.list;
	}
	public void setList(ArrayList<Product> list) {
		this.list = list;
	}
}