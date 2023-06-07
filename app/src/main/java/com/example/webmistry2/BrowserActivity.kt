package com.example.webmistry2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.webmistry2.databinding.ActivityBrowserBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

const val WW="WW"
class BrowserActivity : AppCompatActivity() {
    private lateinit var binding:ActivityBrowserBinding
    private var werUrl:String?=null
    private lateinit var adView: AdView
    private var initialLayoutComplete = false
    private lateinit var adSize: AdSize
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState==null)
        {
            val url=intent.getStringExtra(WebUrl)
            if (url!=null){
             binding.apply {
                 webView.loadUrl(url)
             }
            }
        }else{

            val containKey=savedInstanceState.containsKey(WW)
            if (containKey){
                 werUrl=savedInstanceState.getString(WW)
                if (werUrl!=null){
                    binding.webView.loadUrl(werUrl!!)
                }
            }
        }

        binding.apply {
            webView.apply {
                settings.javaScriptEnabled=true
                webViewClient=object:WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        werUrl=url
                        linearProgressIndicator.visibility= View.GONE
                        linearProgressIndicator.isIndeterminate=false
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        werUrl=url
                        linearProgressIndicator.visibility= View.VISIBLE
                        linearProgressIndicator.setProgress(progress,true)
                        linearProgressIndicator.isIndeterminate=true
                    }
                }
            }
        }
        val onBackPressedCallback=object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(binding.webView.canGoBack()){
                    binding.webView.goBack()
                }else{
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        val windowMetrics = this.windowManager.defaultDisplay
        val bounds = DisplayMetrics()
        windowMetrics.getMetrics(bounds)
        var adWidthPixels = binding.adContainerInBrowserActivity.width.toFloat()
        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0f) {
            adWidthPixels = bounds.widthPixels.toFloat()
        }

        val density = resources.displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        adSize= AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)

        adView = AdView(this)
        binding.adContainerInBrowserActivity.addView(adView)
        // Since we're loading the banner based on the adContainerView size, we need
        // to wait until this view is laid out before we can get the width.
        binding.adContainerInBrowserActivity.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (werUrl!=null){
            outState.putString(WW,werUrl)
        }
    }


    private fun loadBanner() {
        adView.adUnitId = resources.getString(R.string.banner_ad_Unit_ID)

        adView.setAdSize(adSize)

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val adRequest = AdRequest
            .Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.adListener=object : AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                Toast.makeText(this@BrowserActivity, "Banner ad is Loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Toast.makeText(this@BrowserActivity, "Banner ad is Not Loaded", Toast.LENGTH_SHORT).show()
            }
        }
    }

}