package com.example.smokesms

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Chat : AppCompatActivity(), MessagesAdapter.onItemClickListener {
    //
    private var username: String = ""
    private var phoneNumber: String = ""
    private var messageBody = arrayListOf<String>()
    private var messageSender = arrayListOf<String>()
    private var isSent = arrayListOf<String>()
    private var pushToAdapter: List<MessagesData> = emptyList()
    private var adapter = MessagesAdapter(emptyList(),this)
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
            sendSMS(phoneNumber,EditText.text.toString())
            EditText.text.clear()
        }
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
            Toast.makeText(this,"Error occurred", Toast.LENGTH_SHORT).show()
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
        //clickedItem.msgBody = Base64.encodeToString(clickedItem.msgBody.toByteArray(),0)
        val Base = Base64.decode(clickedItem.msgBody,0)
        clickedItem.msgBody = String(Base,Charsets.UTF_8)
        adapter.notifyItemChanged(position)
    }

    private fun sendSMS(number: String, message: String){
        val msgBase: String = Base64.encodeToString(message.toByteArray(),0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(number,null,msgBase,null,null)
    }
}