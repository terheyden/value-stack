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
import io.vavr.CheckedFunction2;
import io.vavr.CheckedPredicate;
import io.vavr.CheckedRunnable;

/**
 * A stack of two values.
 */
public class Value2<B, C> {

    private static final Value2<?, ?> EMPTY = new Value2<>(Optional.empty(), Optional.empty());

    private final Optional<B> optValue1;
    private final Optional<C> optValue2;

    /**
     * Returns an empty {@code Value2} instance.  No value is present for this
     * {@code Value2}.
     *
     * @apiNote
     * Though it may be tempting to do so, avoid testing if an object is empty
     * by comparing with {@code ==} or {@code !=} against instances returned by
     * {@code Value2.empty()}.  There is no guarantee that it is a singleton.
     * Instead, use {@link #isEmpty()} or {@link #isPresent()}.
     *
     * @param <T> The type of the non-existent value
     * @return an empty {@code Value2}
     */
    @SuppressWarnings("unchecked")
    public static <X, Y> Value2<X, Y> empty() {
        return (Value2<X, Y>) EMPTY;
    }

    /**
     * Constructs an instance with the described value.
     *
     * @param value2 the value to describe; it's the caller's responsibility to
     *        ensure the value is non-{@code null} unless creating the singleton
     *        instance returned by {@code empty()}.
     */
    /* package */ Value2(@Nullable B value1, @Nullable C value2) {
        this.optValue1 = Optional.ofNullable(value1);
        this.optValue2 = Optional.ofNullable(value2);
    }

    /**
     * Copy constructor so we don't have to unwrap Optionals.
     */
    @SuppressWarnings("unchecked")
    /* package */ Value2(Optional<? extends B> optValue1, Optional<? extends C> optValue2) {
        this.optValue1 = (Optional<B>) optValue1;
        this.optValue2 = (Optional<C>) optValue2;
    }

    /**
     * Creates a copy of this Value2, with an updated last value.
     */
    private <D> Value2<B, D> copyReplaceLast(Optional<? extends D> newValue2) {
        return new Value2<>(optValue1, newValue2);
    }

    /**
     * Creates a copy of this Value2, with an updated last value.
     */
    private <D> Value2<B, D> copyReplaceLast(@Nullable D newValue2) {
        return new Value2<>(optValue1, Optional.ofNullable(newValue2));
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @apiNote
     * The preferred alternative to this method is {@link #orElseThrow()}.
     *
     * @return the non-{@code null} value described by this {@code Value2}
     * @throws NoSuchElementException if no value is present
     */
    public C get() {
        return optValue2.get();
    }

    /**
     * True if all values are non-null.
     */
    public boolean isPresent() {
        return optValue1.isPresent() && optValue2.isPresent();
    }

    /**
     * True if any value is null.
     */
    public boolean isEmpty() {
        return optValue1.isEmpty() || optValue2.isEmpty();
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
    public Value2<B, C> ifPresent(CheckedConsumer<? super C> action) {
        optValue2.ifPresent(action.unchecked());
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
    public Value2<B, C> ifPresentOrElse(CheckedConsumer<? super C> action, CheckedRunnable emptyAction) {
        optValue2.ifPresentOrElse(action.unchecked(), emptyAction.unchecked());
        return this;
    }

    /**
     * Perform the given action using all values, if present.
     * If any value is empty, does nothing.
     *
     * @return this, for chaining
     */
    public Value2<B, C> ifAllPresent(CheckedConsumer2<? super B, ? super C> action) {

        if (isPresent()) {
            action.unchecked().accept(optValue1.get(), optValue2.get());
        }

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
    public Value2<B, C> ifEmpty(CheckedRunnable emptyAction) {

        if (isEmpty()) {
            emptyAction.unchecked().run();
        }

        return this;
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code Value2} describing the value, otherwise returns an
     * empty {@code Value2}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code Value2} describing the value of this
     *         {@code Value2}, if a value is present and the value matches the
     *         given predicate, otherwise an empty {@code Value2}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public Value2<B, C> filter(CheckedPredicate<? super C> predicate) {
        return isEmpty() ? this : copyReplaceLast(optValue2.filter(predicate.unchecked()));
    }

    /**
     * If a value is present, returns an {@code Value2} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code Value2}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Value2}.
     *
     * @apiNote
     * This method supports post-processing on {@code Value2} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code Value2<Path>}:
     *
     * <pre>{@code
     *     Value2<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code Value2<URI>}, and then
     * {@code map} returns an {@code Value2<Path>} for the desired
     * URI if one exists.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the value returned from the mapping function
     * @return an {@code Value2} describing the result of applying a mapping
     *         function to the value of this {@code Value2}, if a value is
     *         present, otherwise an empty {@code Value2}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> Value2<B, U> map(CheckedFunction1<? super C, ? extends U> mapper) {
        return isEmpty() ? empty() : copyReplaceLast(optValue2.map(mapper.unchecked()));
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code Value2}-bearing mapping function to the value, otherwise returns
     * an empty {@code Value2}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already an {@code Value2}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code Value2}.
     *
     * @param <D> The type of value of the {@code Value2} returned by the
     *            mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code Value2}-bearing mapping
     *         function to the value of this {@code Value2}, if a value is
     *         present, otherwise an empty {@code Value2}
     * @throws NullPointerException if the mapping function is {@code null} or
     *         returns a {@code null} result
     */
    public <D> Value2<B, D> flatMap(CheckedFunction1<? super C, ? extends Optional<? extends D>> mapper) {
        return isEmpty()
            ? empty()
            : copyReplaceLast(optValue2.flatMap(mapper.unchecked()));
    }

    /**
     * Reduce all values to a single stack value.
     */
    public <D> Value1<D> reduceAll(CheckedFunction2<? super B, ? super C, ? extends D> reducer) {
        return isEmpty()
            ? Value1.empty()
            : ValueStack.ofNullable(reducer.unchecked().apply(optValue1.get(), optValue2.get()));
    }

    /**
     * Reduce all values to a single stack value.
     */
    @SuppressWarnings("unchecked")
    public <D> Value1<D> flatReduceAll(CheckedFunction2<? super B, ? super C, Value1<? extends D>> reducer) {
        return isEmpty()
            ? Value1.empty()
            : (Value1<D>) reducer.unchecked().apply(optValue1.get(), optValue2.get());
    }

    /**
     * If a value is present, returns an {@code Value2} describing the value,
     * otherwise returns an {@code Value2} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value2}
     *        to be returned
     * @return returns an {@code Value2} describing the value of this
     *         {@code Value2}, if a value is present, otherwise an
     *         {@code Value2} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public Value2<B, C> or(CheckedFunction0<? extends Optional<? extends C>> supplier) {
        return isPresent() ? this : copyReplaceLast(supplier.unchecked().apply());
    }

    /**
     * If the last added value is empty, use the given value function along with
     * all other present values to create a new value.
     */
    public Value2<B, C> or(CheckedFunction1<? super B, ? extends C> valueFunction) {
        return isPresent() ? this : copyReplaceLast(valueFunction.unchecked().apply(optValue1.get()));
    }

    /**
     * If a value is present, returns an {@code Value2} describing the value,
     * otherwise returns an {@code Value2} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value2}
     *        to be returned
     * @return returns an {@code Value2} describing the value of this
     *         {@code Value2}, if a value is present, otherwise an
     *         {@code Value2} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public Value2<B, C> or(Optional<? extends C> useIfEmpty) {
        return isPresent() ? this : copyReplaceLast(useIfEmpty);
    }

    /**
     * If a value is present, returns an {@code Value2} describing the value,
     * otherwise returns an {@code Value2} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value2}
     *        to be returned
     * @return returns an {@code Value2} describing the value of this
     *         {@code Value2}, if a value is present, otherwise an
     *         {@code Value2} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public Value2<B, C> or(Value1<? extends C> useIfEmpty) {
        return isPresent() ? this : copyReplaceLast(useIfEmpty.toOptional());
    }

    /**
     * If a value is present, returns an {@code Value2} describing the value,
     * otherwise returns an {@code Value2} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Value2}
     *        to be returned
     * @return returns an {@code Value2} describing the value of this
     *         {@code Value2}, if a value is present, otherwise an
     *         {@code Value2} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public Value2<B, C> or(C useIfEmpty) {
        return isPresent() ? this : copyReplaceLast(useIfEmpty);
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote
     * This method can be used to transform a {@code Stream} of optional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Value2<B, C>> os = ..
     *     Stream<C> s = os.flatMap(Value2::stream)
     * }</pre>
     *
     * @return the optional value as a {@code Stream}
     * @since 9
     */
    public Stream<C> stream() {
        return optValue2.stream();
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
        return optValue2.orElse(other);
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
        return optValue2.orElseGet(supplier);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Value2}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    public C orElseThrow() {
        return optValue2.orElseThrow();
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
        return optValue2.orElseThrow(exceptionSupplier.unchecked());
    }

    /**
     * Converts the last value to a Java {@link Optional}.
     */
    public Optional<C> toOptional() {
        return optValue2;
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Value2}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Value2} and;
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

        if (!(obj instanceof Value2<?, ?> optObj)) {
            return false;
        }

        return optValue1.equals(optObj.optValue1)
            && optValue2.equals(optObj.optValue2);
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
        return Objects.hash(optValue1, optValue2);
    }

    /**
     * Returns a non-empty string representation of this {@code Value2}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @implSpec
     * If a value is present the result must include its string representation
     * in the result.  Empty and present {@code Value2}s must be unambiguously
     * differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return "Value2[%s,%s]".formatted(
            optValue1.map(String::valueOf).orElse("null"),
            optValue2.map(String::valueOf).orElse("null"));
    }
}
