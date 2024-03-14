package com.pratik.agricole

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.random.Random

class FarmDetails : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var farmname : TextView
    lateinit var farmsize : TextView
    lateinit var plant_date : TextView
    lateinit var gdd : TextView
    lateinit var farmimage : ImageView
    lateinit var speak : ImageView
    lateinit var stop : ImageView

    lateinit var erzz : TextView
    lateinit var setup : TextView
    lateinit var harvest_date : TextView
    lateinit var crop_yeild : TextView
    lateinit var stocked_crop : TextView
    lateinit var fileUri : Uri
    var tts : TextToSpeech? = null

    var farmnumber : String? = null
    lateinit var ref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_details)
        tts = TextToSpeech(this,this)
        hook()
        val harvest_date_int = Random.nextInt(1, 30)

        harvest_date.text = "May ${harvest_date_int}"

        farmnumber = intent.getStringExtra("farmnumber").toString()

        ref = database.reference.child("users").child(auth.uid.toString()).child("farms").child(
            farmnumber!!
        )
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(it: DataSnapshot) {
                farmname.text = it.child("farmname").value.toString()
                farmsize.text = it.child("farmsize").value.toString() + " ha"

                val y = 832 * it.child("farmsize").value.toString().toFloat()
                crop_yeild.text = "${y} Kg"
                Picasso.get().load(it.child("farmimage").value.toString()).into(farmimage)
                if (it.child("plantdate").exists()){
                    plant_date.text = it.child("plantdate").value.toString()
                }
                if (it.child("gdd").exists()){
                    gdd.text = it.child("gdd").value.toString()
                }
                if (it.child("erz").exists()){
                    erzz.text = it.child("erz").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

//        speak.setOnClickListener {
//            generateSummary(plant_date.text.toString(),harvest_date.text.toString(),gdd.text.toString(),erzz.text.toString(),crop_yeild.text.toString(),stocked_crop.text.toString(),"telugu")
//        }
        speak.setOnClickListener {
            // Build an alert dialog with language options
            val languages = arrayOf("English", "Hindi", "Telugu")
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Select Language")
            alertDialogBuilder.setItems(languages) { dialogInterface: DialogInterface, i: Int ->
                val selectedLanguage = languages[i].toLowerCase()
                generateSummary(
                    plant_date.text.toString(),
                    harvest_date.text.toString(),
                    gdd.text.toString(),
                    erzz.text.toString(),
                    crop_yeild.text.toString(),
                    stocked_crop.text.toString(),
                    selectedLanguage.toLowerCase()
                )
                dialogInterface.dismiss()
            }
            alertDialogBuilder.create().show()
        }

        stop.setOnClickListener {
            tts!!.stop()

        }
        farmimage.setOnClickListener {
            setfarmimage()
        }
        setup.setOnClickListener {
            showCustomAlertDialog()

        }

    }
    fun generateSummary(
        cropPlantDate: String,
        harvestDate: String,
        accumulatedGDD: String,
        effectiveRootZone: String,
        cropYieldPrediction: String,
        stockedCrop: String,
        language: String
    ) {
        // Construct the summary message
        val summaryEnglish = "Crop planted on $cropPlantDate. Expected harvest date is $harvestDate. " +
                "Accumulated Growing Degree Days: $accumulatedGDD. Effective Root Zone: $effectiveRootZone. " +
                "Crop Yield Prediction: $cropYieldPrediction kg. Already stocked crop: $stockedCrop kg."

        val summaryHindi = "फसल $cropPlantDate को लगाई गई। अपेक्षित कटाई की तारीख $harvestDate है। " +
                "एकत्रित वृद्धि डिग्री दिन: $accumulatedGDD। प्रभावी जड़ीद जोन: $effectiveRootZone। " +
                "फसल उत्पादन का अनुमान: $cropYieldPrediction किलोग्राम। पहले ही भंडारित फसल: $stockedCrop किलोग्राम।"

        val summaryTelugu = "మొక్క నుండి $cropPlantDate న నాటినది. కాలువలోకి కొలవడం అప్పుడు $harvestDate. " +
                "అతిగణన పెరుగుదల డిగ్రీ రోజులు: $accumulatedGDD. ప్రభావీ రూట్ జోన్: $effectiveRootZone. " +
                "మొక్క యీల్డ్ అంతాన్ని అంచనా చేయడానికి: $cropYieldPrediction కిలోగ్రాములు. ఇప్పుడు సంగ్రహించబడిన మొక్క: $stockedCrop కిలోగ్రాములు।"


        // Show summary in a dialog box
//        Toast.makeText(this@FarmDetails, summary, Toast.LENGTH_LONG).show()

        val locale = when (language.toLowerCase(Locale.getDefault())) {
            "english" -> Locale.ENGLISH
            "hindi" -> Locale("hi", "IN")
            "telugu" -> Locale("te", "IN")
            else -> Locale.ENGLISH
        }
        tts!!.language = locale
        if(language == "english")tts!!.speak(summaryEnglish, TextToSpeech.QUEUE_FLUSH, null, "")
        if(language == "hindi")tts!!.speak(summaryHindi, TextToSpeech.QUEUE_FLUSH, null, "")
        if(language == "telugu")tts!!.speak(summaryTelugu, TextToSpeech.QUEUE_FLUSH, null, "")



    }



    private fun setfarmimage() {
        Dexter.withContext(applicationContext)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(
                        Intent.createChooser(intent, "Select jpg Files"),
                        101
                    )
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    Toast.makeText(this@FarmDetails,"Permission denied", Toast.LENGTH_SHORT).show()}
                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                    Toast.makeText(this@FarmDetails,"Permission taking", Toast.LENGTH_SHORT).show()
                }
            }).check()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            assert(data != null)
            fileUri = data?.data!!
            farmimage.setImageURI(fileUri)
            uploadimage(fileUri)
        }
    }
    private fun uploadimage(fileUri: Uri) {
        val pd = ProgressDialog(this)
        pd.setTitle("Uploading Image")
        pd.show()

        val reference: StorageReference = FirebaseStorage.getInstance().reference.child("${auth.currentUser!!.uid}/${farmnumber}.jpg")

        reference.putFile(fileUri).addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? -> reference.downloadUrl.addOnSuccessListener { uri: Uri ->

            ref.child("farmimage").setValue(uri.toString())
            pd.dismiss()
            Toast.makeText(baseContext, "File Uploaded", Toast.LENGTH_SHORT).show()

        }
        }
            .addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val percent =
                    (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                pd.setMessage("Uploaded :" + percent.toInt() + "%")
            }
    }

    private fun showCustomAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Details")

        // Set up the layout for the dialog
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // Add EditText fields to the layout
        val editText1 = EditText(this)
        editText1.hint = "Planting Date"
        layout.addView(editText1)

        val editText2 = EditText(this)
        editText2.hint = "Accumlated GDD"
        layout.addView(editText2)

        val editText3 = EditText(this)
        editText3.hint = "Effective root zone"
        layout.addView(editText3)

        builder.setView(layout)

        // Set up the submit button
        builder.setPositiveButton("Submit") { dialog, which ->
            // Retrieve text from EditText fields
            val pd = editText1.text.toString()
            val gdd = editText2.text.toString()
            val erz = editText3.text.toString()
            if (pd.isNotEmpty())ref.child("plantdate").setValue(pd)
            if (gdd.isNotEmpty())ref.child("gdd").setValue(gdd)
            if (erz.isNotEmpty())ref.child("erz").setValue(erz)

        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun hook() {
        auth = FirebaseAuth.getInstance()
        database = Firebase.database
        farmname = findViewById(R.id.farmname)
        farmsize = findViewById(R.id.farmsize)
        farmimage = findViewById(R.id.farmimage)
        plant_date = findViewById(R.id.plant_date)
        gdd = findViewById(R.id.gdd)
        erzz = findViewById(R.id.erz)
        setup = findViewById(R.id.setupdata)
        harvest_date = findViewById(R.id.harvestdate)
        crop_yeild = findViewById(R.id.cropyeild)
        stocked_crop = findViewById(R.id.stocked_crop)
        speak = findViewById(R.id.speaktxt)
        stop = findViewById(R.id.stop)

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed.", Toast.LENGTH_SHORT).show()
        }
    }
}