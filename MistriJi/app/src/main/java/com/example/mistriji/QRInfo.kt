package com.example.mistriji

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class QRInfo(
    val name:String, val code:String, val redeem:String,
    val status:QRStatus=QRStatuses.GENERATED, val date: String =Calendar.getInstance().time.toString()):Serializable

typealias QRStatus=String
object QRStatuses{
    const val GENERATED="GENERATED"
    const val REDEEMED="REDEEMED"
}