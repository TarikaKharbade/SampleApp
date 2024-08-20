package com.example.sampleapp.models

import android.content.Context
import com.example.sampleapp.MainActivity
import com.google.gson.Gson


object DataManager {

    var data = emptyArray<Quote>()


    fun loadAssetsFromFile(context:Context){

        val inputStream = context.assets.open("Quote.json")
        val size : Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)
        val gson = Gson()
        data = gson.fromJson(json,Array<Quote>::class.java)

    }
}