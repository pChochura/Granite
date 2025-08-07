package com.pointlessapps.granite.mica.model

internal class ClosedFloatRange(
    start: Float,
    endInclusive: Float
) : ClosedFloatingPointRange<Float> {
    private val _start = start
    private val _endInclusive = endInclusive
    override val start: Float get() = _start
    override val endInclusive: Float get() = _endInclusive

    override fun lessThanOrEquals(a: Float, b: Float): Boolean = a <= b

    override fun contains(value: Float): Boolean = value >= _start && value <= _endInclusive
    override fun isEmpty(): Boolean = !(_start <= _endInclusive)

    override fun equals(other: Any?): Boolean {
        return other is ClosedFloatRange && (isEmpty() && other.isEmpty() ||
                _start == other._start && _endInclusive == other._endInclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else 31 * _start.hashCode() + _endInclusive.hashCode()
    }

    override fun toString(): String = "$_start..$_endInclusive"
}
