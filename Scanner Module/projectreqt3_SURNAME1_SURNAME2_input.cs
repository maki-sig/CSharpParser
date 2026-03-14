using System;

namespace TestApp {
    class Program {
        static void Main(string[] args) {
            // Test 1: No whitespace operators
            int x=10;
            
            // Test 2: String literal preserving spaces
            string greeting = "Hello World";
            
            /* Test 3: Large block comment
               This should be skipped entirely by the scanner.
               bool fakeKeyword = true;
               int fakeNumber = 999;
             */
             
            if (x == 10 && x <= 20) {
                x++;
            }
            
            Console.WriteLine(greeting);
        }
    }
}
