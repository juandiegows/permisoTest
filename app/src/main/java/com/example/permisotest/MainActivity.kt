package com.example.permisotest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.permisotest.databinding.ActivityMainBinding
import com.example.permisotest.databinding.SelectBottomOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    enum class CODE_PERMISSION {
        NONE,
        CAMERA, GALLERY
    }

    var code = CODE_PERMISSION.NONE

    enum class CODE_RESULT {
        NONE,
        CAMERA,
        OPEN_SETTING,
        GALLERY
    }

    var code_result = CODE_RESULT.NONE

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnGetPhoto.setOnClickListener {
            OpenAlertBottom()
        }
        binding.btnP.setOnClickListener {
            binding.imgEncry.setImageBitmap(binding.imgPhotoCamera.drawable.toBitmap().Encrypt().ToBitmap())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun OpenAlertBottom() {
        var bindingBottom = SelectBottomOptionBinding.bind(
            LayoutInflater.from(this).inflate(R.layout.select_bottom_option, null)
        )
        var alert = BottomSheetDialog(this, R.style.bottomAlertStyle).apply {
            setContentView(bindingBottom.root)
        }
        alert.show()
        bindingBottom.imgCamera.setOnClickListener {
            TakePhoto()
            alert.dismiss()
        }
        bindingBottom.imgGallery.setOnClickListener {
            GetGallery()
            alert.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun GetGallery() {
        code_result = CODE_RESULT.GALLERY
        when {

            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
            -> {
                activityResultJD.launch(Intent.createChooser(Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                }, "Image"))
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                activityResultJD.launch(Intent().apply {
                    data = Uri.fromParts("package", this@MainActivity.packageName, null)
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                })
            }
            else -> {
                getPermision.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun TakePhoto() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> {
                code_result = CODE_RESULT.CAMERA
                activityResultJD.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                activityResultJD.launch(Intent().apply {
                    data = Uri.fromParts("package", this@MainActivity.packageName, null)
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                })
            }
            else -> {
                code = CODE_PERMISSION.CAMERA
                getPermision.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private val activityResultJD =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (code_result) {
                CODE_RESULT.CAMERA -> {
                    try {
                        var bitmap: Bitmap = it.data?.extras?.get("data") as Bitmap
                        binding.imgPhotoCamera.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                    }

                }
                CODE_RESULT.GALLERY ->{
                    try {
                        var bitmap:Bitmap = MediaStore.Images.Media.getBitmap(this@MainActivity.contentResolver, it.data?.data)
                        binding.imgbtnGallery.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                    }
                }
                CODE_RESULT.OPEN_SETTING -> {

                }
                else -> {}
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private val getPermision =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                when (code) {
                    CODE_PERMISSION.CAMERA -> {
                        TakePhoto()
                    }
                    CODE_PERMISSION.GALLERY -> {
                        GetGallery()
                    }
                    else -> {

                    }
                }
                Toast.makeText(this, "permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
}