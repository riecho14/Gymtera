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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.EquipmentData
import com.tugasakhir.gymtera.databinding.ActivityAddEquipmentBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.UUID

@Suppress("DEPRECATION")
class AddEquipmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEquipmentBinding
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
        binding = ActivityAddEquipmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminEquipmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.cardView6.setOnClickListener {
            openGallery()
        }

        binding.tambah.setOnClickListener {
            val toolName = binding.namaAlat.editText?.text.toString()
            val toolDesc = binding.deskripsiAlat.editText?.text.toString()

            if (toolName.isEmpty() || toolDesc.isEmpty()) {
                MotionToast.createColorToast(
                    this,
                    "Kesalahan",
                    "Nama alat dan deskripsi tidak boleh kosong",
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
                FirebaseStorage.getInstance(getString(R.string.storage_url)).reference.child("Equipments/${UUID.randomUUID()}")

            val fileUri = Uri.parse(selectedImage)

            storageReference.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    saveEquipmentDataToDatabase(uri.toString())
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

    private fun saveEquipmentDataToDatabase(imageUrl: String) {
        // Loading
        val progressBar: ProgressBar = findViewById(R.id.loading)
        val cubeGrid: Sprite = CubeGrid()
        progressBar.indeterminateDrawable = cubeGrid
        progressBar.visibility = View.VISIBLE

        val database: FirebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.ref_url))
        val reference: DatabaseReference = database.getReference("Equipments")

        val toolName = binding.namaAlat.editText?.text.toString()
        val toolDesc = binding.deskripsiAlat.editText?.text.toString()

        val equipmentData = EquipmentData(toolName, toolDesc, imageUrl)

        // Push data to the database
        val equipmentId = reference.push().key
        if (equipmentId != null) {
            reference.child(equipmentId).setValue(equipmentData).addOnSuccessListener {
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

                    val intent = Intent(this, AdminEquipmentActivity::class.java)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminEquipmentActivity::class.java)
        startActivity(intent)
        finish()
    }
}