package com.biggates.bumap.ViewModel.ad

import android.content.Context
import com.google.android.gms.ads.*


object Ad {
    lateinit var adView: AdView

    fun init(context : Context){
        adView = AdView(context)
        adView.adSize = AdSize.MEDIUM_RECTANGLE
        adView.adUnitId = "ca-app-pub-2919478410688908/5843075810"
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}