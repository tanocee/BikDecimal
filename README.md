# BikDecimal

A Kotlin Multiplatform library providing arbitrary-precision decimal arithmetic across Android and iOS platforms.

## Features

- **Multiplatform Support**: Works seamlessly on Android and iOS
- **Arbitrary Precision**: Handle decimal numbers with high precision without floating-point errors
- **Easy to Use**: Kotlin operator overloading for natural arithmetic operations
- **Type Safe**: Strongly typed decimal numbers with platform-native implementations

## Supported Platforms

- Android (using `java.math.BigDecimal`)
- iOS (using `NSDecimalNumber`)

## Installation

Add the dependency to your `commonMain` source set:

```kotlin
kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation("jp.co.tanocee:bikdecimal:1.0.0")
    }
  }
}
```

## Usage

### Basic Arithmetic Operations

```kotlin
import jp.co.tanocee.bikdecimal.BikDecimal

val a = BikDecimal("123.45")
val b = BikDecimal("67.89")

val sum = a + b           // 191.34
val difference = a - b    // 55.56
val product = a * b       // 8382.2205
val quotient = a / b      // 1.818...
```

### Constructors

```kotlin
// From String
val fromString = BikDecimal("999.99")

// From Double
val fromDouble = BikDecimal(3.14159)

// From Long
val fromLong = BikDecimal(42L)
```

### Constants

```kotlin
val zero = BikDecimal.ZERO  // 0
val one = BikDecimal.ONE    // 1
```

### Comparison

```kotlin
val x = BikDecimal("100")
val y = BikDecimal("200")

when {
  x < y -> println("x is less than y")
  x > y -> println("x is greater than y")
  x == y -> println("x equals y")
}

// Or use compareTo directly
x.compareTo(y)  // Returns negative, zero, or positive
```

### Conversions

```kotlin
val value = BikDecimal("123.456")

val asString = value.toPlainString()  // "123.456"
val asDouble = value.toDouble()       // 123.456
val asLong = value.toLong()           // 123
```

### Extension Functions

```kotlin
// String to BikDecimal with default value
val valid = "100.5".toBikDecimal()        // BikDecimal("100.5")
val invalid = "invalid".toBikDecimal()    // BikDecimal.ZERO (default)
val custom = "invalid".toBikDecimal(BikDecimal.ONE)  // BikDecimal.ONE

// Sum of collection
data class Product(val name: String, val price: String)
val products = listOf(
  Product("Apple", "1.20"),
  Product("Banana", "0.80"),
  Product("Orange", "1.50")
)

val total = products.sumOf { it.price.toBikDecimal() }  // 3.50
```

### Negative Values

```kotlin
val value = BikDecimal("42.5")
val negated = value.negative()  // -42.5
```

## Sample Application

The [sample](./sample) module contains a Compose Multiplatform application demonstrating all features of BikDecimal. You can run it to see interactive examples of:

- Basic arithmetic operations
- Different constructor types
- Comparison operations
- Conversion methods
- Collection operations with `sumOf`

### Running the Sample App

**Android:**
```shell
./gradlew :sample:assembleDebug
```

**iOS:**
Open the project in Xcode and run the `sample` target.

## Project Structure

- [bikdecimal-core](./bikdecimal-core) - The core library with multiplatform implementation
  - `commonMain` - Common API and extension functions
  - `androidMain` - Android implementation using `java.math.BigDecimal`
  - `iosMain` - iOS implementation using `NSDecimalNumber`
- [sample](./sample) - Sample application demonstrating library usage

## Building

To build the library:

```shell
./gradlew :bikdecimal-core:build
```

To build the entire project including the sample app:

```shell
./gradlew build
```

## License

[LICENSE](./LICENSE)
