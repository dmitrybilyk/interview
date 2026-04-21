class User:
    def __init__(self, name):
        self.name = name

user = User("Dmytro")
print(user.name)

class Animal:
    def __init__(self, _name):
        self.name = _name

    def make_sound(self):
        print(f"{self.name} is making sound")

class Dog(Animal):
    def make_sound(self):
        print(f"Dog {self.name} is barking")

dog = Dog("Linda")
dog.make_sound()