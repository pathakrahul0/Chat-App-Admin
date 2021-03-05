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
import com.adminapp.R
import com.adminapp.databinding.ActivityEmployeeDetailsBinding
import com.adminapp.prefrences.Preference
import com.adminapp.ui.employee_list_activity.EmployeeListActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
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
            .error(R.drawable.ic_user)
            .into(binding.imageView)

        binding.btnAddEmployee.setOnClickListener {
            preference.setUserName(binding.etFullName.text.toString())
            viewModel.updateEmployee(
                binding.etFullName.text.toString(),
                preference.getUserProfilePhoto()!!
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

        viewModel.isUpdated.observe({lifecycle}){
            if (it) {
                Snackbar.make(binding.lEmployeeDetails, "User Updated", Snackbar.LENGTH_LONG).show()
            } else
                Snackbar.make(binding.lEmployeeDetails, "Some thing went wrong", Snackbar.LENGTH_LONG)
                    .show()
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
                uri?.let { viewModel.uploadFirebase( it, this) }
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