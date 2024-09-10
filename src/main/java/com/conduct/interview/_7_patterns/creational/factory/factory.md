There could be creational method - just some method to create object in 
specific way.
Also could be static creational method - replacement for constructor like
in Singleton pattern.

Simple factory is just some static method which returns the
subclass object based on input parameter. 

Factory Method is a method of some Creator class children of which
(concrete creators) create some new object of some class hierarchi.
Like Shape>Square,Circle. It allows to create object and not even
knowing in the client which object type was created.

Abstract factory is used to create families of objects. Like 
create set of furniture (modern, classic etc.)