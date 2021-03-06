package org.genshin.warehouse;

import org.genshin.gsa.VisualCode;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.orders.Orders;
import org.genshin.warehouse.products.Product;
import org.genshin.warehouse.products.Products;
import org.genshin.warehouse.profiles.Profiles;
import org.genshin.warehouse.profiles.Profile;
import org.genshin.warehouse.racks.ContainerTaxon;
import org.genshin.warehouse.racks.WarehouseDivisions;

import android.content.Context;
import android.text.format.DateFormat;
import java.util.Date;

public class Warehouse {
	//Result codes from other Activities
	public static enum ResultCodes { NORMAL, SCAN, SETTINGS, PRODUCT_SELECT,
			PRODUCT_LIST, CONTAINER_SELECT, UPDATE_PRODUCT_BARCODE, STOCK_PRODUCT, ADD_PRODUCT};

	private static Context ctx;
	private static SpreeConnector spree;

	private static ContainerTaxon container;
	private static VisualCode code;

	private static Products products;
	private static Orders orders;
	private static Profiles profiles;

	private static WarehouseDivisions warehouses;

	// 入荷の時に使用
	private static Product selectProduct = new Product();
	private static ContainerTaxon selectContainer = new ContainerTaxon();

	public Warehouse(Context homeContext) {
		Warehouse.ctx = homeContext;
		Warehouse.container = null;
		spree = new SpreeConnector(Warehouse.ctx);
		products = new Products(homeContext);
		orders = new Orders(homeContext);
	}

	public static SpreeConnector Spree() {
		return spree;
	}

	public static Products Products() {
		return Warehouse.products;
	}

	public static Orders Orders() {
		return Warehouse.orders;
	}

	public static Profiles Profiles() {
		if (Warehouse.profiles == null)
			Warehouse.profiles = new Profiles(ctx);

		return profiles;
	}
	
	public static Profile Profile() {
		return Warehouse.Profiles().selected;
	}

	public static WarehouseDivisions Warehouses() {
		if (Warehouse.warehouses == null)
			Warehouse.warehouses = new WarehouseDivisions();

		return Warehouse.warehouses;
	}

	public static void setContext(Context newContext) {
		Warehouse.ctx = newContext;
		spree = new SpreeConnector(Warehouse.ctx);
	}

	public static Context getContext() {
		return Warehouse.ctx;
	}

	public static String getLocalDateString(Date date) {
		return DateFormat.getDateFormat(ctx).format(date);
	}

	public static void setContainer(ContainerTaxon container) {
		Warehouse.container = container;
	}

	public static ContainerTaxon getContainer() {
		return Warehouse.container;
	}

	public static void setVisualCode(VisualCode code) {
		Warehouse.code = code;
	}

	public static VisualCode getVisualCode() {
		return Warehouse.code;
	}

	public static Product getSelectProduct() {
		return Warehouse.selectProduct;
	}
	public static void setSelectProduct(Product product) {
		Warehouse.selectProduct =product;
	}

	public static ContainerTaxon getSelectContainer() {
		return Warehouse.selectContainer;
	}
	public static void setSelectContainer(ContainerTaxon container) {
		Warehouse.selectContainer = container;
	}
}
