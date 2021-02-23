package com.adminapp.ui.employee_list_activity


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.adminapp.prefrences.Preference
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeListActivityViewModel
@Inject constructor(
    private val preference: Preference,
): ViewModel() {



}