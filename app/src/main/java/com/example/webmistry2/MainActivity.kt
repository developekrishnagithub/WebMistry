package com.example.webmistry2

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.webmistry2.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

const val WebUrl="WebUrl"
const val AdminTag="AdminTag"
class MainActivity : AppCompatActivity() ,ClickItem,OnUserEarnedRewardListener{
    private lateinit var binding:ActivityMainBinding
    private lateinit var adView: AdView
    private var initialLayoutComplete = false
    private lateinit var adSize: AdSize
    private var rewardedInterstitialAd:RewardedInterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data =listOfWebTabs()

        val adapter=WebAdapter(data,this)

        binding.recyclerview.adapter=adapter
        binding.recyclerview.layoutManager=GridLayoutManager(this,2)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {
            loadAd()
        }

        val windowMetrics = this.windowManager.defaultDisplay
        val bounds = DisplayMetrics()
        windowMetrics.getMetrics(bounds)
        var adWidthPixels = binding.adFramLayout.width.toFloat()
        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0f) {
            adWidthPixels = bounds.widthPixels.toFloat()
        }

        val density = resources.displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        adSize= AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)


        adView = AdView(this)
        binding.adFramLayout.addView(adView)
        // Since we're loading the banner based on the adContainerView size, we need
        // to wait until this view is laid out before we can get the width.
        binding.adFramLayout.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

        val onBackPressedCallback=object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (rewardedInterstitialAd!=null){
                    rewardedInterstitialAd?.show(this@MainActivity,this@MainActivity)
                }else{
                    finish()
                }
            }

        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)

    }


    override fun clickItem(web: Web) {
        val intent=Intent(this@MainActivity,BrowserActivity::class.java)
        intent.apply {
            putExtra(WebUrl,web.webUrl)
        }

        startActivity(intent)
    }


    private fun listOfWebTabs(): ArrayList<Web> {
        val arrayList=ArrayList<Web>()
        arrayList.apply {
            add(Web("Google","https://www.google.com",R.drawable.google))
            add(Web("Microsoft Bing","https://www.bing.com",R.drawable.bing))
            add(Web("Yahoo!","https://www.yahoo.com",R.drawable.yahoo))
            add(Web("Yandex","https://www.yandex.com",R.drawable.yandex))
            add(Web("DuckDuckGo","https://www.duckduckgo.com",R.drawable.duckduckgo))
            add(Web("Ask.com","https://www.ask.com",R.drawable.ask))
            add(Web("ecosia","https://www.ecosia.com",R.drawable.ecosia))
            add(Web("Opera Mini","https://opera.com",R.drawable.opera_mini))
            add(Web("Start Page","https://startpage.com",R.drawable.startpage))
            add(Web("Brave","https://search.brave.com/",R.drawable.brave))
        }
        return arrayList
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

        adView.adListener=object :AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                Toast.makeText(this@MainActivity, "Banner ad is Loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)

                Toast.makeText(this@MainActivity, "Banner ad is Not Loaded", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAd() {
        RewardedInterstitialAd.load(this, resources.getString(R.string.reword_ad_Unit_ID),
                    AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    rewardedInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(AdminTag, "Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(AdminTag, "Ad dismissed fullscreen content.")
                            finish()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            Log.e(AdminTag, "Ad failed to show fullscreen content.")
                            finish()
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(AdminTag, "Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(AdminTag, "Ad showed fullscreen content.")
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "add Load Error ${adError.message}")
                    rewardedInterstitialAd = null
                }
            })
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        Toast.makeText(this, "You Earn ${p0.amount} Point", Toast.LENGTH_SHORT).show()
    }

}