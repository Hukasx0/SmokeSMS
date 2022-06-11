package com.example.smokesms

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import com.example.smokesms.EncryptionRSA as Encryption

class Chat : AppCompatActivity(), MessagesAdapter.onItemClickListener {
    //
    private var username: String = ""
    private var phoneNumber: String = ""
    private var messageBody = arrayListOf<String>()
    private var messageSender = arrayListOf<String>()
    private var isSent = arrayListOf<String>()
    private var pushToAdapter: List<MessagesData> = emptyList()
    private var adapter = MessagesAdapter(emptyList(),this)
    private val encryptionClass = Encryption()
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val recyclerView = findViewById<RecyclerView>(R.id.Recycler_View)
        val EditText = findViewById<EditText>(R.id.EditText)
        val ImageView = findViewById<ImageView>(R.id.ImageView)
        data()
        readMessages()
        recyclerViewStuff()
        recyclerView.adapter = adapter
        val lManager = LinearLayoutManager(this)
        lManager.setReverseLayout(true)
        recyclerView.layoutManager = lManager
        ImageView.setOnClickListener{
            if(EditText.text.toString() == "/key"){
                EditText.text.clear()
                Toast.makeText(this, getString(R.string.generating_key_please_wait), Toast.LENGTH_LONG).show()
                val pubKey = "<SmokeSMSkey>"+encryptionClass.createRSA(username)
                sendSMS(phoneNumber,pubKey)
                Toast.makeText(this, getString(R.string.key_successfully_generated), Toast.LENGTH_SHORT).show()
            }
            else{
                val sharedPreferences = getSharedPreferences("publicKeys", MODE_PRIVATE)
                val publicKey = sharedPreferences.getString(username,"0")?: "Not Set"
                if(publicKey=="0" || publicKey == "Not Set"){
                    Toast.makeText(this, getString(R.string.public_key_not_set), Toast.LENGTH_SHORT).show()
                }
                else{
                    val msg = encryptionClass.encryptMessage(EditText.text.toString(),publicKey)
                    sendSMS(phoneNumber,msg)
                }
                EditText.text.clear()
            }
        }
        backgroundImage()
    }

    private fun data(){
        username = intent.getStringExtra("username").toString()
        phoneNumber = intent.getStringExtra("phoneNumber").toString()
        this.title = username
        phoneNumber = phoneNumber.replace("\\s".toRegex(),"")
        dataCheck()
    }

    private fun dataCheck(){
        if(phoneNumber == ""){
            Toast.makeText(this,getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
        if(username == ""){
            username = phoneNumber
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //              click settings icon -> Settings.kt
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> startActivity(Intent(this, Settings::class.java))
        }
        return super.onOptionsItemSelected(item)
    }



    private fun readMessages(){
        val uri: Uri = Uri.parse("content://sms")
        val cursor = contentResolver.query(uri,null,null,null,null)
        if (cursor != null) {
            while(cursor.moveToNext()){
                val sender = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString()
                val body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString()
                val type = cursor.getString(cursor.getColumnIndexOrThrow("type")).toString()
                if(sender.contains(phoneNumber)){
                    messageSender.add(sender)
                    messageBody.add(body)
                    isSent.add(type)
                }
            }
            cursor.close()
            if(messageBody.isEmpty()){
                messageBody.add(getString(R.string.tutorial3))
                messageSender.add("SmokeSMS")
                isSent.add("1")
                messageBody.add(getString(R.string.tutorial2))
                messageSender.add("SmokeSMS")
                isSent.add("1")
                messageBody.add(getString(R.string.tutorial1))
                messageSender.add("SmokeSMS")
                isSent.add("1")
            }
        }

    }

    private fun formDataToAdapter(): List<MessagesData>{
        val dataList = ArrayList<MessagesData>()
        for(i in 0 until messageBody.size){
            val listItem = MessagesData(messageBody[i],messageSender[i],isSent[i])
            dataList+=listItem
        }
        return dataList
    }

    private fun recyclerViewStuff(){
        pushToAdapter = formDataToAdapter()
        adapter = MessagesAdapter(pushToAdapter, this)
    }

    override fun onItemClick(position: Int) {
        val clickedItem = pushToAdapter[position]
        val sharedPreferences = getSharedPreferences("publicKeys", MODE_PRIVATE)
        if(clickedItem.msgBody.contains("<SmokeSMSkey>")){
            val end = clickedItem.msgBody.replace("<SmokeSMSkey>","")
            val sharedPrefsEditor = sharedPreferences.edit()
            sharedPrefsEditor.putString(username,end)
            sharedPrefsEditor.apply()
            Toast.makeText(this, getString(R.string.public_key_added), Toast.LENGTH_SHORT).show()
        }
        else{
            clickedItem.msgBody = encryptionClass.decryptMessage(clickedItem.msgBody,username)
            adapter.notifyItemChanged(position)
        }
    }

    private fun sendSMS(number: String, message: String){
        val sms = SmsManager.getDefault()
        val msgParts = sms.divideMessage(message)
        sms.sendMultipartTextMessage(number,null,msgParts,null,null)
    }
    private fun backgroundImage() {
        val imageView = findViewById<ImageView>(R.id.Background)
        val folder = File(getFilesDir(),"SmokeSMS")
        val file = File(folder,"background.jpg")
        if(file.exists()) {
            val bitmap: Bitmap = BitmapFactory.decodeStream(FileInputStream(file))
            imageView.setImageBitmap(bitmap)
        }
    }
}