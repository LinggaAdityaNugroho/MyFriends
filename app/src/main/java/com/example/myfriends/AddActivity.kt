package com.example.myfriends

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.anggapambudi.myfriend.BitmapConverter
import com.example.myfriends.databinding.ActivityAddBinding
import com.example.myfriends.room.FriendDatabase
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.image
import java.io.File

class AddActivity : AppCompatActivity() {

    private val database by lazy { FriendDatabase(this) }
    lateinit var friendAdapter: FriendAdapter
    private val displaySearch = ArrayList<FriendModel>()
    private val list = ArrayList<FriendModel>()
    lateinit var binding: ActivityAddBinding
    var idFriend = 0
    var nama = ""
    var sekolah = ""
    var github = ""
    var fotoProfil = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //binding
        binding = ActivityAddBinding.inflate(layoutInflater)

        saveFriend()
    }

    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                try {
                    val bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    imgAddPhoto.setImageBitmap(bitmap)
                    fotoProfil = BitmapConverter().bitmapToString(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 110) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                Toast.makeText(this, "User tidak memberikan izin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFriend() {
        imgAddPhoto.setOnClickListener {
            if (checkPermissionGallery()) {
                openGallery()
            } else {
                requestPermissionGallery()
            }
        }

        btnSaveAdd.setOnClickListener {
            if (edNameAdd.text?.trim().toString().isEmpty() || edNameAdd.text?.trim().toString()
                    .isEmpty()
            ) {
                edNameAdd.error = "Field tidak boleh kosong!"
                edSchoolAdd.error = "Field tidak boleh kosong"
                edGithubAdd.error = "Field tidak boleh kosong"
            } else {

                val editName = edNameAdd.text?.trim().toString()
                val editSchool = edSchoolAdd.text?.trim().toString()
                val editGithub = edGithubAdd.text?.trim().toString()
                val id = intent.getIntExtra("KEY_ID", 0)

                CoroutineScope(Dispatchers.IO).launch {
                    database.friendDao()
                        .addFriend(FriendModel(id, editName, editSchool, editGithub, fotoProfil))
                }
                finish()
            }
        }
    }
}