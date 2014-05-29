package com.westfield.myapplication2.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import java.util.Collection;

/**
 * Created by mastfish on 23/05/2014.
 */
public class MonitoringActivity extends Activity implements IBeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

    private WebView mWebView;

    public String minor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ranging);
        iBeaconManager.bind(this);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
//        mWebView.loadUrl("http://html5test.com/");
        mWebView.loadUrl("http://10.80.32.224:3000/sydney/stores");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }
    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                if (iBeacons.size() > 0) {
                    IBeacon smallest = null;
                    for (IBeacon iBeacon : iBeacons) {
                        if (smallest == null) {
                            smallest = iBeacon;
                        }else {
                            if (smallest.getAccuracy() > iBeacon.getAccuracy()){
                                smallest = iBeacon;
                            }
                        }
//                        Log.i(TAG,"Saw:" + iBeacon.getMinor() + "|" + iBeacon.getAccuracy());
                    }
                    minor = "" + smallest.getMinor();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String scriptSrc = " window.hack = " + minor ;
                            mWebView.loadUrl("javascript:" + scriptSrc);
                        }
                    });
                }
            }
        });

        try {
            iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

}