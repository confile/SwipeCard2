package de.majestella.serviceDetails;

import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.lorentzos.swipecards.R;
import com.lorentzos.swipecards.ServiceCardDto;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;

public class ServiceDetailsActivity extends AppCompatActivity {

  public static final String DTO = "DTO";

  private Toolbar toolbar;
  private static ServiceCardDto serviceCardDto;
  private SliderLayout sliderLayout;
  private WebView mWebView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_service_details);

    toolbar = (Toolbar)findViewById(R.id.servce_details_toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle("keinen");

    TextView priceDeleted = (TextView) findViewById(R.id.service_details_price_deleted);
//    priceDeleted.setText("This is strike-thru");
    priceDeleted.setPaintFlags(priceDeleted.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


    serviceCardDto = (ServiceCardDto)getIntent().getSerializableExtra(DTO);

    Log.d("TEST","serviceCardDto: "+serviceCardDto);

    sliderLayout = (SliderLayout)findViewById(R.id.service_details_slider_layout);
    sliderLayout.setDuration(4000);
//    sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
    sliderLayout.stopAutoCycle();


    HashMap<String,String> url_maps = new HashMap<String, String>();
    url_maps.put("Hannibal", serviceCardDto.getImageUrls().get(0));


    for(String myUrl : url_maps.values()){
      DefaultSliderView sliderView = new DefaultSliderView(this);
      // initialize a SliderLayout
      sliderView
          .image(myUrl)
          .setScaleType(BaseSliderView.ScaleType.CenterCrop);

      //add your extra information
//      textSliderView.bundle(new Bundle());
//      textSliderView.getBundle()
//          .putString("extra",name);

      sliderLayout.addSlider(sliderView);
    }


    sliderLayout.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return false;
      }
    });


//    ImageView imageView = (ImageView)findViewById(R.id.service_details_titleimage);
//
//    Picasso.with(this)
//        .load(serviceCardDto.getImageUrls().get(0))
//        .centerCrop()
//        .fit()
//        .into(imageView);

    String url = "http://confile.no-ip.biz:8080/majestella/home/test";


    mWebView = (WebView)findViewById(R.id.service_details_webView);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setAllowFileAccess(true);
    mWebView.getSettings().setAllowContentAccess(true);

    // prevent scrolling
    mWebView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return (event.getAction() == MotionEvent.ACTION_MOVE);
      }
    });

    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("MainActivityFragment", "****shouldOverrideUrlLoading: url: " + url);

        // true if not open clicked links
        return false;
      }
    });

    new LoaderTask().execute(url);


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_service_details, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
//    super.onBackPressed();
    supportFinishAfterTransition();
  }


  // http://stackoverflow.com/a/10397886/1055664
  // load the web-page with jsoup
  private class LoaderTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

      String url = params[0];
      String html = "";

      try {
        // Connect to the web site
        Document document = Jsoup.connect(url).get();

        // set link to local style-sheet
        document.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "android.css");

        // make string from jsoup-doc/web-page
        html = document.outerHtml();

      } catch (IOException e) {
        e.printStackTrace();
      }
      return html;
    }

    @Override
    protected void onPostExecute(String html) {

      Log.d("MainActivityFragment","onPostExecute()");

      // display web-page in webview
      mWebView.loadDataWithBaseURL("file:///android_asset/.", html, "text/html", "UTF-8", null);

    }
  }

}
