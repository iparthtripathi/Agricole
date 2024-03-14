package com.pratik.agricole

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pratik.agricole.databinding.FragmentHomeBinding
import com.pratik.agricole.models.FarmAdapter
import com.pratik.agricole.models.FarmAdapterHome
import com.pratik.agricole.models.FarmModel
import org.json.JSONObject
import java.net.URL
import java.util.*


class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var farmlist: ArrayList<FarmModel> = ArrayList()
    lateinit var adapter: FarmAdapterHome
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var ref : DatabaseReference


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val CITY: String = "Tada,IN"
    val API: String = "c6908f27f7d2259a311d61030d2aadae"
    var lat: String? = null
    var lon: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.farmFields.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        adapter = FarmAdapterHome(requireActivity(),farmlist)
        binding.farmFields.adapter = adapter
        database =  FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        ref = database.reference.child("users").child(auth.uid.toString()).child("farms")
        loadfarm()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchLocation()

        binding.seeallfields.setOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.replace(R.id.frame_layout, Feilds())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }

        }

        binding.chatgptcard.setOnClickListener {
            openchatgpt(it)
        }
        loadLocate()
        binding.changelang.setOnClickListener {

            showChangeLanguageDialog(requireActivity());
        }
        return binding.root
    }

    private fun loadfarm() {
        ref.get().addOnSuccessListener {
            farmlist.clear()
            for (item in it.children){
                val farmModel = FarmModel(
                    item.child("farmname").value.toString(),
                    item.child("farmsize").value.toString()+" ha",
                    item.key ,
                    item.child("farmimage").value.toString()
                )
                farmlist.add(farmModel)
            }

            adapter.notifyDataSetChanged()
        }

    }
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            var response: String?

            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.i("lat", lat.toString())
            Log.i("lon", lon.toString())
            Log.i("result", result.toString())
            if (result.isNullOrBlank()) {
                // Handle case where response is empty or null
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("Failed to retrieve weather data. Please check your internet connection.1")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                return
            }
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val temp = main.getDouble("temp").toInt() - 273
                val wind = jsonObj.getJSONObject("wind").getString("speed")
                val humidity = main.getString("humidity")
                val rain = String.format("%.2f", humidity.toInt() * 0.0075)
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val address = jsonObj.getString("name").toString()
                val id = weather.getInt("id")
                val message = weather.getString("description")
                val city = jsonObj.getString("name") + "," + sys.getString("country")
                binding.temperature.text = temp.toString() + " °C"
                binding.wind.text = wind.toString() + " m/s"
                binding.prec.text = rain.toString() + " mm"
                binding.humidity.text = humidity.toString() + " %"
                binding.message.text = message.toString().capitalize()
                binding.location.text = address.toString().capitalize()
                updateWeatherIcon(id)
            } catch (e: Exception) {
                e.printStackTrace()
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("Failed to retrieve weather data. Please check your internet connection.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

    }


    fun openchatgpt(view: View) {
        startActivity(Intent(requireActivity(), ChatGPT::class.java))

    }

    private fun showChangeLanguageDialog(requireActivity: FragmentActivity) {
        val listitems = arrayOf("हिंदी", "తెలుగు", "English")

        val mBuilder = AlertDialog.Builder(requireActivity)
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listitems, -1) { dialog, which ->
            if (which == 0) {
                setLocate("hi")
                requireActivity.recreate()
            } else if (which == 1) {
                setLocate("te")
                requireActivity.recreate()
            } else if (which == 2) {
                setLocate("en")
                requireActivity.recreate()
            }

            dialog.dismiss()
        }
        val mDialog = mBuilder.create()

        mDialog.show()


    }


    private fun updateWeatherIcon(condition: Int) {
        if (condition >= 0 && condition <= 300) {
            binding.weatherIcon.setAnimationFromUrl("https://assets3.lottiefiles.com/packages/lf20_6rza2vis.json")
        } else if (condition >= 300 && condition <= 500) {
            binding.weatherIcon.setAnimationFromUrl("https://assets1.lottiefiles.com/packages/lf20_mhlhglws.json")
        } else if (condition >= 500 && condition <= 600) {
            binding.weatherIcon.setAnimationFromUrl("https://assets3.lottiefiles.com/private_files/lf30_LPtaP2.json")
        } else if (condition >= 600 && condition <= 700) {
            binding.weatherIcon.setAnimationFromUrl("https://assets5.lottiefiles.com/temp/lf20_RHbbn6.json")
        } else if (condition >= 701 && condition <= 771) {
            binding.weatherIcon.setAnimationFromUrl("https://assets5.lottiefiles.com/temp/lf20_HflU56.json")
        } else if (condition >= 772 && condition < 800) {
            binding.weatherIcon.setAnimationFromUrl("https://assets6.lottiefiles.com/packages/lf20_1eaisi3u.json")
        } else if (condition == 800) {
            binding.weatherIcon.setAnimationFromUrl("https://assets5.lottiefiles.com/packages/lf20_jqfghjiz.json")
        } else if (condition >= 801 && condition <= 804) {
            binding.weatherIcon.setAnimationFromUrl("https://assets7.lottiefiles.com/packages/lf20_trr3kzyu.json")
        } else if (condition >= 900 && condition <= 902) {
            binding.weatherIcon.setAnimationFromUrl("https://assets3.lottiefiles.com/packages/lf20_6rza2vis.json")
        }
        if (condition == 903) {
            binding.weatherIcon.setAnimationFromUrl("https://assets1.lottiefiles.com/temp/lf20_WtPCZs.json")
        }
        if (condition == 904) {
            binding.weatherIcon.setAnimationFromUrl("https://assets5.lottiefiles.com/packages/lf20_jqfghjiz.json")
        }
        if (condition >= 905 && condition <= 1000) {
            binding.weatherIcon.setAnimationFromUrl("https://assets10.lottiefiles.com/private_files/lf30_LPtaP2.json")
        }
    }


    private fun setLocate(Lang: String) {

        val locale = Locale(Lang)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale
        requireActivity().baseContext.resources.updateConfiguration(
            config,
            requireActivity().baseContext.resources.displayMetrics
        )

        val editor = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun loadLocate() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != null) {
            setLocate(language)
        }
    }

    private fun fetchLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                lat = it.latitude.toString()
                lon = it.longitude.toString()
                weatherTask().execute()
            }
        }
    }
}