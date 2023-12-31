package com.tugasakhir.gymtera.admin.features

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.ExerciseData
import com.tugasakhir.gymtera.databinding.ActivityAddExerciseBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.UUID

@Suppress("DEPRECATION")
class AddExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExerciseBinding
    private var selectedImage: String? = null

    private val pickImageLauncher =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImage = uri.toString()
                    binding.choseImg.setImageURI(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val equipmentId = intent.getStringExtra("equipmentId")

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            navigateBack(equipmentId)
        }

        // Binding components
        val items = arrayOf("Beginner", "Intermediate", "Advance")
        (binding.kategori.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)

        binding.cardView6.setOnClickListener {
            openGallery()
        }

        binding.tambah.setOnClickListener {
            val exerciseName = binding.namaLatihan.editText?.text.toString()
            val exerciseDesc = binding.deskripsiLatihan.editText?.text.toString()
            val exerciseCategory = binding.kategori.editText?.text.toString()

            if (exerciseName.isEmpty() || exerciseDesc.isEmpty() || exerciseCategory.isEmpty()) {
                MotionToast.createColorToast(
                    this,
                    "Kesalahan",
                    "Nama latihan, deskripsi dan kategori tidak boleh kosong",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.ft_regular)
                )
            } else {
                // Loading
                val progressBar: ProgressBar = findViewById(R.id.loading)
                val cubeGrid: Sprite = CubeGrid()
                progressBar.indeterminateDrawable = cubeGrid
                progressBar.visibility = View.VISIBLE
                binding.tambah.isEnabled = false

                uploadImageToFirebaseStorage()
            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedImage != null) {
            val storageReference: StorageReference =
                FirebaseStorage.getInstance(getString(R.string.storage_url)).reference.child("Exercise/${UUID.randomUUID()}")

            val fileUri = Uri.parse(selectedImage)

            storageReference.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    saveExerciseDataToDatabase(uri.toString())
                }
            }.addOnFailureListener { e ->
                MotionToast.createColorToast(
                    this,
                    "Kesalahan",
                    "Mengunggah gambar gagal: ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.ft_regular)
                )
            }
        } else {
            MotionToast.createColorToast(
                this,
                "Kesalahan",
                "Silakan pilih gambar",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, R.font.ft_regular)
            )
        }
    }

    private fun saveExerciseDataToDatabase(exerImage: String) {
        // Loading
        val progressBar: ProgressBar = findViewById(R.id.loading)
        val cubeGrid: Sprite = CubeGrid()
        progressBar.indeterminateDrawable = cubeGrid
        progressBar.visibility = View.VISIBLE

        val equipmentId = intent.getStringExtra("equipmentId")
        val reference = equipmentId?.let {
            FirebaseDatabase.getInstance(getString(R.string.ref_url)).reference.child("Equipments")
                .child(it).child("Exercise")
        }

        val newExerciseRef = reference?.push()
        val exerciseId = newExerciseRef?.key

        val exerciseName = binding.namaLatihan.editText?.text.toString()
        val exerciseDesc = binding.deskripsiLatihan.editText?.text.toString()
        val exerciseCategory = binding.kategori.editText?.text.toString()

        val exerciseData = ExerciseData(exerciseName, exerciseDesc, exerciseCategory, exerImage)

        // Push data to the database
        if (equipmentId != null && exerciseId != null) {
            newExerciseRef.setValue(exerciseData).addOnSuccessListener {
                Handler().postDelayed({
                    progressBar.visibility = View.GONE

                    MotionToast.createColorToast(
                        this,
                        "Berhasil",
                        "Data berhasil ditambahkan",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.ft_regular)
                    )

                    val intent = Intent(this, AdminExerciseActivity::class.java)
                    intent.putExtra("equipmentId", equipmentId)
                    startActivity(intent)
                    finish()
                }, 3000)
            }.addOnFailureListener { e ->
                Handler().postDelayed({
                    progressBar.visibility = View.GONE

                    MotionToast.createColorToast(
                        this,
                        "Kesalahan",
                        "Gagal menyimpan data: ${e.message}",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.ft_regular)
                    )
                }, 3000)
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun navigateBack(equipmentId: String?) {
        val intent = Intent(this, AdminExerciseActivity::class.java)
        intent.putExtra("equipmentId", equipmentId)
        startActivity(intent)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateBack(intent.getStringExtra("equipmentId"))
    }
}