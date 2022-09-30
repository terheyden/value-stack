# Value Stack

_Collect and combine multiple values in Java in a functional way._

## Overview with examples

```java
// In Functional Programming, single values chain together to form a result.
// In this example: name -> UUID -> id string
String userIdStr = Optional.of("Cora")
    .map(name -> TestService.findUserId(name))
    .map(userId -> userId.toString())
    .get();

// However, oftentimes there are multiple results that you want to combine.
// In pure FP, you'd use memoization, or perhaps a monad or a tuple.
// In Java, it usually means abandoning FP for common simple cases.
// Consider, instead, a value stack:
//
// name -> UUID -> (name + UUID) = user

TestUser goodUser = ValueStack.of("Cora")                          // stack contains: "Cora"
    .andOf(name -> TestService.findUserId(name))                   // stack contains: "Cora" + UUID
    .reduce((name, userId) -> TestService.loginUser(userId, name)) // "Cora" + UUID = user obj
    .get();

// Notice that in the .reduceAll() call, the vars have strong names and strong types.
// You get a great blend of FP and Java.
//
// Also, Value objects are identical to Java Optional objects
// (with a few extra methods added), so they should be familiar to use.
//
// Let's do one more example:

ValueStack
    .ofNullable(NULL_STR)
    .ifEmpty(() -> LOG.warn("Name is null! Using backup source."))
    .or(() -> Optional.of("Cora"))
    .andOfNullable(name -> lookupUserId(name))
    .throwIfEmpty()
    .ifAllPresent((name, userId) -> LOG.info("User: {} {}", name, userId));

// Almost all of the available value methods apply to the most recently added value.
// (Methods that deal with the entire stack have 'all' in the name.)
// You can validate and adjust each value as it's added to the stack.
```
