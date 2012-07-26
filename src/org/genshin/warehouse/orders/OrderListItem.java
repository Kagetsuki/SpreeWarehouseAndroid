package org.genshin.warehouse.orders;

import java.util.ArrayList;
import java.util.Date;

import org.genshin.warehouse.orders.OrdersMenuActivity.menuCodes;
import org.genshin.warehouse.products.Variant;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class OrderListItem {
	public String number;
	public Date date;
	public String name;
	public int count;
	public double price;
	public String division;
	public String paymentState;
	public String pickingState;
	public String packingState;
	public String shipmentState;
	
	public OrderListItem(String number, Date date, String name, int count, double price,
			String division, String paymentState, String pickingState, 
			String packingState, String shipmentState) {
		super();
		this.number = number;
		this.date = date;
		this.name = name;
		this.count = count;
		this.price = price;
		this.division = division;
	
		// ソート用に各種ステータスを数字に変換
		// 状態が　完了の時：1　一部完了の時：2　未の時：3　許可待ちの時：4　手渡しの時：5　不明の時：6
		if (paymentState != null)
			this.paymentState = paymentState;
		else
			this.paymentState = "6";
		if (pickingState != null)
			this.pickingState = pickingState;
		else
			this.pickingState = "6";
		if (packingState != null)
			this.packingState = packingState;
		else
			this.packingState = "6";
		if (shipmentState != null)
			this.shipmentState = shipmentState;
		else
			this.shipmentState = "6";
	}
}