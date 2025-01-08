# Module alchemist

Alchemist allows you to manage physical quanities defined in the [International System of Units](https://en.wikipedia.org/wiki/International_System_of_Units). 
Like [Duration](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-duration/), alchemist models its quanities as a value class with a single underlying `Long` value. 

### Type Safety

Alchemist's main goal is to provide type safety for arithmetic on physical quanities. There's no need to pass loosely 
typed `Long`, `Int`, or `Double` values around anymore! 

```kt
val energy1 = 10.wattHours
val energy2 = 10.millijoules
println(energy1 + energy2) // OK: both are type Energy.

val power = 10.watts
println(energy1 + power) // Compiler Error! 
```

### Scalar Arithmetic 

Physical quantities expose scalar arithmetic like the following:

```kt
val power = 10.watts
println(power / 2) // "5W"
println(power * 100) // "1kW"
println(-power) // "-10W"
```

and each quantity exposes typed arithmetic: 

```kt
val first = 10.wattHours
val second = 2.wattHours
println(first + second) // "12Wh"
println(first - second) // "8Wh"
println(second - first) // "-8Wh"
println(first / second) // "5.0"
```

### Physical Arithmetic 

Physical quantities expose valid physical arithmetic defined in the International System of Units. For example: 

```kt
val energy = 10.wattHours
val power = 10.watts
val duration: Duration = energy / power
println(duration) // "1h"

val length = 10.nanometers
val force: Force = energy / length
println(force) // "3.6TN"

val velocity = length / 1.seconds
println(velocity) // 10 nm/s

val acceleration = velocity / 1.seconds
println(acceleration) // 10 nm/s²
```

### Integration with the Kotlin Standard Library 

Kotlin provides a measure of time, called [`Duration`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-duration/), 
in its standard library. Alchemist integrates with Duration for physical arithmetic rather than supplying its own 
implementation of time. 

```kt
val time: kotlin.time.Duration = 1.hours
val energy = time * 1.watts
println(energy) // "1Wh"
println(energy / time) // "1W"

val velocity = 1.meters / time
println(velocity) // "1 m/s"
println(velocity * time) // "1m"

val acceleration = velocity / time
println(velocity) // "1 m/s²
println(acceleration * time) // "1 m/s"
```

### Custom Quantity Unit Implementation 

Every unit alchemist exposes is an interface, thus allowing you to implement non-standard or uncommonly used units: 

```kt
// Alchemist does not provide horsepower out of the box. 
object HorsePower : PowerUnit {
    override val symbol: String get() = "hp"
    override val microwattScale: Long get() = 745_699_871
}

val Int.horsepower: Power get() = this.toPower(HorsePower)

println(1.horsepower) // "745.7W"
```

Note that alchemist does not provide certain units out of the box because they're impossible to represent without losing 
precision. In the above example, 1 horsepower is actually 745,699,871.58μW.

### Infinity 

While Long values can store huge numbers, sometimes they're not enough for the operations you're attempting to 
perform. Rather than silently over or underflowing during an arithmetic operation, Alchemist will clamp your quantities 
to a special infinite value. These special infinite values have some restrictions. 

```kt
// This would overflow. 
val infiniteEnergy = 100.joules * Long.MAX_VALUE 
println(infiniteEnergy) // "Infinity"

// Infinite values don't behave like other values. 
println(infiniteEnergy / Long.MAX_VALUE) // "Infinity"
println(infiniteEnergy * 10) // "Infinity"
println(-infiniteEnergy) // "-Infinity"

// Some operations are invalid with infinite values. 
println(infiniteEnergy / infiniteEnergy) // Oops! This throws IllegalArgumentException. 
println(infiniteEnergy * -infiniteEnergy) // This throws, too. 
```

### We're alchemists, not physicists! 

Alchemist is not built with physics engines in mind. Instead, Alchemist aims to provide type safety and reasonable levels of precision 
that are sufficient for most business use cases. For example, Alchemist aims to help you model how much energy an entire country used 
for a year with a reasonable level of precision, but it won't help you model how much energy the Sun produces in a day. 

Additionally, Alchemist values type safety over modeling arbitrary quantities derived from SI base units. 
Introducing that functionality involves too many compromises and would be better handled by a different library with different goals.

If you need to model things like how much energy your air conditioning used in a year, or how far your compact electric vehicle charged 
with the electricity from your solar panels can drive, great! Alchemist might be a good fit for you. We hope you like it. 
