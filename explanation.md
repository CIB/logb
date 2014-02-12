# Overview

Formal logic(and more specifically, high order logic) provides us with the necessary format to represent arbitrary information. logb's information format builds on this:

* Entities: Atomic elements and sets that group them
* Statements: Relations on entities
* Nested Statements: Statements are also Entities

Furthermore, logb extends this basic format with a few concepts:

* Entity structures: Entities that "contain" entities, comparable to structs in C.
* Patterns: A way to specify sets of statements and entities by introducing "variables" into them. Comparable in functionality to "queries" in prolog.

# Modules

Since logb is not a formal mathematics tool, but rather a practical tool, it does not limit itself to mathematical axioms and tools. This means that for any one problem domain, certain assumptions and definitions can be made about the domain. For instance, when dealing with rendering, we'll want to make assumptions about how data in the framebuffer relates to the image displayed on the screen and so on. These kinds of assumptions are stored in a module, making them easy to plug in and out.

All entity types, statement types and axioms must be defined in a module. These are all up to the programmer of the module - logb itself has no way of verifying that the definitions in the module match their real-world counterparts, rather this is the assumption which enables logb to make accurate inferences in the first place.

# Entities

Entities are the basic building block of our format. In logb, everything except the things hardcoded in modules, such as entity types and statement types, is an entity. Entities often represent something "outside" logb's format, such as numbers or pixels. However, statements are entities as well.

Examples of entities:

* `Integer(1)` - Entity referring to something "outside"(in this case, an integer)
* `Point(Integer(1), Integer(1))` - Entity structure, containing two Integer entities
* `Equals(Integer(1), Integer(1))` - Statement

Every entity has a type, e.g. "Integer". "outside" entities have a data pointer, e.g "1". Entity structures have a child table, e.g. `{Integer(1), Integer(1)}`. Statements always have the entity type "statement", but they also have a statement type, e.g. `Equals`. Statements have a parameter table, e.g. `{lefthand = Integer(1), righthand = Integer(1)}`.

# Statements

A statement connects entities. You can imagine entities as "nodes" and statements as "edges"(this analogy isn't perfect, as statements themselves are also entities). Inferences, or "thinking", in logb consists mostly of creating new statements from existing ones, so statements are a vital part of the system.

Examples of statements:

* `Equals(Integer(1), Integer(1))`
* `IsRaining()`
* `And(Equals(Integer(1), Integer(1)), IsRaining())`
* `ForAll %X: ElementOf(%X, PosIntegers) => GreaterOrEqual(%X, 0)`

Every statement has a statement type and a parameter table. Any entity can be a parameter, including other statements.

The `ForAll` example makes use of patterns, which we'll discuss in detail later.

# Patterns

Patterns are a way to describe groups of entities(usually statements). Where regular entities can only contain other entities, e.g. `And(A, B)` where `A` and `B` are atomic entities, patterns can contain "placeholders"(variables) in any place where an entity can be placed. For example, a pattern with the placeholder `%A` could be `AND(%A, B)`. Since `%A` is a placeholder, not an entity, we can 'replace' it, for example replacing `%A` with `foo` would lead to `AND(foo, B)`.

As you can probably see, a pattern can be seen as a function, which takes an "assignment" for each of its "parameters" and yields an entity. For instance, let's call our previous example "patternA":

    patternA(%A) := AND(%A, B)

We defined this pattern much like one might define a mathematical function. We can now invoke various "calls" on this pattern, yielding us different results.

    patternA(%A = 10)       -> AND(10, B)
    patternA(%A = "foo")    -> AND("foo", B)
    patternA(%A = B)        -> AND(B, B)

As patterns can have multiple different "placeholders", we might also replace only a few of these placeholders.

    patternAB(%A, %B) := AND(%A, %B)

    patternAB(%A = FOO, %B = BAR) -> AND(FOO, BAR)
    patternAB(%A = FOO)           -> AND(FOO, %B)

The last two examples are fundamentally different. `AND(FOO, BAR)` is a simple statement, with atomic arguments. `AND(FOO, %B)` is a (new) pattern, which we could define as:

    patternB(%B) := AND(FOO, %B)

## Pattern Constraints

Patterns can place constraints on their variables. For instance, in a pattern `GreaterOrEqual(%A, 10)`, you might want to specify that the replacement for `%A` must be in the set of positive integers.

    positivePattern(%X) := GreaterOrEqual(%X, 0) WHERE IsElement(%X, PosIntegers)

The first part of the pattern should be familiar. It's a simple pattern with the variable `%X`. The additional `WHERE` clause specifies that `%X` can only be substituted with something that matches the clause. For instance, `%X = 10` is possible, as `10` is a positive integer. However, `%X = "foo"` is not possible. If we compare this to our previous "function analogy", we now have a *partial function*: Some combinations of variable replacements, those that don't match the constraints, just aren't mapped by our pattern.

## Querying

Since a pattern describes a whole set of entities, we can use it to *query* for *matching* entities. The simplest way to do so is to take a list of entities, and one by one check for each entity in the list whether it matches the pattern. For example, we have the following set of statements:

	ISGREATER(5, 2)
	ISGREATER(4, 2)
	ISGREATER(3, 2)

Now we'd like to know a number smaller than 3. To do this, we simply create a pattern: `QUERY(%X) := ISGREATER(3, %X)`. For any entity that we can substitute for `%X` and receive an existing(true) statement, we know that entity is smaller than 3. If we insert `QUERY(%X = 2)`, we get `ISGREATER(3, 2)`, which is indeed a valid statement. Thus, `%X = 2` is the result of our query, and we can transfer this back into our natural language: "2 is smaller than 3".

## Pattern Matching

The pattern matching algorithm isn't trivial, so I'll explain it here. Input to the match is a pattern, and an entity for which we'd like to check whether it matches. We start with an empty set of substitutions. Then we traverse the pattern and the entity simultanously, for each "node" making sure that either:

    1. The two nodes are identical and (in a recursive step) their children are identical
    2. If the node in the pattern is a variable, the variable can be substituted with the node in the entity. For this, it has to match constraints and not violate previous assignments to that variable.

The process gets much more complex when the entity to match is itself a pattern. `TODO`

# Inference

Creating new statements out of existing ones is a central task for logb. This process is called inference. Inference is done by so-called "generators" that take a set of statements as input, and can yield new statements as result. The assumption is that the generator knows that the input statements (formally) imply the output statements.

Here is a simple example of an inference:

    Assumption: And(A, B)
    Result:     A

It is quite trivial to check that this inference is true.

## Inference Graph

Every inferred statement has a set of "dependencies" or "assumptions" it was inferred from. We can represent this relationship as edges going from the dependencies to their inferred statements. This will create an acyclic graph, which we can store and use later to verify our results by re-checking all assumptions.

Simple inference graph(linear):

    AND(OR(A, B), C) --> OR(A, B) --> NOT(AND(NOT(A), NOT(B)))

# Generators

As described in the previous section, generators are responsible for taking a set of input statements and infering new statements. Generators are implemented as actual algorithms, which means they're axiomatic. If a generator algorithm is broken, its results are not reliable.

Since feeding all combinations of statements to a generator would be very inefficient, generators provide means to tell the caller what statements it needs. For this, the caller supplies a statement it would like to prove(or disprove), and the generator will yield patterns for statements that would be helpful for doing so.

For instance, consider we want to prove `A`. The AndEliminationGenerator would be given this statement, and return patterns for statements it could use to prove `A`:

    AND(A, %X) WHERE %X IS STATEMENT
    AND(%X, A) WHERE %X IS STATEMENT

We can then recursively keep looking for these patterns, until we find a statement that we already know to be true.

# Logical operators

Logical operators are very fundamental statements that will be seen in any form of logical reasoning. They thus form an integral(although modular) part of logb. We will also introduce more readable ways to write out these oeprators, which we will use in some of the informal examples. However, these informal ways to write the operators might not become part of logb's language.

## And(A, B)

True if and only if both `A` and `B` are true, false otherwise.

    And(A, B)
    &&(A, B)
    A && B

Inferences:

    And(A, B) --> A
    And(A, B) --> B
    A, B      --> And(A, B)

## Or(A, B)

True if either `A` or `B` is true, false otherwise

    Or(A, B)
    ||(A, B)
    A || B

Inferences:
    Or(A, B), Not(A) --> B
    A                --> Or(A, B)

## Not(A)

True if and only if `A` is not true.

    Not(A)
    !(A)
    !A

Inferences:
    Not(Not(A))      --> A
    A                --> Not(Not(A))

## Implies(A, B)

The idea behind this operator is that `A` must always imply `B`, that is, if `A` is true, `B` must also be true. It doesn't make a lot of sense outside a ForAll, as we'll see later.

    Implies(A, B)
    =>(A, B)
    A => B

Inferences:
    A => B, A        --> B
    A => B, Not(B)   --> Not(A)

It's of note that the implication operator in theory can yield us new "inferences". As discussed, an inference needs a set of assumptions and a set of results. Assume we have a set of assumptions, combined with AND `A && B && C`, and a set of results for these assumptions, again combined with AND `D && E`. Now consider the following implication:

    (A && B && C) => (D && E)

Now assuming `A`, `B` and `C`, we also know(see inference rules for AND) `A && B && C`. So given the inference rule for Implies:

    (A && B && C), (A && B && C) => (D && E) --> (D && E)

In other words, `(A && B && C) => (D && E)` is analogous to an inference. Given the statements AND'ed in the left-hand side of the implication, we can infer the right-hand side of the implication. This makes implications very useful, and implications(or more specifically, forall statements containing implications) will be as important as inferences themselves later on.

## Equivalence

Simply put, `A` is equivalent to `B` if and only if `A` implies `B`, and `B` implies `A`.

    Equivalent(A, B)
    <==>(A, B)
    A <==> B

Inferences:
    A <==> B, A --> B
    A <==> B, B --> A

Equivalence means that one (sub-)statement can be replaced with another without changing the meaning of the statement.

## ForAll

`ForAll` is one of the special types of statements that interact with patterns. A `ForAll` statement says this: Whatever you substitute for the variables of my pattern, you will receive a statement that is true.

    fishSwims(%A): isFish(%A) => swims(%A)

    ForAll fishSwims

This is written intentionally explicitly, to show you what is happening. Usually, you'd write it more like this:

    forAll %A: isFish(%A) => swims(%A)

So let's try that example. Can we substitute anything for `%A` and receive a true statement?

    fishSwims(dog) = isFish(dog) => swims(dog)

Well, a dog doesn't swim, but since a dog is also not a fish, the implication still yields true.

    fishSwims(salmon) = isFish(salmon) => swims(salmon)
    
This seems to ring true enough.

If the set of entities that `ForAll` can choose from is finite, we can easily convert it to a statement without ForAll.

    Foo := {1, 2, 3}

    ForAll %X in Foo: GreaterOrEqual(3, %X)

    Resolved to:

    GreaterOrEqual(3, 1) AND GreaterOrEqual(3, 2) AND GreaterOrEqual(3, 3)

Inferences:
    ForAll %X: A(%X), ForAll %Y: B(%Y)          --> ForAll %Z: A(%Z) && B(%Z)

    ForAll %X: A(%X) => B(%X), ForAll %X: A(%X) --> ForAll(%X): B(%X)

`ForAll` also has an "IN" syntax variant, which is mostly a simple syntax sugar.

    ForAll %X in MySet: A(%X)

This syntax sugar is equivalent to:

    ForAll %X: IsElement(%X, MySet) => A(%X)

## Exists

`Exists`, like `ForAll`, takes a pattern. However, rather than specifying that you can substitute anything, it specifies that there is at least *one* combination of substitutions that will yield a true statement.

    Foo := {1, 2, 3}
    Exists %X in Foo: GreaterOrEqual(3, %X)

As before, since the set is finite, we can convert this Exists statement to an equivalent one:

    GreaterOrEqual(3, 1) OR GreaterOrEqual(3, 2) OR GreaterOrEqual(3, 3)

Exists also has an "IN" syntax variant, and it works exactly as in `ForAll`.

# Scopes

## Assumption scopes

An assumption scope is a "context" in which a statement always is true. For example:

    Scope Foo() =>:
        Bar()

Assumption scopes can be created by "removing" the left-hand side of an implication. Likewise, statements in assumption scopes can be converted back to "global" statements by re-adding the left-hand implication.

    Foo() => Bar()

    equivalent to

    Scope Foo() =>:
        Bar()

Assumption scopes can be used for performing reasoning on the right-hand side of an implication.

    A() => B()
    B() => C()

    Scope A() =>:
        1: B()
        2: B() => C()
        3: 1. && 2. --> C()

Now remove C() from the assumption scope.

    A() => C()

And thus we demonstrated transitivity of the implication operator.

## ForAll/Exists scopes

Inferences work fine on regular statements, but how could they be applied to patterns?

    ForAll %X: IsFish(%X) => CanSwim(%X)
    ForAll %X: CanSwim(%X) => IsWaterproof(%X)

Now we'd like to prove that all fish are waterproof:

    ForAll %X: IsFish(%X) => IsWaterproof(%X)

How could we go about that? For a single fish, we can use simple inferences on the implications.

    1: IsFish(MyFish)
    2: IsFish(MyFish) => CanSwim(MyFish)
    3: CanSwim(MyFish) => IsWaterproof(MyFish)
    4: 1. && 2. --> CanSwim(MyFish)
    5: 4. && 3. --> IsWaterproof(MyFish)

But how can we extrapolate this proof onto all fish? The solution are scopes. A scope is a statement bound to a specific context, e.g. a `ForAll`. Imagine "removing" our first two implications from their `ForAll` statement, and putting them in a "scope" instead.


    1. ForAll %X: IsFish(%X) => CanSwim(%X)
    2. ForAll %X: CanSwim(%X) => IsWaterproof(%X)

becomes

    Scope ForAll %X:
        1. IsFish(%X)  => CanSwim(%X)
        2. CanSwim(%X) => IsWaterproof(%X)

And from there on, we can simply reason it out as we have done before.

    Scope ForAll %X:
        1: IsFish(%X) => CanSwim(%X)
        2: CanSwim(%X) => IsWaterproof(%X)
        3: IsFish(%X)
        4: 1. && 3. --> CanSwim(%X)
        5: 2. && 4. --> IsWaterproof(%X)

After that, we can move `5.` back out of the scope and into the ForAll.

    ForAll %X: IsWaterproof(%X)
