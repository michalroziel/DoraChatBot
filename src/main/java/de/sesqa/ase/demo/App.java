package de.sesqa.ase.demo;

    import com.google.common.base.Joiner;

    import java.util.Arrays;

/**
     * Hello world!
     */
    public class App {
        public static void main(String[] args) {

            System.out.println("Hello World!");

            /*

            // names:
            String[] names = {"Peter", "Paul", "Mary"};

            // Fix: Use the correct variable 'names' instead of 'args'
            String joinedNames = Joiner.on(", ").join(names);

            System.out.println("Hello " + joinedNames + "!");


             */

            int array1[] = {1, 2, 3};
            int array2[] = {4, 5, 6};

            /****
             * Fix: Use Arrays.equals() to compare the contents of the arrays
             * <p>Arrays.equals(array1, array2) returns true if the two arrays are equal in length and contain the same elements in the same order.</p>
             * instead of using the equals method directly on the array reference.
             */

            boolean res = Arrays.equals(array1, array2);
            System.out.println(res);




        }




    }