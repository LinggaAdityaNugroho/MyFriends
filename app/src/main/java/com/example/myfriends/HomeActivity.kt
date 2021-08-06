package com.example.myfriends

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfriends.databinding.ActivityHomeBinding
import com.example.myfriends.room.FriendDatabase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity


class HomeActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val database by lazy { FriendDatabase(this) }
    lateinit var friendAdapter: FriendAdapter
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(tbHome)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButton()
        setupRecyclerView()
    }

    private fun setupButton() {
        flAdd.setOnClickListener {
            val moveAdd = Intent(this, AddActivity::class.java)
            startActivity(moveAdd)
        }
        tvViewAll.onClick {
            startActivity<AllActivity>()
        }
    }

    private fun setupRecyclerView() {
        friendAdapter = FriendAdapter(this, arrayListOf())

        binding.rvNewFriend.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNewFriend.adapter = friendAdapter
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
                friendAdapter.setData(getDataFriend)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_home, menu)

        val search = menu!!.findItem(R.id.icSearch)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

        return true

    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchDatabase(newText)
        }
        return true
    }

    private fun searchDatabase(newText: String) {
        val searchQuery = "%$newText%"

        CoroutineScope(Dispatchers.IO).launch {
            val getData = database.friendDao().searchDatabase(searchQuery)
            withContext(Dispatchers.Main) {
                friendAdapter.setData(getData)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }
}