package com.terheyden.value;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Value1Test unit tests.
 */
public class Value1Test {

    @Nullable
    private static final String NULL_STR = null;

    private Value1<String> goodOpt;
    private Value1<String> badOpt;

    @BeforeEach
    public void beforeEach() {
        goodOpt = ValueStack.of("Cora");
        badOpt = ValueStack.ofNullable(NULL_STR);
    }

    @Test
    public void testBasics() {

        // Assert that when value1 is null, no actions are taken.
        UUID nullUuid = badOpt
            .filter(name -> name.length() > 0)         // This would throw...
            .map(name -> TestService.findUserId(name))
            .orElse(null);

        assertNull(nullUuid);
    }

    @Test
    public void testThrow() {

        assertThrows(NoSuchElementException.class, () -> badOpt.orElseThrow());
    }

    @Test
    public void testRunIfEmpty() {

        AtomicInteger count = new AtomicInteger(0);

        badOpt.ifEmpty(count::incrementAndGet);
        assertEquals(1, count.get());

        goodOpt.ifEmpty(count::incrementAndGet);
        assertEquals(1, count.get());
    }

    @Test
    public void testGet() {

        assertEquals("Cora", goodOpt.get());
        assertThrows(NoSuchElementException.class, () -> badOpt.get());
    }

    @Test
    public void testOr() {

        assertEquals("Cora", goodOpt.orElse("Cora2"));
        assertEquals("Cora2", badOpt.orElse("Cora2"));
        assertEquals("Cora", goodOpt.or(() -> Optional.ofNullable(NULL_STR)).get());
        assertEquals("Cora", goodOpt.or(badOpt).get());
        assertEquals("Cora", goodOpt.or(Optional.of("Cora2")).get());
        assertEquals("Cora", goodOpt.or("Cora2").get());
        assertEquals("Cora2", badOpt.or("Cora2").get());
        assertEquals("Cora2", badOpt.or(Optional.of("Cora2")).get());
        assertEquals("Cora2", badOpt.or(ValueStack.of("Cora2")).get());
        assertEquals("Cora2", badOpt.or(() -> Optional.of("Cora2")).get());
    }

    @Test
    public void testIsPresent() {

        assertTrue(goodOpt.isPresent());
        assertFalse(goodOpt.isEmpty());
        assertFalse(badOpt.isPresent());
        assertTrue(badOpt.isEmpty());

        AtomicInteger count = new AtomicInteger(0);

        goodOpt.ifPresent(name -> count.incrementAndGet());
        assertEquals(1, count.get());

        badOpt.ifPresent(name -> count.incrementAndGet());
        assertEquals(1, count.get());

        goodOpt.ifEmpty(count::incrementAndGet);
        assertEquals(1, count.get());

        badOpt.ifEmpty(count::incrementAndGet);
        assertEquals(2, count.get());
    }

    @Test
    public void testFilter() {

        Value1<String> goodOpt2 = goodOpt
            .filter(name -> name.length() > 0)
            .map(String::toUpperCase);

        assertEquals("CORA", goodOpt2.get());

        Value1<String> badOpt2 = badOpt.filter(name -> name.length() > 0);
        assertFalse(badOpt2.isPresent());
        assertThrows(NoSuchElementException.class, () -> badOpt2.get());

        Value1<String> goodOpt3 = goodOpt.filter(name -> name.length() > 10);
        assertFalse(goodOpt3.isPresent());
        assertThrows(NoSuchElementException.class, () -> goodOpt3.get());

        assertEquals(4, goodOpt.flatMap(name -> Optional.of(name.length())).get());
        // name.length() would cause an NPE here, but it doesn't, so we know it's not being evaluated.
        assertFalse(badOpt2.flatMap(name -> Optional.of(name.length())).isPresent());
    }
}
