package com.example.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import com.example.mapdemo.GeoFenceActivity;
import com.example.mapdemo.R;
import com.example.mapdemo.RouteActivity;
import com.example.mapdemo.SettingActivity;
import com.example.mapdemo.WeatherActivity;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private String[] mListTitles;
	private ListView mDrawerList;
	private TabHost tabhost;
	private RadioGroup mainTab;
	private Intent weather;// 天气
	private Intent goout;// 出行
	private Intent alarm;// 提醒
	private Intent setting;// 设置

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
		mListTitles = getResources().getStringArray(R.array.drawer_array);
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer);
		String[] from = { "icon", "text" };
		int[] to = { R.id.nav_icon, R.id.nav_text };
		int[] drawables = { R.drawable.alarm, R.drawable.equip,
				R.drawable.contacts, R.drawable.setting };

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

	}

	protected void selectItem(int position) {

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mListTitles[position]);

		switch (position) {
		case 0:// 天气
			Intent weatherintent = new Intent(MainActivity.this,
					WeatherActivity.class);
			startActivity(weatherintent);
			break;

		case 1:// 出行
			Intent outintent = new Intent(MainActivity.this,
					RouteActivity.class);
			startActivity(outintent);
			break;

		case 2:// 到站提醒
			Intent alarmintent = new Intent(MainActivity.this,
					GeoFenceActivity.class);
			startActivity(alarmintent);
			break;

		case 3:// setting
			Intent settingintent = new Intent(MainActivity.this,
					SettingActivity.class);
			startActivity(settingintent);
			break;

		default:
			break;
		}
		closeDrawer();

		mainTab = (RadioGroup) findViewById(R.id.main_tab_group);
		mainTab.setOnCheckedChangeListener(this);
		tabhost = getTabHost();

		weather = new Intent(this, WeatherActivity.class);
		tabhost.addTab(tabhost
				.newTabSpec("weather")
				.setIndicator(getResources().getString(R.string.weather),
						getResources().getDrawable(R.drawable.alarm))
				.setContent(weather));
		goout = new Intent(this, RouteActivity.class);
		tabhost.addTab(tabhost
				.newTabSpec("goout")
				.setIndicator(getResources().getString(R.string.goout),
						getResources().getDrawable(R.drawable.alarm))
				.setContent(goout));
		alarm = new Intent(this, GeoFenceActivity.class);
		tabhost.addTab(tabhost
				.newTabSpec("alarm")
				.setIndicator(getResources().getString(R.string.alarm),
						getResources().getDrawable(R.drawable.alarm))
				.setContent(alarm));
		setting = new Intent(this, SettingActivity.class);
		tabhost.addTab(tabhost
				.newTabSpec("setting")
				.setIndicator(getResources().getString(R.string.setting),
						getResources().getDrawable(R.drawable.alarm))
				.setContent(setting));

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		if (mDrawerLayout != null && isDrawerOpen())
			showGlobalContextActionBar();

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.main_tab_weather:
			this.tabhost.setCurrentTabByTag("weather");
			break;
		case R.id.main_goout:
			this.tabhost.setCurrentTabByTag("goout");
			break;
		case R.id.main_tab_alarm:
			this.tabhost.setCurrentTabByTag("alarm");
			break;
		case R.id.main_tab_settings:
			this.tabhost.setCurrentTabByTag("setting");
			break;

		default:
			break;
		}

	}

}
