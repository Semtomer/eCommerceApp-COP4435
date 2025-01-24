package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerceapp.Model.CategoryModel
import com.example.ecommerceapp.Model.ItemsModel
import com.example.ecommerceapp.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _banner = MutableLiveData<List<SliderModel>>()
    private val _category = MutableLiveData<List<CategoryModel>>()
    private val _recommended = MutableLiveData<List<ItemsModel>>()

    val banners: LiveData<List<SliderModel>> = _banner
    val categories: LiveData<List<CategoryModel>> = _category
    val recommended: LiveData<List<ItemsModel>> = _recommended

    fun loadBanners() {

        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val lists = mutableListOf<SliderModel>()

                for (childSnapshot in snapshot.children) {

                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if (list != null) { lists.add(list) }
                }

                _banner.value = lists
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun loadCategories() {

        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {

                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) { lists.add(list) }
                }
                _category.value = lists
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun loadRecommended() {

        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("showRecommended").equalTo(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {

                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null) { lists.add(list) }
                }
                _recommended.value = lists
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun loadFiltered(id: String) {

        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("categoryId").equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {

                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null) { lists.add(list) }
                }
                _recommended.value = lists
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}