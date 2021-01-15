package com.example.worktime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.worktime.databinding.FragmentTaskListBinding

private const val TAG = "TaskListFragment"

class TaskListFragment : Fragment() {
    private var _binding : FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_task_list,container,false)




        return  rootView
    }
}