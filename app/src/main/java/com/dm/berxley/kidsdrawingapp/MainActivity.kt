package com.dm.berxley.kidsdrawingapp

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawing_view:DrawingView? = null
    private var mImageButtonCurrentpaint: ImageButton? = null
    private var ibGallery: ImageButton? = null

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == RESULT_OK && result.data != null){
                 val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageURI(result.data?.data)
            }

        }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        permissions -> permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value

            if (isGranted){
                if (permissionName == Manifest.permission.READ_MEDIA_IMAGES
                    || permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){

                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(intent)

                }
            }else{
                if (permissionName == Manifest.permission.READ_MEDIA_IMAGES){
                    Toast.makeText(this, "$permissionName Denied", Toast.LENGTH_SHORT).show()
                }

                if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this, "$permissionName Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawing_view = findViewById(R.id.drawing_view)
        drawing_view?.setSizeForBrush(20.toFloat())

        val linearLayout: LinearLayout = findViewById(R.id.ll_paint_colors)

        mImageButtonCurrentpaint = linearLayout[1] as ImageButton
        mImageButtonCurrentpaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        val ib_brush: ImageButton = findViewById(R.id.ib_brush)

        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        ibGallery = findViewById(R.id.ib_gallery)
        ibGallery?.setOnClickListener {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
//                //showRationaleDialog("Kids Drawing App", "Kids drawing app needs to access your external storage")
//            }else{
//
//            }
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            ))
        }else{
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
//                //showRationaleDialog("Kids Drawing App", "Kids drawing app needs to access your external storage")
//
//            }else{
//
//            }

            requestPermission.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ))
        }
    }

    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drawing_view?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }


        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drawing_view?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drawing_view?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view: View){

        if (view !== mImageButtonCurrentpaint){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawing_view?.setColor(colorTag)

            imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))
            mImageButtonCurrentpaint?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_normal))
            mImageButtonCurrentpaint = view
        }

    }

    private fun showRationaleDialog(title: String, message:String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cancel", DialogInterface.OnClickListener { dialogInterface, _ -> {
            dialogInterface.dismiss()
        } })
        builder.create().show()
    }
}