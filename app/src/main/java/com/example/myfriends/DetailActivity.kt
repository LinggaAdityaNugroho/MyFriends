package com.example.myfriends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggapambudi.myfriend.BitmapConverter
import com.example.myfriends.databinding.ActivityDetailBinding
import com.example.myfriends.room.FriendDatabase
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.tvGithub
import kotlinx.android.synthetic.main.activity_detail.tvName
import kotlinx.android.synthetic.main.activity_detail.tvSchool
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_rv_new_friend.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class DetailActivity : AppCompatActivity() {

    private val database by lazy { FriendDatabase(this) }
    lateinit var friendAdapter: FriendAdapter
    private lateinit var binding: ActivityDetailBinding
    var idFriend = 0
    var nama = ""
    var sekolah = ""
    var github = ""
    var fotoProfil = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        idFriend = intent.getIntExtra("KEY_ID", 0)
        nama = intent.getStringExtra("KEY_NAME") ?: ""
        sekolah = intent.getStringExtra("KEY_SCHOOL") ?: ""
        github = intent.getStringExtra("KEY_GITHUB") ?: ""
        fotoProfil = intent.getStringExtra("KEY_IMG") ?: ""

        val bitmap = BitmapConverter().stringToBitmap(this, fotoProfil)
        imgDetailPhoto.setImageBitmap(bitmap)

        setupButton()
        viewFriend()
    }

    private fun setupButton() {
        val intentId = intent.getIntExtra("KEY_ID", 0)
        val name = intent.getStringExtra("KEY_NAME")
        val school = intent.getStringExtra("KEY_SCHOOL")
        val github = intent.getStringExtra("KEY_GITHUB")

        btnDelete.onClick {
            CoroutineScope(Dispatchers.IO).launch {
                database.friendDao()
                    .deleteFriend(FriendModel(intentId, "$name", "$school", "$github", "$fotoProfil"))
            }
            finish()
        }

        btnEdit.onClick {
            val intent = Intent(this@DetailActivity, EditActivity::class.java)
            intent.putExtra("KEY_ID", intentId)
            intent.putExtra("KEY_NAME", name)
            intent.putExtra("KEY_SCHOOL", school)
            intent.putExtra("KEY_GITHUB", github)
            intent.putExtra("KEY_IMG", fotoProfil)
            startActivity(intent)
        }
    }
    private fun viewFriend() {
        val name = intent.getStringExtra("KEY_NAME")
        val school = intent.getStringExtra("KEY_SCHOOL")
        val github = intent.getStringExtra("KEY_GITHUB")

        tvName.setText(name)
        tvSchool.setText(school)
        tvGithub.setText(github)
    }
}
