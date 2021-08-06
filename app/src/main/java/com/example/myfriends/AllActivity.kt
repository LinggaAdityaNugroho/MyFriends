package com.example.myfriends

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfriends.adapter.FriendAdapterViewAll
import com.example.myfriends.databinding.ActivityAllBinding
import com.example.myfriends.room.FriendDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val database by lazy { FriendDatabase(this) }
    lateinit var friendAdapterViewAll: FriendAdapterViewAll
    private lateinit var binding: ActivityAllBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)

        binding = ActivityAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        friendAdapterViewAll = FriendAdapterViewAll(this, arrayListOf())

        binding.rvViewAll.layoutManager =
            GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        binding.rvViewAll.adapter = friendAdapterViewAll
    }


    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val getDataFriend = database.friendDao().getFriend()
            Log.d("AddActivity", "dataFriendResponse: $getDataFriend")
            withContext(Dispatchers.Main) {
                friendAdapterViewAll.setData(getDataFriend)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_home, menu)

        val sear = menu?.findItem(R.id.icSearch)
        val seachV  = sear?.actionView as? SearchView
        seachV?.isSubmitButtonEnabled = true
        seachV?.setOnQueryTextListener(this)

        val search = menu?.findItem(R.id.edSearch)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchDatabase(newText)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"

        CoroutineScope(Dispatchers.IO).launch {
            val getData = database.friendDao().searchDatabase(searchQuery)
            withContext(Dispatchers.Main) {
                friendAdapterViewAll.setData(getData)
            }
        }
    }
}