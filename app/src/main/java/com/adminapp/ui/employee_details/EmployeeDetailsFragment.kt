package com.adminapp.ui.employee_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.adminapp.databinding.EmployeeDetailsFragmentBinding
import com.adminapp.model.Employee
import com.adminapp.utils.Constants
import com.google.android.material.snackbar.Snackbar
import java.util.*

class EmployeeDetailsFragment : Fragment() {


    private lateinit var detailsViewModel: EmployeeDetailsViewModel
    private lateinit var binding: EmployeeDetailsFragmentBinding
    var employeeName: String? = null
    var employeeId: String? = null
    var employeePhone: String? = null

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

        employeeId = arguments?.getString("id")
        employeeName = arguments?.getString("name")
        employeePhone = arguments?.getString("phone")

        if (employeeName != null&&employeePhone!=null) {
            binding.etFullName.setText(employeeName)
            binding.etPhone.setText(employeePhone)
            binding.btnAddEmployee.text = "Update Employee"
        }

        binding.btnAddEmployee.setOnClickListener {
            if (validateEmployee()) {
                if (employeeName != null&&employeePhone!=null)
                    detailsViewModel.updateEmployee(
                        id = employeeId!!,
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

        detailsViewModel.isEmployeeExists.observe({ lifecycle }) {
            if (it) {
//                findNavController().popBackStack()
                requireActivity().onBackPressed()
            } else {
                Snackbar.make(binding.lEmployeeDetails, "Employee already exists", Snackbar.LENGTH_LONG).show()
            }
        }

        detailsViewModel.isLoading.observe({ lifecycle }) {
            if (it) binding.loader.visibility = View.VISIBLE
            else binding.loader.visibility = View.GONE
        }

    }

    private fun validateEmployee(): Boolean {
        if (binding.etFullName.text.toString().isEmpty()) {
            Snackbar.make(binding.lEmployeeDetails, "Enter employee name", Snackbar.LENGTH_LONG).show()
            return false
        }

        else if (binding.etPhone.text.toString().isEmpty()) {
            Snackbar.make(binding.lEmployeeDetails, "Enter phone number", Snackbar.LENGTH_LONG).show()
            return false
        }
        else if (binding.etPhone.text.toString().length < 10) {
            Snackbar.make(binding.lEmployeeDetails, "Enter valid phone number", Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }


}