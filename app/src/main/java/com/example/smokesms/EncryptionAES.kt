package com.example.smokesms

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

class EncryptionAES {
    private val AndroidKeyStore: String = "AndroidKeyStore"
    fun createAES(alias: String){
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,AndroidKeyStore)
        val keyParameters = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).setKeySize(256).build()
        keyGen.init(keyParameters)
        keyGen.generateKey()
    }
    fun checkKeyExist(alias: String): Boolean{
        val androidKeyStore = KeyStore.getInstance(AndroidKeyStore)
        androidKeyStore.load(null)
        return androidKeyStore.containsAlias(alias)
    }
    fun encrypt(plainText: String, alias: String): Pair<String, String>{
        val androidKeyStore = KeyStore.getInstance(AndroidKeyStore)
        androidKeyStore.load(null)
        val secretKey  = androidKeyStore.getKey(alias,null)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val ciphertext: ByteArray = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        val masterKeyIv = cipher.iv
        return Pair(Base64.encodeToString(ciphertext,0),Base64.encodeToString(masterKeyIv,0))
    }
    fun decrypt(encryptedText: String,ivBytesBase64: String, alias: String): String{
        val androidKeyStore = KeyStore.getInstance(AndroidKeyStore)
        androidKeyStore.load(null)
        val secretKey  = androidKeyStore.getKey(alias,null)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
        val ivBytes = IvParameterSpec(Base64.decode(ivBytesBase64,0))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivBytes)
        return String(cipher.doFinal(Base64.decode(encryptedText.toByteArray(StandardCharsets.UTF_8),0),))
    }
}