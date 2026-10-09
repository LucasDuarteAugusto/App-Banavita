package org.junit.Assert
fun assertEquals(expected: Any?, actual: Any?) { check(expected == actual) { "Expected $expected, got $actual" } }
fun assertEquals(expected: Double, actual: Double, tolerance: Double) { check(kotlin.math.abs(expected - actual) <= tolerance) { "Expected $expected, got $actual" } }
fun assertNull(value: Any?) { check(value == null) { "Expected null, got $value" } }
fun assertTrue(value: Boolean) { check(value) }
fun assertFalse(value: Boolean) { check(!value) }
