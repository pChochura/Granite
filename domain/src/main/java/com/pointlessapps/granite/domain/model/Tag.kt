package com.pointlessapps.granite.domain.model

data class Tag(
    val id: Int,
    val name: String,
    val color: Int,
    val isBuiltIn: Boolean,
)
