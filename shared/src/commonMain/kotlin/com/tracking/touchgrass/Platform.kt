package com.tracking.touchgrass

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform