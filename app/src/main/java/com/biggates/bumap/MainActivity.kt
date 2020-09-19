package com.biggates.bumap

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.biggates.bumap.Interface.RetrofitService
import com.biggates.bumap.Model.Bus
import com.biggates.bumap.Model.Calendar
import com.biggates.bumap.Model.Notice
import com.biggates.bumap.Singleton.BusList
import com.biggates.bumap.Singleton.CalendarList
import com.biggates.bumap.Singleton.NoticeList
import com.biggates.bumap.Singleton.RetrofitClient
import com.biggates.bumap.ui.bus.BusFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_bus.*
import kotlinx.android.synthetic.main.fragment_bus.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var myAPI: RetrofitService
    private lateinit var retrofit : Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        search_text.setOnClickListener {
            navController.navigate(R.id.action_nav_home_to_searchFragment)
            search_edit.isEnabled = true
            search_edit.visibility = View.VISIBLE
            search_edit.requestFocus()
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            search_text.visibility = View.GONE
        }

        retrofit = RetrofitClient.getInstance()
        myAPI = retrofit.create(RetrofitService::class.java)

        myAPI.getCalendar().enqueue(object  : Callback<ArrayList<Calendar>> {
            override fun onFailure(call: Call<ArrayList<Calendar>>, t: Throwable) {
                Toast.makeText(applicationContext,"서버에 문제가 생겼습니다." + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Calendar>>,
                response: Response<ArrayList<Calendar>>
            ) {
                if(response.isSuccessful){
                    var response =response.body()

                    if(response != null) CalendarList.setList(response)
                    Toast.makeText(applicationContext,"캘린더 불러오기 완료",Toast.LENGTH_SHORT).show()
                }
            }

        })


        myAPI.getNotice().enqueue(object  : Callback<ArrayList<Notice>>{
            override fun onFailure(call: Call<ArrayList<Notice>>, t: Throwable) {
                Toast.makeText(applicationContext,"서버에 문제가 생겼습니다." + t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Notice>>,
                response: Response<ArrayList<Notice>>
            ) {
                if(response.isSuccessful){
                    var response =response.body()

                    if(response != null) NoticeList.setList(response)
                    Toast.makeText(applicationContext,"공지사항 불러오기 완료",Toast.LENGTH_SHORT).show()

                }
            }

        })

        myAPI.getBus().enqueue(object  : Callback<ArrayList<Bus>>{
            override fun onFailure(call: Call<ArrayList<Bus>>, t: Throwable) {
                Toast.makeText(applicationContext,"서버에 문제가 생겼습니다." + t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Bus>>,
                response: Response<ArrayList<Bus>>
            ) {
                if(response.isSuccessful){
                    var response =response.body()

                    if(response != null) BusList.setList(response)
                    Toast.makeText(applicationContext,"셔틀버스 시간표 불러오기 완료",Toast.LENGTH_SHORT).show()
                }
            }

        })




    }



//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.activity_main_drawer, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

        }

        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var navHostFragment = supportFragmentManager.fragments.first() as? NavHostFragment
        if(navHostFragment != null) {
            val childFragments = navHostFragment.childFragmentManager.fragments[0]
            if(childFragments is BusFragment){
                if(childFragments!!.recycler_view_bus.visibility == View.VISIBLE){
                    var rect = Rect()
                    var rect2 = Rect()
                    childFragments!!.select_layout_bus.getGlobalVisibleRect(rect2)
                    childFragments!!.recycler_view_bus.getGlobalVisibleRect(rect)



                    if(childFragments.isSelectRecyclerViewScroll != 1 && !rect.contains(ev!!.rawX.toInt(), ev!!.rawY.toInt()) && !rect2.contains(ev!!.rawX.toInt(), ev!!.rawY.toInt())){
                        childFragments!!.recycler_view_bus.visibility = View.GONE
                    }


                }

                if(childFragments.isKeyboardShowing){
                    var rect = Rect()
                    var rect2 = Rect()
                    childFragments!!.recycler_view_bus_search.getGlobalVisibleRect(rect)
                    childFragments!!.app_bar_layout_bus.getGlobalVisibleRect(rect2)
                    if(childFragments.isSearchRecyclerViewScroll != 1 && !rect.contains(ev!!.rawX.toInt(), ev!!.rawY.toInt()) && !rect2.contains(ev!!.rawX.toInt(), ev!!.rawY.toInt())){
                        childFragments!!.recycler_view_bus_search.visibility = View.GONE
                        childFragments!!.search_edit_bus.clearFocus()
                        childFragments!!.search_edit_bus.text.clear()
                        childFragments.imm.hideSoftInputFromWindow(childFragments.view!!.getWindowToken(), 0);
                    }

                }

            }

        }

        return super.dispatchTouchEvent(ev)
    }
}
