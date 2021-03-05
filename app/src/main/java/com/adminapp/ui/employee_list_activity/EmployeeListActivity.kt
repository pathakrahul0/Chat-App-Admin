package com.adminapp.ui.employee_list_activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.adminapp.R
import com.adminapp.adapter.EmployeeScreenAdapter
import com.adminapp.databinding.ActivityEmployeeListBinding
import com.adminapp.interfaces.OnItemClicks
import com.adminapp.model.Employee
import com.adminapp.prefrences.Preference
import com.adminapp.ui.chat.ChatActivity
import com.adminapp.ui.employee_details_activity.EmployeeDetailsActivity
import com.adminapp.ui.employee_login.EmployeeLoginActivity
import com.adminapp.ui.group.GroupActivity
import com.adminapp.ui.search.SearchActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeListActivity : AppCompatActivity() {

    val tag = "EmployeeListActivity"

    @Inject
    lateinit var preference: Preference

    private lateinit var viewModel: EmployeeListActivityViewModel
    private lateinit var binding: ActivityEmployeeListBinding
    private var employeeList = ArrayList<Employee>()
    private var employeeAdapter: EmployeeScreenAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmployeeListActivityViewModel::class.java)
        binding = ActivityEmployeeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewModel.getEmployees()

        employeeAdapter = EmployeeScreenAdapter(employeeList, this, object : OnItemClicks {
            override fun onEmployeeClick(position: Int) {
                startActivity(
                    Intent(
                        this@EmployeeListActivity,
                        ChatActivity::class.java
                    ).putExtra("receiverId", employeeList[position].id)
                )
            }
        })

        binding.rvEmployee.adapter = employeeAdapter

        viewModel.employeesData.observe({ lifecycle }) {
            if (it.size > 0) {
                binding.tvNoDataFound.visibility = View.GONE
                employeeList.clear()
                employeeList.addAll(it)
                employeeAdapter?.notifyDataSetChanged()
            } else {
                binding.tvNoDataFound.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe({ lifecycle }) {
            if (it) binding.loader.visibility = View.VISIBLE
            else binding.loader.visibility = View.GONE
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_group -> {
                startActivity(Intent(this, GroupActivity::class.java))
                true
            }
            R.id.profile -> {
                startActivity(Intent(this, EmployeeDetailsActivity::class.java))
                true
            }
            R.id.search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                preference.clearData()
                startActivity(Intent(this, EmployeeLoginActivity::class.java))
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}