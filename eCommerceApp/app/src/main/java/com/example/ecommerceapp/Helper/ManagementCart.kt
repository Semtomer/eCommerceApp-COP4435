package com.example.ecommerceapp.Helper

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.ecommerceapp.Model.ItemsModel

class ManagementCart(private val context: Context) {

    private val sharedPrefs = SharedPrefs(context)

    fun insertItem(item: ItemsModel) {
        val listItem = getListCart()
        val existAlready = listItem.any { it.title == item.title }
        val index = listItem.indexOfFirst { it.title == item.title }

        if (existAlready)
            listItem[index].numberInCart = item.numberInCart
        else
            listItem.add(item)

        sharedPrefs.putListObject("CartList", listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return sharedPrefs.getListObject("CartList") }

    fun minusItem(
        listItem: SnapshotStateList<ItemsModel>,
        position: Int,
        listener: ChangeNumberItemsListener)
    {
        if (listItem[position].numberInCart == 1)
            listItem.removeAt(position)
        else
            listItem[position].numberInCart--

        sharedPrefs.putListObject("CartList", ArrayList(listItem))
        listener.onChanged()
    }

    fun plusItem(
        listItem: SnapshotStateList<ItemsModel>,
        position: Int,
        listener: ChangeNumberItemsListener)
    {
        listItem[position].numberInCart++

        sharedPrefs.putListObject("CartList", ArrayList(listItem))
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberInCart
        }
        return fee
    }
}