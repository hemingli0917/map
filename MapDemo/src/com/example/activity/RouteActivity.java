package com.example.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnPOIClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.overlay.BusRouteOverlay;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.map3d.demo.util.OffLineMapUtils;
import com.amap.map3d.demo.util.ToastUtil;
import com.example.activity.RouteSearchPoiDialog.OnListItemClick;

/**
 * AMapV2地图中简单介绍route搜索
 */
public class RouteActivity extends Activity implements OnMarkerClickListener,
		OnPOIClickListener, OnMapClickListener, OnInfoWindowClickListener,
		InfoWindowAdapter, OnPoiSearchListener, OnRouteSearchListener,
		LocationSource, AMapLocationListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	// 点位
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private String mcitycode;

	private Button drivingButton;
	private Button busButton;
	private Button walkButton;

	private ImageButton startImageButton;
	private ImageButton endImageButton;
	private ImageButton routeSearchImagebtn;
	// 抽屉
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private String[] mListTitles;
	private ListView mDrawerList;

	private EditText startTextView;
	private EditText endTextView;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private int busMode = RouteSearch.BusDefault;// 公交默认模式
	private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private BusRouteResult busRouteResult;// 公交模式查询结果
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式
	private int seachType = 2;// 2代表直接输入 ，1代表地图选址
	private int seachState = 1; // 0代表加锁状态，1代表取消状态

	private String strStart;
	private String strEnd;
	private String mcity;
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	private PoiSearch.Query startSearchQuery;
	private PoiSearch.Query endSearchQuery;

	private boolean isClickStart = false;
	private boolean isClickTarget = false;
	private Marker startMk, targetMk;
	private RouteSearch routeSearch;
	public ArrayAdapter<String> aAdapter;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route_activity);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.rout_drawer_layout);

		mDrawerLayout.setDrawerShadow(R.drawable.navigation_drawer_shadow,
				GravityCompat.START);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				/* empty */
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				/* empty */
			}
		};

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mListTitles = getResources().getStringArray(R.array.drawer_array);
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer);
		String[] from = { "icon", "text" };
		int[] to = { R.id.nav_icon, R.id.nav_text };
		int[] drawables = { R.drawable.weather, R.drawable.outting,
				R.drawable.alarm, R.drawable.setting };

		List<Map<String, Object>> menus = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mListTitles.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("icon", drawables[i]);
			map.put("text", mListTitles[i]);
			menus.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, menus,
				R.layout.navigator_item, from, to);
		mDrawerList.setAdapter(adapter);
		mDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});

		MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		selectItem(1);
	}

	protected void selectItem(int position) {

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mListTitles[position]);

		switch (position) {
		case 0:// 天气
			Intent weatherintent = new Intent(RouteActivity.this,
					WeatherActivity.class);
			startActivity(weatherintent);

			break;

		case 1:// 出行
			init();
			break;

		case 2:// 到站提醒
			Intent alarmintent = new Intent(RouteActivity.this,
					BuslineActivity.class);
			startActivity(alarmintent);
			break;

		case 3:// setting
			Intent settingintent = new Intent(RouteActivity.this,
					SettingActivity.class);
			startActivity(settingintent);
			break;

		default:
			break;

		}
		closeDrawer();

	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
			registerListener();// 注册监听
		}

		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		startTextView = (EditText) findViewById(R.id.autotextview_roadsearch_start);
		endTextView = (EditText) findViewById(R.id.autotextview_roadsearch_goals);
		startTextView.setOnClickListener(this);
		endTextView.setOnClickListener(this);
		busButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_transit);
		busButton.setOnClickListener(this);
		drivingButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_driving);
		drivingButton.setOnClickListener(this);
		walkButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_walk);
		walkButton.setOnClickListener(this);
		startImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_startoption);
		startImageButton.setOnClickListener(this);
		endImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_endoption);
		endImageButton.setOnClickListener(this);
		routeSearchImagebtn = (ImageButton) findViewById(R.id.imagebtn_roadsearch_search);
		routeSearchImagebtn.setOnClickListener(this);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.setTrafficEnabled(true);

		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setCompassEnabled(true);// 指南针
		aMap.getUiSettings().setLogoPosition(
				AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
		aMap.getUiSettings().setZoomPosition(
				AMapOptions.ZOOM_POSITION_RIGHT_CENTER);// 缩放按钮
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
		finish();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 选择公交模式
	 */
	private void busRoute() {
		routeType = 1;// 标识为公交模式
		busMode = RouteSearch.BusDefault;
		drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
		busButton.setBackgroundResource(R.drawable.mode_transit_on);
		walkButton.setBackgroundResource(R.drawable.mode_walk_off);

	}

	/**
	 * 选择驾车模式
	 */
	private void drivingRoute() {
		routeType = 2;// 标识为驾车模式
		drivingButton.setBackgroundResource(R.drawable.mode_driving_on);
		busButton.setBackgroundResource(R.drawable.mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.mode_walk_off);
	}

	/**
	 * 选择步行模式
	 */
	private void walkRoute() {
		routeType = 3;// 标识为步行模式
		walkMode = RouteSearch.WalkMultipath;
		drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
		busButton.setBackgroundResource(R.drawable.mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.mode_walk_on);
	}

	/**
	 * 在地图上选取起点
	 */
	private void startImagePoint() {
		ToastUtil.show(RouteActivity.this, "在地图上点击您的起点");
		isClickStart = true;
		isClickTarget = false;
		seachType = 1;// 地图选址
		aMap.clear();

		registerListener();
	}

	/**
	 * 在地图上选取终点
	 */
	private void endImagePoint() {
		ToastUtil.show(RouteActivity.this, "在地图上点击您的终点");
		isClickTarget = true;
		isClickStart = false;
		seachType = 1;// 地图选址
		aMap.clear();
		registerListener();
	}

	/**
	 * 点击搜索按钮开始Route搜索
	 */
	public void searchRoute() {
		strStart = startTextView.getText().toString().trim();
		strEnd = endTextView.getText().toString().trim();
		if (strStart == null || strStart.length() == 0) {
			ToastUtil.show(RouteActivity.this, "请选择起点");
			return;
		}
		if (strEnd == null || strEnd.length() == 0) {
			ToastUtil.show(RouteActivity.this, "请选择终点");
			return;
		}
		if (strStart.equals(strEnd)) {
			ToastUtil.show(RouteActivity.this, "起点与终点距离很近，您可以步行前往");
			return;
		}
		if (seachType == 1) {

			searchRouteResult(startPoint, endPoint);// 进行路径规划搜索
		} else if (seachType == 2) {
			startSearchResult();
			if (seachState == 1)
				searchRouteResult(startPoint, endPoint);// 进行路径规划搜索

		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

	}

	// 处理地图层次
	@Override
	public boolean onMarkerClick(Marker marker) {
		isClickStart = false;
		isClickTarget = false;
		if (marker.equals(startMk) && seachType == 1) {
			System.out.println("起点-------------" + strStart + "");

			startTextView.setText(strStart);
			seachState = 0;
			startSearchResult();// 开始搜起点
			// startPoint =
			// AMapUtil.convertToLatLonPoint(startMk.getPosition());
			// startMk.hideInfoWindow();
			startMk.remove();

		} else if (marker.equals(targetMk) && seachType == 1) {
			endTextView.setText(strEnd);
			seachState = 0;
			endSearchResult();// 开始搜终点
			// endPoint = AMapUtil.convertToLatLonPoint(targetMk.getPosition());
			// targetMk.hideInfoWindow();
			targetMk.remove();

		}
		return false;

	}

	@Override
	public void onPOIClick(Poi poi) {
		if (isClickStart) {
			// aMap.clear();
			MarkerOptions markOptiopns = new MarkerOptions();
			markOptiopns.position(poi.getCoordinate());
			TextView textView = new TextView(getApplicationContext());
			textView.setText("起始位置" + poi.getName());
			strStart = poi.getName();
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.BLACK);
			textView.setBackgroundResource(R.drawable.custom_info_bubble);
			markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));

			startMk = aMap.addMarker(markOptiopns);
			startMk.showInfoWindow();
		} else if (isClickTarget) {
			// aMap.clear();
			MarkerOptions markOptiopns = new MarkerOptions();
			markOptiopns.position(poi.getCoordinate());
			TextView textView = new TextView(getApplicationContext());
			textView.setText("到" + poi.getName() + "去");
			strEnd = poi.getName();
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.BLACK);
			textView.setBackgroundResource(R.drawable.custom_info_bubble);
			markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
			targetMk = aMap.addMarker(markOptiopns);
			targetMk.showInfoWindow();

		}

	}

	@Override
	public void onMapClick(LatLng latng) {

	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(RouteActivity.this);
		aMap.setOnMarkerClickListener(RouteActivity.this);
		aMap.setOnInfoWindowClickListener(RouteActivity.this);
		aMap.setInfoWindowAdapter(RouteActivity.this);
		aMap.setOnPOIClickListener(RouteActivity.this);
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		progDialog.setMessage("正  在  搜  索");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 查询路径规划起点
	 */
	public void startSearchResult() {
		strStart = startTextView.getText().toString().trim();
		if (startPoint != null && strStart.equals("当前位置")) {
			return;
		} else {
			showProgressDialog();

			startSearchQuery = new PoiSearch.Query(strStart, "", "mcitycode"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			startSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			startSearchQuery.setPageSize(20);// 设置每页返回多少条数据
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					startSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();// 异步poi查询

		}
	}

	/**
	 * 查询路径规划终点
	 */
	public void endSearchResult() {
		strEnd = endTextView.getText().toString().trim();
		if (endPoint != null && strEnd.equals("地图上的终点")) {
			// searchRouteResult(startPoint, endPoint);
			return;
		} else {
			showProgressDialog();
			endSearchQuery = new PoiSearch.Query(strEnd, "", "mcitycode"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			endSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			endSearchQuery.setPageSize(20);// 设置每页返回多少条数据

			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					endSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();
			// 异步poi查询
		}
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// 公交路径规划

			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "成都", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == 2) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
					null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == 3) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {

	}

	/**
	 * POI搜索结果回调
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {// 返回成功
			if (result != null && result.getQuery() != null
					&& result.getPois() != null && result.getPois().size() > 0) {// 搜索poi的结果
				if (result.getQuery().equals(startSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("您要找的起点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog,
								PoiItem startpoiItem) {
							startPoint = startpoiItem.getLatLonPoint();
							strStart = startpoiItem.getTitle();
							startTextView.setText(strStart);
							seachType = 2;
							if (seachState == 0) {

							} else if (seachState == 1) {
								endSearchResult();

							}

						}

					});
				} else if (result.getQuery().equals(endSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("您要找的终点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog, PoiItem endpoiItem) {
							endPoint = endpoiItem.getLatLonPoint();
							strEnd = endpoiItem.getTitle();
							endTextView.setText(strEnd);
							seachType = 2;
							seachState = 1;

						}

					});
				}
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(RouteActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(RouteActivity.this, getString(R.string.error_other)
					+ rCode);
		}
	}

	/**
	 * 公交路线查询回调
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				// aMap.clear();// 清理地图上的所有覆盖物
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
						busPath, busRouteResult.getStartPos(),
						busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(RouteActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(RouteActivity.this, getString(R.string.error_other)
					+ rCode);
		}
	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				// aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(RouteActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(RouteActivity.this, getString(R.string.error_other)
					+ rCode);
		}
	}

	/**
	 * 步行路线结果回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				// aMap.clear();// 清理地图上的所有覆盖物
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
						aMap, walkPath, walkRouteResult.getStartPos(),
						walkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(RouteActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(RouteActivity.this, getString(R.string.error_other)
					+ rCode);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagebtn_roadsearch_startoption:
			startImagePoint();
			break;
		case R.id.imagebtn_roadsearch_endoption:
			endImagePoint();
			break;
		case R.id.imagebtn_roadsearch_tab_transit:
			busRoute();
			break;
		case R.id.imagebtn_roadsearch_tab_driving:
			drivingRoute();
			break;
		case R.id.imagebtn_roadsearch_tab_walk:
			walkRoute();
			break;
		case R.id.imagebtn_roadsearch_search:
			searchRoute();
			break;
		default:
			break;
		}
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	/*
	 * 定位成功回调
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);
				mcitycode = amapLocation.getCityCode();

			} else {
				Log.e("AmapErr", "Location ERR:"
						+ amapLocation.getAMapException().getErrorCode());
			}
		}

	}

	/**
	 * 激活定位
	 * <p>
	 * Title: activate
	 * 
	 * @param listener
	 * @see com.amap.api.maps.LocationSource#activate(com.amap.api.maps.LocationSource.OnLocationChangedListener)
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
		}

	}

	public void closeDrawer() {
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		}
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout
						.isDrawerOpen(findViewById(R.id.navigation_drawer));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;

	}

}
