package com.example.ecommerceapp.Activity

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ecommerceapp.Model.ItemsModel
import com.example.ecommerceapp.R

@Composable
fun ListItems(items: List<ItemsModel>)
{
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(1000.dp)
            .padding(start = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items.size)
        { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecommendedItem(items, row)
            }
        }
    }
}

@Composable
fun RecommendedItem(items: List<ItemsModel>, pos: Int) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp)
            .height(225.dp)
    ) {
        AsyncImage(
            model = items[pos].picUrl.firstOrNull(),
            contentDescription = items[pos].title,
            modifier = Modifier
                .width(175.dp)
                .background(colorResource(R.color.LightGrey), shape = RoundedCornerShape(10.dp))
                .height(175.dp)
                .padding(8.dp)
                .clickable {
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("object", items[pos])
                    }
                    context.startActivity(intent, null)
                },
            contentScale = ContentScale.Inside
        )

        Text(
            text = items[pos].title,
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    tint = colorResource(R.color.Orange)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = items[pos].rating.toString(),
                    color = Color.Black,
                    fontSize = 13.sp,
                )
            }

            Text(
                text = "$${items[pos].price}",
                color = colorResource(R.color.logoBlue),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
