<div align="left">

  <picture>
    <img width="600" alt="Résultat logo" src="images/resultat-banner.png">
  </picture>

</div>

## What is Résultat?

Résultat is a fork of Kotlin [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/) with a loading
state.

## Why?

Because sometimes you start a project based on Kotlin Result and you realise you need a loading state. This library can
be used as a drop-in replacement, with the same API and convenient conversion utility functions.

## Integration

### Manual integration

The recommended way to integrate it is to copy the [Resultat.kt](resultat/src/commonMain/kotlin/Resultat.kt) into you
project,
because it doesn't depend on any other library and you shouldn't add a dependency for such a small and critical piece of
software.
It also allows to rename it to whatever you want including *Result*, to ease migration and usage.

### Using Gradle

```kotlin
implementation("fr.haan.resultat:resultat:1.0.0-rc1")
```

## Usage

The usage of this class is the same as kotlin Result. You may take a look
at [this article](https://medium.com/@jcamilorada/arrow-try-is-dead-long-live-kotlin-result-5b086892a71e)
if you are not already familiar with it.
*Resultat* keep the exact same behaviour, and add similar helper and *side effect* methods to handle the
loading state.
For instance:

```kotlin
val resulat: Resultat<Customer>
resultat
    .onFailure { exception ->
        println("Error: ${exception.message}")
    }
    .onLoading { println("Loading customer with ID: $id...") }
    .onSuccess { customer ->
        println("Hello, ${customer.firstName} ${customer.lastName} !")
    }
```

Similarly the [fold](https://nicolashaan.github.io/resultat/resultat/fr.haan.resultat/fold.html) method have a third `loading`
parameter:

```kotlin
val resulat: Resultat<Customer>
resultat.fold(
    onSuccess = {
        Resultat.success(it)
    },
    onLoading = {
        Resultat.success(defaultCustomer)
    },
    onFailure = {
        Resultat.success(defaultCustomer)
    },
)
```

### Interoperability with Kotlin Result
Convenient methods are provided to convert a [Kotlin Result](https://nicolashaan.github.io/resultat/resultat/fr.haan.resultat/to-result.html) to a Resultat and 
[vice versa](https://nicolashaan.github.io/resultat/resultat/fr.haan.resultat/to-resultat.html).
```kotlin
val resultat: Resultat<String> = Resultat.success("Hello")
val result: Result<String> = resultat.toResult()
// Notice the nullable type because loading state is mapped to null
val newResultat: Resultat<String>? = result.ToResultat()
```


## API Reference

See [API documentation](https://nicolashaan.github.io/resultat/resultat/fr.haan.resultat/-resultat/index.html)


Made with ❤️ at [![BAM.tech](images/bam-logo.svg)](https://www.bam.tech)