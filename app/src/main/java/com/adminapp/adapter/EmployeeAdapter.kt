package com.adminapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adminapp.R
import com.adminapp.databinding.AdapterEmployeeBinding
import com.adminapp.interfaces.OnItemClicks
import com.adminapp.model.Employee
import com.adminapp.utils.Constants
import com.bumptech.glide.Glide

class EmployeeAdapter(
    private val employeeList: ArrayList<Employee>,
    private val activity: Activity,
    private val onItemClicks: OnItemClicks,
) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_employee,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.rowBinding.tvEmployeeName.text = employee.name
        holder.rowBinding.tvPhone.text = employee.phone
        if (!employee.profileImageUrl.isNullOrEmpty())
            Glide.with(activity).load(employee.profileImageUrl).thumbnail(0.1f)
                .error(R.drawable.ic_user)
                .into(holder.rowBinding.profileImage)
        holder.rowBinding.tvTime.text = Constants.getDateTime(employee.updatedAt, "dd/MM/yyyy")
        holder.rowBinding.lEmployee.setOnClickListener {
            onItemClicks.onEmployeeClick(position)
        }

    }


    override fun getItemCount(): Int {
        return employeeList.size
    }


    inner class ViewHolder(var rowBinding: AdapterEmployeeBinding) :
        RecyclerView.ViewHolder(rowBinding.root)
}