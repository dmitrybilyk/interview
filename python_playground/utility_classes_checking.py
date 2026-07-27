from collections import Counter
from collections import defaultdict
from collections import deque
from collections import namedtuple

# Define the "Schema"
User = namedtuple('User', ['name', 'role', 'level'])

# Create an instance
dmytro = User(name="Dmytro", role="Software Engineer", level="Senior")

print(dmytro.name) # Access by name instead of dmytro[0]

queue = deque(["task1", "task2"])
queue.append("task3")      # Add to end
queue.appendleft("task0")  # Add to front (O(1)!)

first = queue.popleft()    # Remove from front

words = ["java", "python", "java", "kotlin", "java"]

counts = Counter(words)
print(counts.total())
print(counts["java"])
print(counts.most_common(5))

groups = defaultdict(list)
groups["backend"].append("Java")
groups["backend"].append("kotlin")
print(groups)