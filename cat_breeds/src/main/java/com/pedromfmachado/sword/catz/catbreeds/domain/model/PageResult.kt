package com.pedromfmachado.sword.catz.catbreeds.domain.model

data class PageResult<T>(
    val items: List<T>,
    val hasMorePages: Boolean,
)
