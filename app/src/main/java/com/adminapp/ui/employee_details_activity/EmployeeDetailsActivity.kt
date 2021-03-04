package com.adminapp.ui.employee_details_activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.adminapp.databinding.ActivityEmployeeDetailsBinding
import com.adminapp.prefrences.Preference
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var preference: Preference

    var PERMISSION_ALL = 1
    var PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var viewModel: EmployeeDetailsViewModel
    private lateinit var binding: ActivityEmployeeDetailsBinding
    var employeeName: String? = null
    var employeeId: String? = null
    var employeePhone: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmployeeDetailsViewModel::class.java)
        binding = ActivityEmployeeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        employeeId = intent?.getStringExtra("id")
        employeeName = intent?.getStringExtra("name")
        employeePhone = intent?.getStringExtra("phone")
        employeePhone = intent?.getStringExtra("profileImageUrl")

        binding.etFullName.setText(preference.getUserName())
        binding.etPhone.setText(preference.getUserPhone())

        Glide.with(this)
            .load(preference.getUserProfilePhoto())
            .thumbnail(0.1f)
            .into(binding.imageView)

        binding.btnAddEmployee.setOnClickListener {
            viewModel.uploadFirebase(
                binding.etFullName.text.toString(),
                Uri.parse(preference.getUserProfilePhoto()),
                this
            )
        }

        binding.addImage.setOnClickListener {
            if (!hasPermissions(*PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, 1)
            }

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                preference.setUserProfilePhoto(uri.toString())
                Glide.with(this)
                    .load(uri.toString())
                    .thumbnail(0.1f)
                    .into(binding.imageView)
                uri?.let { viewModel.uploadFirebase(binding.etFullName.text.toString(), it, this) }
            }
        }
    }

    private fun hasPermissions(vararg permissions: String?): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}