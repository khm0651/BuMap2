package com.biggates.bumap.ui.ad

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.contains
import androidx.core.view.get
import com.biggates.bumap.MainActivity
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.ad.Ad
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_ad.*

class AdActivity() : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)

        ad_layout.addView(Ad.adView,0)

        cancel_ad.setOnClickListener {
            finish()
        }

        finish_ad.setOnClickListener {
            System.exit(0)
        }
    }


    override fun onDestroy() {
        ad_layout.removeViewAt(0)
        super.onDestroy()
    }
    override fun onBackPressed() {
        System.exit(0)
        super.onBackPressed()
    }
}
