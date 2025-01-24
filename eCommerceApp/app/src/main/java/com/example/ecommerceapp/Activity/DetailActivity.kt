package com.example.ecommerceapp.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import com.example.ecommerceapp.Model.Comment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

import com.example.ecommerceapp.Helper.ManagementCart
import com.example.ecommerceapp.Model.ItemsModel
import com.example.ecommerceapp.R

class DetailActivity : BaseActivity() {
    private lateinit var item: ItemsModel
    private lateinit var managementCart: ManagementCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = intent.getParcelableExtra<ItemsModel>("object")!!
        managementCart = ManagementCart(this)

        setContent {
            DetailScreen( item = item,
                onBackClick = { finish() },
                onAddToCartClick = {
                    item.numberInCart = 1
                    managementCart.insertItem(item)
                },
                onCartClick = {
                    startActivity(Intent(this, CartActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun DetailScreen(
    item: ItemsModel,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var selectedImageUrl by remember {
        mutableStateOf(item.picUrl.first()) }
    var selectedModelIndex by remember {
        mutableIntStateOf(-1) }
    var commentText by remember { mutableStateOf("") }
    val comments = remember { mutableStateListOf<Comment>() }
    val itemId = item.title  // Product ID
    val focusRequester = remember { FocusRequester() }

    // Yorumları yükle
    LaunchedEffect(itemId) {
        loadComments(itemId, comments)
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (lazyColumnRef) = createRefs()

        LazyColumn(
            modifier = Modifier
                .constrainAs(lazyColumnRef){ // Constrain the LazyColumn
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            item {
                ConstraintLayout(
                    modifier = Modifier
                        .padding(top = 36.dp, bottom = 16.dp)
                        .fillMaxWidth()
                ) {
                    val (back, fav) = createRefs()

                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .constrainAs(back) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                            .background(colorResource(R.color.LightGrey),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .constrainAs(fav) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                    )
                }

                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .background(
                            colorResource(R.color.LightGrey),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(top = 8.dp, bottom = 8.dp)
                )

                LazyRow(modifier = Modifier.padding(vertical = 16.dp)) {
                    items(item.picUrl) { imageUrl ->
                        ImageThumbnail(
                            imageUrl = imageUrl,
                            isSelected = selectedImageUrl == imageUrl,
                            onClick = { selectedImageUrl = imageUrl }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(end = 16.dp)
                    )

                    Text(
                        text = "$${item.price}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }

                RatingBar(rating = item.rating)

                ModelSelector(
                    models = item.model,
                    selectedModelIndex = selectedModelIndex,
                    onModelSelected = { selectedModelIndex = it }
                )

                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            item { // Comments section as a single item
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

                    // Display comments using a simple Column or other layout
                    comments.forEach { comment ->
                        CommentItem(comment)
                    }

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Comment") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .focusRequester(focusRequester)
                    )

                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                addComment(itemId, commentText, comments)
                                commentText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.logoBlue))
                    ) {
                        Text("Send")
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onAddToCartClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.logoBlue)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "Buy Now", fontSize = 18.sp)
                    }

                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier.background(
                            colorResource(R.color.LightGrey),
                            shape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Column(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
        Text(text = comment.user, fontWeight = FontWeight.Bold)
        Text(text = comment.text)
        Text(text = comment.timestamp, fontSize = 12.sp, color = Color.Gray)
    }
}

private fun addComment(itemId: String, commentText: String, comments: MutableList<Comment>) {
    val user = "user1"
    val newComment = Comment(user, commentText, getCurrentTimestamp())

    // Add the new comment to the list first
    comments.add(newComment)

    val database = FirebaseDatabase.getInstance()
    val commentsRef = database.getReference("comments").child(itemId.toString())

    commentsRef.push().setValue(newComment) // No need for addOnSuccessListener here
        .addOnFailureListener {
            // If the Firebase operation fails, remove the comment from the list
            comments.remove(newComment)
        }
}

private fun loadComments(itemId: String, comments: MutableList<Comment>) {
    val database = FirebaseDatabase.getInstance()
    val commentsRef = database.getReference("comments").child(itemId.toString())

    commentsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            comments.clear()
            for (childSnapshot in snapshot.children) {
                val comment = childSnapshot.getValue(Comment::class.java)
                comment?.let { comments.add(it) }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Hata işleme
        }
    })
}

private fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

@Composable
fun ImageThumbnail(
    imageUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backColor = if (isSelected)
                        colorResource(R.color.lightBlue)
                    else
                        colorResource(R.color.LightGrey)

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(55.dp)
            .then(
                if (isSelected) {
                    Modifier.border(1.dp, colorResource(R.color.logoBlue),
                                    RoundedCornerShape(10.dp))
                } else { Modifier }
            )
            .background(backColor, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun RatingBar(rating: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "Select Model",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
            tint = colorResource(R.color.Orange)
        )

        Text(text = "$rating Rating",
             style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ModelSelector(
    models: List<String>,
    selectedModelIndex: Int,
    onModelSelected: (Int) -> Unit
) {
    LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
        itemsIndexed(models) { index, model ->
            Box(
                modifier = Modifier
                .padding(end = 8.dp)
                .height(48.dp)
                .then(
                    if (index == selectedModelIndex) {
                        Modifier.border(
                            1.dp,
                            colorResource(R.color.logoBlue),
                            RoundedCornerShape(10.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .background(
                    if (index == selectedModelIndex) {
                        colorResource(R.color.lightBlue)
                    } else {
                        colorResource(R.color.LightGrey)
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onModelSelected(index) }
                .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = model,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = if (index == selectedModelIndex)
                                colorResource(R.color.logoBlue)
                            else
                                colorResource(R.color.black),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
