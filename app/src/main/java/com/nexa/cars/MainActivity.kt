package com.nexa.cars

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NexaBlue = Color(0xFF0878B9)
private val NexaBlack = Color(0xFF111318)
private val PageWhite = Color(0xFFF7F8FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = NexaBlue)) {
                NexaCarsHome()
            }
        }
    }
}

@Composable
private fun NexaCarsHome() {
    Surface(modifier = Modifier.fillMaxSize(), color = PageWhite) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexaBlack)
                    .padding(horizontal = 22.dp, vertical = 24.dp)
            ) {
                Text("NEXA CARS", color = Color.White, fontSize = 25.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text("NEXA Autonation • Palwal",
                    color = Color(0xFFB8C1CC), fontSize = 13.sp)
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text("Car Quotation", color = NexaBlack, fontSize = 24.sp,
                    fontWeight = FontWeight.Bold)
                Text("Choose a model to prepare a customer quotation.",
                    color = Color(0xFF626B76), fontSize = 14.sp)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("MARUTI SUZUKI", color = NexaBlue, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("MODEL 01", color = Color(0xFF89919B), fontSize = 11.sp)
                        }
                        Text("Baleno", color = NexaBlack, fontSize = 27.sp,
                            fontWeight = FontWeight.Bold)
                        Text("Select a variant and enter the latest approved prices.",
                            color = Color(0xFF626B76), fontSize = 13.sp)
                        Button(onClick = { }, modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)) {
                            Text("Create quotation")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Text("Version 1.0 • Initial project setup",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFF89919B), fontSize = 11.sp)
            }
        }
    }
}
