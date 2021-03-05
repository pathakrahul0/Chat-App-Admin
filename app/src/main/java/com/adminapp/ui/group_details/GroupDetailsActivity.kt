package com.adminapp.ui.group_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adminapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: GroupDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
    }
}