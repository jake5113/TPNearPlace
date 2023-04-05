package com.jake5113.tpkaosearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jake5113.tpkaosearchapp.activities.MainActivity
import com.jake5113.tpkaosearchapp.adapters.PlaceListRecyclerAdapter
import com.jake5113.tpkaosearchapp.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {
    lateinit var binding: FragmentPlaceListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity에 있는 대량의 데이터를 소환
        val ma: MainActivity = requireActivity() as MainActivity
        //if(ma.searchPlaceResponse == null) return
        //ma.searchPlaceResponse ?: return
        //binding.recycler.adapter = PlaceListRecyclerAdapter(requireActivity(), ma.searchPlaceResponse!!.documents)
        ma.searchPlaceResponse?.apply {
            binding.recycler.adapter = PlaceListRecyclerAdapter(requireActivity(), documents)
        }

    }
}