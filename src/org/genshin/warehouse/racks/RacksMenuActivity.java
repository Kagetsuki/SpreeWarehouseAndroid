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
    private TextView currentRack;

    private static int mode;
    private String rootName;
    private String rootId;
    private String currentName;
    private String currentId;

	private WarehouseDivisions warehouses;
    private ArrayList<RacksListData> warehouseRoots;
    private ArrayList<ArrayList<RacksListData>> containerTaxonomyNodes;

    private ContainerTaxonomy selectContainerTaxonomy;

	private void hookupInterface() {
		intent = getIntent();
		// モード取得
		final String modeString = intent.getStringExtra("MODE");
		// 一度拡張した後かどうか
		final String expand = intent.getStringExtra("EXPAND");

		// モード判別
		if (modeString != null) {
			// 入荷画面からコンテナを選択しにきたかどうか
			if (modeString.equals("CONTAINER_SELECT"))
				mode = Warehouse.ResultCodes.CONTAINER_SELECT.ordinal();
			else
				mode = Warehouse.ResultCodes.NORMAL.ordinal();
		} else
			mode = Warehouse.ResultCodes.NORMAL.ordinal();

		racksRootList = (ExpandableListView) findViewById(R.id.racks_root_list);
		rootRack = (TextView) findViewById(R.id.racks_root);

		currentRack = (TextView) findViewById(R.id.racks_select);
		// 一度拡張した後かどうか
		if (expand != null) {
			if (expand.equals("MORE")) {
				// 親コンテナ名、現在表示中のコンテナ名の取得
				rootName = intent.getStringExtra("GROUP_NAME");
				rootRack.setText(rootName);
				currentName = intent.getStringExtra("CHILD_NAME");
				currentRack.setText(currentName);
				currentId = intent.getStringExtra("CHILD_ID");
			}
		} else {
			rootRack.setText("/");
			// ルートを表示している時は現在表示中のコンテナのTextViewを非表示にする
			currentRack.setVisibility(View.INVISIBLE);
		}
		currentRack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 入荷画面からコンテナを選択しにきた時のみ動作
				if (mode == Warehouse.ResultCodes.CONTAINER_SELECT.ordinal() && expand != null) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(Warehouse.getContext());
					dialog.setTitle(currentRack.getText() + getString(R.string.select_container));
					dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// currentRackの場合はすでにWarehouseに格納されている。パスだけ格納
							String path = rootRack.getText() + " / " + currentRack.getText();
							Warehouse.getSelectContainer().setFullPath(path);
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
			new showExpandRacksMenu(this, currentId).execute();
		} else
			new showRacksMenuList(this).execute();

		// 子要素をクリックした時
        racksRootList.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				// 必要なデータ取得
				RacksMenuAdapter adapter = (RacksMenuAdapter)parent.getExpandableListAdapter();
				String groupName = adapter.getGroup(groupPosition).getName();
				String childId = adapter.getChild(groupPosition, childPosition).getId();
				String childName = adapter.getChild(groupPosition, childPosition).getName();
				String childPermalink = adapter.getChild(groupPosition, childPosition).getPermalink();

				// 入荷画面からコンテナ選択にきた場合にも詳細表示にも必要。Warehouseに格納しておく
				ContainerTaxon containerData = new ContainerTaxon();
				containerData.setName(childName);
				containerData.setId(Integer.parseInt(childId));
				containerData.setPermalink(childPermalink);
				Warehouse.setSelectContainer(containerData);

				// 下の階層に移動か詳細に移動か
				if (adapter.getChild(groupPosition, childPosition).getIcon()) {
					intent = new Intent(getApplicationContext(), RacksMenuActivity.class);
					intent.putExtra("EXPAND", "MORE");
				} else
					intent = new Intent(getApplicationContext(), RackDetailsActivity.class);

				// 次の表示用に格納
				intent.putExtra("GROUP_NAME", groupName);
				intent.putExtra("CHILD_NAME", childName);
				intent.putExtra("CHILD_ID", childId);

				// 入荷画面からコンテナ選択に来た場合
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
					String childId = adapter.getGroup(groupPosition).getId();
					String childName = adapter.getGroup(groupPosition).getName();
					String childPermalink = adapter.getGroup(groupPosition).getPermalink();
					String gName = rootName + " / " + currentRack.getText();
					intent = new Intent(getApplicationContext(), RackDetailsActivity.class);
					// 更に親要素の名前。親要素がない場合はRootなので/を入れる
					if (expand.equals("MORE")) {
						intent.putExtra("GROUP_NAME", gName);
					} else	{
						intent.putExtra("GROUP_NAME", "/");
					}
					intent.putExtra("CHILD_NAME", childName);

					// 子要素がない場合はWarehouseに選択したコンテナデータを格納しておく
					ContainerTaxon containerData = new ContainerTaxon();
					containerData.setName(childName);
					containerData.setId(Integer.parseInt(childId));
					containerData.setPermalink(childPermalink);
					Warehouse.setSelectContainer(containerData);

					// 入荷画面からコンテナ選択に来た場合
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

	// 最初のコンテナ表示
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
			// 親要素
			warehouseDivisionMap.setName(warehouses.getDivisions().get(i).getName());

			ArrayList<RacksListData> taxonomyNodeList = new ArrayList<RacksListData>();
			// 子要素が空の場合nullを挿入
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

					// 更に子要素があるかないかで表示アイコンが変化
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
			// 親要素
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
