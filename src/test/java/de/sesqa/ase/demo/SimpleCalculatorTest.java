package de.sesqa.ase.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;


public class SimpleCalculatorTest {

    @Test
    @DisplayName("Adding 2 and 3 should return 5")
    void add_givenTwoAndThree_ExpectFive() {
        SimpleCalculator calculator = new SimpleCalculator();

        // Arrange: Create an instance of SimpleCalculator
        final int a = 2;
        final int b = 3;

        final int result = calculator.add(a, b);
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Subtracting 7 and 3 should return 4")
    void subtract_givenSevenAndThree_ExpectFour() {

        SimpleCalculator calculator = new SimpleCalculator();

        // Arrange: Create an instance of SimpleCalculator
        final int a = 7;
        final int b = 3;

        // Act: Call the subtract method
        final int result = calculator.subtract(a, b);

        // Assert: Check if the result is as expected
        assertThat(result).isEqualTo(4);

    }


    @Test
     void subtract_givenFiveAndThree_ExpectTwo() {
        SimpleCalculator calculator = new SimpleCalculator();

        final int a = 5;
        final int b = 3;

        final int result = calculator.subtract(a, b);
        assertEquals(2, result);
    }







    @Test
    @DisplayName("Multiplying 4 and 5 should return 20")
    void multiply_givenFourAndFive_ExpectTwenty() {
        SimpleCalculator calculator = new SimpleCalculator();
        int result = calculator.multiply(4, 5);
        assertEquals(20, result, "Expected 20 but got " + result);
    }

    @Test
    @DisplayName("Dividing 6 by 3 should return 2")
    void divide_givenSixAndThree_ExpectTwo() {
        SimpleCalculator calculator = new SimpleCalculator();

        final int a = 6 ;
        final int b = 3;

        int result = calculator.divide(a, b);
        assertEquals(2, result, "Expected 5 but got " + result);
    }

    @Test
    @DisplayName("Dividing by zero should throw IllegalArgumentException")
    void divideByZero_shouldThrowIllegalArgumentException() {
        SimpleCalculator calculator = new SimpleCalculator();

        final int a = 10;
        final int b = 0;

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.divide(a, b);
        });

    
    }


    
}
