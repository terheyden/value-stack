package com.terheyden.value;

import javax.annotation.Nullable;
import java.util.Optional;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import io.vavr.CheckedRunnable;

/**
 * A value stack can collect and combine multiple values in a functional way.
 * Value objects are identical to {@link Optional}, with some added features to support combining values.
 * <p>
 * New methods added (besides the ones from {@link Optional}):
 * <ul>
 *   <li>{@link Value1#Value1(Optional)} — avoids having to unwrap Optionals</li>
 *   <li>{@link #of(Optional)} — support for conversion between Values and Optionals</li>
 *   <li>{@link Value1#andOf(Object)} — multi-value support; adds a new value to the stack</li>
 *   <li>{@link Value1#andOf(Optional)}  — multi-value support; adds a new value to the stack</li>
 *   <li>{@link Value1#andOf(CheckedFunction1)}  — multi-value support; adds a new value to the stack</li>
 *   <li>{@link Value1#andOfNullable(Object)}  — multi-value support; adds a new value to the stack</li>
 *   <li>{@link Value1#andOfNullable(CheckedFunction1)}  — multi-value support; adds a new value to the stack</li>
 *   <li>{@link Value1#or(Optional)} — replace the last value, if empty, with the given optional</li>
 *   <li>{@link Value1#or(Value1)}  — replace the last empty value with the given replacement</li>
 *   <li>{@link Value1#or(CheckedFunction0)} — use all values to generate a new value to replace the empty one</li>
 *   <li>{@link Value1#or(Object)} — a chainable version of {@code orElse()}, uses the given value to replace empty</li>
 *   <li>{@link Value1#ifEmpty(CheckedRunnable)} — to complement ifPresent()</li>
 *   <li>{@link Value2#ifAllPresent(CheckedConsumer2)} — use all values without needing to reduce</li>
 *   <li>{@link Value1#toOptional()} — support conversion between Values and Optionals</li>
 *   <li>{@link Value2#reduceAll(CheckedFunction2)} — combine all stack values into a result</li>
 *   <li>{@link Value2#flatReduceAll(CheckedFunction2)} — combine all stack values into a result Value</li>
 * </ul>
 * <p>
 * Changes to {@link Optional} methods (all non-breaking):
 * <ul>
 *   <li>All functional interfaces now handle checked exceptions (uses Vavr library)</li>
 *   <li>{@link Value1#ifPresent(CheckedConsumer)} — now returns self for chaining</li>
 *   <li>{@link Value1#ifPresentOrElse(CheckedConsumer, CheckedRunnable)} — now returns self for chaining</li>
 * </ul>
 */
public final class ValueStack {

    private ValueStack() {
        // Private constructor since this shouldn't be instantiated.
    }

    /////////////////////////////////////
    // This class contains package-private helper methods
    // as well as the public static factory methods.

    /**
     * Throw any exception unchecked.
     */
    @SuppressWarnings("unchecked")
    /* package */ static <E extends Throwable, R> R throwUnchecked(Throwable throwable) throws E {
        throw (E) throwable;
    }

    ////////////////////////////////////////
    // Factory methods:

    /**
     * Create a new value stack with the given initial value.
     * Value objects work exactly like {@link Optional}, but can be combined.
     */
    public static <A> Value1<A> ofNullable(@Nullable A value) {
        return new Value1<>(value);
    }

    /**
     * Create a new value stack with the given initial values.
     * Value objects work exactly like {@link Optional}, but can be combined.
     */
    public static <A, B> Value2<A, B> ofNullable(@Nullable A value1, @Nullable B value2) {
        return new Value2<>(value1, value2);
    }

    /**
     * Create a new value stack with the given initial value.
     * Value objects work exactly like {@link Optional}, but can be combined.
     */
    public static <A> Value1<A> of(A value) {
        return new Value1<>(value);
    }

    /**
     * Create a new value stack with the given initial values.
     * Value objects work exactly like {@link Optional}, but can be combined.
     */
    public static <A, B> Value2<A, B> of(A value1, B value2) {
        return new Value2<>(value1, value2);
    }

    /**
     * Create a new value stack with the given initial value.
     * Value objects work exactly like {@link Optional}, but can be combined.
     */
    public static <A> Value1<A> of(Optional<? extends A> optValue) {
        return new Value1<>(optValue);
    }

    /**
     * Create a new value stack with the given initial values.
     * Value objects work exactly like {@link Optional}, but can be combined.
     */
    public static <A, B> Value2<A, B> of(Optional<? extends A> optValue1, Optional<? extends B> optValue2) {
        return new Value2<>(optValue1, optValue2);
    }
}
