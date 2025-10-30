package jp.co.tanocee.bikdecimal

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform
