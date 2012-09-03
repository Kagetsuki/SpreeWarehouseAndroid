package org.genshin.warehouse.orders;

import java.util.Date;

public class OrderListItem {
	private String number;
	private Date date;
	private String name;
	private int count;
	private double price;
	private String division;
	private String paymentState;
	private String pickingState;
	private String packingState;
	private String shipmentState;

	// コンストラクタ
	public OrderListItem(String number, Date date, String name, int count, double price, String division,
			String paymentState, String pickingState, String packingState, String shipmentState) {
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

	// 各種ゲッター
	public String getNumber() {
		return this.number;
	}

	public Date getDate() {
		return this.date;
	}

	public String getName() {
		return this.name;
	}

	public int getCount() {
		return this.count;
	}

	public double getPrice() {
		return this.price;
	}

	public String getDivision() {
		return this.division;
	}

	public String getPaymentState() {
		return this.paymentState;
	}

	public String getPickingState() {
		return this.pickingState;
	}

	public String getPackingState() {
		return this.packingState;
	}

	public String getShipmentState() {
		return this.shipmentState;
	}
}