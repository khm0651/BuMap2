package com.example.bumap

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bumap.Interface.RetrofitService
import com.example.bumap.Model.Calendar
import com.example.bumap.Model.Notice
import com.example.bumap.Singleton.CalendarList
import com.example.bumap.Singleton.NoticeList
import com.example.bumap.Singleton.RetrofitClient
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
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

}
