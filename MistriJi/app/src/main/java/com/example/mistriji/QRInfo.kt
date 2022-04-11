package com.example.mistriji

import java.io.Serializable

data class QRInfo(val name:String, val code:String, val redeem:String, val status:QRStatus=QRStatuses.GENERATED):Serializable

typealias QRStatus=String
object QRStatuses{
    const val GENERATED="GENERATED"
    const val REDEEMED="REDEEMED"
}