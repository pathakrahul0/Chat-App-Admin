package com.adminapp.ui.employee_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.adminapp.databinding.EmployeeDetailsFragmentBinding
import com.adminapp.model.Employee
import java.util.*

class EmployeeDetailsFragment : Fragment() {


    private lateinit var detailsViewModel: EmployeeDetailsViewModel
    private lateinit var binding: EmployeeDetailsFragmentBinding
    var employeeData: Employee? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EmployeeDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detailsViewModel = ViewModelProvider(this).get(EmployeeDetailsViewModel::class.java)

        employeeData = arguments?.getParcelable("employeeData")
        if (employeeData != null) {
            binding.etFullName.setText(employeeData?.name)
            binding.etPhone.setText(employeeData?.phone)
        }

        binding.btnAddEmployee.setOnClickListener {
            if (employeeData != null)
                detailsViewModel.updateEmployee(
                    id = employeeData?.id!!,
                    name = "${binding.etFullName.text}",
                    phone = "${binding.etPhone.text}",
                    updatedAt = Date().time
                )
            else
                detailsViewModel.addEmployee(
                    name = "${binding.etFullName.text}",
                    phone = "${binding.etPhone.text}",
                    createdAt = Date().time,
                    updatedAt = Date().time
                )
        }
    }

}