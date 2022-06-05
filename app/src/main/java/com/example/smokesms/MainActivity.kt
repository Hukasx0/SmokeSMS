package com.example.smokesms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var permissionCode: Int = 0

    //               Contacts information
    private var contactsId = arrayListOf<String>()
    private var contactsName = arrayListOf<String>()
    private var contactsNums = arrayListOf<String>()
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions()
    }

    private fun doStuff() {
        getContactsNames()
        listContacts()
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

    private fun permissions() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,Manifest.permission.READ_EXTERNAL_STORAGE),
                permissionCode
            )
        }
        doStuff()
    }

    private fun getContactsNames() {
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id =  cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID))
                contactsId.add(id)
                val name =  cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                contactsName.add(name)
                val number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsNums.add(number)
            }
            cursor.close()
        }
    }
    private fun listContacts(){
        val listView = findViewById<ListView>(R.id.main_ListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsName)
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this@MainActivity,Chat::class.java)
            intent.putExtra("username",contactsName[i])
            intent.putExtra("phoneNumber",contactsNums[i])
            startActivity(intent)
        }
    }
}