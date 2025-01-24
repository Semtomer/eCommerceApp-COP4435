package com.example.ecommerceapp.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.ecommerceapp.Helper.ChangeNumberItemsListener
import com.example.ecommerceapp.Helper.ManagementCart
import com.example.ecommerceapp.Model.ItemsModel
import com.example.ecommerceapp.R

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CartScreen( ManagementCart(this),
                onBackClick = { finish() })
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun CartScreen(
        managementCart: ManagementCart =
            ManagementCart(LocalContext.current),
        onBackClick: () -> Unit = {}
    ) {
        val cartItems = remember {
            mutableStateListOf<ItemsModel>().also {
                it.addAll(managementCart.getListCart())
            }
        }

        val tax = remember { mutableDoubleStateOf(0.0) }
        val deliveryFee by remember { mutableDoubleStateOf(10.0) }

        calculatorCart(managementCart, tax)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            ConstraintLayout(modifier = Modifier.padding(top = 36.dp))
            {
                val (backBtn, cartTxt) = createRefs()

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(cartTxt) { centerTo(parent) },
                    text = "Your Cart",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .background(colorResource(R.color.LightGrey),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                    )
                }
            }

            if (cartItems.isEmpty()) {
                Text(
                    text = "Cart is empty",
                    modifier = Modifier.align(
                               Alignment.CenterHorizontally)
                )
            } else {
                CartList(
                    cartItems = cartItems,
                    managementCart
                ) {
                    cartItems.clear()
                    cartItems.addAll(managementCart.getListCart())
                    calculatorCart(managementCart, tax)
                }

                CartSummary(
                    itemTotal = managementCart.getTotalFee(),
                    tax = tax.doubleValue,
                    delivery = deliveryFee
                )
            }
        }
    }
}


private fun calculatorCart(
    managementCart: ManagementCart,
    tax: MutableState<Double>)
{
    val percentTax = 0.02
    tax.value = Math.round((managementCart.getTotalFee() * percentTax) * 100.0) / 100.0
}

@Composable
private fun CartSummary(
    itemTotal: Double,
    tax: Double,
    delivery: Double
) {

    val total = itemTotal + tax + delivery

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Item Total:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = "$$itemTotal")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Tax:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = "$$tax")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Delivery:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = "$$delivery")
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(R.color.grey))
                .padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Total:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.grey)
            )
            Text(text = "$$total")
        }

        Button(
            onClick = {},
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.logoBlue)),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Check Out",
                fontSize = 18.sp,
                color = Color.White,
            )
        }
    }
}

@Composable
fun CartList(
    cartItems: SnapshotStateList<ItemsModel>,
    managementCart: ManagementCart,
    onItemChange: () -> Unit
) {
    LazyColumn(Modifier.padding(top = 16.dp)) {
        items(cartItems) { item ->
            CartItem(
                cartItems,
                item = item,
                managementCart = managementCart,
                onItemChange = onItemChange
            )
        }
    }
}

@Composable
fun CartItem(
    cartItems: SnapshotStateList<ItemsModel>,
    item: ItemsModel,
    managementCart: ManagementCart,
    onItemChange: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
    ) {
        val (pic, titleTxt, feeEachTime, totalEachItem, quantity) = createRefs()

        Image(
            painter = rememberAsyncImagePainter(item.picUrl[0]),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .background(colorResource(R.color.LightGrey), shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
                .constrainAs(pic) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Text(
            text = item.title,
            modifier = Modifier
                .constrainAs(titleTxt) {
                    start.linkTo(pic.end)
                    top.linkTo(pic.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )

        Text(
            text = "$${item.price}", color = colorResource(R.color.logoBlue),
            modifier = Modifier
                .constrainAs(feeEachTime) {
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )

        Text(
            text = "$${item.numberInCart * item.price}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(totalEachItem) {
                    start.linkTo(titleTxt.start)
                    bottom.linkTo(pic.bottom)
                }
                .padding(start = 8.dp, bottom = 8.dp)
        )

        ConstraintLayout(
            modifier = Modifier
            .width(100.dp)
            .constrainAs(quantity) {
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .background(
                colorResource(R.color.LightGrey),
                shape = RoundedCornerShape(10.dp)
            )
        ) {
            val (plusCartBtn, minusCartBtn, numberItemTxt) = createRefs()

            Text(
                text = item.numberInCart.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(numberItemTxt) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )

            Box(
                modifier = Modifier
                .padding(2.dp)
                .size(28.dp)
                .background(
                    colorResource(R.color.logoBlue),
                    shape = RoundedCornerShape(10.dp)
                )
                .constrainAs(plusCartBtn) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clickable {
                    managementCart.plusItem(
                        cartItems,
                        cartItems.indexOf(item),
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                onItemChange()
                            }
                        })
                }
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .background(
                        colorResource(R.color.white),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .constrainAs(minusCartBtn) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        managementCart.minusItem(
                            cartItems,
                            cartItems.indexOf(item),
                            object : ChangeNumberItemsListener {
                                override fun onChanged() {
                                    onItemChange()
                                }
                            })
                    }
            ) {
                Text(
                    text = "-",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
