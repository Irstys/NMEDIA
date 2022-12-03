package ru.netology.nmedia.dto

data class Ad(
    override val id: Long,
    val name : String,
) : FeedItem