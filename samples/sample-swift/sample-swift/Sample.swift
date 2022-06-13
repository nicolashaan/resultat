//
// Resultat library sample swift code
// Copyright 2022 Nicolas Haan.
//

import Foundation
import Resultat


class Customer {
    let firstName: String
    let lastName: String
    init(firstName: String, lastName: String) {
        self.firstName = firstName
        self.lastName = lastName
    }
}

class CustomerRepository {
    let customers = [
        1 : Customer(firstName: "Erich", lastName: "Gamma"),
        2 : Customer(firstName:"Richard", lastName: "Heim"),
        3 : Customer(firstName:"Ralph", lastName: "Johnson"),
        4 : Customer(firstName:"John",  lastName: "Vlissides"),
    ]
    
    func getCustomerById(id: Int, callback:  @escaping (Resultat<Customer>) -> Void) {
        // Unfortunately cast is required because of Kotlin/Objective-C/Swift interop
        guard let loadingValue = Resultat<Customer>.companion.loading() as? Resultat<Customer> else {
            return
        }
        
        callback(loadingValue)
        // Simulate long running loading
        Thread.sleep(forTimeInterval: 1)
        
        if let customer = self.customers[id] {
            // Unfortunately cast is required because of Kotlin/Objective-C/Swift interop
            guard let value = Resultat<Customer>.companion.success(value: customer) as? Resultat<Customer> else {
                return
            }
            callback(value)
        } else {
            // Unfortunately cast is required because of Kotlin/Objective-C/Swift interop
            guard let value = Resultat<Customer>.companion.failure(exception: KotlinThrowable(message: "Unknown customer with ID: \(id)")) as? Resultat<Customer> else {
                return
            }
            callback(
                value
            )
        }
    }
}

class CustomerGreeterWithSideEffectFunctions {
    let repository: CustomerRepository
    
    init(repository: CustomerRepository) {
        self.repository = repository
    }
    
    func welcomeCustomer(id: Int) {
        repository.getCustomerById(id: id, callback: { resultat in
            resultat.onLoading {
                print("Loading customer with ID: \(id)...")
            }.onSuccess {param in
                // Force cast is needed due to Kotlin interop
                let customer = param as! Customer
                print("Hello, \(customer.firstName) \(customer.lastName) !")
            }.onFailure { error in
                print("Error: \(error)")
            }
        })
    }
}

@main
struct Sample {
    static func main() throws {
        let customerRepository = CustomerRepository()
        let greeter = CustomerGreeterWithSideEffectFunctions(repository: customerRepository)
        greeter.welcomeCustomer(id: 1)
        greeter.welcomeCustomer(id: 2)
        greeter.welcomeCustomer(id: 5)
    }
}
