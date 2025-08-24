public class StringManipulation {
    public static void main(String[] args) {
        // 1. String literal
        String str1 = "Java Programming";

        // 2. new String() constructor
        String str2 = new String("Java Programming");

        // 3. Character array
        char[] charArray = {'J', 'a', 'v', 'a', ' ', 'P', 'r', 'o', 'g', 'r', 'a', 'm', 'm', 'i', 'n', 'g'};
        String str3 = new String(charArray);

        System.out.println("str1: " + str1);
        System.out.println("str2: " + str2);
        System.out.println("str3: " + str3);
// TODO: Compare the strings using == and .equals()

        System.out.println("\nComparing with == operator:");
        System.out.println("str1 == str2: " + (str1 == str2));  
        System.out.println("str1 == str3: " + (str1 == str3));  
        System.out.println("str2 == str3: " + (str2 == str3));  

        
        System.out.println("\nComparing with .equals() method:");
        System.out.println("str1.equals(str2): " + str1.equals(str2));  
        System.out.println("str1.equals(str3): " + str1.equals(str3));  
        System.out.println("str2.equals(str3): " + str2.equals(str3));  
        
        System.out.println("\nExplanation:");
        System.out.println("== compares references (memory addresses), so it returns true only if both refer to the same object.");
        System.out.println(".equals() compares the content of the strings, so it returns true if the text is the same.");

        String quote = "Programming Quote:\n\"Code is poetry\" - Unknown\nPath: C:\\Java\\Projects";
        System.out.println("\n" + quote);
    }
}
