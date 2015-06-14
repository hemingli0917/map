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
 * AMapV2��ͼ�м򵥽���route����
 */
public class RouteActivity extends Activity implements OnMarkerClickListener,
		OnPOIClickListener, OnMapClickListener, OnInfoWindowClickListener,
		InfoWindowAdapter, OnPoiSearchListener, OnRouteSearchListener,
		LocationSource, AMapLocationListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	// ��λ
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private String mcitycode;

	private Button drivingButton;
	private Button busButton;
	private Button walkButton;

	private ImageButton startImageButton;
	private ImageButton endImageButton;
	private ImageButton routeSearchImagebtn;
	// ����
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private String[] mListTitles;
	private ListView mDrawerList;

	private EditText startTextView;
	private EditText endTextView;
	private ProgressDialog progDialog = null;// ����ʱ������
	private int busMode = RouteSearch.BusDefault;// ����Ĭ��ģʽ
	private int drivingMode = RouteSearch.DrivingDefault;// �ݳ�Ĭ��ģʽ
	private int walkMode = RouteSearch.WalkDefault;// ����Ĭ��ģʽ
	private BusRouteResult busRouteResult;// ����ģʽ��ѯ���
	private DriveRouteResult driveRouteResult;// �ݳ�ģʽ��ѯ���
	private WalkRouteResult walkRouteResult;// ����ģʽ��ѯ���
	private int routeType = 1;// 1������ģʽ��2����ݳ�ģʽ��3������ģʽ
	private int seachType = 2;// 2����ֱ������ ��1�����ͼѡַ
	private int seachState = 1; // 0�������״̬��1����ȡ��״̬

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
		mapView.onCreate(bundle);// �˷���������д
		selectItem(1);
	}

	protected void selectItem(int position) {

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mListTitles[position]);

		switch (position) {
		case 0:// ����
			Intent weatherintent = new Intent(RouteActivity.this,
					WeatherActivity.class);
			startActivity(weatherintent);

			break;

		case 1:// ����
			init();
			break;

		case 2:// ��վ����
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
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
			registerListener();// ע�����
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
	 * ����һЩamap������
	 */
	private void setUpMap() {
		aMap.setLocationSource(this);// ���ö�λ����
		aMap.setTrafficEnabled(true);

		aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.getUiSettings().setCompassEnabled(true);// ָ����
		aMap.getUiSettings().setLogoPosition(
				AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
		aMap.getUiSettings().setZoomPosition(
				AMapOptions.ZOOM_POSITION_RIGHT_CENTER);// ���Ű�ť
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
		// ���ö�λ������Ϊ��λģʽ �������ɶ�λ��������ͼ������������ת����
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
		finish();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * ѡ�񹫽�ģʽ
	 */
	private void busRoute() {
		routeType = 1;// ��ʶΪ����ģʽ
		busMode = RouteSearch.BusDefault;
		drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
		busButton.setBackgroundResource(R.drawable.mode_transit_on);
		walkButton.setBackgroundResource(R.drawable.mode_walk_off);

	}

	/**
	 * ѡ��ݳ�ģʽ
	 */
	private void drivingRoute() {
		routeType = 2;// ��ʶΪ�ݳ�ģʽ
		drivingButton.setBackgroundResource(R.drawable.mode_driving_on);
		busButton.setBackgroundResource(R.drawable.mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.mode_walk_off);
	}

	/**
	 * ѡ����ģʽ
	 */
	private void walkRoute() {
		routeType = 3;// ��ʶΪ����ģʽ
		walkMode = RouteSearch.WalkMultipath;
		drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
		busButton.setBackgroundResource(R.drawable.mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.mode_walk_on);
	}

	/**
	 * �ڵ�ͼ��ѡȡ���
	 */
	private void startImagePoint() {
		ToastUtil.show(RouteActivity.this, "�ڵ�ͼ�ϵ���������");
		isClickStart = true;
		isClickTarget = false;
		seachType = 1;// ��ͼѡַ
		aMap.clear();

		registerListener();
	}

	/**
	 * �ڵ�ͼ��ѡȡ�յ�
	 */
	private void endImagePoint() {
		ToastUtil.show(RouteActivity.this, "�ڵ�ͼ�ϵ�������յ�");
		isClickTarget = true;
		isClickStart = false;
		seachType = 1;// ��ͼѡַ
		aMap.clear();
		registerListener();
	}

	/**
	 * ���������ť��ʼRoute����
	 */
	public void searchRoute() {
		strStart = startTextView.getText().toString().trim();
		strEnd = endTextView.getText().toString().trim();
		if (strStart == null || strStart.length() == 0) {
			ToastUtil.show(RouteActivity.this, "��ѡ�����");
			return;
		}
		if (strEnd == null || strEnd.length() == 0) {
			ToastUtil.show(RouteActivity.this, "��ѡ���յ�");
			return;
		}
		if (strStart.equals(strEnd)) {
			ToastUtil.show(RouteActivity.this, "������յ����ܽ��������Բ���ǰ��");
			return;
		}
		if (seachType == 1) {

			searchRouteResult(startPoint, endPoint);// ����·���滮����
		} else if (seachType == 2) {
			startSearchResult();
			if (seachState == 1)
				searchRouteResult(startPoint, endPoint);// ����·���滮����

		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

	}

	// �����ͼ���
	@Override
	public boolean onMarkerClick(Marker marker) {
		isClickStart = false;
		isClickTarget = false;
		if (marker.equals(startMk) && seachType == 1) {
			System.out.println("���-------------" + strStart + "");

			startTextView.setText(strStart);
			seachState = 0;
			startSearchResult();// ��ʼ�����
			// startPoint =
			// AMapUtil.convertToLatLonPoint(startMk.getPosition());
			// startMk.hideInfoWindow();
			startMk.remove();

		} else if (marker.equals(targetMk) && seachType == 1) {
			endTextView.setText(strEnd);
			seachState = 0;
			endSearchResult();// ��ʼ���յ�
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
			textView.setText("��ʼλ��" + poi.getName());
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
			textView.setText("��" + poi.getName() + "ȥ");
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
	 * ע�����
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(RouteActivity.this);
		aMap.setOnMarkerClickListener(RouteActivity.this);
		aMap.setOnInfoWindowClickListener(RouteActivity.this);
		aMap.setInfoWindowAdapter(RouteActivity.this);
		aMap.setOnPOIClickListener(RouteActivity.this);
	}

	/**
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setCanceledOnTouchOutside(false);// �����ڵ��Dialog���Ƿ�ȡ��Dialog������
		progDialog.setMessage("��  ��  ��  ��");
		progDialog.show();
	}

	/**
	 * ���ؽ��ȿ�
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * ��ѯ·���滮���
	 */
	public void startSearchResult() {
		strStart = startTextView.getText().toString().trim();
		if (startPoint != null && strStart.equals("��ǰλ��")) {
			return;
		} else {
			showProgressDialog();

			startSearchQuery = new PoiSearch.Query(strStart, "", "mcitycode"); // ��һ��������ʾ��ѯ�ؼ��֣��ڶ�������ʾpoi�������ͣ�������������ʾ�������Ż��߳�����
			startSearchQuery.setPageNum(0);// ���ò�ѯ�ڼ�ҳ����һҳ��0��ʼ
			startSearchQuery.setPageSize(20);// ����ÿҳ���ض���������
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					startSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();// �첽poi��ѯ

		}
	}

	/**
	 * ��ѯ·���滮�յ�
	 */
	public void endSearchResult() {
		strEnd = endTextView.getText().toString().trim();
		if (endPoint != null && strEnd.equals("��ͼ�ϵ��յ�")) {
			// searchRouteResult(startPoint, endPoint);
			return;
		} else {
			showProgressDialog();
			endSearchQuery = new PoiSearch.Query(strEnd, "", "mcitycode"); // ��һ��������ʾ��ѯ�ؼ��֣��ڶ�������ʾpoi�������ͣ�������������ʾ�������Ż��߳�����
			endSearchQuery.setPageNum(0);// ���ò�ѯ�ڼ�ҳ����һҳ��0��ʼ
			endSearchQuery.setPageSize(20);// ����ÿҳ���ض���������

			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					endSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();
			// �첽poi��ѯ
		}
	}

	/**
	 * ��ʼ����·���滮����
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// ����·���滮

			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "�ɶ�", 0);// ��һ��������ʾ·���滮�������յ㣬�ڶ���������ʾ������ѯģʽ��������������ʾ������ѯ�������ţ����ĸ�������ʾ�Ƿ����ҹ�೵��0��ʾ������
			routeSearch.calculateBusRouteAsyn(query);// �첽·���滮����ģʽ��ѯ
		} else if (routeType == 2) {// �ݳ�·���滮
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
					null, null, "");// ��һ��������ʾ·���滮�������յ㣬�ڶ���������ʾ�ݳ�ģʽ��������������ʾ;���㣬���ĸ�������ʾ�������򣬵����������ʾ���õ�·
			routeSearch.calculateDriveRouteAsyn(query);// �첽·���滮�ݳ�ģʽ��ѯ
		} else if (routeType == 3) {// ����·���滮
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// �첽·���滮����ģʽ��ѯ
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {

	}

	/**
	 * POI��������ص�
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {// ���سɹ�
			if (result != null && result.getQuery() != null
					&& result.getPois() != null && result.getPois().size() > 0) {// ����poi�Ľ��
				if (result.getQuery().equals(startSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// ȡ��poiitem����
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("��Ҫ�ҵ������:");
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
					List<PoiItem> poiItems = result.getPois();// ȡ��poiitem����
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("��Ҫ�ҵ��յ���:");
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
	 * ����·�߲�ѯ�ص�
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				// aMap.clear();// �����ͼ�ϵ����и�����
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
	 * �ݳ�����ص�
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				// aMap.clear();// �����ͼ�ϵ����и�����
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
	 * ����·�߽���ص�
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				// aMap.clear();// �����ͼ�ϵ����и�����
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
	 * ��λ�ɹ��ص�
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
	 * ���λ
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
			// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
			// ע�����ú��ʵĶ�λʱ��ļ������С���֧��Ϊ2000ms���������ں���ʱ�����removeUpdates()������ȡ����λ����
			// �ڶ�λ�������ں��ʵ��������ڵ���destroy()����
			// ����������ʱ��Ϊ-1����λֻ��һ��
			// �ڵ��ζ�λ����£���λ���۳ɹ���񣬶��������removeUpdates()�����Ƴ����󣬶�λsdk�ڲ����Ƴ�
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
	 * ֹͣ��λ
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
