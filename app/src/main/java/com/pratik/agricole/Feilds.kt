package com.pratik.agricole

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pratik.agricole.databinding.FragmentFeildsBinding
import com.pratik.agricole.databinding.FragmentProfileBinding
import com.pratik.agricole.models.FarmAdapter
import com.pratik.agricole.models.FarmModel
import java.util.ArrayList

class Feilds : Fragment() {


    private var _binding: FragmentFeildsBinding? = null
    private val binding get() = _binding!!
    var farmlist: ArrayList<FarmModel> = ArrayList()
    lateinit var adapter: FarmAdapter
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var ref : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentFeildsBinding.inflate(inflater, container, false)

        binding.vediorv.layoutManager = LinearLayoutManager(requireActivity())
        adapter = FarmAdapter(requireActivity(),farmlist)
        binding.vediorv.adapter = adapter
        database =  FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        ref = database.reference.child("users").child(auth.uid.toString()).child("farms")
       loadfarm()
        binding.addfarm.setOnClickListener{
            showCustomAlertDialog()
        }


        return binding.root
    }

    private fun loadfarm() {
        ref.get().addOnSuccessListener {
            farmlist.clear()
            for (item in it.children){
                val farmModel = FarmModel(
                    item.child("farmname").value.toString(),
                    item.child("farmsize").value.toString(),
                    item.key ,
                    item.child("farmimage").value.toString()
                )
                farmlist.add(farmModel)
            }

            adapter.notifyDataSetChanged()
        }

    }

    private fun showCustomAlertDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Enter Details")

        // Set up the layout for the dialog
        val layout = LinearLayout(requireActivity())
        layout.orientation = LinearLayout.VERTICAL

        // Add EditText fields to the layout
        val editText1 = EditText(requireActivity())
        editText1.hint = "Farm Name"
        layout.addView(editText1)

        val editText2 = EditText(requireActivity())
        editText2.hint = "Farm Size"
        layout.addView(editText2)

        val editText3 = EditText(requireActivity())
        editText3.hint = "Farm Number"
        layout.addView(editText3)

        builder.setView(layout)

        // Set up the submit button
        builder.setPositiveButton("Submit") { dialog, which ->
            // Retrieve text from EditText fields
            val fn = editText1.text.toString()
            val fs = editText2.text.toString()
            val fnumber = editText3.text.toString()

            val farmModel = FarmModel(fn, fs, fnumber , "https://bit.ly/3wJmFtF")

            ref.child(fnumber).setValue(farmModel)
            loadfarm()
//            requireActivity().recreate()

        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

}