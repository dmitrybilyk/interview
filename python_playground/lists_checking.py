from typing import List

# This tells the IDE "treat this like List<String>"
names: List[str] = ["Dmytro", "Alisa", 3]

for n in names:
    print(n)

list = ["first", "second", "aa"]

list2 = list[:2:1]

list2.append("newFirst")
list2.append("newFirstToDelete")

del list2[3]

for l2 in list2:
    print(l2)

def printBiggerthan3Reversed(someList):
    for l in [i[::-1] for i in someList if len(i) > 3]:
        print(l)

# printBiggerthan3Reversed(list)