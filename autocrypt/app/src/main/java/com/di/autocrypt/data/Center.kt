package com.di.autocrypt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "centers")
data class Center(
    @PrimaryKey @ColumnInfo(name = "id")
    @SerializedName("id") @Expose
    val id:Int,
    @SerializedName("sido") @Expose
    val sido:String,
    @SerializedName("sigungu") @Expose
    val sigungu:String,
    @SerializedName("centerName") @Expose
    val centerName:String,
    @SerializedName("facilityName") @Expose
    val facilityName:String,
    @SerializedName("zipCode") @Expose
    val zipCode:String,
    @SerializedName("address") @Expose
    val address:String,
    @SerializedName("lat") @Expose
    val lat:String,
    @SerializedName("lng") @Expose
    val lng:String,
    @SerializedName("createdAt") @Expose
    val createdAt:String,
    @SerializedName("updatedAt") @Expose
    val updatedAt:String,
    @SerializedName("centerType") @Expose
    val centerType:String,
    @SerializedName("org") @Expose
    val org:String,
    @SerializedName("phoneNumber") @Expose
    val phoneNumber:String,
)
