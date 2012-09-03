package org.genshin.warehouse.racks;

import java.util.ArrayList;

import org.genshin.gsa.network.NetworkTask;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.orders.Order;
import org.genshin.warehouse.orders.SaveOrder;
import org.genshin.warehouse.stocking.StockingMenuActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

public class RacksMenuActivity extends Activity {
	private ExpandableListView racksRootList;
    private RacksMenuAdapter adapter;

    private Intent intent;
    private TextView rootRack;
    private TextView selectRack;

    private static int mode;
    private String groupName;
    private String groupId;
    private String childId;

	private WarehouseDivisions warehouses;
    private ArrayList<RacksListData> warehouseRoots;
    private ArrayList<ArrayList<RacksListData>> containerTaxonomyNodes;

    private ContainerTaxonomy selectContainerTaxonomy;

	private void hookupInterface() {
		intent = getIntent();
		mode = Warehouse.ResultCodes.NORMAL.ordinal();
		final String modeString = intent.getStringExtra("MODE");
		final String expand = intent.getStringExtra("EXPAND");
		String selectName = null;

		// モード判別
		if (modeString != null) {
			if (modeString.equals("CONTAINER_SELECT"))
				mode = Warehouse.ResultCodes.CONTAINER_SELECT.ordinal();
		} else
			mode = Warehouse.ResultCodes.NORMAL.ordinal();

		racksRootList = (ExpandableListView) findViewById(R.id.racks_root_list);
		rootRack = (TextView) findViewById(R.id.racks_root);

		selectRack = (TextView) findViewById(R.id.racks_select);
		// 表示判別
		if (expand != null) {
			if (expand.equals("MORE")) {
				groupName = intent.getStringExtra("GROUP_NAME");
				groupId = intent.getStringExtra("GROUP_ID");
				rootRack.setText(groupName);
				selectName = intent.getStringExtra("CHILD_NAME");
				childId = intent.getStringExtra("CHILD_ID");
				selectRack.setText(selectName);
			}
		} else {
			childId = null;
			rootRack.setText("/");
		}
		selectRack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// コンテナ選択の時のみ動作
				if (mode == Warehouse.ResultCodes.CONTAINER_SELECT.ordinal()) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(Warehouse.getContext());
					dialog.setTitle(getString(R.string.select_container));
					dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							Intent intent = new Intent(getApplicationContext(), StockingMenuActivity.class);

							startActivity(intent);
						}
					});
					dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
					dialog.show();
				}
			}
		});

		// 一度拡張した後かどうか（Rootでない）
		if (expand != null) {
			new showExpandRacksMenu(this, childId).execute();
		} else
			new showRacksMenuList(this).execute();

		// 子要素をクリックした時
        racksRootList.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				RacksMenuAdapter adapter = (RacksMenuAdapter)parent.getExpandableListAdapter();
				String groupName = adapter.getGroup(groupPosition).getName();
				String groupId = adapter.getGroup(groupPosition).getId();
				String childId = adapter.getChild(groupPosition, childPosition).getId();
				String childName = adapter.getChild(groupPosition, childPosition).getName();

				if (mode == Warehouse.ResultCodes.CONTAINER_SELECT.ordinal()) {
					Warehouse.setSelectContainerName(adapter.getGroup(groupPosition).getName());
					Warehouse.setSelectContainerId(adapter.getGroup(groupPosition).getId());
					Warehouse.setSelectContainerPermalink(adapter.getGroup(groupPosition).getPermalink());
				}

				// 下の階層に移動か詳細に移動か
				if (adapter.getChild(groupPosition, childPosition).getIcon()) {
					intent = new Intent(getApplicationContext(), RacksMenuActivity.class);
					intent.putExtra("EXPAND", "MORE");
				} else
					intent = new Intent(getApplicationContext(), RackDetailsActivity.class);

				intent.putExtra("GROUP_NAME", groupName);
				intent.putExtra("GROUP_ID", groupId);
				intent.putExtra("CHILD_NAME", childName);
				intent.putExtra("CHILD_ID", childId);

				// 別画面からコンテナ選択に来た場合
				if (mode == Warehouse.ResultCodes.CONTAINER_SELECT.ordinal())
					intent.putExtra("MODE", "CONTAINER_SELECT");

				startActivity(intent);

				return false;
			}
		});

        // 親要素をクリックした時
        racksRootList.setOnGroupClickListener(new OnGroupClickListener() {
			public boolean onGroupClick(ExpandableListView parent, View v,
													int groupPosition, long id) {
				if (adapter.getChild(groupPosition, 0).getId() == null) {
					// 子要素が空の場合の処理
					//String childId = adapter.getGroup(groupPosition).getId();
					String childId = "0";
					String childName = adapter.getGroup(groupPosition).getName();
					String gName = groupName + " / " + selectRack.getText();
					intent = new Intent(getApplicationContext(), RackDetailsActivity.class);
					if (expand.equals("MORE")) {
						intent.putExtra("GROUP_NAME", gName);
						intent.putExtra("GROUP_ID", "");
					} else	{
						intent.putExtra("GROUP_NAME", "/");
						intent.putExtra("GROUP_ID", "");
					}
					intent.putExtra("CHILD_NAME", childName);
					intent.putExtra("CHILD_ID", childId);

					// 別画面からコンテナ選択に来た場合
					if (mode == Warehouse.ResultCodes.CONTAINER_SELECT.ordinal())
						intent.putExtra("MODE", "CONTAINER_SELECT");

					startActivity(intent);
					return true;
				}
				else
					return false;
			}
		});

        //if (adapter.getGroupCount() == 0) {
        // 	finishActivity(Warehouse.ResultCodes.CONTAINER_SELECT.ordinal());
        //}
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.racks);

        Warehouse.setContext(this);
        Warehouse.Warehouses();

		hookupInterface();
	}

	// コンテナ表示
	class showRacksMenuList extends NetworkTask {

		public showRacksMenuList(Context ctx) {
			super(ctx);
		}

		@Override
		protected void process() {
			warehouses = Warehouse.Warehouses();
			warehouses.getWarehouses();
			putJsonData();
		}

		protected void complete() {
			racksRootList = (ExpandableListView) findViewById(R.id.racks_root_list);
			adapter = new RacksMenuAdapter(
					Warehouse.getContext(), warehouseRoots, containerTaxonomyNodes);
			racksRootList.setAdapter(adapter);
	        racksRootList.setGroupIndicator(null);
		}
	}

	// 下の階層のコンテナ表示
	class showExpandRacksMenu extends showRacksMenuList {
		private String selectId;

		public showExpandRacksMenu(Context ctx, String selectId) {
			super(ctx);
			this.selectId = selectId;
		}

		@Override
		protected void process() {
			warehouses = Warehouse.Warehouses();
			warehouses.getWarehouses();
			selectContainerTaxonomy = new ContainerTaxonomy(selectId);
			putJsonDataExpand(selectId);
		}
	}

	// 最初の表示データ
	public void putJsonData() {
        warehouseRoots = new ArrayList<RacksListData>();
        containerTaxonomyNodes = new ArrayList<ArrayList<RacksListData>>();

		for (int i = 0; i < warehouses.getCount(); i++) {
			RacksListData warehouseDivisionMap = new RacksListData();
			warehouseDivisionMap.setName(warehouses.getDivisions().get(i).getName());
			warehouseDivisionMap.setId("" + warehouses.getDivisions().get(i).getId());

			ArrayList<RacksListData> taxonomyNodeList = new ArrayList<RacksListData>();
			if (warehouses.getDivisions().get(i).getContainers().size() == 0) {
				RacksListData taxonomyNode = new RacksListData();
				taxonomyNode.setGroup(null);
				taxonomyNode.setName(null);
				taxonomyNode.setId(null);
				taxonomyNodeList.add(taxonomyNode);
			} else {
				for (int j = 0; j < warehouses.getDivisions().get(i).getContainers().size(); j++) {
					RacksListData taxonomyNode = new RacksListData();
					taxonomyNode.setGroup(warehouses.getDivisions().get(i).getName());
					taxonomyNode.setName(warehouses.getDivisions().get(i).getContainers().get(j).getName());
					taxonomyNode.setId("" + warehouses.getDivisions().get(i).getContainers().get(j).getId());
					taxonomyNode.setPermalink
									(warehouses.getDivisions().get(i).getContainers().get(j).getPermalink());
					taxonomyNodeList.add(taxonomyNode);

					if (warehouses.getDivisions().get(i).getContainers().getTaxonomies().get(j).getChild())
						taxonomyNode.setIcon(true);
				}
			}
			containerTaxonomyNodes.add(taxonomyNodeList);
			warehouseRoots.add(warehouseDivisionMap);
		}
	}

	// 次の表示データ
	public void putJsonDataExpand(String selectId) {

		warehouseRoots = new ArrayList<RacksListData>();
        containerTaxonomyNodes = new ArrayList<ArrayList<RacksListData>>();

    	for (int i = 0; i < selectContainerTaxonomy.getList().size(); i++) {
			RacksListData warehouseDivisionMap = new RacksListData();
			warehouseDivisionMap.setName(selectContainerTaxonomy.getList().get(i).getName());
			warehouseDivisionMap.setId("" + selectContainerTaxonomy.getList().get(i).getId());
			warehouseDivisionMap.setPermalink(selectContainerTaxonomy.getList().get(i).getPermalink());

			ArrayList<RacksListData> taxonomyNodeList = new ArrayList<RacksListData>();

			// 子要素がない場合はnull挿入・・・とりあえず今は参考データがないので全部null
			RacksListData taxonomyNode = new RacksListData();
			taxonomyNode.setGroup(null);
			taxonomyNode.setName(null);
			taxonomyNode.setId(null);
			taxonomyNodeList.add(taxonomyNode);

			containerTaxonomyNodes.add(taxonomyNodeList);
			warehouseRoots.add(warehouseDivisionMap);
		}
    }

	public static enum menuCodes { newRegister };

	// メニュー
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
        // メニューアイテムを追加する
        menu.add(Menu.NONE, menuCodes.newRegister.ordinal(), Menu.NONE, res.getString(R.string.new_register));
        return super.onCreateOptionsMenu(menu);
    }

	// メニュー実装
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == menuCodes.newRegister.ordinal()) {
			Intent intent = new Intent(this, RackEditActivity.class);
			intent.putExtra("IS_NEW", true);
            startActivity(intent);

			return true;
		}

        return false;
    }

	// 長押しで最初の画面へ
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	    	startActivity(new Intent(this, WarehouseActivity.class));
	        return true;
	    }
	    return super.onKeyLongPress(keyCode, event);
	}
}
