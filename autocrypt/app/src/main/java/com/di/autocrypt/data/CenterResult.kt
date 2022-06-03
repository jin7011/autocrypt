package com.di.autocrypt.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CenterResult(
    @SerializedName("data") @Expose
    val centerList: List<Center>,
    @SerializedName("page") @Expose
    val page: Int,
    @SerializedName("perPage") @Expose
    val perPage: Int,
    @SerializedName("totalCount") @Expose
    val totalCount: Int,
    @SerializedName("currentCount") @Expose
    val currentCount: Int,
    @SerializedName("matchCount") @Expose
    val matchCount: Int,
)
