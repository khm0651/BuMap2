package com.example.bumap.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bumap.Adapter.SearchAdapter
import com.example.bumap.MainActivity
import com.example.bumap.Model.Location

import com.example.bumap.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : Fragment() {

    private lateinit var search_recyclerView : RecyclerView
    private var searchList : ArrayList<String> = arrayListOf()
    private var locationList : ArrayList<Location> = arrayListOf()
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
        searchAdapter = SearchAdapter(context!!, searchList, locationList)
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

        val ref = FirebaseDatabase.getInstance().reference

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searchList.clear()
                locationList.clear()
                for(snapshot in dataSnapshot.children){

                    var arr = arrayListOf<String>()
                    var name = snapshot.child("name").value.toString()
                    var location : Location = snapshot.child("location").getValue(Location::class.java)!!
                    var range = Math.round((name.length.toDouble()/ input.length.toDouble())).toInt()
                    for(i in 0 until range){
                        if((i+input.length) <= name.length){
                            arr.add(name.substring(i, i + input.length))
                        }
                    }

                    for (char in arr){
                        if(char.equals(input)){
                            searchList.add(name)
                            locationList.add(location)
                        }
                    }

                    searchAdapter.notifyDataSetChanged()

                    for(floorSnapshot in snapshot.child("floor").children){
                        var floor = floorSnapshot.key.toString()
                        for(roomSnapshot in floorSnapshot.children){
                            var arr2 = arrayListOf<String>()
                            var roomName = roomSnapshot.child("name").value.toString()
                            var range2 = Math.round((roomName.length.toDouble()/ input.length.toDouble())).toInt()

                            for(i in 0 until range2){
                                if((i+input.length) <= roomName.length){
                                    arr2.add(roomName.substring(i, i + input.length))
                                }
                            }

                            for (char in arr2){
                                if(char.equals(input)){
                                    searchList.add(roomName+ "-" + name + " " + floor+"층")
                                    locationList.add(location)
                                }
                            }

                            searchAdapter.notifyDataSetChanged()
                        }
                    }

                }


            }
        })
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
