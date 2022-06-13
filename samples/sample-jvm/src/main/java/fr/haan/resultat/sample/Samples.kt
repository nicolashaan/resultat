package fr.haan.resultat.sample

import fr.haan.resultat.*
import kotlin.concurrent.thread

data class Customer(
    val firstName: String,
    val lastName: String,
)

open class CustomerRepository {
    private val customers = hashMapOf<Int, Customer>(
        1 to Customer("Erich", "Gamma"),
        2 to Customer("Richard", "Heim"),
        3 to Customer("Ralph", "Johnson"),
        4 to Customer("John", "Vlissides"),
    )

    open fun getCustomerById(id: Int, callback: (Resultat<Customer>) -> Unit) {
        thread {
            callback(Resultat.loading())
            // Simulate long running loading
            Thread.sleep(1000)
            val customer = customers.get(id)
            if (customer != null) {
                callback(Resultat.success(customer))
            } else {
                callback(
                    Resultat.failure(IllegalArgumentException("Unknown customer with ID: $id"))
                )
            }
        }
    }
}

class CustomerGreeterWithSealedClass(private val repository: CustomerRepository) {

    fun welcomeCustomer(id: Int) {
        repository.getCustomerById(id) { resultat ->
            when (resultat) {
                is Resultat.Failure -> println("Error: ${resultat.exception.message}")
                is Resultat.Loading -> println("Loading customer with ID: $id...")
                is Resultat.Success -> {
                    val customer = resultat.value
                    println("Hello, ${customer.firstName} ${customer.lastName} !")
                }
            }
        }
    }
}

class CustomerGreeterWithSideEffectFunctions(private val repository: CustomerRepository) {

    fun welcomeCustomer(id: Int) {
        repository.getCustomerById(id) { resultat ->
            resultat
                .onFailure { exception ->
                    println("Error: ${exception.message}")
                }
                .onLoading { println("Loading customer with ID: $id...") }
                .onSuccess { customer ->
                    println("Hello, ${customer.firstName} ${customer.lastName} !")
                }
        }
    }
}

class CustomerRepositoryWithFold() : CustomerRepository() {
    override fun getCustomerById(id: Int, callback: (Resultat<Customer>) -> Unit) {
        val defaultCustomer = Customer(
            firstName = "John",
            lastName = "Doe",
        )
        val noErrorCallback: (Resultat<Customer>) -> Unit = { resultat ->
            callback(
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
            )
        }
        super.getCustomerById(id, noErrorCallback)
    }

}

class CustomerRepositoryWithMap() : CustomerRepository() {
    override fun getCustomerById(id: Int, callback: (Resultat<Customer>) -> Unit) {
        val decoratedCallback: (Resultat<Customer>) -> Unit = { resultat ->
            callback(resultat.map { customer -> customer.copy(firstName = "Sir ${customer.firstName}") })
        }
        super.getCustomerById(id, decoratedCallback)
    }
}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val customerRepository = CustomerRepository()
        run {
            val greeter = CustomerGreeterWithSealedClass(customerRepository)
            greeter.welcomeCustomer(1)
            greeter.welcomeCustomer(2)
            greeter.welcomeCustomer(5)
        }
        run {
            val greeter = CustomerGreeterWithSideEffectFunctions(customerRepository)
            greeter.welcomeCustomer(1)
            greeter.welcomeCustomer(2)
            greeter.welcomeCustomer(5)
        }
        run {
            val greeter = CustomerGreeterWithSideEffectFunctions(CustomerRepositoryWithMap())
            greeter.welcomeCustomer(1)
            greeter.welcomeCustomer(2)
            greeter.welcomeCustomer(5)
        }
        run {
            val greeter = CustomerGreeterWithSideEffectFunctions(CustomerRepositoryWithFold())
            greeter.welcomeCustomer(1)
            greeter.welcomeCustomer(2)
            greeter.welcomeCustomer(5)
        }
    }
}