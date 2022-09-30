package com.terheyden.value;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Value1Test unit tests.
 */
public class Value2Test {

    private static final Logger LOG = getLogger(Value2Test.class);

    @Nullable
    private static final String NULL_STR = null;
    @Nullable private static final UUID NULL_UUID = null;

    private Value1<String> goodOpt1;
    private Value2<String, UUID> goodOpt2;
    private Value1<String> badOpt1;
    private Value2<String, UUID> badOpt2;

    @BeforeEach
    public void beforeEach() {
        goodOpt1 = ValueStack.of("Cora");
        badOpt1 = ValueStack.ofNullable(NULL_STR);
        goodOpt2 = ValueStack.of("Cora", UUID.randomUUID());
        badOpt2 = ValueStack.ofNullable(NULL_STR, NULL_UUID);
    }

    @Test
    public void tutorial() {

        // In Functional Programming, single values chain together to form a result.
        // In this example: name -> UUID -> id string
        String userIdStr = Optional.of("Cora")
            .map(name -> TestService.findUserId(name))
            .map(userId -> userId.toString())
            .get();

        // However, oftentimes there are multiple results that you want to combine.
        // In pure FP, you'd use memoization, or perhaps a monad or a tuple.
        // In Java, it usually means abandoning FP for common simple cases.
        // I suggest that we could try using Value2:
        //
        // name -> UUID -> (name + UUID) = user

        TestUser goodUser = ValueStack.of("Cora")                       // Value2 stores: "Cora"
            .andOf(name -> TestService.findUserId(name))                   // Value2 stores: "Cora" + UUID
            .reduceAll((name, userId) -> TestService.loginUser(userId, name)) // "Cora" + UUID = user obj
            .get();

        // Notice that in the .reduceAll() call, the vars have strong names and strong types.
        // You get a great blend of FP and Java.
        //
        // Let's do one more example:

        ValueStack
            .ofNullable(NULL_STR)
                .ifEmpty(() -> LOG.warn("Name is null! Using backup source."))
                .or(() -> Optional.of("Cora"))
            .andOf(name -> null)
                .or(() -> Optional.of(UUID.randomUUID()))
            .ifAllPresent((name, userId) -> LOG.info("User: {} {}", name, userId));

        // The Java Optional methods you're used to are all there, just extended to support
        // a second value, and with a little more usability sprinkled in.
        //
        // .reduceAll() is the only newly-added method.
        // It reduces down to a Java Optional, and any Value2 can be converted
        // back into a Java Optional at any time via .getOptional().
    }

    @Test
    public void testBasics() {

        // Ideal scneario — two values that you then combine.

        TestUser goodUser = goodOpt1
            .andOf(name -> TestService.findUserId(name))
            .reduceAll((name, userId) -> TestService.loginUser(userId, name))
            .get();

        assertEquals("Cora", goodUser.userName());

        // Assert that when value1 is null, no actions are taken.
        UUID nullUuid = badOpt1
            .filter(name -> name.length() > 0)         // This would throw...
            .map(name -> TestService.findUserId(name))
            .orElse(null);

        assertNull(nullUuid);
    }

    @Test
    public void testNullAnnotation() {

        // orElse() is marked as @Nullable, is it going to grief us when we're not null?
        String cora = ValueStack.of("Cora").orElse(null);
        String cora3 = ValueStack.of("Cora").orElse("Cora3");
        assertEquals(4, cora.length());
        assertEquals(4, cora3.length());

        String cora2 = Optional.of("Cora2").orElse(null);
        assertEquals(5, cora2.length());
    }

    @Test
    public void testThrow() {

        assertThrows(NoSuchElementException.class, () -> badOpt1.orElseThrow());
    }

    @Test
    public void testRunIfEmpty() {

        AtomicInteger count = new AtomicInteger(0);

        badOpt1.ifEmpty(count::incrementAndGet);
        assertEquals(1, count.get());

        goodOpt1.ifEmpty(count::incrementAndGet);
        assertEquals(1, count.get());
    }

    @Test
    public void testGet() {

        assertEquals("Cora", goodOpt1.get());
        assertThrows(NoSuchElementException.class, () -> badOpt1.get());
    }

    @Test
    public void testOr() {

        assertEquals("Cora", goodOpt1.orElse("Cora2"));
        assertEquals("Cora2", badOpt1.orElse("Cora2"));
        assertEquals("Cora", goodOpt1.or(() -> Optional.ofNullable(NULL_STR)).get());
        assertEquals("Cora", goodOpt1.or(badOpt1).get());
        assertEquals("Cora", goodOpt1.or(Optional.of("Cora2")).get());
        assertEquals("Cora", goodOpt1.or("Cora2").get());
        assertEquals("Cora2", badOpt1.or("Cora2").get());
        assertEquals("Cora2", badOpt1.or(Optional.of("Cora2")).get());
        assertEquals("Cora2", badOpt1.or(ValueStack.of("Cora2")).get());
        assertEquals("Cora2", badOpt1.or(() -> Optional.of("Cora2")).get());
    }

    @Test
    public void testIsPresent() {

        assertTrue(goodOpt1.isPresent());
        assertFalse(goodOpt1.isEmpty());
        assertFalse(badOpt1.isPresent());
        assertTrue(badOpt1.isEmpty());

        AtomicInteger count = new AtomicInteger(0);

        goodOpt1.ifPresent(name -> count.incrementAndGet());
        assertEquals(1, count.get());

        badOpt1.ifPresent(name -> count.incrementAndGet());
        assertEquals(1, count.get());

        goodOpt1.ifEmpty(count::incrementAndGet);
        assertEquals(1, count.get());

        badOpt1.ifEmpty(count::incrementAndGet);
        assertEquals(2, count.get());
    }

    @Test
    public void testFilter() {

        Value1<String> goodOpt2 = goodOpt1
            .filter(name -> name.length() > 0)
            .map(String::toUpperCase);

        assertEquals("CORA", goodOpt2.get());

        Value1<String> badOpt2 = badOpt1.filter(name -> name.length() > 0);
        assertFalse(badOpt2.isPresent());
        assertThrows(NoSuchElementException.class, () -> badOpt2.get());

        Value1<String> goodOpt3 = goodOpt1.filter(name -> name.length() > 10);
        assertFalse(goodOpt3.isPresent());
        assertThrows(NoSuchElementException.class, () -> goodOpt3.get());

        assertEquals(4, goodOpt1.flatMap(name -> Optional.of(name.length())).get());
        // name.length() would cause an NPE here, but it doesn't, so we know it's not being evaluated.
        assertFalse(badOpt2.flatMap(name -> Optional.of(name.length())).isPresent());
    }

    @Test
    public void testCovariance() {

        TestBird bird1 = new TestBird("Tweety1", 3, 2);
        TestBird bird2 = new TestBird("Tweety2", 3, 2);
        Value1<TestBird> birds1 = ValueStack.of(new TestBird("Tweety", 3, 2));
        Value2<TestBird, TestBird> birds2 = ValueStack.of(bird1, bird2);

        Value2<TestBird, TestAnimal> animal1 = birds1.andOf(an -> new TestBird(an.getName(), 3, 2));
        assertNotNull(animal1);

        Value1<TestAnimal> animal3 = birds2.flatReduceAll((a1, a2) -> ValueStack.of(a1));
        assertNotNull(animal3);

        Value1<TestAnimal> animal4 = Value1.empty();
        Value1<TestAnimal> animal5 = animal4.or(birds1);
        assertNotNull(animal5);
    }
}
