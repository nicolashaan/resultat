//
// Resultat library sample swift code
// Copyright 2022 Nicolas Haan.
//

import Foundation
import Resultat



/**
 This class doesn't work as intented but is kept to experiment around Kotlin / Swift type interoperability
 */
class CustomerGreeterWithSealedClass {
    let repository: CustomerRepository
    
    init(repository: CustomerRepository) {
        self.repository = repository
    }
    
    func welcomeCustomer(id: Int) {
        repository.getCustomerById(id: id, callback: { resultat in
            

            switch resultat {
                // This cast cannot succeed, this is a limitation of Kotlin interop with generics
                // and the Nothing type used to define Resultat.Failure
            case let failure as ResultatFailure:
                print("Error: \(failure.exception.message)")
                // Same reason, this can never succeed
                
            case is ResultatLoading:
                print("Loading customer with ID: \(id)...")
                
            case let success as ResultatSuccess<Customer>:
                let customer = success.getOrThrow() as! Customer
                print("Hello,\(customer.firstName) \(customer.lastName) !")
            default:
                print("Unexpected Resultat subclass")
            }
        })
    }
}




func brokenMain() {
    let repository = CustomerRepository()
    let greeter2 = CustomerGreeterWithSealedClass(repository: repository)
    greeter2.welcomeCustomer(id: 1)
    greeter2.welcomeCustomer(id: 2)
    greeter2.welcomeCustomer(id: 5)
}
