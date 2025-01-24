package com.example.ecommerceapp.Helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.ecommerceapp.Model.ItemsModel
import com.google.gson.Gson

class SharedPrefs(context: Context) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()

    private fun getListString(key: String): ArrayList<String> {
        val jsonString = preferences.getString(key, "") ?: ""
        return ArrayList(jsonString.split("‚‗‚").toList())
    }

    fun getListObject(key: String): ArrayList<ItemsModel> {
        val objStrings = getListString(key)
        val playerList = ArrayList<ItemsModel>()

        if (objStrings.isEmpty() || objStrings.all { it.isEmpty() }) {
            return playerList // Return empty list if no valid JSON strings
        }

        for (jObjString in objStrings) {
            // Skip empty strings
            if (jObjString.isNotEmpty()) {
                val player: ItemsModel = gson.fromJson(jObjString, ItemsModel::class.java)
                playerList.add(player)
            }
        }

        return playerList
    }

    private fun putListString(key: String, stringList: ArrayList<String>) {
        checkForNullKey(key)
        val myStringList = stringList.toTypedArray()
        preferences.edit().putString(key, myStringList.joinToString("‚‗‚")).apply()
    }

    fun putListObject(key: String, playerList: ArrayList<ItemsModel>) {
        checkForNullKey(key)
        val objStrings = ArrayList<String>()
        for (player in playerList) {
            objStrings.add(gson.toJson(player))
        }
        putListString(key, objStrings)
    }

    private fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }
}