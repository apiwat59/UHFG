package com.pda.uhf_g;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.handheld.uhfr.UHFRManager;
import com.pda.uhf_g.util.LogUtil;
import com.pda.uhf_g.util.ScanUtil;
import com.pda.uhf_g.util.SharedUtil;
import com.uhf.api.cls.Reader;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static int type = -1;
    private AppBarConfiguration mAppBarConfiguration;
    public SharedPreferences mSharedPreferences;
    public UHFRManager mUhfrManager;
    public NavController navController;
    private ScanUtil scanUtil;
    SharedUtil sharedUtil;
    private TextView tvDeviceInfo;
    public boolean isConnectUHF = false;
    public List<String> listEPC = new ArrayList();
    long exitSytemTime = 0;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        this.tvDeviceInfo = (TextView) view.findViewById(R.id.textView_deviceinfo);
        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        this.mSharedPreferences = getSharedPreferences("UHF", 0);
        AppBarConfiguration build = new AppBarConfiguration.Builder(this.navController.getGraph()).setDrawerLayout(drawer).build();
        this.mAppBarConfiguration = build;
        NavigationUI.setupActionBarWithNavController(this, this.navController, build);
        NavigationUI.setupWithNavController(navigationView, this.navController);
        this.navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() { // from class: com.pda.uhf_g.MainActivity.1
            @Override // androidx.navigation.NavController.OnDestinationChangedListener
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                LogUtil.e("destination = " + destination.getNavigatorName());
            }
        });
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        initModule();
        setScanKeyDisable();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        setScanKeyEnable();
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeModule();
    }

    private void setScanKeyDisable() {
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion > 24) {
            ScanUtil scanUtil = ScanUtil.getInstance(this);
            this.scanUtil = scanUtil;
            scanUtil.disableScanKey("134");
            this.scanUtil.disableScanKey("137");
        }
    }

    private void setScanKeyEnable() {
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion > 24) {
            ScanUtil scanUtil = ScanUtil.getInstance(this);
            this.scanUtil = scanUtil;
            scanUtil.enableScanKey("134");
            this.scanUtil.enableScanKey("137");
        }
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initModule() {
        UHFRManager uHFRManager = UHFRManager.getInstance();
        this.mUhfrManager = uHFRManager;
        if (uHFRManager != null) {
            SharedUtil sharedUtil = new SharedUtil(this);
            this.sharedUtil = sharedUtil;
            Reader.READER_ERR err = this.mUhfrManager.setPower(sharedUtil.getPower(), this.sharedUtil.getPower());
            if (err == Reader.READER_ERR.MT_OK_ERR) {
                this.isConnectUHF = true;
                this.mUhfrManager.setRegion(Reader.Region_Conf.valueOf(this.sharedUtil.getWorkFreq()));
                Toast.makeText(getApplicationContext(), "FreRegion:" + Reader.Region_Conf.valueOf(this.sharedUtil.getWorkFreq()) + "\nRead Power:" + this.sharedUtil.getPower() + "\nWrite Power:" + this.sharedUtil.getPower(), 1).show();
                setParam();
                if (this.mUhfrManager.getHardware().equals("1.1.01")) {
                    type = 0;
                    return;
                }
                return;
            }
            Reader.READER_ERR err1 = this.mUhfrManager.setPower(30, 30);
            if (err1 == Reader.READER_ERR.MT_OK_ERR) {
                this.isConnectUHF = true;
                this.mUhfrManager.setRegion(Reader.Region_Conf.valueOf(this.mSharedPreferences.getInt("freRegion", 1)));
                Toast.makeText(getApplicationContext(), "FreRegion:" + Reader.Region_Conf.valueOf(this.mSharedPreferences.getInt("freRegion", 1)) + "\nRead Power:30\nWrite Power:30", 1).show();
                setParam();
                return;
            }
            Toast.makeText(this, getString(R.string.module_init_fail), 0).show();
            return;
        }
        Toast.makeText(this, getString(R.string.module_init_fail), 0).show();
    }

    private void setParam() {
        this.mUhfrManager.setGen2session(this.sharedUtil.getSession());
        this.mUhfrManager.setTarget(this.sharedUtil.getTarget());
        this.mUhfrManager.setQvaule(this.sharedUtil.getQvalue());
        this.mUhfrManager.setFastID(this.sharedUtil.getFastId());
        boolean b = this.mSharedPreferences.getBoolean("show_rr_advance_settings", false);
        if (b) {
            int jgTime = this.mSharedPreferences.getInt("jg_time", 6);
            int dwell = this.mSharedPreferences.getInt("dwell", 2);
            this.mUhfrManager.setRrJgDwell(jgTime, dwell);
        }
    }

    private void closeModule() {
        UHFRManager uHFRManager = this.mUhfrManager;
        if (uHFRManager != null) {
            uHFRManager.close();
            this.mUhfrManager = null;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_about /* 2131296608 */:
                this.navController.navigate(R.id.nav_about);
                break;
            case R.id.nav_help /* 2131296610 */:
                this.navController.navigate(R.id.nav_help);
                break;
            case R.id.nav_inventory /* 2131296613 */:
                this.navController.navigate(R.id.nav_inventory);
                break;
            case R.id.nav_inventory_led /* 2131296614 */:
                this.navController.navigate(R.id.nav_inventory_led);
                break;
            case R.id.nav_read_write_tag /* 2131296615 */:
                this.navController.navigate(R.id.nav_read_write_tag);
                break;
            case R.id.nav_setting /* 2131296616 */:
                this.navController.navigate(R.id.nav_setting);
                break;
            case R.id.nav_temp /* 2131296617 */:
                this.navController.navigate(R.id.nav_temp);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (System.currentTimeMillis() - this.exitSytemTime > 2000) {
                Toast.makeText(getApplicationContext(), R.string.exit_app, 0).show();
                this.exitSytemTime = System.currentTimeMillis();
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // androidx.appcompat.app.AppCompatActivity
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, this.mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override // com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.e("pang", "item = " + item.getItemId());
        return false;
    }
}
