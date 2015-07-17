package com.gtyga.mupyojung;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.URISyntaxException;


public class MainActivity extends Activity {

	private static final String server = "http://www.4kerface.com/index.php?mid=main&act=dispMemberLoginForm";
	private WebView webView;
	private boolean isUserWantExit = false;		// 종료체크
	private final String TAG = "MainActivity";

	@SuppressLint("JavascriptInterface")

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Launch Activity
		startActivity(new Intent(this, LaunchActivity.class));
		
		if (webView==null) {
			webView = (WebView) findViewById(R.id.webview);
			this.initializeWebView();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.setIntent(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN) {
			switch(keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(isUserWantExit) {
					isUserWantExit = false;
					finish();
				}
				else {
					if ( (webView.getUrl()!=null &&	
							webView.canGoBack()) ||
							(webView.getUrl().contains("404.html") || 
							webView.getUrl().contains("500.html") || 
							webView.getUrl().contains("503.html")) ) {					
						webView.goBack();
						return true;
					} 
					else {
						Toast.makeText(MainActivity.this, getString(R.string.finish_message), Toast.LENGTH_SHORT).show();
						isUserWantExit = true;

						Handler mExitHandler = new Handler();
						mExitHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								isUserWantExit = false;
							}
						}, 1800);
					}
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void initializeWebView() {
		WebSettings set = webView.getSettings();
		set.setJavaScriptEnabled(true); // javascript를 실행할 수 있도록 설정
		//set.setJavaScriptCanOpenWindowsAutomatically (true);   // javascript가 window.open()을 사용할 수 있도록 설정
		//set.setBuiltInZoomControls(true); // 안드로이드에서 제공하는 줌 아이콘을 사용할 수 있도록 설정
		//set.setPluginState(WebSettings.PluginState.ON_DEMAND); // 플러그인을 사용할 수 있도록 설정
		//set.setSupportMultipleWindows(true); // 여러개의 윈도우를 사용할 수 있도록 설정
		//set.setSupportZoom(true); // 확대,축소 기능을 사용할 수 있도록 설정
		//set.setBlockNetworkImage(false); // 네트워크의 이미지의 리소스를 로드하지않음
		//set.setLoadsImagesAutomatically(true); // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정
		//set.setUseWideViewPort(true); // wide viewport를 사용하도록 설정
		//set.setCacheMode(WebSettings.LOAD_NO_CACHE); // 웹뷰가 캐시를 사용하지 않도록 설정
		// user-agent 변조 
		//Log.d(TAG, webView.getSettings().getUserAgentString());
//		webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + getString(R.string.agent_settings));

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				new AlertDialog.Builder(view.getContext())
					.setTitle("")
					.setMessage(message)
					.setPositiveButton(getString(R.string.confirm), new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					})
					.setCancelable(false)
					.create()
					.show();
				return true;
			}
			
			@Override
			public boolean onJsConfirm(WebView view, String url, String message,
					final JsResult result) {
				new AlertDialog.Builder(view.getContext())
					.setTitle("")
					.setMessage(message)
					.setNegativeButton(getString(R.string.confirm), new AlertDialog.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();						
						}
					})
					.setPositiveButton(getString(R.string.cancel), new AlertDialog.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					})
					.setCancelable(false)
					.create()
					.show();
				return true;
			}
			
			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, JsPromptResult result) {
				return false;
			}
	
		});
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView  view, String  url) {

				if (url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
					startActivity(intent);
					return true;
				}

				// chrome 버전 intent 실행
				if (url.startsWith("intent:")) {
					try {
						Intent intent = null;
						// intent 정합성 체크
						try {
							intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
						} 
						catch (URISyntaxException ex) {
							Log.e(TAG, "Bad URI " + url + ":" + ex.getMessage());
							return false;
						}

						// 앱설치 체크
						if (getPackageManager().resolveActivity(intent, 0) == null) {
							String packagename = intent.getPackage();
							if (packagename != null) {
								Uri uri = Uri.parse("market://search?q=pname:" + packagename);
								intent = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);

								return true;
							}
						}
					} 
					catch (Exception e) {
						return false;
					}
				}
				
				if (url.startsWith("sms:")) {
					Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
					startActivity(intent);
					return true;
				}

				if (url.startsWith("mailto:")) {
					Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
					startActivity(intent);
					return true;
				}

				return false;
			}

			
		});

		webView.loadUrl(server);
	}

}

