package com.apkide.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform