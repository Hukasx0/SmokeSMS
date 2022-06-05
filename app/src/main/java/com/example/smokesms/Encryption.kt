package com.example.smokesms

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

// AndroidKeyStore for private keys
        // SharedPreferences for public keys

class Encryption {

    private val AndroidKeyStore: String = "AndroidKeyStore"
    fun createRSA(contactNameAlias: String): String {
        val keyPairG =
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore)
        val keyParameters = KeyGenParameterSpec.Builder(
            contactNameAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1).setKeySize(2048).build()
        keyPairG.initialize(keyParameters)
        val keypair = keyPairG.genKeyPair()
        return Base64.encodeToString(keypair.public.encoded, Base64.DEFAULT)
    }
    fun encryptMessage(plainText: String, publicKey: String): String{
        val data: ByteArray = Base64.decode(publicKey,Base64.DEFAULT)
        val spec = X509EncodedKeySpec(data)
        val fact = KeyFactory.getInstance("RSA")
        val PublicKey = fact.generatePublic(spec)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, PublicKey)
        return Base64.encodeToString(cipher.doFinal(plainText.toByteArray()),0).toString()
    }
    fun decryptMessage(encryptedText: String, alias: String): String{
        val androidKeyStore = KeyStore.getInstance(AndroidKeyStore)
        androidKeyStore.load(null)
        val privateKey  = androidKeyStore.getKey(alias,null)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(Base64.decode(encryptedText.toByteArray(),0),))
    }
}