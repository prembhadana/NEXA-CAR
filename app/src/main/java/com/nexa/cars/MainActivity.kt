package com.nexa.cars

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

private val Blue = Color(0xFF0878B9)
private val Deep = Color(0xFF075985)
private val Ink = Color(0xFF101820)
private val Bg = Color(0xFFF3F6F9)
private val Muted = Color(0xFF667085)
private val Green = Color(0xFF16865B)
private val Red = Color(0xFFC2413B)
private fun money(v: Long) = "₹" + NumberFormat.getNumberInstance(Locale("en","IN")).format(v)
private fun amount(s: String) = s.filter { it.isDigit() }.toLongOrNull() ?: 0L

private data class Variant(val name:String, val price:Long, val fuel:String, val features:List<String>)
private data class Model(val name:String, val subtitle:String, val variants:List<Variant>, val hero:String)
private val commonSafety = listOf("6 Airbags","ABS with EBD","ESP / Stability Control","ISOFIX Child Seat Anchorages")
private val models = listOf(
  Model("Baleno","Premium Hatchback • Petrol / S-CNG", listOf(
   Variant("Sigma MT",599360,"Petrol",commonSafety+listOf("Reverse Parking Sensors","ESP with Hill Hold Assist")),
   Variant("Delta MT",700360,"Petrol",commonSafety+listOf("Touchscreen Infotainment","Steering Mounted Controls")),
   Variant("Zeta MT",800360,"Petrol",commonSafety+listOf("SmartPlay Pro+","Rear View Camera","Cruise Control")),
   Variant("Alpha MT",910360,"Petrol",commonSafety+listOf("Head-Up Display","360 View Camera","Wireless Charger")),
   Variant("Alpha (O) MT",950360,"Petrol",commonSafety+listOf("Level 2+ ADAS","Adaptive Cruise Control","Lane Keep Assist","Advanced Emergency Braking")),
   Variant("Delta AGS",750360,"Petrol AGS",commonSafety+listOf("AGS Automatic Transmission")),
   Variant("Zeta AGS",850360,"Petrol AGS",commonSafety+listOf("SmartPlay Pro+","Rear View Camera","AGS Automatic")),
   Variant("Alpha AGS",960360,"Petrol AGS",commonSafety+listOf("Head-Up Display","360 View Camera","Wireless Charger")),
   Variant("Alpha (O) AGS",999460,"Petrol AGS",commonSafety+listOf("Level 2+ ADAS","Adaptive Cruise Control","Lane Keep Assist")),
   Variant("Delta CNG MT",792860,"S-CNG",commonSafety+listOf("S-CNG","Factory Fitted CNG")),
   Variant("Zeta CNG MT",892860,"S-CNG",commonSafety+listOf("S-CNG","SmartPlay Pro+")),
   Variant("Delta CNG AGS",832860,"S-CNG AGS",commonSafety+listOf("S-CNG","AGS Automatic")),
   Variant("Zeta CNG AGS",932860,"S-CNG AGS",commonSafety+listOf("S-CNG","SmartPlay Pro+","AGS Automatic"))
  ),"baleno"),
  Model("Fronx","Sporty Compact SUV • Petrol / Turbo / CNG", listOf(
   Variant("Sigma 1.2 MT",690361,"Petrol",commonSafety+listOf("LED Tail Lamps","Rear Parking Sensors")),
   Variant("Delta 1.2 MT",775360,"Petrol",commonSafety+listOf("SmartPlay Infotainment","Steering Controls")),
   Variant("Delta+ 1.2 MT",814960,"Petrol",commonSafety+listOf("LED DRLs","Alloy Wheels")),
   Variant("Delta 1.2 AGS",825360,"Petrol AGS",commonSafety+listOf("AGS Automatic","SmartPlay Infotainment")),
   Variant("Delta+ 1.2 AGS",864960,"Petrol AGS",commonSafety+listOf("AGS Automatic","Alloy Wheels")),
   Variant("Turbo Delta+ MT",897360,"Turbo Petrol",commonSafety+listOf("1.0L Boosterjet Turbo","Smart Hybrid Technology")),
   Variant("Turbo Zeta MT",976360,"Turbo Petrol",commonSafety+listOf("1.0L Boosterjet Turbo","Smart Hybrid","Paddle Shifters (AT only)")),
   Variant("Turbo Alpha MT",1061360,"Turbo Petrol",commonSafety+listOf("1.0L Boosterjet Turbo","Smart Hybrid","Cruise Control")),
   Variant("Turbo Alpha (O) MT",1075360,"Turbo Petrol",commonSafety+listOf("1.0L Boosterjet Turbo","Smart Hybrid","Premium Features")),
   Variant("Turbo Zeta AT",1104360,"Turbo AT",commonSafety+listOf("6-Speed Automatic","Paddle Shifters","Smart Hybrid")),
   Variant("Turbo Alpha AT",1189360,"Turbo AT",commonSafety+listOf("6-Speed Automatic","Paddle Shifters","Cruise Control")),
   Variant("Turbo Alpha (O) AT",1203300,"Turbo AT",commonSafety+listOf("6-Speed Automatic","Paddle Shifters","Premium Features")),
   Variant("Sigma CNG MT",794340,"S-CNG",commonSafety+listOf("Factory Fitted S-CNG")),
   Variant("Delta CNG MT",874360,"S-CNG",commonSafety+listOf("Factory Fitted S-CNG","SmartPlay Infotainment"))
  ),"fronx"),
  Model("Grand Vitara","Premium SUV • Strong Hybrid / Smart Hybrid / CNG", listOf(
   Variant("Sigma 1.5 MT",1077720,"Petrol",commonSafety+listOf("SmartPlay Infotainment")),
   Variant("Delta 1.5 MT",1215470,"Petrol",commonSafety+listOf("Rear Camera","Cruise Control")),
   Variant("Zeta 1.5 MT",1365770,"Petrol",commonSafety+listOf("Panoramic Sunroof","Connected Car Tech")),
   Variant("Alpha 1.5 MT",1535470,"Petrol",commonSafety+listOf("360 View Camera","Head-Up Display")),
   Variant("Alpha (O) 1.5 CVT",1962770,"Strong Hybrid",commonSafety+listOf("Strong Hybrid","Panoramic Sunroof","Ventilated Seats","360 View Camera")),
   Variant("Zeta+ 1.5 CVT",1797370,"Strong Hybrid",commonSafety+listOf("Strong Hybrid","Panoramic Sunroof")),
   Variant("Alpha 1.5 CVT",1955970,"Strong Hybrid",commonSafety+listOf("Strong Hybrid","Panoramic Sunroof","360 View Camera")),
   Variant("Alpha AWD 1.5 AT",1863370,"AllGrip 4x4",commonSafety+listOf("ALLGRIP Select 4x4","Paddle Shifters")),
   Variant("Zeta CNG 1.5 MT",1465770,"S-CNG",commonSafety+listOf("Factory Fitted S-CNG")),
   Variant("Delta CNG 1.5 MT",1305470,"S-CNG",commonSafety+listOf("Factory Fitted S-CNG"))
  ),"grand"),
  Model("XL6","Premium 6-Seater • Smart Hybrid / CNG", listOf(
   Variant("Zeta 1.5 MT",1163070,"Petrol",commonSafety+listOf("Captain Seats","SmartPlay Pro")),
   Variant("Alpha 1.5 MT",1239570,"Petrol",commonSafety+listOf("Captain Seats","Cruise Control","SmartPlay Pro")),
   Variant("Alpha+ 1.5 MT",1317870,"Petrol",commonSafety+listOf("Ventilated Seats","Captain Seats","360 View Camera")),
   Variant("Alpha+ 1.5 MT Dual Tone",1333270,"Petrol",commonSafety+listOf("Ventilated Seats","Captain Seats","Dual Tone")),
   Variant("Zeta 1.5 AT",0L,"Petrol AT",commonSafety+listOf("Captain Seats","Automatic Transmission")),
   Variant("Alpha 1.5 AT",1394560,"Petrol AT",commonSafety+listOf("Captain Seats","Cruise Control","Automatic Transmission")),
   Variant("Alpha+ 1.5 AT",1453270,"Petrol AT",commonSafety+listOf("Ventilated Seats","Captain Seats","360 View Camera")),
   Variant("Zeta CNG 1.5 MT",0L,"S-CNG",commonSafety+listOf("Captain Seats","Factory Fitted S-CNG"))
  ),"xl6"),
  Model("e-Vitara","Electric SUV • 49 kWh / 61 kWh", listOf(
   Variant("Delta 49 kWh",1619578,"Electric",commonSafety+listOf("49 kWh Battery","HEARTECT-e Platform","Connected Car Features")),
   Variant("Zeta 61 kWh",1794577,"Electric",commonSafety+listOf("61 kWh Battery","Digital Cockpit","Connected Car Features")),
   Variant("Alpha 61 kWh",1999578,"Electric",commonSafety+listOf("61 kWh Battery","Fixed Glass Sunroof","Ambient Lighting","Digital Cockpit")),
   Variant("Alpha 61 kWh Dual Tone",1999578,"Electric",commonSafety+listOf("61 kWh Battery","Fixed Glass Sunroof","Dual Tone"))
  ),"ev")
 )

private data class Quote(
 val customer:String,val mobile:String,val model:String,val variant:String,val ex:Long,val rto:Long,
 val insurance:Long,val fastag:Long,val accessories:Long,val warranty:Long,val otherAdd:Long,
 val consumer:Long,val booking:Long,val exchange:Long,val additional:Long,val smart:Long,
 val upgrade:Long,val scrappage:Long,val corporate:Long,val total:Long,val rmName:String,val rmPhone:String
)

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState)
  setContent { MaterialTheme(colorScheme=lightColorScheme(primary=Blue,secondary=Deep,background=Bg,surface=Color.White)){ NexaApp { q -> sharePdf(q) } } }
 }
 private fun sharePdf(q:Quote){
  val doc=PdfDocument(); val page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create()); val c=page.canvas
  val p=Paint(Paint.ANTI_ALIAS_FLAG); val black=android.graphics.Color.rgb(16,24,32); val blue=android.graphics.Color.rgb(8,120,185)
  c.drawColor(android.graphics.Color.WHITE); p.color=black; c.drawRect(0f,0f,595f,135f,p)
  p.color=android.graphics.Color.WHITE;p.textSize=27f;p.typeface=Typeface.create("sans-serif",Typeface.BOLD);c.drawText("NEXA",38f,49f,p)
  p.textSize=12f;p.typeface=Typeface.create("sans-serif",Typeface.NORMAL);c.drawText("AUTONATION  •  PALWAL",40f,73f,p);c.drawText("PREMIUM CAR QUOTATION",40f,103f,p)
  var y=169f
  fun heading(s:String){p.color=blue;p.textSize=12f;p.typeface=Typeface.create("sans-serif",Typeface.BOLD);c.drawText(s,40f,y,p);y+=25f}
  fun row(label:String,value:String,color:Int=black,bold:Boolean=false){
   p.color=android.graphics.Color.rgb(85,96,108);p.textSize=12f;p.typeface=Typeface.create("sans-serif",Typeface.NORMAL);c.drawText(label,42f,y,p)
   p.color=color;p.textSize=13f;p.typeface=Typeface.create("sans-serif",if(bold)Typeface.BOLD else Typeface.NORMAL);p.textAlign=Paint.Align.RIGHT;c.drawText(value,552f,y,p);p.textAlign=Paint.Align.LEFT;y+=24f
  }
  heading("CUSTOMER & VEHICLE")
  row("Customer",q.customer.ifBlank{"—"});row("Mobile",q.mobile.ifBlank{"—"});row("Vehicle",q.model+"  •  "+q.variant);row("Relationship Manager",q.rmName.ifBlank{"NEXA Autonation"}+"  •  "+q.rmPhone)
  y+=8;heading("PRICE BREAKUP")
  row("Ex-showroom",money(q.ex));row("RTO / Registration  (+)", "+ "+money(q.rto),black)
  if(q.insurance>0)row("Insurance  (+)","+ "+money(q.insurance),black)
  if(q.fastag>0)row("FASTag  (+)","+ "+money(q.fastag),black)
  if(q.accessories>0)row("Accessories  (+)","+ "+money(q.accessories),black)
  if(q.warranty>0)row("Extended Warranty  (+)","+ "+money(q.warranty),black)
  if(q.otherAdd>0)row("Other Charges  (+)","+ "+money(q.otherAdd),black)
  y+=5;heading("OFFERS & DISCOUNTS")
  val discounts=listOf("Consumer Offer" to q.consumer,"Diwali Booking Offer" to q.booking,"All Exchange Offer" to q.exchange,"Additional Exchange Offer" to q.additional,"Smart Exchange Plan" to q.smart,"Upgrade Loyalty Bonus" to q.upgrade,"Scrappage Bonus" to q.scrappage,"Key Corporate Offer" to q.corporate)
  discounts.filter{it.second>0}.forEach{row(it.first+"  (−)","− "+money(it.second),android.graphics.Color.rgb(22,134,91))}
  y+=8;p.color=blue;c.drawRect(34f,y-15f,561f,y+52f,p);p.color=android.graphics.Color.WHITE;p.textSize=12f;p.typeface=Typeface.create("sans-serif",Typeface.BOLD);c.drawText("ESTIMATED ON-ROAD PRICE",49f,y+11f,p);p.textSize=22f;c.drawText(money(q.total),49f,y+39f,p)
  y+=92;p.color=android.graphics.Color.DKGRAY;p.textSize=9f;p.typeface=Typeface.create("sans-serif",Typeface.NORMAL)
  c.drawText("Offer eligibility and exchange benefits are subject to scheme terms and document verification.",40f,y,p)
  c.drawText("Prices are estimates based on uploaded Palwal price list and selected inputs. Confirm before booking.",40f,y+15f,p)
  c.drawText("NEXA Autonation • Palwal",40f,805f,p)
  doc.finishPage(page);val f=File(cacheDir,"NEXA-Quotation-${System.currentTimeMillis()}.pdf");f.outputStream().use{doc.writeTo(it)};doc.close()
  val uri:Uri=FileProvider.getUriForFile(this,"com.nexa.cars.fileprovider",f)
  val intent=Intent(Intent.ACTION_SEND).apply{type="application/pdf";putExtra(Intent.EXTRA_SUBJECT,"NEXA Quotation - ${q.model}");putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)}
  startActivity(Intent.createChooser(intent,"Share NEXA quotation"))
 }
}

@Composable private fun NexaApp(onPdf:(Quote)->Unit){
 val ctx= LocalContext.current
 val prefs=remember{ctx.getSharedPreferences("nexa_profile",0)}
 var rmName by remember{mutableStateOf(prefs.getString("rm_name","")?:"")}
 var rmPhone by remember{mutableStateOf(prefs.getString("rm_phone","")?:"")}
 var page by remember{mutableStateOf("home")}
 var selectedModel by remember{mutableStateOf(models[0])}
 var showProfile by remember{mutableStateOf(false)}
 if(showProfile) AlertDialog(onDismissRequest={showProfile=false},title={Text("Relationship Manager Details")},text={
  Column(verticalArrangement=Arrangement.spacedBy(8.dp)){Field("RM Name",rmName,{rmName=it},false);Field("RM Mobile Number",rmPhone,{rmPhone=it},true)}
 },confirmButton={TextButton(onClick={prefs.edit().putString("rm_name",rmName).putString("rm_phone",rmPhone).apply();showProfile=false}){Text("Save")}},dismissButton={TextButton(onClick={showProfile=false}){Text("Cancel")}})
 when(page){
  "home" -> HomeScreen(rmName,rmPhone,{showProfile=true},{m->selectedModel=m;page="quote"})
  "quote" -> QuoteScreen(selectedModel,rmName,rmPhone,{page="home"},onPdf)
 }
}

@Composable private fun HomeScreen(rm:String,phone:String,onProfile:()->Unit,onChoose:(Model)->Unit){
 Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())){
  Column(Modifier.fillMaxWidth().background(Ink).padding(horizontal=22.dp,vertical=25.dp)){
   Text("NEXA",color=Color.White,fontSize=28.sp,fontWeight=FontWeight.ExtraBold,letterSpacing=4.sp)
   Text("AUTONATION  •  PALWAL",color=Color(0xFF8CCFF0),fontSize=11.sp,letterSpacing=2.sp)
   Spacer(Modifier.height(22.dp));Text("A premium experience.",color=Color.White,fontSize=25.sp,fontWeight=FontWeight.Bold)
   Text("Professional quotations for every NEXA drive.",color=Color(0xFFCBD5E1),fontSize=13.sp)
  }
  Column(Modifier.padding(17.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
    Column{Text("DASHBOARD",color=Blue,fontSize=10.sp,fontWeight=FontWeight.Bold,letterSpacing=1.5.sp);Text("Choose your car",fontSize=24.sp,fontWeight=FontWeight.ExtraBold,color=Ink)}
    TextButton(onClick=onProfile){Text("RM PROFILE",color=Blue,fontWeight=FontWeight.Bold)}
   }
   Card(shape=RoundedCornerShape(17.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){
    Column(Modifier.padding(15.dp)){
     Text("RELATIONSHIP MANAGER",color=Muted,fontSize=10.sp,fontWeight=FontWeight.Bold)
     Text(if(rm.isBlank())"Add your name and mobile number" else rm,color=Ink,fontSize=16.sp,fontWeight=FontWeight.Bold)
     if(phone.isNotBlank())Text(phone,color=Muted,fontSize=12.sp)
     TextButton(onClick=onProfile){Text(if(rm.isBlank())"Set up profile  →" else "Edit profile  →")}
    }
   }
   models.forEach { m ->
    Card(Modifier.fillMaxWidth().clickable{onChoose(m)},shape=RoundedCornerShape(19.dp),colors=CardDefaults.cardColors(containerColor=Color.White),elevation=CardDefaults.cardElevation(2.dp)){
     Column {
      Box(Modifier.fillMaxWidth().height(145.dp).background(Color(0xFFE6EDF3)),contentAlignment=Alignment.Center){
       Text(m.name.uppercase(),color=Deep,fontSize=27.sp,fontWeight=FontWeight.ExtraBold,letterSpacing=1.5.sp)
      }
      Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=13.dp),verticalAlignment=Alignment.CenterVertically){
       Column(Modifier.weight(1f)){Text(m.name,color=Ink,fontSize=20.sp,fontWeight=FontWeight.Bold);Text(m.subtitle,color=Muted,fontSize=11.sp)}
       Button(onClick={onChoose(m)},shape=RoundedCornerShape(10.dp),contentPadding=PaddingValues(horizontal=14.dp,vertical=9.dp)){Text("Quote  →",fontSize=12.sp)}
      }
     }
    }
   }
   Text("PRICE LIST EFFECTIVE 19-SEP-2026  •  OFFERS NOV-2026",color=Muted,fontSize=10.sp,fontWeight=FontWeight.SemiBold)
   Text("All estimates are editable. Confirm current prices, eligibility and applicable scheme terms before booking.",color=Muted,fontSize=10.sp)
  }
 }
}

@Composable private fun QuoteScreen(model:Model,rm:String,phone:String,onBack:()->Unit,onPdf:(Quote)->Unit){
 var variantIndex by remember(model){mutableIntStateOf(0)}
 val variant=model.variants[variantIndex]
 var customer by remember{mutableStateOf("")};var mobile by remember{mutableStateOf("")}
 var exText by remember(variant){mutableStateOf(variant.price.toString())}
 var insurance by remember{mutableStateOf("0")};var fastag by remember{mutableStateOf("800")}
 var accessories by remember{mutableStateOf("0")};var warranty by remember{mutableStateOf("0")};var otherAdd by remember{mutableStateOf("0")}
 var consumer by remember{mutableStateOf("0")};var booking by remember{mutableStateOf("0")};var exchange by remember{mutableStateOf("0")}
 var additional by remember{mutableStateOf("0")};var smart by remember{mutableStateOf("0")};var upgrade by remember{mutableStateOf("0")}
 var scrap by remember{mutableStateOf("0")};var corporate by remember{mutableStateOf("0")}
 var expanded by remember{mutableStateOf(false)}
 val ex=amount(exText)
 val rto=(if(ex>600000) ex*8/100 else ex*5/100)+5650
 val ins=amount(insurance);val fast=amount(fastag);val acc=amount(accessories);val warr=amount(warranty);val add=amount(otherAdd)
 val discounts=listOf(amount(consumer),amount(booking),amount(exchange),amount(additional),amount(smart),amount(upgrade),amount(scrap),amount(corporate))
 val total=(ex+rto+ins+fast+acc+warr+add-discounts.sum()).coerceAtLeast(0)
 Column(Modifier.fillMaxSize().background(Bg)){
  Row(Modifier.fillMaxWidth().background(Ink).padding(16.dp),verticalAlignment=Alignment.CenterVertically){
   Text("‹",color=Color.White,fontSize=30.sp,modifier=Modifier.clickable(onClick=onBack).padding(end=12.dp))
   Column{Text("NEW QUOTATION",color=Color.White,fontSize=16.sp,fontWeight=FontWeight.Bold);Text("NEXA Autonation • Palwal",color=Color(0xFFB8C1CC),fontSize=11.sp)}
  }
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(15.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
   Text(model.name.uppercase(),color=Blue,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.6.sp)
   Text("Build your quotation",color=Ink,fontSize=24.sp,fontWeight=FontWeight.ExtraBold)
   Panel("01  CUSTOMER DETAILS"){
    Field("Customer name",customer,{customer=it},false);Field("Mobile number",mobile,{mobile=it},true)
   }
   Panel("02  SELECT VARIANT"){
    Box{OutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(11.dp)){
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(variant.name,color=Ink);Text("⌄",color=Blue)}
    }
     DropdownMenu(expanded, {expanded=false}, modifier=Modifier.fillMaxWidth(.88f).heightIn(max=380.dp)){
      model.variants.forEachIndexed{i,v->DropdownMenuItem(text={Column{Text(v.name,fontWeight=FontWeight.SemiBold);Text(if(v.price>0)money(v.price) else "Price not listed",color=Muted,fontSize=11.sp)}},onClick={variantIndex=i;expanded=false})}
     }
    }
    Field("Ex-showroom price (₹)",exText,{exText=it},true)
    Text("Fuel / Powertrain: ${variant.fuel}",color=Muted,fontSize=11.sp)
   }
   Panel("03  VARIANT FEATURES"){
    variant.features.distinct().forEach{FeatureChip(it)}
    Text("Feature availability is based on the uploaded brochure. Confirm variant-specific fitment before customer commitment.",color=Muted,fontSize=10.sp)
   }
   Panel("04  PRICE ADDITIONS  (+)"){
    PriceEntry("Insurance (₹)",insurance,{insurance=it});PriceEntry("FASTag (₹)",fastag,{fastag=it})
    PriceEntry("Accessories (₹)",accessories,{accessories=it});PriceEntry("Extended warranty (₹)",warranty,{warranty=it})
    PriceEntry("Other charges (₹)",otherAdd,{otherAdd=it})
    PriceRow("RTO / Registration  (+)",money(rto),Blue)
    Text("RTO formula: ${if(ex>600000)"8%" else "5%"} of ex-showroom + ₹5,650",color=Muted,fontSize=10.sp)
   }
   Panel("05  NOVEMBER 2026 OFFERS  (−)"){
    Text("Enter only the benefits for which the customer is eligible. Offer sheet is for Delhi-NCR.",color=Muted,fontSize=10.sp)
    PriceEntry("Consumer offer (₹)",consumer,{consumer=it});PriceEntry("Diwali booking offer (₹)",booking,{booking=it})
    PriceEntry("All exchange offer (₹)",exchange,{exchange=it});PriceEntry("Additional exchange offer (₹)",additional,{additional=it})
    PriceEntry("Smart exchange plan (₹)",smart,{smart=it});PriceEntry("Upgrade loyalty bonus (₹)",upgrade,{upgrade=it})
    PriceEntry("Scrappage bonus (₹)",scrap,{scrap=it});PriceEntry("Key corporate offer (₹)",corporate,{corporate=it})
    Text("Exchange and scrappage cannot be clubbed. Upgrade loyalty is for eligible old MSIL customers and can be clubbed with exchange, not scrappage. Verify scheme conditions.",color=Red,fontSize=10.sp)
   }
   Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Ink)){
    Column(Modifier.fillMaxWidth().padding(17.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
     Text("LIVE PRICE SUMMARY",color=Color(0xFF8CCFF0),fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.3.sp)
     PriceRow("Ex-showroom",money(ex),Color.White);PriceRow("Total additions  (+)",money(rto+ins+fast+acc+warr+add),Color.White)
     PriceRow("Total offers  (−)","− "+money(discounts.sum()),Color(0xFF70D6A8))
     Divider(color=Color(0xFF48515B))
     Text("ESTIMATED ON-ROAD PRICE",color=Color(0xFFCBD5E1),fontSize=11.sp,fontWeight=FontWeight.Bold)
     Text(money(total),color=Color.White,fontSize=30.sp,fontWeight=FontWeight.ExtraBold)
    }
   }
   Button(onClick={onPdf(Quote(customer,mobile,model.name,variant.name,ex,rto,ins,fast,acc,warr,add,amount(consumer),amount(booking),amount(exchange),amount(additional),amount(smart),amount(upgrade),amount(scrap),amount(corporate),total,rm,phone))},modifier=Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(13.dp)){Text("Generate Professional PDF",fontWeight=FontWeight.Bold)}
   OutlinedButton(onClick=onBack,modifier=Modifier.fillMaxWidth()){Text("Back to models")}
   Spacer(Modifier.height(18.dp))
  }
 }
}

@Composable private fun Panel(title:String,content:@Composable ColumnScope.()->Unit){
 Card(shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=Color.White),elevation=CardDefaults.cardElevation(1.dp)){
  Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
   Text(title,color=Blue,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.sp);content()
  }
 }
}
@Composable private fun Field(label:String,value:String,onChange:(String)->Unit,numeric:Boolean){
 OutlinedTextField(value,onChange,label={Text(label,fontSize=12.sp)},modifier=Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(10.dp),keyboardOptions=KeyboardOptions(keyboardType=if(numeric)KeyboardType.Number else KeyboardType.Text))
}
@Composable private fun PriceEntry(label:String,value:String,onChange:(String)->Unit){
 OutlinedTextField(value,onChange,label={Text(label,fontSize=11.sp)},modifier=Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(10.dp),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))
}
@Composable private fun FeatureChip(label:String){
 Surface(color=Color(0xFFEAF4FA),shape=RoundedCornerShape(30.dp)){Text("✓  $label",modifier=Modifier.padding(horizontal=11.dp,vertical=7.dp),color=Deep,fontSize=11.sp,fontWeight=FontWeight.Medium)}
}
@Composable private fun PriceRow(label:String,value:String,color:Color){
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
  Text(label,color=Color(0xFF667085),fontSize=12.sp,modifier=Modifier.weight(1f));Text(value,color=color,fontSize=12.sp,fontWeight=FontWeight.SemiBold)
 }
}
