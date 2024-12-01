package com.ezgiyilmaz.sporfinder.models

data class FilterCriteria(
    val category: String?,
    val lookingFor: String?,
    val date: String?,
    val time: String?,
    val city: String?,
    val township: String?
)
