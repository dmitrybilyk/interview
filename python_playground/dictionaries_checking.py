dicts = {1: "Dmytro", 2: "Andriy"}

print(dicts.get(15))

for k in dicts.keys():
    print(dicts[k])

del dicts[1]


for k in dicts.keys():
    print(dicts[k])

prices = {"apple": 100, "banana": 50}

# Syntax: {key: new_value for key, value in original.items()}
discounted_prices = {k: v * 0.9 for k, v in prices.items()}

print(discounted_prices)
# Output: {'apple': 90.0, 'banana': 45.0}


words = ["apple", "banana", "apricot", "cherry", "avocado", "date"]

wordsDict = {}
for w in words:
    if w.startswith("a"):
        wordsDict[w] = len(w)

for w in wordsDict.keys():
    print(w, wordsDict[w])