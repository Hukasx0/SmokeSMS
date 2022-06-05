package com.example.smokesms

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        this.title = getString(R.string.settings)
        val textView = findViewById<TextView>(R.id.textView)
        val link = findViewById<TextView>(R.id.link)
        textView.setOnClickListener {
            pickImage()
        }
        link.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Hukasx0/SmokeSMS"))
            startActivity(intent)
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val inputStream = data.data?.let { contentResolver.openInputStream(it) }
            val bitmap = BitmapFactory.decodeStream(inputStream)
            saveImageToStorage(bitmap)
        }
    }

    private fun saveImageToStorage(bitmap: Bitmap){
        val path = File(getFilesDir(),"SmokeSMS")
        path.mkdirs()
        val bgFile = File(path, "background.jpg")
        if(bgFile.exists()){
            bgFile.delete()
        }
        val fileOutStr = FileOutputStream(bgFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStr)
        fileOutStr.flush()
        fileOutStr.close()
    }
}