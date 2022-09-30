package com.terheyden.value;


import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedPredicate;
import io.vavr.CheckedRunnable;

/**
 * A stack with a single value.
 * Create via static methods in the {@link ValueStack} class.
 */
public final class Value1<C> {

    private static final Value1<?> EMPTY = new Value1<>(Optional.empty());

    private final Optional<C> optValue1;

    /**
     * Returns an empty {@code Value1} instance.  No value is present for this
     * {@code Value1}.
     *
     * @apiNote
     * Though it may be tempting to do so, avoid testing if an object is empty
     * by comparing with {@code ==} or {@code !=} against instances returned by
     * {@code Value1.empty()}.  There is no guarantee that it is a singleton.
     * Instead, use {@link #isEmpty()} or {@link #isPresent()}.
     *
     * @param <X> The type of the non-existent value
     * @return an empty {@code Value1}
     */
    public static <X> Value1<X> empty() {
        @SuppressWarnings("unchecked")
        Value1<X> t = (Value1<X>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the described value.
     *
     * @param value the value to describe; it's the caller's responsibility to
     *        ensure the value is non-{@code null} unless creating the singleton
     *        instance returned by {@code empty()}.
     */
    /* package */ Value1(@Nullable C value) {
        this.optValue1 = Optional.ofNullable(value);
    }

    /**
     * Copy constructor so we don't have to unwrap Optionals.
     */
    @SuppressWarnings("unchecked")
    /* package */ Value1(Optional<? extends C> optValue) {
        this.optValue1 = (Optional<C>) optValue;
    }

    /**
     * Adds an additional Value to track, resulting in a new {@link Value2}.
     */
    public <D> Value2<C, D> andOf(D value) {
        return isEmpty() ? Value2.empty() : new Value2<>(optValue1, Optional.of(value));
    }

    /**
     * Adds an additional Value to track, resulting in a new {@link Value2}.
     */
    public <D> Value2<C, D> andOf(Optional<? extends D> optValue) {
        return isEmpty() ? Value2.empty() : new Value2<>(optValue1, optValue);
    }

    /**
     * Uses the existing value, if present, to create a new value to track.
     * If the existing value is empty, then an empty {@link Value2} is returned.
     */
    public <D> Value2<C, D> andOf(CheckedFunction1<? super C, ? extends D> valueFunction) {

        return isEmpty()
            ? Value2.empty()
            : new Value2<>(optValue1, optValue1.map(valueFunction.unchecked()));
    }

    /**
     * Returns an {@code Value1} describing the given value, if
     * non-{@code null}, otherwise returns an empty {@code Value1}.
     *
     * @param value the possibly-{@code null} value to describe
     * @param <D> the type of the value
     * @return an {@code Value1} with a present value if the specified value
     *         is non-{@code null}, otherwise an empty {@code Value1}
     */
    public <D> Value2<C, D> andOfNullable(@Nullable D value) {
        return isEmpty() ? Value2.empty() : new Value2<>(optValue1, Optional.ofNullable(value));
    }

    /**
     * Uses the existing value, if present, to create a new value to track.
     * If the existing value is empty, then an empty {@link Value2} is returned.
     */
    public <D> Value2<C, D> andOfNullable(CheckedFunction1<? super C, ? extends D> valueFunction) {

        return isEmpty()
            ? Value2.empty()
            : new Value2<>(optValue1, optValue1.map(valueFunction.unchecked()));
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @apiNote
     * The preferred alternative to this method is {@link #orElseThrow()}.
     *
     * @return the non-{@code null} value described by this {@code Value1}
     * @throws NoSuchElementException if no value is present
     */
    public C get() {
        return optValue1.get();
    }

    /**
     * If a value is present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        return optValue1.isPresent();
    }

    /**
     * If a value is  not present, returns {@code true}, otherwise
     * {@code false}.
     *
     * @return  {@code true} if a value is not present, otherwise {@code false}
     * @since   11
     */
    public boolean isEmpty() {
        return optValue1.isEmpty();
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     * @return this, for chaining
     * @throws NullPointerException if value is present and the given action is
     *         {@code null}
     */
    public Value1<C> ifPresent(CheckedConsumer<? super C> action) {
        optValue1.ifPresent(action.unchecked());
        return this;
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     *        present
     * @return this, for chaining
     * @throws NullPointerException if a value is present and the given action
     *         is {@code null}, or no value is present and the given empty-based
     *         action is {@code null}.
     * @since 9
     */
    public Value1<C> ifPresentOrElse(CheckedConsumer<? super C> action, CheckedRunnable emptyAction) {
        optValue1.ifPresentOrElse(action.unchecked(), emptyAction.unchecked());
        return this;
    }

    /**
     * Performs the empty-based action if no value is present, otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     *        present
     * @return this, for chaining
     * @throws NullPointerException if a value is present and the given action
     *         is {@code null}, or no value is present and the given empty-based
     *         action is {@code null}.
     * @since 9
     */
    public Value1<C> ifEmpty(CheckedRunnable emptyAction) {

        if (isEmpty()) {
            emptyAction.unchecked().run();
        }

        return this;
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code Value1} describing the value, otherwise returns an
     * empty {@code Value1}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code Value1} describing the value of this
     *         {@code Value1}, if a value is present and the value matches the
     *         given predicate, otherwise an empty {@code Value1}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public Value1<C> filter(CheckedPredicate<? super C> predicate) {
        return isEmpty() ? this : new Value1<>(optValue1.filter(predicate.unchecked()));
    }

    /**
     * If a value is present, returns an {@code Value1} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code Value1}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Value1}.
     *
     * @apiNote
     * This method supports post-processing on {@code Value1} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code Value1<Path>}:
     *
     * <pre>{@code
     *     Value1<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code Value1<URI>}, and then
     * {@code map} returns an {@code Value1<Path>} for the desired
     * URI if one exists.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the value returned from the mapping function
     * @return an {@code Value1} describing the result of applying a mapping
     *         function to the value of this {@code Value1}, if a value is
     *         present, otherwise an empty {@code Value1}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> Value1<U> map(CheckedFunction1<? super C, ? extends U> mapper) {
        return isEmpty() ? empty() : new Value1<>(optValue1.map(mapper.unchecked()));
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code Value1}-bearing mapping function to the value, otherwise returns
     * an empty {@code Value1}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already an {@code Value1}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code Value1}.
     *
     * @param <U> The type of value of the {@code Value1} returned by the
     *            mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code Value1}-bearing mapping
     *         function to the value of this {@code Value1}, if a value is
     *         present, otherwise an empty {@code Value1}
     * @throws NullPointerException if the mapping function is {@code null} or
     *         returns a {@code null} result
     */
    public <U> Value1<U> flatMap(CheckedFunction1<? super C, ? extends Optional<? extends U>> mapper) {
        return isEmpty() ? empty() : new Value1<>(optValue1.flatMap(mapper.unchecked()));
    }

    /**
     * If a value is present, returns an {@code Value1} describing the value,
     * otherwise returns an {@code Value1} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value1}
     *        to be returned
     * @return returns an {@code Value1} describing the value of this
     *         {@code Value1}, if a value is present, otherwise an
     *         {@code Value1} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public Value1<C> or(CheckedFunction0<? extends Optional<? extends C>> supplier) {
        return isPresent() ? this : new Value1<>(supplier.unchecked().apply());
    }

    /**
     * If a value is present, returns an {@code Value1} describing the value,
     * otherwise returns an {@code Value1} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value1}
     *        to be returned
     * @return returns an {@code Value1} describing the value of this
     *         {@code Value1}, if a value is present, otherwise an
     *         {@code Value1} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public Value1<C> or(Optional<? extends C> useIfEmpty) {
        return isPresent() ? this : new Value1<>(useIfEmpty);
    }

    /**
     * If a value is present, returns an {@code Value1} describing the value,
     * otherwise returns an {@code Value1} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value1}
     *        to be returned
     * @return returns an {@code Value1} describing the value of this
     *         {@code Value1}, if a value is present, otherwise an
     *         {@code Value1} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    @SuppressWarnings("unchecked")
    public Value1<C> or(Value1<? extends C> useIfEmpty) {
        return isPresent() ? this : (Value1<C>) useIfEmpty;
    }

    /**
     * If a value is present, returns an {@code Value1} describing the value,
     * otherwise returns an {@code Value1} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value1}
     *        to be returned
     * @return returns an {@code Value1} describing the value of this
     *         {@code Value1}, if a value is present, otherwise an
     *         {@code Value1} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public Value1<C> or(C useIfEmpty) {
        return isPresent() ? this : new Value1<>(useIfEmpty);
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote
     * This method can be used to transform a {@code Stream} of optional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Value1<C>> os = ..
     *     Stream<C> s = os.flatMap(Value1::stream)
     * }</pre>
     *
     * @return the optional value as a {@code Stream}
     * @since 9
     */
    public Stream<C> stream() {
        return optValue1.stream();
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * {@code other}.
     *
     * @param other the value to be returned, if no value is present.
     *        May be {@code null}.
     * @return the value, if present, otherwise {@code other}
     */
    @Nullable
    public C orElse(@Nullable C other) {
        return optValue1.orElse(other);
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     *         supplying function
     * @throws NullPointerException if no value is present and the supplying
     *         function is {@code null}
     */
    @Nullable
    public C orElseGet(Supplier<? extends C> supplier) {
        return optValue1.orElseGet(supplier);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Value1}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    public C orElseThrow() {
        return optValue1.orElseThrow();
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @apiNote
     * A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *        exception to be thrown
     * @return the value, if present
     * @throws X if no value is present
     * @throws NullPointerException if no value is present and the exception
     *          supplying function is {@code null}
     */
    public <X extends Throwable> C orElseThrow(CheckedFunction0<? extends X> exceptionSupplier) throws X {
        return optValue1.orElseThrow(exceptionSupplier.unchecked());
    }

    /**
     * Converts the last value to a Java {@link Optional}.
     */
    public Optional<C> toOptional() {
        return optValue1;
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Value1}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Value1} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object
     *         otherwise {@code false}
     */
    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Value1<?> optObj)) {
            return false;
        }

        return optValue1.equals(optObj.optValue1);
    }

    /**
     * Returns the hash code of the value, if present, otherwise {@code 0}
     * (zero) if no value is present.
     *
     * @return hash code value of the present value or {@code 0} if no value is
     *         present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(optValue1.orElse(null));
    }

    /**
     * Returns a non-empty string representation of this {@code Value1}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @implSpec
     * If a value is present the result must include its string representation
     * in the result.  Empty and present {@code Value1}s must be unambiguously
     * differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return "Value1[%s]".formatted(
            optValue1.map(String::valueOf).orElse("null"));
    }
}
