package com.biggates.bumap.ui.notice_bumap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.NoticeBuMapAdapter
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.noticeBuMap.NoticeBuMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_notice_bu_map.*

class NoticeBuMapActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_bu_map)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var recyclerView = notice_bumap_recyclerview
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = NoticeBuMapAdapter(applicationContext,NoticeBuMap.buNotice.value!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}
