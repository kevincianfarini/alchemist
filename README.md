# Alchemist

Manage physical units. Inspired by kotlin.time.Duration. Alchemist allow type safe arithmetic between different 
physical quantities defined in the [International System of Units](https://en.wikipedia.org/wiki/International_System_of_Units#).

```kt
val time: Duration = 10.seconds
val length: Length = 10.kilometers
val velocity: Velocity = length / time
val acceleration: Acceleration = velocity / time
val mass: Mass = 10.kilograms
val force: Force = acceleration * mass
val energy: Energy = force * length 
val power: Power = energy / time
val area: Area = length * length 
val volume: Volume = length * length * length 
```

## Alchemist's Goals 

1. Model physical quantities as Kotlin value classes which wrap a single `Long` value. 
2. Provide logical arithmetic between different physical quantities, like `power = energy / time`. 
3. Allow for the implementation of custom units on physical quanities that Alchemist does not provide, such as horsepower as a unit of `Power` or Rankine degrees for `Temperature`. 
4. (In the future) Allow different order of magnitude precision and wider ranges of valid values. 
5. Easy extensibility for custom formulas, such as `energyₖ = ½ * mass * velocity²`.

## Alchemist's Non-Goals

1. Using generic or floating point values as the underlying storage mechanism. 
2. Representing arbitrary formulaic expressions. 
3. Providing as many formulaic conversions as possible out of the box, such as `energyₖ = ½ * mass * velocity²`.
4. Infinitely precise values. 
5. Infinitely large ranges of valid values. 

## Platform Support 

| Platform             | Tested in CI |
|----------------------|--------------|
| androidNativeArm32   | ❌            |
| androidNativeArm64   | ❌            |
| androidNativeX64     | ❌            |
| androidNativeX86     | ❌            |
| iosArm64             | ❌            |
| iosSimulatorArm64    | ✅            |
| iosX64               | ✅            |
| js                   | ✅            |
| jvm                  | ✅            |
| linuxArm64           | ❌            |
| linuxX64             | ✅            |
| macosArm64           | ✅            |
| macosX64             | ✅            |
| mingwX64             | ✅            |
| tvosArm64            | ❌            |
| tvosSimulatorArm64   | ✅            |
| tvosX64              | ✅            |
| wasmJs               | ✅            |
| wasmWasi             | ❌            |
| watchosArm32         | ❌            |
| watchosArm64         | ❌            |
| watchosDeviceArm64   | ❌            |
| watchosSimuatorArm64 | ✅            |
| watchosX64           | ✅            |
