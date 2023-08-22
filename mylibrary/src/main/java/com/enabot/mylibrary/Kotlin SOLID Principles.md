## Kotlin SOLID Principles
![](https://miro.medium.com/v2/resize:fit:720/format:webp/1*GU5Gn_9OaoG5_gVxLtxrKA.png)
> 😮Many Kotlin developers do not have full knowledge of SOLID principles, and even if they know, they are not aware of why it is used. Are you ready to learn all the details?

The SOLID principles are as follows:

- 1.Single Responsibility Principle
- 2.Open/Closed Principle
- 3.Liskov Substitution Principle
- 4.Interface Segregation Principle
- 5.Dependency Inversion Principle


### 1.Single Responsibility Principle

The principle of Single Responsibility (SRP) is a part of SOLID
programming principles in object-oriented programming. It signifies that
a particular class should have only one purpose to change. It means that
a class should possess only one responsibility or a job. SRP is useful
in maintaining classes and functions by keeping them organized and easy
to comprehend. When a class has multiple responsibilities, these can
unintentionally affect other tasks or jobs of that class, resulting in
unexpected behavior, bugs, and increased maintenance costs.

**Violation:**

```kotlin
// Single Responsibility Principle Violation
// In this example the System class is trying to handle many different situation at the same time. 
// This approach can cause major problems in the future.
class SystemManager {
    fun addUser(user: User) { }
    fun deleteUser(user: User) { }
    fun sendNotification(notification:String) {}
    fun sendEmail(user: User, email: String) {}
}
```

**Correct Usage:**

```kotlin
// Single Responsibility Principle Correct Usage:
// As seen in this example, we divided our System class into specific parts
// And placed the functions in their respective classes.

class MailManager() {
    fun sendEmail(user: User, email: String) {}
}

class NotificationManager() {
    fun sendNotification(notification: String) {}
}

class UserManager {
    fun addUser(user: User) {}
    fun deleteUser(user: User) {}
}
```
### 2.Open/Closed Principle

The Open/Closed Principle is a rule in object-oriented design that says classes, 
modules, functions, and other software entities should be open for extension but 
closed for modification. This means you can add new things to 
a class without changing its original code. So instead of changing the class itself, 
you can write new code that uses the existing class to add new features. 
Doing this makes the code easier to maintain and reuse.

**Violation:**
```kotlin
// Open/Closed Principle Violation
// In this example, when we try to add something new to our class,
// we have to rewrite our existing code, which can cause problems later on.
class Shape(val type: String, val width: Double, val height: Double)

fun calculateArea(shape: Shape): Double {
    if (shape.type == "rectangle") {
        return shape.width * shape.height
    } else if (shape.type == "circle") {
        return Math.PI * shape.width * shape.width
    }
    return 0.0
}
```
In this example, when we try to add something new to our class, we have to rewrite our existing code, which can cause problems later on.

**Correct Usage:**
```kotlin
// Open/Closed Principle Correct Usage
// As in correct usage, instead of changing the class itself,
// we wrote new classes using our existing class 
// and implemented our functions under new classes.

interface Shape {
    fun area(): Double
}

class Rectangle(val width: Double, val height: Double) : Shape {
    override fun area() = width * height
}

class Circle(val radius: Double) : Shape {
    override fun area() = Math.PI * radius * radius
}

fun calculateArea(shape: Shape) = shape.area()
```
As in correct usage, instead of changing the class itself, we wrote new classes using our existing class and implemented our functions under new classes.

### 3.Liskov Substitution Principle

The Liskov Substitution Principle is an important rule in object-oriented programming. 
It says that if you have a program that works with a certain type of object, 
you should be able to use any subtype of that object without any problems. 
This means that all the methods and properties in the main class should also work for 
all the sub-classes without needing to change anything.

**Violation:**
```kotlin
// Liskov Substitution Principle Violation:
// As we saw in this example, the method we wrote in our main class should work properly in 
// its subclasses according to the Liskov principle, 
// but when our subclass inherited from our superclass, our fly method did not work as expected.

open class Bird {
    open fun fly() {}
}

class Penguin : Bird() {
    override fun fly() {
        print("Penguins can't fly!")
    }
}
```
**Correct Usage:**
```kotlin
// Liskov Substitution Principle Correct Usage
// As you can see in this example, all the things we write in the superclass will be valid in the subclasses, 
// because we have implemented the method that is not valid for subclasses by creating an interface and 
// implementing it where we need it.

open class Bird {
    // common bird methods and properties
}

interface IFlyingBird {
    fun fly(): Boolean
}

class Penguin : Bird() {
    // methods and properties specific to penguins
}

class Eagle : Bird(), IFlyingBird {
    override fun fly(): Boolean {
        return true
    }
}
```
### 4.Interface Segregation Principle
The Interface Segregation Principle is a rule for making computer programs. 
It says that when we make different parts of a program, we shouldn’t make them all the same way. 
Instead, we should make them smaller and more specific, so that other parts of the program 
don’t have to depend on things they don’t need. This helps us make code that’s easier to change and 
take care of, because each part only does what it needs to do.

**Violation:**
```kotlin
// Interface Segregation Principle Violation
// When we look at our example, we see that the interface we created contains many methods.
// If we do everything inside a common interface, we may have made unnecessary use in the places that 
// implement our interface.
// Instead, we can divide our system into smaller interface parts.

interface Animal {
    fun swim()
    fun fly()
}

class Duck : Animal {
    override fun swim() {
        println("Duck swimming")
    }

    override fun fly() {
        println("Duck flying")
    }
}

class Penguin : Animal {
    override fun swim() {
        println("Penguin swimming")
    }

    override fun fly() {
        throw UnsupportedOperationException("Penguin cannot fly")
    }
}
```
**Correct Usage:**
```kotlin
// Interface Segregation Principle Correct Usage
// As we saw in the correct usage example, dividing the system into smaller interfaces and 
// using them where we needed them made it much easier to change the system in the future.

interface CanSwim {
    fun swim()
}

interface CanFly {
    fun fly()
}

class Duck : CanSwim, CanFly {
    override fun swim() {
        println("Duck swimming")
    }

    override fun fly() {
        println("Duck flying")
    }
}

class Penguin : CanSwim {
    override fun swim() {
        println("Penguin swimming")
    }
}
```
### 5.Dependency Inversion Principle
The Dependency Inversion Principle is a SOLID principle that states that high-level modules should not 
depend on low-level modules, but both should depend on abstractions. 
This means that classes should depend on abstractions, not on concrete implementations. 
The idea behind DIP is to decouple components from each other, which makes the code more modular, 
easier to test, and more maintainable.

**Violation:**
```kotlin
// Dependency Inversion Principle Violation
// As we can see in this example, each of our payment methods is processed separately in our Service class 
// in a hard code way.
// Instead of a hard code implementation, the system needed to be DEPEND to an abstract structure.

class PaymentService {
    private val paymentProcessorPaypal = PaypalPaymentProcessor()
    private val paymentProcessorStripe = StripePaymentProcessor()

    fun processPaymentWithPaypal(amount: Double): Boolean {
        return paymentProcessorPaypal.processPayment(amount)
    }

    fun processPaymentWithStripe(amount: Double): Boolean {
        return paymentProcessorStripe.processPayment(amount)
    }
}

class PaypalPaymentProcessor {
    fun processPayment(amount: Double): Boolean {
        // Process payment via Paypal API
        return true
    }
}

class StripePaymentProcessor {
    fun processPayment(amount: Double): Boolean {
        // Process payment via Stripe API
        return true
    }
}


fun main() {
    val paymentService = PaymentService()
    println(paymentService.processPaymentWithPaypal(50.0)) // Process payment via Paypal API
    println(paymentService.processPaymentWithStripe(50.0)) // Process payment via Stripe API
}
```
**Correct Usage:**
```kotlin
// Dependency Inversion Principle Correct Usage
// In the correct usage example, we did not have to implement hard code about our payment methods in our Service class,
// because we set up an abstract structure with the interface that we created.

interface PaymentProcessor {
    fun processPayment(amount: Double): Boolean
}

class PaypalPaymentProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): Boolean {
        // Process payment via Paypal API
        return true
    }
}

class StripePaymentProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): Boolean {
        // Process payment via Stripe API
        return true
    }
}

class PaymentService(private val paymentProcessor: PaymentProcessor) {
    fun processPayment(amount: Double): Boolean {
        return paymentProcessor.processPayment(amount)
    }
}

fun main() {
    val paymentProcessor = PaypalPaymentProcessor()
    val paymentService = PaymentService(paymentProcessor)
    println(paymentService.processPayment(50.0)) // Process payment via Paypal API
}
```
**Conclusion**
As a result, the SOLID principles are essential for creating maintainable, scalable, 
and efficient software in Kotlin. Leveraging Kotlin’s unique features and constructs, 
developers can design modular, loosely coupled systems that adhere to these guidelines.
Adhering to the SOLID principles not only improves code testability but also encourages 
a culture of continuous improvement and best practices. Ultimately, 
employing these principles in Kotlin development results in higher-quality software that 
can be effectively maintained and adapted to evolving requirements.

![](https://miro.medium.com/v2/resize:fit:720/0*3GwTQzOYpl0Slslp)
