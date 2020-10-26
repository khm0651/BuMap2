package com.biggates.bumap.ui.bus

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Adapter.BusAdapter
import com.biggates.bumap.MainActivity
import com.biggates.bumap.Model.Bus
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.bus.BusViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_bus.view.*
import kotlin.collections.ArrayList


class BusFragment : Fragment() {

    var isSelectRecyclerViewScroll = 0
    var isSearchRecyclerViewScroll = 0
    var searchBusTitleList : ArrayList<String> = arrayListOf()
    var isKeyboardShowing = false
    var globalListener = ViewTreeObserver.OnGlobalLayoutListener {
        var r = Rect();
        view!!.getWindowVisibleDisplayFrame(r);
        var screenHeight = view!!.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        var keypadHeight = screenHeight - r.bottom;


        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
            // keyboard is opened
            if (!isKeyboardShowing) {
                isKeyboardShowing = true
            }
        } else {
            // keyboard is closed
            if (isKeyboardShowing) {
                isKeyboardShowing = false
                view!!.search_edit_bus.clearFocus()
                view!!.search_edit_bus.text.clear()
            }
        }
    }
    lateinit var imm:InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_bus, container, false)

        if(findNavController().currentDestination.toString() != "home_fragment"){
            activity!!.app_bar_layout_main.visibility = View.GONE
        }

        imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        BusViewModel.isViewLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                view.progressbar_bus.visibility = View.VISIBLE
            }else{
                view.progressbar_bus.visibility = View.GONE

                var recyclerView = view.recycler_view_bus
                var linearLayoutManager = LinearLayoutManager(context)
                recyclerView.layoutManager = linearLayoutManager
                var busTitleList : ArrayList<String> = ArrayList<String>()
                var busList = BusViewModel.busList.value!!
                for(i in busList){
                    busTitleList.add(i.busStation!!)
                }
                busTitleList.sort()
                var busAdapter = BusAdapter(context!!, busTitleList, view, imm)
                recyclerView.adapter = busAdapter


                var searchRecyclerView = view.recycler_view_bus_search
                var linearLayoutManagerSearch = LinearLayoutManager(context)
                searchRecyclerView.layoutManager = linearLayoutManagerSearch
                var busSearchAdapter = BusAdapter(context!!,searchBusTitleList, view,imm)
                searchRecyclerView.adapter = busSearchAdapter

                view.getViewTreeObserver().addOnGlobalLayoutListener(globalListener)

                view.search_edit_bus.addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if(view.search_edit_bus.text.toString() != ""){
                            if(view.recycler_view_bus.visibility == View.VISIBLE){
                                view.recycler_view_bus.visibility = View.GONE
                            }
                            view.recycler_view_bus_search.visibility = View.VISIBLE
                            searchBusStation(s.toString(),BusViewModel.busList.value!!,busSearchAdapter)


                        }else{
                            view.recycler_view_bus_search.visibility = View.GONE
                        }
                    }

                })

                view.search_edit_bus.setOnTouchListener { v, event ->
                    if(view.recycler_view_bus.visibility == View.VISIBLE){
                        view.recycler_view_bus.visibility = View.GONE
                    }
                    false
                }


                view.recycler_view_bus.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        isSelectRecyclerViewScroll = newState
                    }

                })

                view.recycler_view_bus_search.addOnScrollListener(object :RecyclerView.OnScrollListener(){

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        isSearchRecyclerViewScroll=newState
                    }
                })


                view.select_layout_bus.setOnClickListener {
                    if(view.recycler_view_bus.visibility == View.GONE){
                        view.recycler_view_bus.visibility = View.VISIBLE
                    }else{
                        view.recycler_view_bus.visibility = View.GONE
                    }

                    if(view.search_edit_bus.hasFocus()){
                        view.recycler_view_bus_search.visibility = View.GONE
                        view.search_edit_bus.clearFocus()
                        view.search_edit_bus.text.clear()
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

            }
        })

        return view
    }



    private fun searchBusStation(s: String, busList: ArrayList<Bus>, busSearchAdapter: BusAdapter) {
        searchBusTitleList.clear()
        for(bus in busList){
            if(bus.busStation!!.contains(s)){
                searchBusTitleList.add(bus.busStation!!)
            }
        }
        busSearchAdapter.notifyDataSetChanged()

    }


    private fun moveFromSearchToHome() {
        findNavController().navigate(R.id.action_busFragment_to_nav_home)
        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
    }

    private var onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            moveFromSearchToHome()
            view!!.getViewTreeObserver().removeOnGlobalLayoutListener(globalListener)
        }

    }


}


