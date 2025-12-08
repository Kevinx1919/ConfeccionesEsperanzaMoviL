package com.confecciones.esperanza.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- MODELOS DE DATOS PARA LA UI ---
data class Product(val id: Int, val name: String, val price: Double)

val productList = listOf(
    Product(1, "Camiseta Premium Algodón", 25000.0),
    Product(2, "Pantalón Cargo Ajustado", 80000.0),
    Product(3, "Chaqueta Denim Clásica", 120000.0),
    Product(4, "Vestido Midi Floral", 95000.0),
    Product(5, "Sudadera con Capucha Gris", 70000.0),
    Product(6, "Zapatillas Deportivas", 150000.0),
    Product(7, "Falda Plisada Negra", 65000.0),
    Product(8, "Bufanda de Lana Tejida", 30000.0),
    Product(9, "Cinturón de Cuero", 45000.0),
    Product(10, "Calcetines Tobilleros Pack", 20000.0)
)

// --- COMPONENTES REUTILIZABLES ---

@Composable
fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun OrderStatusBadge(status: String?) {
    val statusText = status ?: "Desconocido"
    val (backgroundColor, textColor) = when (statusText.uppercase()) {
        "EN PROCESO" -> Color(0xFFE0E7FF) to Color(0xFF4338CA)
        "PENDIENTE" -> Color(0xFFFEF9C3) to Color(0xFF854D0E)
        "EN PRODUCCIÓN" -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        "COMPLETADO" -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        "CANCELADO" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        else -> Color.LightGray to Color.DarkGray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value ?: "N/A",
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}