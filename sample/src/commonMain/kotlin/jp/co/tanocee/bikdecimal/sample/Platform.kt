package jp.co.tanocee.bikdecimal.sample

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform
