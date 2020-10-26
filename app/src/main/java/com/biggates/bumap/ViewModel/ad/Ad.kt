package com.biggates.bumap.ViewModel.ad

import android.content.Context
import com.google.android.gms.ads.*


object Ad {
    lateinit var adView: AdView

    fun init(context : Context){
        adView = AdView(context)
        adView.adSize = AdSize.MEDIUM_RECTANGLE
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}