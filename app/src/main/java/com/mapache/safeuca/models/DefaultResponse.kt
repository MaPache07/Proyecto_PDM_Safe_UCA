package com.mapache.safeuca.models

data class DefaultResponse(
    val error : Message
)

data class Message(
    val message : String
)