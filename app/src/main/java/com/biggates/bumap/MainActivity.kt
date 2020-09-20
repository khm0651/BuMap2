package com.biggates.bumap

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.biggates.bumap.Retrofit.RetrofitService
import com.biggates.bumap.ViewModel.bus.BusViewModel
import com.biggates.bumap.ViewModel.calendar.CalendarViewModel
import com.biggates.bumap.ViewModel.notice.NoticeViewModel
import com.biggates.bumap.ViewModel.ViewModelFactory
import com.biggates.bumap.ui.bus.BusFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_bus.*
import retrofit2.Retrofit


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var myAPI: RetrofitService
    private lateinit var retrofit : Retrofit

    lateinit var noticeViewModel : NoticeViewModel
    lateinit var busViewModel : BusViewModel
    lateinit var calendarViewModel : CalendarViewModel

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

        noticeViewModel = ViewModelProvider(this,ViewModelFactory("notice")).get(NoticeViewModel::class.java)
        noticeViewModel.setContext(applicationContext)
        noticeViewModel.loadNoitce()

        busViewModel = ViewModelProvider(this,ViewModelFactory("bus")).get(BusViewModel::class.java)
        busViewModel.setContext(applicationContext)
        busViewModel.loadBus()

        calendarViewModel = ViewModelProvider(this,ViewModelFactory("calendar")).get(CalendarViewModel::class.java)
        calendarViewModel.setContext(applicationContext)
        calendarViewModel.loadCalendar()



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
