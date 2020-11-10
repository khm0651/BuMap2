package com.biggates.bumap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.biggates.bumap.Model.LoginParam
import com.biggates.bumap.Model.Message
import com.biggates.bumap.Retrofit.RetrofitClient
import com.biggates.bumap.Retrofit.RetrofitService
import com.biggates.bumap.ViewModel.ad.Ad
import com.biggates.bumap.ViewModel.bus.BusViewModel
import com.biggates.bumap.ViewModel.calendar.CalendarViewModel
import com.biggates.bumap.ViewModel.notice.NoticeViewModel
import com.biggates.bumap.ViewModel.building.BuBuilding
import com.biggates.bumap.ViewModel.loginInfo.LoginInfo
import com.biggates.bumap.ViewModel.noticeBuMap.NoticeBuMap
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.biggates.bumap.ViewModel.shuttleBus.ShuttleBus
import com.biggates.bumap.ui.bus.BusFragment
import com.biggates.bumap.ui.notice_bumap.NoticeBuMapActivity
import com.biggates.bumap.ui.schedule.ScheduleFragment
import com.google.android.gms.ads.*
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_bus.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    val myAPI = RetrofitClient.getInstance().create(RetrofitService::class.java)
    lateinit var dbFirestore : FirebaseDatabase
    private var APP_UPDATE_REQUSET_CODE = 1004
    lateinit var appUpdateManager : AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val navController = findNavController(R.id.nav_host_fragment)
        var pref = getSharedPreferences("userInfo",0)
        var isAutoLogin = pref.getBoolean("isAutoLogin",false)
        var editor = pref.edit()
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    APP_UPDATE_REQUSET_CODE
                )
            }
        }

        MobileAds.initialize(this) {}
        Ad.init(applicationContext)
        FirebaseApp.initializeApp(applicationContext)
        dbFirestore = FirebaseDatabase.getInstance()
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        clearInfo()

        logout.setOnClickListener {
            logout.visibility = View.GONE
            headerView.login_info_header.visibility = View.GONE
            LectureScheduleViewModel.setIsViewLoading(true)
            LectureScheduleViewModel.setLectureSchedule(null)
            LectureScheduleViewModel._viewStart.postValue(false)
            var id = pref.getString("id","null")!!
            //dbFirestore.reference.child("users").child(id).removeValue()
            editor.apply{
                clear()
                commit()
            }
            headerView.id_header.text = ""
            headerView.name_hedaer.text = ""
            Toast.makeText(applicationContext,"로그아웃",Toast.LENGTH_SHORT).show()
        }
        search_text.setOnClickListener {
            navController.navigate(R.id.action_nav_home_to_searchFragment)
            search_edit.isEnabled = true
            search_edit.visibility = View.VISIBLE
            search_edit.requestFocus()
            var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            search_text.visibility = View.GONE
        }

        if(isAutoLogin){
            LoginInfo.autoLogin.postValue(true)
            var id = pref.getString("id","null")!!
            var pw = pref.getString("pw","null")!!
            var loginParam = LoginParam()
            loginParam.setId(id)
            loginParam.setPw(pw)
            buLogin(loginParam,editor)
        }

        notice_BuMap.setOnClickListener {
            startActivity(Intent(applicationContext,NoticeBuMapActivity::class.java))
        }

        LoginInfo.autoLogin.observe(this@MainActivity, Observer {
            if(it){
                var id = pref.getString("id","null")!!
                var name = pref.getString("name","null")!!
                logout.visibility = View.VISIBLE

                headerView.login_info_header.visibility = View.VISIBLE
                headerView.id_header.text = id
                headerView.name_hedaer.text = name.substring(0,name.indexOf("("))

            }
        })

        NoticeBuMap.setContext(applicationContext)
        NoticeBuMap.loadBuNotice()

        NoticeViewModel.setContext(applicationContext)
        NoticeViewModel.loadNoitce()

        ShuttleBus.setContext(applicationContext)
        ShuttleBus.loadShuttleBus()

        if(BusViewModel.busList.value.isNullOrEmpty()){
            BusViewModel.setContext(applicationContext)
            BusViewModel.loadBus()
        }

        if(CalendarViewModel.calendarList.value.isNullOrEmpty()){
            CalendarViewModel.setContext(applicationContext)
            CalendarViewModel.loadCalendar()
        }


        if(BuBuilding.buBuilding.value.isNullOrEmpty()){
            BuBuilding.setContext(applicationContext)
            BuBuilding.loadBuBuilding()
        }

    }

    private fun clearInfo() {
        LectureScheduleViewModel.setIsViewLoading(true)
        LectureScheduleViewModel.setLectureSchedule(null)
        LectureScheduleViewModel._viewStart.postValue(false)
        NoticeViewModel.setIsViewLoading(true)
        NoticeViewModel.setNoticeList(arrayListOf())
    }


    private fun buLogin(param: LoginParam, editor: SharedPreferences.Editor) {
        val myAPI = RetrofitClient.getInstance().create(RetrofitService::class.java)
        myAPI.buLogin(param).enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.isSuccessful){
                    var message = response.body()!!

                    if(message.getMessage("status") == "success"){
                        LectureScheduleViewModel.setContext(applicationContext)
                        LectureScheduleViewModel.loadSchedule(param)
                        LectureScheduleViewModel._viewStart.postValue(true)

                    }else{
                        Toast.makeText(applicationContext,"${message.getMessage("message")}\n비밀번호가 변경된것 같습니다 다시로그인해 주세요.\n비밀번호 오류 횟수 초기화를 할려면 PC로 로그인해주세요.",Toast.LENGTH_LONG).show()
                        editor.apply {
                            clear()
                            commit()
                        }

                    }
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "로그인 실패\n서버 또는 네트워크에 문제가 생겼습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

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

            if(childFragments is ScheduleFragment){
                if(childFragments.recycler_view_schedule.visibility == View.VISIBLE){
                    var rect = Rect()
                    var rect2 = Rect()
                    childFragments!!.select_layout_schedule.getGlobalVisibleRect(rect2)
                    childFragments!!.recycler_view_schedule.getGlobalVisibleRect(rect)

                    if(childFragments.isSelectRecyclerViewScroll != 1 && !rect.contains(ev!!.rawX.toInt(), ev!!.rawY.toInt()) && !rect2.contains(ev!!.rawX.toInt(), ev!!.rawY.toInt())){
                        childFragments!!.recycler_view_schedule.visibility = View.GONE
                    }
                }
            }

        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUSET_CODE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }

    }

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        APP_UPDATE_REQUSET_CODE
                    );
                }
            }
    }

}
