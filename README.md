# MathEvals

A powerful Kotlin library for evaluating mathematical expressions dynamically at runtime.

## Features

- Parse and evaluate mathematical expressions from strings
- Support for basic arithmetic operations: +, -, *, /, ^, %
- Built-in mathematical functions: sin, cos, tan, log, ln, sqrt, etc.
- Support for variables and constants (pi, e)
- Scientific notation support
- Comprehensive error handling and validation
- Thread-safe evaluation
- Protection against malicious expressions and infinite calculations

## Installation

### Gradle

```kotlin
repositories {
    mavenCentral()
    // Or for GitHub Packages
    maven {
        url = uri("https://maven.pkg.github.com/yourusername/MathEvals")
    }
}

dependencies {
    implementation("fr.ftnl.libs:math-evals:1.0.0")
}
```

### Maven

```xml
<dependency>
    <groupId>fr.ftnl.libs</groupId>
    <artifactId>math-evals</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Basic Usage

```kotlin
import fr.ftnl.libs.mathEval.maths.MathCalculator

fun main() {
    val calculator = MathCalculator()

    // Simple expressions
    val result1 = calculator.calculate("2 + 3 * 4")  // 14.0

    // Expressions with parentheses
    val result2 = calculator.calculate("2 * (3 + 4)")  // 14.0

    // Expressions with functions
    val result3 = calculator.calculate("sin(pi/2)")  // 1.0

    // Expressions with variables
    val variables = mapOf("x" to 3.0, "y" to 4.0)
    val result4 = calculator.calculate("x^2 + y^2", variables)  // 25.0
}
```

### Supported Operations

- Addition: `+`
- Subtraction: `-`
- Multiplication: `*`
- Division: `/`
- Exponentiation: `^` or `**`
- Modulo: `%`

### Supported Functions

- Trigonometric: `sin`, `cos`, `tan`, `asin`, `acos`, `atan`
- Logarithmic: `log` (base 10), `ln` (natural logarithm)
- Other: `sqrt`, `abs`, `exp`, `ceil`, `floor`

### Constants

- `pi`: 3.141592653589793
- `e`: 2.718281828459045

### Error Handling

```kotlin
import fr.ftnl.libs.mathEval.maths.MathCalculator
import fr.ftnl.libs.mathEval.tokenizer.TokenizationException

fun main() {
    val calculator = MathCalculator()

    try {
        calculator.calculate("1/0")
    } catch (e: ArithmeticException) {
        println("Arithmetic error: ${e.message}")
    }

    try {
        calculator.calculate("1 + + 2")
    } catch (e: TokenizationException) {
        println("Syntax error: ${e.message}")
    }
}
```

## Advanced Usage

### Custom Functions

You can register your own custom functions:

```kotlin
val calculator = MathCalculator()

// Register a custom function that doubles its input
calculator.registerFunction("double", { x -> x * 2 })

// Use the custom function in expressions
val result = calculator.calculate("double(5)")  // Returns 10.0

// Register a custom function that converts degrees to radians
calculator.registerFunction("toRadians", { degrees -> degrees * Math.PI / 180 })

// Combine custom and built-in functions
val result2 = calculator.calculate("sin(toRadians(90))")  // Returns 1.0

// Remove a custom function when no longer needed
calculator.removeFunction("double")
```

### Debugging Expressions

You can inspect the tokens generated from an expression:

```kotlin
val tokens = calculator.showTokens("2 * (3 + 4)")
tokens.forEach { println("${it.type}: ${it.value}") }
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
