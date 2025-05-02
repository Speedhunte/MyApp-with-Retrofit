package com.example.myapp.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Product
import com.example.model.Reviews
import com.example.myapp.R
import kotlin.math.roundToInt

@Composable
fun ProductDetailsScreen(
    //onBack: ()->Unit,
    product: Product) {
    val scrollState = rememberScrollState()
    var expandedDescription by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        ProductImage(product)

        Column(modifier = Modifier.padding(16.dp)) {
            ProductMainInfo(product)
            ProductPriceSection(product)
            ProductStockSection(product)
            ProductDescription(product, expandedDescription) {
                expandedDescription = !expandedDescription
            }
            ProductDetailsSection(product)
            ProductReviewsSection(product)
        }
    }
}

@Composable
private fun ProductImage(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.images.firstOrNull())
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        if (product.discountPercentage > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "-${product.discountPercentage.roundToInt()}%",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProductMainInfo(product: Product) {
    Text(
        text = product.title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        RatingBar(rating = product.rating)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${product.reviews.size} reviews",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.alpha(0.6f)
        )
    }
}

@Composable
private fun ProductPriceSection(product: Product) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "$${product.price}",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (product.discountPercentage > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$${"%.2f".format(product.price * (1 + product.discountPercentage / 100))}",
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ProductStockSection(product: Product) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = when {
                        product.stock <= 0 -> MaterialTheme.colorScheme.error
                        product.stock < 10 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
                product.stock <= 0 -> "Out of stock"
                product.stock < 10 -> "Only ${product.stock} left!"
                else -> "In stock"
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ProductDescription(
    product: Product,
    expanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextButton(
            onClick = onToggleExpand,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(text = if (expanded) "Show less" else "Read more")
        }
    }
}

@Composable
private fun ProductDetailsSection(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Product Details",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        DetailRow("Brand", product.brand.orEmpty())
        DetailRow("Category", product.category)
        DetailRow("SKU", product.sku)
        DetailRow("Weight", "${product.weight} oz")
        DetailRow(
            "Dimensions",
            "${product.dimensions.width}x${product.dimensions.height}x${product.dimensions.depth} cm"
        )
        DetailRow("Warranty", product.warrantyInformation)
        DetailRow("Shipping", product.shippingInformation)
        DetailRow("Return Policy", product.returnPolicy)
    }
}

@Composable
private fun ProductReviewsSection(product: Product) {
    if (product.reviews.isNotEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Customer Reviews",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            product.reviews.forEach { review ->
                ReviewItem(review)
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Reviews) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingBar(
                    rating = review.rating.toDouble()
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = review.reviewerName.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            review.comment?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RatingBar(
    rating: Double,
    starsColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) starsColor else starsColor.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
