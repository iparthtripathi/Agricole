package com.pratik.agricole

import android.R
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.pratik.agricole.databinding.FragmentProfileBinding


class Profile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    public val binding get() = _binding!!
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?



    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentProfileBinding.inflate(inflater, container, false)
        /* val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
             val galleryUri = it
             try{
                 binding.profile.setImageURI(galleryUri)
             }catch(e:Exception){
                 e.printStackTrace()
             }

         }
         binding.profile.setOnClickListener {
             galleryLauncher.launch("image/*")
         }*/


         */
        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser!!.email.toString()
        val modifiedEmail = email.substring(0, email.length - 10)
        binding.number.text = modifiedEmail

        binding.profile.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }









        return binding.root
    }
    val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.profile.setImageURI(imgUri)
            }
        }


}