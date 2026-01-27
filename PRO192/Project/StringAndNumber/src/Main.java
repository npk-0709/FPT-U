public class Main {
    public static void main(String[] args) {
        MyProcesser processor = new MyProcesser();

        System.out.println("===== DEMO XỬ LÝ CHUỖI (STRING PROCESSING) =====\n");

        // 1. Đếm ký tự
        String text = "Hello World";
        System.out.println("Chuỗi: " + text);
        System.out.println("Số ký tự: " + processor.countCharacters(text));

        // 2. Chuyển đổi chữ hoa/thường
        System.out.println("\nChữ hoa: " + processor.toUpperCase(text));
        System.out.println("Chữ thường: " + processor.toLowerCase(text));
        System.out.println("Viết hoa chữ đầu: " + processor.capitalizeFirst(text));
        System.out.println("Viết hoa mỗi từ: " + processor.capitalizeWords("hello world from java"));

        // 3. Đảo ngược chuỗi
        System.out.println("\nĐảo ngược: " + processor.reverseString(text));

        // 4. Kiểm tra palindrome
        String palindrome = "racecar";
        System.out.println("\n'" + palindrome + "' có phải palindrome? " + processor.isPalindrome(palindrome));

        // 5. Đếm xuất hiện ký tự
        System.out.println("\nSố lần xuất hiện 'l' trong '" + text + "': " + processor.countOccurrences(text, 'l'));

        // 6. Đếm từ
        String sentence = "Java is a powerful programming language";
        System.out.println("\nCâu: " + sentence);
        System.out.println("Số từ: " + processor.countWords(sentence));

        // 7. Xử lý khoảng trắng
        String messy = "  Too   many   spaces  ";
        System.out.println("\nChuỗi gốc: '" + messy + "'");
        System.out.println("Sau khi xử lý: '" + processor.trimSpaces(messy) + "'");

        // 8. Thay thế chuỗi con
        System.out.println("\nThay 'Java' bằng 'Python': " + processor.replaceSubstring(sentence, "Java", "Python"));

        // 9. Tìm kiếm chuỗi con
        System.out.println("\nCó chứa 'powerful'? " + processor.contains(sentence, "powerful"));
        System.out.println("Vị trí 'programming': " + processor.indexOf(sentence, "programming"));

        // 10. Đếm nguyên âm và phụ âm
        System.out.println("\nSố nguyên âm trong '" + text + "': " + processor.countVowels(text));
        System.out.println("Số phụ âm trong '" + text + "': " + processor.countConsonants(text));

        // 11. Xử lý số trong chuỗi
        String mixed = "abc123def456";
        System.out.println("\nChuỗi hỗn hợp: " + mixed);
        System.out.println("Xóa số: " + processor.removeDigits(mixed));
        System.out.println("Chỉ giữ số: " + processor.keepDigitsOnly(mixed));

        System.out.println("\n===== DEMO XỬ LÝ SỐ (NUMBER PROCESSING) =====\n");

        // 1. Kiểm tra chẵn/lẻ
        int num = 15;
        System.out.println("Số: " + num);
        System.out.println("Là số chẵn? " + processor.isEven(num));
        System.out.println("Là số lẻ? " + processor.isOdd(num));

        // 2. Kiểm tra số nguyên tố
        int primeTest = 17;
        System.out.println("\n" + primeTest + " có phải số nguyên tố? " + processor.isPrime(primeTest));

        // 3. Giai thừa
        System.out.println("\nGiai thừa của 5: " + processor.factorial(5));

        // 4. Lũy thừa
        System.out.println("2^10: " + processor.power(2, 10));

        // 5. Mảng số
        int[] numbers = {5, 2, 8, 1, 9, 3};
        System.out.println("\nMảng: [5, 2, 8, 1, 9, 3]");
        System.out.println("Số lớn nhất: " + processor.findMax(numbers));
        System.out.println("Số nhỏ nhất: " + processor.findMin(numbers));
        System.out.println("Tổng: " + processor.sum(numbers));
        System.out.println("Trung bình: " + processor.average(numbers));

        // 6. GCD và LCM
        int a = 48, b = 18;
        System.out.println("\nGCD của " + a + " và " + b + ": " + processor.gcd(a, b));
        System.out.println("LCM của " + a + " và " + b + ": " + processor.lcm(a, b));

        // 7. Đảo ngược số
        int numToReverse = 12345;
        System.out.println("\nSố gốc: " + numToReverse);
        System.out.println("Đảo ngược: " + processor.reverseNumber(numToReverse));

        // 8. Đếm và tính tổng chữ số
        System.out.println("Số chữ số: " + processor.countDigits(numToReverse));
        System.out.println("Tổng các chữ số: " + processor.sumOfDigits(numToReverse));

        // 9. Kiểm tra số palindrome
        int palindromeNum = 12321;
        System.out.println("\n" + palindromeNum + " có phải số palindrome? " + processor.isNumberPalindrome(palindromeNum));

        // 10. Kiểm tra số Armstrong
        int armstrong = 153;
        System.out.println(armstrong + " có phải số Armstrong? " + processor.isArmstrong(armstrong));

        // 11. Chuyển đổi số và chuỗi
        System.out.println("\nChuyển số 100 thành chuỗi: '" + processor.numberToString(100) + "'");
        System.out.println("Chuyển chuỗi '200' thành số: " + processor.stringToNumber("200"));

        // 12. Làm tròn
        double pi = 3.14159265359;
        System.out.println("\nLàm tròn " + pi + " đến 2 chữ số: " + processor.round(pi, 2));

        // 13. Số hoàn hảo
        int perfect = 28;
        System.out.println("\n" + perfect + " có phải số hoàn hảo? " + processor.isPerfectNumber(perfect));

        // 14. Fibonacci
        System.out.println("\nSố Fibonacci thứ 10: " + processor.fibonacci(10));

        // 15. Sắp xếp mảng
        int[] unsorted = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("\nMảng gốc: [64, 34, 25, 12, 22, 11, 90]");
        int[] sorted = processor.sortAscending(unsorted);
        System.out.print("Sắp xếp tăng dần: [");
        for (int i = 0; i < sorted.length; i++) {
            System.out.print(sorted[i] + (i < sorted.length - 1 ? ", " : ""));
        }
        System.out.println("]");

        sorted = processor.sortDescending(unsorted);
        System.out.print("Sắp xếp giảm dần: [");
        for (int i = 0; i < sorted.length; i++) {
            System.out.print(sorted[i] + (i < sorted.length - 1 ? ", " : ""));
        }
        System.out.println("]");
    }
}