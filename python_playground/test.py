import asyncio

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



def f(x=[]):
    x.append(1)
    return x

print(f(["smile"]))

a = [1]
b = [1]

print(a == b)  # True
print(a is b)  # False

print([x for x in range(1, 10)])
range_ = (x for x in range(1, 10))
print(next(range_))
print(next(range_))
print(next(range_))

async def calculate1():
    await asyncio.sleep(3)
    print("after wait")

calculate1()

print("something to print")

def multiplyNTimes(n):
    def multiply(x):
        return x * n
    return multiply

times2 = multiplyNTimes(2)
times3 = multiplyNTimes(3)

print(times3(15))