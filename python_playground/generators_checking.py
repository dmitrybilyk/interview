
def generate_value():
    counter = 0
    while True: # Infinite loop!
        yield counter
        counter += 1

gen = generate_value()
print(next(gen)) # 0
print(next(gen)) # 1
print(next(gen)) # 2


def generateValue2():
    for i in range(0, 1000):
        yield i


value_ = generateValue2()
print(next(value_))
print(next(value_))
print(next(value_))