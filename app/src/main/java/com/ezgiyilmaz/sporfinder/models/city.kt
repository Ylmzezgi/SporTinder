package com.ezgiyilmaz.sporfinder.models

import kotlinx.serialization.Serializable
//cities ve city modellerinde verilerin JSON gibi formatlarda serileştirilebilir olmasını sağlar.
@Serializable
data class cities( // ŞEHİR LİSTESİ
    val type: String,
    val version: String?,
    val comment: String?,
    val name: String?,
    val database: String?,
    val data: List<city>?,
)
@Serializable
data class city( // HER BİR ŞEHRİN İDS sini ve İSMİNİ tutar
    val id: String,
    val name: String,
)
