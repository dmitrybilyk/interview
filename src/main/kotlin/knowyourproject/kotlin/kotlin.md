                     ┌─────────────────────────┐
                     │      Kotlin Philosophy   │
                     └─────────────────────────┘
                                │
    ┌───────────────┬───────────┼───────────────┬───────────────┐
    │               │           │               │               │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ Immutability│ │ Final by     │ │ Null Safety │ │ No Checked  │ │ DSL Support │
│ (val by     │ │ default      │ │              │ │ Exceptions │ │ (first-class│
│ default)    │ │ (classes/    │ │              │ │             │ │ lambdas)    │
└─────────────┘ │ functions)  │ └─────────────┘ └─────────────┘ └─────────────┘
│         └─────────────┘        │                │                 │
│             │                   │                │                 │
│  Safe &     │   Prevent          │  Compile-time  │  Cleaner code   │
│  predictable│ accidental        │  null checks   │  (no try/catch)│
│  code       │ overrides          │  (NPE-free)   │                 │
│             │                    │                │                 │
▼             ▼                    ▼                ▼                 ▼
Fewer runtime   Safer inheritance    Less crashes     Focus on          Concise,
bugs & side-    & predictable        & bugs          meaningful       readable APIs
effects         code                                  error handling




CORE LANGUAGE (must be rock-solid)
Types & Null Safety

Nullable vs non-nullable types (T?)

Safe calls (?.)

Elvis operator (?:)

!! (when and why it’s dangerous)

Smart casts

Platform types (String!) ← important for Java interop

lateinit vs lazy

Functions

Default arguments

Named arguments

Expression-bodied functions

Varargs

Local functions

Tail recursion (tailrec)

Infix functions (when appropriate)

Function references (::)

Higher-order functions

Lambdas vs anonymous functions

Lambdas with receiver

Inline functions (you already touched this 👍)

Classes & Objects

Primary vs secondary constructors

Init blocks

data class (generated methods, copy semantics)

sealed class vs sealed interface

enum class vs sealed

object (singleton)

Companion objects

Visibility modifiers (esp. internal)

Nested vs inner classes

⚙️ STANDARD LIBRARY / IDIOMS (very important)
Scope Functions (you already did a deep dive)

let

run

apply

also

with

Know receiver vs argument

Know return value

Know when NOT to use them

Interviewers love asking:

“Which scope function would you use here and why?”

Collections

Immutable vs mutable collections

List vs MutableList

map, flatMap, mapNotNull

filter, filterIsInstance

groupBy, associate, associateBy

fold, reduce

any, all, none

sequence vs collection (lazy evaluation)

Performance implications

Strings

String templates

Raw strings (""")

Regex usage

🔁 CONTROL FLOW (Kotlin-specific)

when as expression (not statement)

when with:

ranges

types (is)

guards

if as expression

try as expression

Nothing return type (advanced but important)

🧬 TYPE SYSTEM (advanced interviews love this)
Generics

Declaration-site variance (out, in)

Use-site variance

Star projections (*)

Type constraints (where)

Reified type parameters

Why Java generics feel worse after Kotlin

Type Aliases

typealias

When to use them (readability, APIs)

🚀 INLINE & PERFORMANCE
Inline Functions

What inline actually does

noinline

crossinline

Lambda allocation elimination

When inline is bad

Why stdlib uses inline heavily

Value Classes (inline classes)

@JvmInline value class

Zero-cost abstractions

JVM boxing caveats

Use cases: IDs, wrappers, domain types

🧩 EXTENSIONS (you already started)

Extension functions

Extension properties

Static dispatch (important!)

Why extensions do NOT override methods

Name conflicts

API design with extensions

🔗 JAVA INTEROP (CRITICAL in real projects)

Platform types

@JvmStatic

@JvmOverloads

@JvmField

@Throws

SAM conversions

Calling Kotlin from Java

Calling Java from Kotlin

Nullability annotations from Java

🧵 CONCURRENCY & COROUTINES (often expected)
Coroutines

suspend functions

Coroutine builders (launch, async)

Job, Deferred

Structured concurrency

Dispatchers

Cancellation

Exception handling

withContext

Flow basics

Cold vs hot streams

Even if not deep — conceptual clarity is expected.

🧠 DSLs & METAPROGRAMMING

Lambdas with receiver

@DslMarker

Type-safe builders

Operator overloading

invoke operator

Custom DSL design principles

📦 ANNOTATIONS & REFLECTION

Annotation declaration

Annotation targets

Retention policies

Reflection basics (KClass, ::class)

When reflection is used vs avoided

🧪 TESTING & QUALITY

Kotlin test frameworks

Mocking pitfalls with final classes

Why everything is final by default

open vs allopen plugin

🧱 BUILD & TOOLING (often underestimated)

Kotlin/JVM vs Kotlin/JS vs Kotlin/Native

Gradle Kotlin DSL vs Groovy

Compiler options

-X flags (conceptual awareness)

ABI stability

🧠 DESIGN & PHILOSOPHY (senior interviews)

You should be able to explain:

Why Kotlin favors immutability

Why val is default

Why everything is final

Why checked exceptions are removed

Why null safety is enforced at compile time

Why DSLs are a first-class citizen

🎯 INTERVIEW EXPECTATION REALITY CHECK

You do NOT need:
❌ memorization of all APIs
❌ knowing every stdlib function

You DO need:
✅ correct mental models
✅ tradeoffs
✅ “why this exists” answers

🧠 If you want a brutal senior-level prep path

I’d suggest this order:

Lambdas, receivers, inline (you’re here 👍)

Generics + variance

Java interop

Coroutines fundamentals

DSL & API design

Performance pitfalls