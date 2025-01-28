package com.example.retrofitapp.presentation.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.retrofitapp.R
import com.example.retrofitapp.databinding.FragmentPracticeBinding
import com.taskeasy.design.calendar.CalendarLayout
import com.taskeasy.design.modal.Task
import com.taskeasy.design.util.getColorInt
import java.time.LocalDate


class PracticeFragment :Fragment() {
    private lateinit var  binding: FragmentPracticeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPracticeBinding.inflate(layoutInflater)
        val view =  binding.root

        val list: ArrayList<Task> = arrayListOf()
        val yearList: MutableList<Int> = mutableListOf()
        yearList.addAll(listOf( 2017, 2018, 2019,
            2020, 2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029,
            2030, 2031, 2032, 2033))


        binding.calendarView.setYearDropdownVisible(true, yearList)
        list.add(
            Task(
                startDate = LocalDate.of(2024, 5, 18),
                endDate = LocalDate.of(2024, 5, 18),
                taskName = "Exam: Information Security",
                taskColor = "#4E8420",
                taskImage = com.taskeasy.design.R.drawable.ic_clock,
                iconColor = getColorInt(com.taskeasy.design.R.color.color_success),
            )
        )
        list.add(
            Task(
                startDate = LocalDate.of(2024, 5, 27),
                endDate = LocalDate.of(2024, 5, 27),
                taskName = "Exam: Machine Learning",
                taskColor = "#4E8420",
                taskImage = com.taskeasy.design.R.drawable.ic_clock,
                iconColor = getColorInt(com.taskeasy.design.R.color.color_success)
            )
        )
        list.add(
            Task(
                startDate = LocalDate.of(2024, 6, 3),
                endDate = LocalDate.of(2024, 6, 3),
                taskName = "Exam: Blockchain",
                taskColor = "#4E8420",
                taskImage = com.taskeasy.design.R.drawable.ic_clock,
                iconColor = getColorInt(com.taskeasy.design.R.color.color_success)
            )
        )

        val layout  : CalendarLayout ?= null
        layout?.setYearDropdownVisible(true,yearList )
        binding.calendarView.setTaskList(list)


        val options = mutableListOf("Option 1", "Option 2", "Option 3")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.layout_dropdown_item, R.id.tvText, options)
        binding.silSelect.etInput.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                Log.i(javaClass.name, "***** ${options[position]}")
            }
        }
        binding.sifllSelect.etInput.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                Log.i(javaClass.name, "***** ${options[position]}")
            }
        }

        binding.calendarView.onMonthScroll = {
            Log.d(javaClass.name, "________________________${it.year} >>> ${it.month}")
        }
        binding.calendarView.onDateSelected = {
            Log.d(javaClass.name, "____________________>>$it")
        }
        binding.calendarView.onHeightChangeCallBack = { cellHeight, _ ->
            Log.d(javaClass.name, "______________________$cellHeight")
        }
        binding.calendarView.onHeightExpanded = { cellHeight, _ ->
            Log.d(javaClass.name, "______________________$cellHeight")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

//         Set up navigation back button
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}