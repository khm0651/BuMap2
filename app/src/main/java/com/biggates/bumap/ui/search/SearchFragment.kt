package com.biggates.bumap.ui.search

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Adapter.SearchAdapter
import com.biggates.bumap.MainActivity
import com.biggates.bumap.Model.Location

import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.building.BuBuilding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.N)
class SearchFragment : Fragment() {

    private lateinit var search_recyclerView : RecyclerView
    private var searchList : ArrayList<String> = arrayListOf()
    private var locationList : ArrayList<Location> = arrayListOf()
    private var arrayList : ArrayList<HashMap<String,Location>> = arrayListOf()
    private lateinit var  searchAdapter: SearchAdapter
    private var onBackPressedCallback = object : OnBackPressedCallback (true){
        override fun handleOnBackPressed() {
            moveFromSearchToHome()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_search, container, false)

        search_recyclerView = view.recycler_view_search
        search_recyclerView.visibility = View.VISIBLE
        search_recyclerView?.setHasFixedSize(true)
        search_recyclerView?.layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(context!!, arrayList)
        search_recyclerView.adapter = searchAdapter

        (activity as MainActivity).search_edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if((context as MainActivity).search_edit.text.toString() != ""){
                    searchBuilding(s.toString())
                }
            }

        })


        return view
    }


    private fun searchBuilding(input: String) {

        arrayList.clear()
        BuBuilding.buBuilding.value!!.forEach { buildingKey, b ->
            var location = b.location
            var name = b.name

            if(name.contains(input)){
                var map : HashMap<String,Location> = hashMapOf()
                map.put(name,location)
                arrayList.add(map)
            }


            b.floor.forEach { floorKey, f ->
                f.roomNumber.forEach { roomKey, r ->
                    var roomName = r.name
                    var roomNum = roomKey
                    if(roomName.contains(input)){
                        var map : HashMap<String,Location> = hashMapOf()
                        map.put(roomName+ "+" + name + " " + floorKey+"층+"+roomNum+"호",location)
                        arrayList.add(map)
                    }
                }
            }
        }
        Collections.sort(arrayList, object : Comparator<HashMap<String, Location>> {
            override fun compare(
                o1: HashMap<String, Location>?,
                o2: HashMap<String, Location>?
            ): Int {
                var a = o1!!.keys.first().split("+")[0]
                var b = o2!!.keys.first().split("+")[0]
                if(a.startsWith(input)){
                    return -1
                }else if(b.startsWith(input)){
                    return 1
                }else{
                    return a.compareTo(b)
                }

            }

        })
        searchAdapter.notifyDataSetChanged()


    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).toolbar.setNavigationOnClickListener {

            when(findNavController().currentDestination!!.label){

                "fragment_search" -> {
                    moveFromSearchToHome()
                }

                "fragment_home" -> {
                    val drawerLayout: DrawerLayout = (activity as MainActivity).findViewById(R.id.drawer_layout)
                    drawerLayout.openDrawer(Gravity.LEFT)
                }

                else -> {
                    findNavController().popBackStack(R.id.nav_home,false)
                }

            }

        }
    }

    private fun moveFromSearchToHome() {
        findNavController().navigate(R.id.action_searchFragment_to_nav_home)
        (activity as MainActivity).search_edit.isEnabled = false
        (activity as MainActivity).search_edit.visibility = View.GONE
        (activity as MainActivity).search_text.visibility = View.VISIBLE
        (activity as MainActivity).search_edit.clearFocus()
        (activity as MainActivity).search_edit.text.clear()
    }




}
