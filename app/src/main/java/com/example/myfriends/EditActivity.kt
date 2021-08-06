package com.example.myfriends

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anggapambudi.myfriend.BitmapConverter
import com.example.myfriends.databinding.ActivityAddBinding
import com.example.myfriends.databinding.ActivityEditBinding
import com.example.myfriends.room.FriendDatabase
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import java.util.concurrent.Executors

class EditActivity : AppCompatActivity() {

    private val database by lazy { FriendDatabase(this) }
    private lateinit var binding: ActivityEditBinding
    var idFriend = 0
    var nama = ""
    var sekolah = ""
    var github = ""
    var fotoProfil = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //get data
        idFriend = intent.getIntExtra("KEY_ID", 0)
        nama = intent.getStringExtra("KEY_NAME") ?: ""
        sekolah = intent.getStringExtra("KEY_SCHOOL") ?: ""
        github = intent.getStringExtra("KEY_GITHUB") ?: ""
        fotoProfil = intent.getStringExtra("KEY_IMG") ?: ""

        //bitmap
        val bitmap = BitmapConverter().stringToBitmap(this, fotoProfil)

        //binding
        binding = ActivityEditBinding.inflate(layoutInflater)
        imgEditPhoto.setImageBitmap(bitmap)
        binding.edNameEdit.setText(nama)
        binding.edSchoolEdit.setText(sekolah)
        binding.edGithubEdit.setText(github)

        setupButton()
        viewFriend()
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
                    imgEditPhoto.setImageBitmap(bitmap)
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

    private fun setupButton() {
        val intentId = intent.getIntExtra("KEY_ID", 0)
        val name = intent.getStringExtra("KEY_NAME")
        val school = intent.getStringExtra("KEY_SCHOOL")
        val github = intent.getStringExtra("KEY_GITHUB")

        intent.putExtra("KEY_ID", intentId)
        intent.putExtra("KEY_NAME", name)
        intent.putExtra("KEY_SCHOOL", school)
        intent.putExtra("KEY_GITHUB", github)
        intent.putExtra("KEY_IMG", fotoProfil)

        imgEditPhoto.setOnClickListener {
            if (checkPermissionGallery()) {
                openGallery()
            } else {
                requestPermissionGallery()
            }
        }

        btnSaveEdit.setOnClickListener {
            if (edNameEdit.text?.trim().toString().isEmpty() || edNameEdit.text?.trim().toString()
                    .isEmpty()
            ) {
                edNameEdit.error = "Field tidak boleh kosong!"
                edSchoolEdit.error = "Field tidak boleh kosong"
                edGithubEdit.error = "Field tidak boleh kosong"
            } else {

                val editName = edNameEdit.text?.trim().toString()
                val editSchool = edSchoolEdit.text?.trim().toString()
                val editGithub = edGithubEdit.text?.trim().toString()
                val id = intent.getIntExtra("KEY_ID", 0)

                CoroutineScope(Dispatchers.IO).launch {
                    database.friendDao()
                        .editFriend(FriendModel(id, editName, editSchool, editGithub, fotoProfil))
                }
                startActivity<HomeActivity>()
            }
        }
    }

    private fun viewFriend() {
        val name = intent.getStringExtra("KEY_NAME")
        val school = intent.getStringExtra("KEY_SCHOOL")
        val github = intent.getStringExtra("KEY_GITHUB")

        edNameEdit.setText(name)
        edSchoolEdit.setText(school)
        edGithubEdit.setText(github)
    }

}