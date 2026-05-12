public class Main {
    public static void main(String[] args) {
        MyProcessor processor = new MyProcessor();
        String input = "Hello, World!";
        System.out.println("Reversed String: " + processor.reverseString(input));
        System.out.println("Number of Vowels: " + processor.countVowels(input));
        System.out.println("Number of Special Characters: " + processor.countSpecialCharacters(input));
    }
}