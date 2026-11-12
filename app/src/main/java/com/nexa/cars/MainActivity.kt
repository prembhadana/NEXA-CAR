package com.nexa.cars

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.text.NumberFormat
import java.util.Locale

private val NexaBlue = Color(0xFF0878B9)
private val NexaDeepBlue = Color(0xFF075985)
private val NexaBlack = Color(0xFF101318)
private val PageBg = Color(0xFFF3F5F8)
private val Muted = Color(0xFF697586)
private val Green = Color(0xFF16865B)

private data class BalenoVariant(val name: String, val price: Long, val cng: Boolean = false)

private val balenoVariants = listOf(
    BalenoVariant("Sigma MT", 599360),
    BalenoVariant("Delta MT", 700360),
    BalenoVariant("Zeta MT", 800360),
    BalenoVariant("Alpha MT", 910360),
    BalenoVariant("Alpha (O) MT", 950360),
    BalenoVariant("Delta AGS", 750360),
    BalenoVariant("Zeta AGS", 850360),
    BalenoVariant("Alpha AGS", 960360),
    BalenoVariant("Alpha (O) AGS", 999460),
    BalenoVariant("Delta CNG MT", 792860, true),
    BalenoVariant("Zeta CNG MT", 892860, true),
    BalenoVariant("Delta CNG AGS", 832860, true),
    BalenoVariant("Zeta CNG AGS", 932860, true)
)

private fun money(value: Long): String =
    NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(value)

private fun parseAmount(value: String): Long = value.filter { it.isDigit() }.toLongOrNull() ?: 0L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = NexaBlue,
                    secondary = NexaDeepBlue,
                    background = PageBg,
                    surface = Color.White
                )
            ) {
                NexaCarsApp(
                    onSharePdf = { details -> createAndSharePdf(details) }
                )
            }
        }
    }

    private fun createAndSharePdf(details: QuoteDetails) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var y = 58f

        paint.color = android.graphics.Color.rgb(16, 19, 24)
        canvas.drawRect(0f, 0f, 595f, 130f, paint)
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 28f
        paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        canvas.drawText("NEXA CARS", 40f, 58f, paint)
        paint.textSize = 14f
        paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
        canvas.drawText("NEXA Autonation • Palwal", 40f, 86f, paint)

        y = 170f
        paint.color = android.graphics.Color.rgb(16, 19, 24)
        paint.textSize = 22f
        paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        canvas.drawText("CAR QUOTATION", 40f, y, paint)
        y += 38f
        paint.textSize = 14f
        paint.typeface = android.graphics.Typeface.NORMAL
        val rows = listOf(
            "Customer: ${details.customer.ifBlank { "Customer" }}",
            "Mobile: ${details.mobile.ifBlank { "Not provided" }}",
            "Model: Maruti Suzuki Baleno",
            "Variant: ${details.variant}",
            "Ex-showroom: ${money(details.exShowroom)}",
            "RTO / Registration: ${money(details.rto)}",
            "Insurance: ${money(details.insurance)}",
            "FASTag: ${money(details.fastag)}",
            "Accessories: ${money(details.accessories)}",
            "Extended Warranty: ${money(details.warranty)}",
            "Other Discount: -${money(details.discount)}",
            "CNG Rebate: -${money(details.cngRebate)}",
            "November 2026 Baleno offer: ₹0",
            "ON-ROAD ESTIMATE: ${money(details.total)}"
        )
        rows.forEachIndexed { index, row ->
            if (index == rows.lastIndex) {
                paint.color = android.graphics.Color.rgb(7, 88, 133)
                paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
                paint.textSize = 18f
            } else {
                paint.color = android.graphics.Color.rgb(55, 65, 81)
                paint.typeface = android.graphics.Typeface.NORMAL
                paint.textSize = 14f
            }
            canvas.drawText(row, 40f, y, paint)
            y += if (index == rows.lastIndex) 42f else 34f
        }
        paint.color = android.graphics.Color.GRAY
        paint.textSize = 10f
        paint.typeface = android.graphics.Typeface.NORMAL
        canvas.drawText("Estimate only. Confirm current dealership-approved charges before delivery.", 40f, 790f, paint)
        document.finishPage(page)

        val file = File(cacheDir, "Nexa-Cars-${System.currentTimeMillis()}.pdf")
        file.outputStream().use { document.writeTo(it) }
        document.close()
        val uri: Uri = FileProvider.getUriForFile(this, "com.nexa.cars.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_SUBJECT, "NEXA Cars Quotation - ${details.variant}")
            putExtra(Intent.EXTRA_TEXT, "NEXA Autonation, Palwal - Baleno quotation")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share quotation PDF"))
    }
}

private data class QuoteDetails(
    val customer: String,
    val mobile: String,
    val variant: String,
    val exShowroom: Long,
    val rto: Long,
    val insurance: Long,
    val fastag: Long,
    val accessories: Long,
    val warranty: Long,
    val discount: Long,
    val cngRebate: Long,
    val total: Long
)

@Composable
private fun NexaCarsApp(onSharePdf: (QuoteDetails) -> Unit) {
    var showQuotation by remember { mutableStateOf(false) }
    if (showQuotation) {
        QuotationScreen(onBack = { showQuotation = false }, onSharePdf = onSharePdf)
    } else {
        HomeScreen(onCreateQuotation = { showQuotation = true })
    }
}

@Composable
private fun HomeScreen(onCreateQuotation: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(PageBg).verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(NexaBlack)
                .padding(start = 22.dp, end = 22.dp, top = 28.dp, bottom = 25.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).background(NexaBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("N", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 23.sp) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("NEXA CARS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Text("AUTONATION • PALWAL", color = Color(0xFFB8C1CC), fontSize = 11.sp, letterSpacing = 1.5.sp)
                }
            }
            Spacer(Modifier.height(25.dp))
            Text("Drive the experience.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text("Create smart, professional car quotations.", color = Color(0xFFB8C1CC), fontSize = 13.sp)
        }

        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("DASHBOARD", color = NexaBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text("Welcome back", color = NexaBlack, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                Surface(color = Color(0xFFE3F2FC), shape = RoundedCornerShape(50)) {
                    Text("v1.1", modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp), color = NexaDeepBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = NexaBlack)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("QUICK QUOTATION", color = Color(0xFF78C8F1), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                    Text("Your next deal
starts here.", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
                    Text("Select a vehicle, add customer details and prepare an on-road estimate.", color = Color(0xFFCFD6DE), fontSize = 13.sp)
                    Button(
                        onClick = onCreateQuotation,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexaBlue)
                    ) {
                        Text("Create new quotation", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text("AVAILABLE MODEL", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onCreateQuotation),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE6F4FC), shape = RoundedCornerShape(7.dp)) {
                            Text("MARUTI SUZUKI", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = NexaDeepBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("13 VARIANTS", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Baleno", color = NexaBlack, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Petrol • CNG  |  MT • AGS", color = Muted, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Prepare quotation", color = NexaBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("→", color = NexaBlue, fontSize = 20.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoTile("13", "Variants", Modifier.weight(1f))
                InfoTile("PDF", "Shareable quote", Modifier.weight(1f))
                InfoTile("Auto", "RTO estimate", Modifier.weight(1f))
            }
            Text("Prices are editable. Verify final dealership charges before sharing.", color = Muted, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun InfoTile(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = NexaBlue, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(label, color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun QuotationScreen(onBack: () -> Unit, onSharePdf: (QuoteDetails) -> Unit) {
    var selectedVariant by remember { mutableStateOf(balenoVariants[0]) }
    var expanded by remember { mutableStateOf(false) }
    var customer by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var exShowroomText by remember { mutableStateOf(balenoVariants[0].price.toString()) }
    var insuranceText by remember { mutableStateOf("") }
    var fastagText by remember { mutableStateOf("600") }
    var accessoriesText by remember { mutableStateOf("0") }
    var warrantyText by remember { mutableStateOf("0") }
    var discountText by remember { mutableStateOf("0") }

    val exShowroom = parseAmount(exShowroomText)
    val rtoBase = if (exShowroom > 600000L) exShowroom * 8 / 100 else exShowroom * 5 / 100
    val rto = rtoBase + 5650L
    val cngRebate = if (selectedVariant.cng) exShowroom * 16 / 1000 else 0L
    val insurance = parseAmount(insuranceText)
    val fastag = parseAmount(fastagText)
    val accessories = parseAmount(accessoriesText)
    val warranty = parseAmount(warrantyText)
    val discount = parseAmount(discountText)
    val total = (exShowroom + rto + insurance + fastag + accessories + warranty - discount - cngRebate).coerceAtLeast(0L)
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().background(PageBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(NexaBlack).padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", color = Color.White, fontSize = 32.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 14.dp))
            Column {
                Text("NEW QUOTATION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp, letterSpacing = 1.sp)
                Text("NEXA Autonation • Palwal", color = Color(0xFFB8C1CC), fontSize = 11.sp)
            }
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Column {
                Text("BALENO", color = NexaBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.5.sp)
                Text("Build your quotation", color = NexaBlack, fontWeight = FontWeight.ExtraBold, fontSize = 25.sp)
                Text("Choose variant and enter confirmed charges.", color = Muted, fontSize = 12.sp)
            }

            SectionCard("01  CUSTOMER DETAILS") {
                InputField("Customer name", customer, { customer = it }, false)
                InputField("Mobile number", mobile, { mobile = it }, true)
            }

            SectionCard("02  SELECT VARIANT") {
                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(selectedVariant.name, color = NexaBlack)
                            Text("⌄", color = NexaBlue)
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.88f).heightIn(max = 360.dp)) {
                        balenoVariants.forEach { variant ->
                            DropdownMenuItem(
                                text = { Text("${variant.name}  •  ${money(variant.price)}", fontSize = 13.sp) },
                                onClick = {
                                    selectedVariant = variant
                                    exShowroomText = variant.price.toString()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                InputField("Ex-showroom price (₹)", exShowroomText, { exShowroomText = it }, true)
                Text("Variant price is prefilled and can be edited if the approved price changes.", color = Muted, fontSize = 10.sp)
            }

            SectionCard("03  PRICE BREAKUP") {
                InputField("Insurance (₹) — enter approved amount", insuranceText, { insuranceText = it }, true)
                InputField("FASTag (₹)", fastagText, { fastagText = it }, true)
                InputField("Accessories (₹)", accessoriesText, { accessoriesText = it }, true)
                InputField("Extended warranty (₹)", warrantyText, { warrantyText = it }, true)
                InputField("Other discount (₹)", discountText, { discountText = it }, true)
                if (selectedVariant.cng) {
                    PriceLine("CNG rebate (1.6%)", "- ${money(cngRebate)}", Green)
                }
                PriceLine("RTO formula", if (exShowroom > 600000L) "8% + ₹5,650" else "5% + ₹5,650", Muted)
                PriceLine("Calculated RTO", money(rto), NexaBlack)
                PriceLine("November 2026 Baleno offers", "₹0", Muted)
            }

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = NexaBlack)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ESTIMATED ON-ROAD PRICE", color = Color(0xFFB8C1CC), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Text(money(total), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Includes entered charges and calculated RTO. Insurance must be entered to complete estimate.", color = Color(0xFFB8C1CC), fontSize = 11.sp)
                }
            }

            Button(
                onClick = {
                    onSharePdf(
                        QuoteDetails(customer, mobile, selectedVariant.name, exShowroom, rto, insurance, fastag, accessories, warranty, discount, cngRebate, total)
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NexaBlue)
            ) {
                Text("Generate & share PDF quotation", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(13.dp)) {
                Text("Back to dashboard")
            }
            Text("Note: November 2026 Baleno consumer, exchange and upgrade offers are set to ₹0. Confirm all final prices with dealership records.", color = Muted, fontSize = 10.sp)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Text(title, color = NexaBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.1.sp)
            content()
        }
    }
}

@Composable
private fun InputField(label: String, value: String, onValueChange: (String) -> Unit, numeric: Boolean) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(11.dp),
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NexaBlue,
            focusedLabelColor = NexaBlue
        )
    )
}

@Composable
private fun PriceLine(label: String, value: String, valueColor: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
