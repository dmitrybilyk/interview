# Gang of 7 — Design Patterns Interview Guide

Covers 7 essential GoF patterns: 3 behavioral (Command, Strategy, Visitor) + 4 structural (Adapter, Decorator, Proxy, Bridge).

---

## Behavioral Patterns — HOW objects communicate

---

## Command

### Problem
You need to parameterize actions, queue them, log them, or support undo — but the invoker shouldn't know the details of what it's calling.

### Solution
Wrap a request as an object. The invoker calls `execute()` without knowing what happens inside.

### Structure
```
Client → creates ConcreteCommand(receiver)
Invoker → calls command.execute()
Command interface → execute(), undo()
ConcreteCommand → delegates to Receiver
Receiver → does the actual work
```

### Java
```java
interface Command { void execute(); void undo(); }

class LightOnCommand implements Command {
    private final Light light;
    LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
    public void undo()    { light.off(); }
}

class RemoteControl {  // Invoker
    private final Deque<Command> history = new ArrayDeque<>();
    void press(Command cmd) { cmd.execute(); history.push(cmd); }
    void undoLast()         { if (!history.isEmpty()) history.pop().undo(); }
}
```

### When to use
- Undo/redo (editor, game)
- Job queues / task schedulers
- Macro recording
- Producer-consumer (commands are tasks on a queue)

### Key one-liners
- Decouples invoker from receiver — invoker doesn't care what the command does.
- History stack of commands → free undo/redo.
- Lambda = anonymous Command implementation in Java 8+.

---

## Strategy

### Problem
You have multiple algorithms for the same task (sorting, pricing, compression) and need to switch between them at runtime without `if/else` sprawl.

### Solution
Define a family of algorithms behind a common interface. The context delegates to whichever strategy is injected.

### Structure
```
Context → holds Strategy reference, calls strategy.execute()
Strategy interface → algorithm()
ConcreteStrategyA, ConcreteStrategyB → different implementations
```

### Java
```java
interface SortStrategy { void sort(int[] arr); }

class QuickSort implements SortStrategy { public void sort(int[] arr) { /* quicksort */ } }
class MergeSort implements SortStrategy { public void sort(int[] arr) { /* mergesort */ } }

class Sorter {  // Context
    private SortStrategy strategy;
    void setStrategy(SortStrategy s) { this.strategy = s; }
    void sort(int[] data)            { strategy.sort(data); }
}

// Usage
Sorter s = new Sorter();
s.setStrategy(new QuickSort());
s.sort(data);
s.setStrategy(new MergeSort());  // switch at runtime
s.sort(data);
```

### When to use
- Multiple payment methods (PayPal, card, crypto)
- Different discount/pricing rules per customer segment
- Multiple compression/serialization formats
- Replacing large if-else or switch chains

### Key one-liners
- Open/Closed Principle: add new strategy without modifying Context.
- In Java 8+: `SortStrategy` is a `@FunctionalInterface` → pass lambdas directly.
- vs Command: Strategy = HOW to do one thing. Command = WHAT action to perform (+ undo, queue).

---

## Visitor

### Problem
You have an object hierarchy (AST nodes, file system items, shapes) and need to add new operations without modifying every class.

### Solution
Separate the operation from the object structure. Each element `accept(visitor)` — the visitor dispatches the right `visit(ConcreteElement)`.

### Structure
```
Visitor interface → visit(ConcreteElementA), visit(ConcreteElementB)
ConcreteVisitor → implements visit for each type
Element interface → accept(Visitor)
ConcreteElement → accept(v) { v.visit(this); }
```

### Java
```java
interface Shape { void accept(ShapeVisitor v); }
interface ShapeVisitor {
    void visit(Circle c);
    void visit(Rectangle r);
}

class Circle    implements Shape { public void accept(ShapeVisitor v) { v.visit(this); } double radius; }
class Rectangle implements Shape { public void accept(ShapeVisitor v) { v.visit(this); } double w, h; }

class AreaCalculator implements ShapeVisitor {
    public void visit(Circle c)    { System.out.println(Math.PI * c.radius * c.radius); }
    public void visit(Rectangle r) { System.out.println(r.w * r.h); }
}

// Usage
List<Shape> shapes = List.of(new Circle(), new Rectangle());
ShapeVisitor calc = new AreaCalculator();
shapes.forEach(s -> s.accept(calc));
```

### When to use
- AST traversal / compilers (evaluate, serialize, lint all in separate visitors)
- Report generation (same object tree, many different output formats)
- Object hierarchy is stable; operations on it change frequently

### Key one-liners
- Enables **double dispatch** — method chosen by both object type and visitor type.
- Open/Closed Principle on operations side (new visitor = new operation, no class changes).
- Downside: adding a new Element type forces all visitors to change.
- vs Strategy: Visitor works across a whole hierarchy; Strategy replaces one algorithm in one context.

---

## Structural Patterns — HOW objects are composed

---

## Adapter

### Problem
You have an existing interface (e.g., a third-party library) that is incompatible with what your code expects. You can't change either side.

### Solution
Wrap the incompatible class in an Adapter that implements the target interface, translating calls.

### Structure
```
Target interface → what the client expects
Adapter → implements Target, wraps Adaptee
Adaptee → existing incompatible class
```

### Java
```java
interface MediaPlayer { void play(String file); }

class VlcPlayer { public void playVlc(String f) { /* vlc */ } }

class VlcAdapter implements MediaPlayer {
    private final VlcPlayer vlc = new VlcPlayer();
    public void play(String file) { vlc.playVlc(file); }  // translate
}

// Client uses MediaPlayer, doesn't know VlcPlayer exists
MediaPlayer player = new VlcAdapter();
player.play("movie.vlc");
```

### Real-world Java examples
- `Arrays.asList()` — adapts array to `List`
- `InputStreamReader(InputStream)` — adapts byte stream to character stream
- Spring's `HandlerAdapter` — adapts various handler types to `DispatcherServlet`

### When to use
- Integrating legacy or third-party code
- Making two incompatible interfaces work together
- No access to either side's source

### Key one-liners
- Composition over inheritance (Object Adapter pattern — preferred in Java).
- Solves structural incompatibility at the interface boundary.
- vs Decorator: Adapter changes the interface; Decorator keeps the same interface but adds behavior.
- vs Facade: Adapter makes one thing compatible; Facade simplifies a whole subsystem.

---

## Decorator

### Problem
You need to add behavior to an object dynamically at runtime without modifying the class and without an explosion of subclasses for every combination.

### Solution
Wrap the object in a Decorator that implements the same interface and adds behavior before/after delegating.

### Structure
```
Component interface
ConcreteComponent → base object
Decorator (abstract) → wraps Component, delegates
ConcreteDecorator → adds specific behavior
```

### Java
```java
interface Coffee { double cost(); String description(); }

class SimpleCoffee implements Coffee {
    public double cost()        { return 1.0; }
    public String description() { return "Coffee"; }
}

abstract class CoffeeDecorator implements Coffee {
    protected final Coffee wrapped;
    CoffeeDecorator(Coffee c) { this.wrapped = c; }
}

class MilkDecorator extends CoffeeDecorator {
    MilkDecorator(Coffee c) { super(c); }
    public double cost()        { return wrapped.cost() + 0.3; }
    public String description() { return wrapped.description() + ", Milk"; }
}

class SugarDecorator extends CoffeeDecorator {
    SugarDecorator(Coffee c) { super(c); }
    public double cost()        { return wrapped.cost() + 0.2; }
    public String description() { return wrapped.description() + ", Sugar"; }
}

// Stack decorators at runtime
Coffee order = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
System.out.println(order.description() + " $" + order.cost());
// → Coffee, Milk, Sugar $1.5
```

### Real-world Java examples
- `BufferedInputStream(new FileInputStream(...))` — wraps stream with buffering
- `Collections.unmodifiableList(list)` — wraps with read-only guard
- `HttpServletRequestWrapper` in servlet filters

### When to use
- Adding behavior combinations without class explosion (n features → n decorators, not 2^n subclasses)
- Wrapping third-party objects you can't subclass
- Layered behavior: logging + caching + auth wrapping the same service

### Key one-liners
- Same interface in and out — client doesn't know it's wrapped.
- vs Proxy: Decorator adds new behavior; Proxy controls access to the same behavior.
- vs Inheritance: Decorator is dynamic at runtime; subclassing is static at compile time.

---

## Proxy

### Problem
You need to control access to an object — for lazy initialization, caching, security checks, remote calls, or logging — without the client knowing.

### Solution
The Proxy implements the same interface as the real subject and intercepts calls, adding pre/post logic.

### Structure
```
Subject interface
RealSubject → does the real work
Proxy → implements Subject, holds reference to RealSubject, adds logic
```

### Java
```java
interface Image { void display(); }

class RealImage implements Image {
    private final String file;
    RealImage(String f) { this.file = f; loadFromDisk(); }  // expensive
    private void loadFromDisk() { System.out.println("Loading " + file); }
    public void display() { System.out.println("Displaying " + file); }
}

class ImageProxy implements Image {  // Virtual proxy — lazy loading
    private final String file;
    private RealImage real;
    ImageProxy(String f) { this.file = f; }  // no load yet
    public void display() {
        if (real == null) real = new RealImage(file);  // load on first use
        real.display();
    }
}
```

### Types of proxy
| Type | Purpose |
|---|---|
| **Virtual** | Lazy initialization of expensive object |
| **Protection** | Access control / role checks before delegating |
| **Remote** | Hides network call (RMI, gRPC stub) |
| **Caching** | Cache results, skip real call if cached |
| **Logging/Monitoring** | Intercept calls to log, measure timing |

### Real-world Java examples
- Spring AOP (`@Transactional`, `@Cacheable`) — JDK dynamic proxy or CGLIB proxy
- Hibernate lazy-loaded collections — proxy until accessed
- `java.lang.reflect.Proxy` — dynamic proxy at runtime

### When to use
- Adding cross-cutting concerns (logging, auth, metrics) without modifying classes
- Lazy loading heavy resources
- Spring's `@Transactional` / `@Cacheable` annotations

### Key one-liners
- Same interface as the real object — client is unaware.
- vs Decorator: Proxy controls access; Decorator adds functionality. (Overlap is real — Spring AOP uses proxy to decorate.)
- Spring creates proxies automatically for `@Transactional` beans.

---

## Bridge

### Problem
You have two independent dimensions of variation (e.g., Shape × Color, Device × Remote) and inheritance produces a class explosion: `RedCircle`, `BlueCircle`, `RedSquare`…

### Solution
Separate the **abstraction** (high-level control) from the **implementation** (low-level work) into two hierarchies connected by composition.

### Structure
```
Abstraction → holds reference to Implementor, delegates
RefinedAbstraction → extends Abstraction
Implementor interface → low-level operations
ConcreteImplementorA, B → platform-specific implementations
```

### Java
```java
// Implementor hierarchy (the "what")
interface DrawingAPI { void drawCircle(double x, double y, double r); }
class OpenGLDrawing  implements DrawingAPI { public void drawCircle(double x, double y, double r) { /* GL */ } }
class SVGDrawing     implements DrawingAPI { public void drawCircle(double x, double y, double r) { /* SVG */ } }

// Abstraction hierarchy (the "who")
abstract class Shape {
    protected DrawingAPI api;  // bridge
    Shape(DrawingAPI api) { this.api = api; }
    abstract void draw();
}

class Circle extends Shape {
    private double x, y, r;
    Circle(double x, double y, double r, DrawingAPI api) { super(api); this.x=x; this.y=y; this.r=r; }
    public void draw() { api.drawCircle(x, y, r); }  // delegates to implementor
}

// Combine freely — no class explosion
Shape c1 = new Circle(1, 2, 3, new OpenGLDrawing());
Shape c2 = new Circle(1, 2, 3, new SVGDrawing());
```

### When to use
- Two independent axes of variation (abstraction + implementation)
- Switching implementations at runtime (e.g., different DB drivers, rendering backends)
- Avoiding class explosion from multiple inheritance dimensions

### Key one-liners
- "Prefer composition over inheritance" — the bridge IS a composition.
- vs Adapter: Bridge is designed up front to separate concerns; Adapter is a retrofit for incompatible code.
- vs Strategy: Strategy is a single algorithm swapped; Bridge separates two full hierarchies.
- JDBC is a real-world Bridge: `Connection` (abstraction) + driver implementation (implementor).

---

## Pattern Comparison

### Structural patterns side-by-side
| | Adapter | Decorator | Proxy | Bridge |
|---|---|---|---|---|
| **Changes interface?** | Yes — converts one to another | No — same in/out | No — same in/out | No — separates two hierarchies |
| **Purpose** | Compatibility | Add behavior | Control access | Decouple abstraction from impl |
| **At runtime?** | Fixed | Dynamic wrapping | Often fixed | Swappable implementor |
| **Common use** | Legacy integration | Layered features | AOP, lazy load | Multi-platform code |

### Behavioral patterns side-by-side
| | Command | Strategy | Visitor |
|---|---|---|---|
| **Encapsulates** | An action (with undo) | An algorithm | An operation across a hierarchy |
| **Who decides behavior** | Invoker picks command | Context picks strategy | Visitor + element together (double dispatch) |
| **Extensible on** | New commands | New strategies | New operations (new visitors) |
| **Costly to extend** | — | — | New element types (update all visitors) |
| **Common use** | Undo, queues, macros | Swappable algorithms | AST traversal, report generation |

---

## Interview One-Liners

- **Command** = action as object → supports undo, queuing, macro recording. Lambda is a one-method Command.
- **Strategy** = swap algorithm at runtime → eliminates if-else chains. Same interface, different behavior.
- **Visitor** = double dispatch → add operations to a stable hierarchy without modifying it. New element type = all visitors must update.
- **Adapter** = compatibility shim → changes interface. `InputStreamReader` is the canonical Java example.
- **Decorator** = wraps same interface, stacks at runtime → `BufferedInputStream` wrapping `FileInputStream`.
- **Proxy** = same interface, controls access → Spring `@Transactional` is a proxy under the hood.
- **Bridge** = two independent hierarchies composed → avoids M×N class explosion. JDBC is the classic example.
- Adapter vs Decorator: Adapter changes interface; Decorator keeps it.
- Proxy vs Decorator: Proxy controls access to the real object; Decorator adds new behavior.
- Bridge vs Adapter: Bridge is designed up front; Adapter is a retrofit.
