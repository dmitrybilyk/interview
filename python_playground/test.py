a = 20
b = 5

print(a ** b)

# someInput = input()
# print(someInput)

list = ["one", "two"]
print(list[::-1])

numbers = [1, 2, 3, 4]
new_numbers = [i**2 for i in numbers if i > 3]
print(new_numbers)

def someGenerator():
    c = 0
    while(True):
        yield c
        c += 1


generator = someGenerator()
print(next(generator))
print(next(generator))
print(next(generator))
print(next(generator))
