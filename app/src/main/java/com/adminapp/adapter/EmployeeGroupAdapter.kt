package com.adminapp.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adminapp.R
import com.adminapp.databinding.AdapterEmployeeGroupBinding
import com.adminapp.interfaces.OnItemClicks
import com.adminapp.model.Employee
import com.adminapp.utils.Constants
import com.bumptech.glide.Glide

class EmployeeGroupAdapter(
    private val employeeList: ArrayList<Employee>,
    private val activity: Activity,
    private val onItemClicks: OnItemClicks,
) : RecyclerView.Adapter<EmployeeGroupAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_employee_group,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.rowBinding.tvEmployeeName.text = employee.name
        holder.rowBinding.tvPhone.text = employee.phone
        Glide.with(activity).load(employee.profileImageUrl).thumbnail(0.1f)
            .error(R.drawable.ic_user)
            .into(holder.rowBinding.profileImage)
        holder.rowBinding.tvTime.text = Constants.getDateTime(employee.updatedAt, "dd/MM/yyyy")
        holder.rowBinding.lEmployee.setOnClickListener {
            employee.isSelected = !employee.isSelected
            holder.rowBinding.lEmployee.setBackgroundColor(if (employee.isSelected) Color.CYAN else Color.WHITE)
            onItemClicks.onEmployeeClick(position)

        }

    }


    override fun getItemCount(): Int {
        return employeeList.size
    }


    inner class ViewHolder(var rowBinding: AdapterEmployeeGroupBinding) :
        RecyclerView.ViewHolder(rowBinding.root)
}