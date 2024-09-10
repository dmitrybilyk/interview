S ingle responsibility - every class should have just single reason to be changed. It's easier to change, 
test and read such a class. High cohesion should be achieved when module has all related actions 
grouped in one place, not spread through the module.
Note: Loose coupling correlates with high cohesion as tightly couple classes lead to the need to 
make changes in many places when you make changes in one.
- Class `Book` and Class `PrintSomething` (from the book) should be separate.
We can re-use `PrintSomething` btw

O pen/Closed principle - open for extension and Closed for modification
- Instead of modifying existing class we may extend it and change whatever we need.
Adding of new features should be done by adding new code, not by editing existing.

L iskov Substitution principle - We should be able to use the derived class anywhere 
we can use it's parent.

I nterface Segregation - Should have more interfaces with less responsibility rather than to have single 
with everything to avoid empty implementations.

D ependency inversion principle - Classes should depend on abstractions, 
not on particular implementations. In DIP polymorphism takes part and also DI 
(which helps to implement IoC). With the abstractions it's easier to test, extend, deploy