package jp.co.tanocee.bikdecimal

class Greeting {
  private val platform = getPlatform()

  fun greet(): String {
    return "Hello, ${platform.name}!"
  }
}
