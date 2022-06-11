package com.example.smokesms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.math.BigInteger
import java.security.MessageDigest
import com.example.smokesms.EncryptionAES as encryptionAES

class PinLogin : AppCompatActivity() {
    val encryptionAES = encryptionAES()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_login)
        val password = findViewById<EditText>(R.id.editTextTextPassword)
        val textView = findViewById<TextView>(R.id.textView3)
        val sharedPreferences = getSharedPreferences("SmokeSmsPreferences", MODE_PRIVATE)
        var option = 0
        val intentData = intent.getStringExtra("changingPassword")
        if(encryptionAES.checkKeyExist("MasterKey")){
            if (intentData == "changePassword"){
                textView.text = getString(R.string.change_password)
            }
            else{
                option = 1
            }
        }
        else{
            textView.text = getString(R.string.create_password)

        }
        password.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if(password.length() >= 8){
                    val typedPass = password.text.toString()
                    if(option == 0){
                        if(intentData == "changePassword"){
                            val sharedPrefsEditor = sharedPreferences.edit()
                            val aesEncryption = encryptionAES.encrypt(sha512encrypt(typedPass), "MasterKey")
                            sharedPrefsEditor.putString("appPassword",aesEncryption.first)
                            sharedPrefsEditor.putString("appPasswordIV",aesEncryption.second)
                            sharedPrefsEditor.apply()
                            val intent = Intent(this@PinLogin,Settings::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            encryptionAES.createAES("MasterKey")
                            val sharedPrefsEditor = sharedPreferences.edit()
                            val aesEncryption = encryptionAES.encrypt(sha512encrypt(typedPass), "MasterKey")
                            sharedPrefsEditor.putString("appPassword",aesEncryption.first)
                            sharedPrefsEditor.putString("appPasswordIV",aesEncryption.second)
                            sharedPrefsEditor.apply()
                            val intent = Intent(this@PinLogin,PinLogin::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    else if(option == 1){
                        if(sha512encrypt(typedPass) == encryptionAES.decrypt(sharedPreferences.getString("appPassword","").toString(),
                                                                             sharedPreferences.getString("appPasswordIV","").toString(),
                                                                        "MasterKey")){
                            if(intentData == "changePasswordVerify"){
                                val intent = Intent(this@PinLogin, PinLogin::class.java)
                                intent.putExtra("changingPassword","changePassword")
                                startActivity(intent)
                                finish()
                            }
                            else{
                                val intent = Intent(this@PinLogin, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        else{
                            password.text.clear()
                            toast(getString(R.string.password_is_incorrect))
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
    })
}
    private fun toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
    private fun sha512encrypt(plainText: String): String{
        val md = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(plainText.toByteArray())
        val bi = BigInteger(1,messageDigest)
        var hash = bi.toString(16)
        while(hash.length < 32){
            hash = "0$hash"
        }
        return hash
    }
}