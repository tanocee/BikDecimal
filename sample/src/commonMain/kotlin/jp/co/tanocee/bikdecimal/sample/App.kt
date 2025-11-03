package jp.co.tanocee.bikdecimal.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import jp.co.tanocee.bikdecimal.BikDecimal
import jp.co.tanocee.bikdecimal.sumOf
import jp.co.tanocee.bikdecimal.toBikDecimal
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  MaterialTheme {
    Column(
      modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .safeContentPadding()
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = "BikDecimal Sample",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
      )

      // Basic arithmetic operations
      SampleCard(title = "Basic Arithmetic Operations") {
        val a = BikDecimal("123.45")
        val b = BikDecimal("67.89")

        ExampleRow("Addition", "123.45 + 67.89", (a + b).toPlainString())
        ExampleRow("Subtraction", "123.45 - 67.89", (a - b).toPlainString())
        ExampleRow("Multiplication", "123.45 × 67.89", (a * b).toPlainString())
        ExampleRow("Division", "123.45 ÷ 67.89", (a / b).toPlainString())
      }

      // Constructor variations
      SampleCard(title = "Constructors") {
        ExampleRow("From String", "\"999.99\"", BikDecimal("999.99").toPlainString())
        ExampleRow("From Double", "3.14159", BikDecimal(3.14159).toPlainString())
        ExampleRow("From Long", "42L", BikDecimal(42L).toPlainString())
      }

      // Constants
      SampleCard(title = "Constants") {
        ExampleRow("ZERO", "BikDecimal.ZERO", BikDecimal.ZERO.toPlainString())
        ExampleRow("ONE", "BikDecimal.ONE", BikDecimal.ONE.toPlainString())
      }

      // Comparison
      SampleCard(title = "Comparison") {
        val x = BikDecimal("100")
        val y = BikDecimal("200")
        val z = BikDecimal("100")

        ExampleRow("100 vs 200", "compareTo", x.compareTo(y).toString())
        ExampleRow("200 vs 100", "compareTo", y.compareTo(x).toString())
        ExampleRow("100 vs 100", "compareTo", x.compareTo(z).toString())
      }

      // Negative
      SampleCard(title = "Negative") {
        val value = BikDecimal("42.5")
        ExampleRow("Original", "42.5", value.toPlainString())
        ExampleRow("Negative", "-42.5", value.negative().toPlainString())
      }

      // Conversion
      SampleCard(title = "Conversions") {
        val value = BikDecimal("123.456")
        ExampleRow("toPlainString", "123.456", value.toPlainString())
        ExampleRow("toDouble", "123.456", value.toDouble().toString())
        ExampleRow("toLong", "123.456", value.toLong().toString())
      }

      // String extension
      SampleCard(title = "String Extension") {
        ExampleRow("Valid", "\"100.5\".toBikDecimal()", "100.5".toBikDecimal().toPlainString())
        ExampleRow(
          "Invalid",
          "\"invalid\".toBikDecimal()",
          "invalid".toBikDecimal().toPlainString()
        )
      }

      // sumOf example
      SampleCard(title = "Collection Operations") {
        data class Product(val name: String, val price: String)

        val products = listOf(
          Product("Apple", "1.20"),
          Product("Banana", "0.80"),
          Product("Orange", "1.50")
        )

        val total = products.sumOf { it.price.toBikDecimal() }

        Column {
          products.forEach { product ->
            ExampleRow(product.name, "", product.price)
          }
          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
          ExampleRow("Total", "sumOf", total.toPlainString())
        }
      }
    }
  }
}

@Composable
fun SampleCard(
  title: String,
  content: @Composable () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(4.dp))
      content()
    }
  }
}

@Composable
fun ExampleRow(
  label: String,
  operation: String,
  result: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
      )
      if (operation.isNotEmpty()) {
        Text(
          text = operation,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    Text(
      text = result,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.secondary
    )
  }
}
