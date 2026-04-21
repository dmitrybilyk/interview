# Declaration
my_set = {"Java", "Kotlin", "Python"}
skills = set(["Kafka", "Spring", "Java"]) # From a list

# Adding and Removing
my_set.add("Docker")
my_set.remove("Kotlin")  # Throws KeyError if not present
my_set.discard("C++")    # Safe: Does nothing if not present

# Membership testing (Very fast)
if "Java" in my_set:
    print("Java is in the set")

dmytroSet = set(["First", "Second"])
for s in dmytroSet:
    print(s)

dmytroSet2 = {"again", "set"}
