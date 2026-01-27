public class MyProcesser {
    /**
     =========XỬ LÝ CHUỖI (STRING PROCESSING) - 24 phương thức:
     countCharacters - Đếm số ký tự
     toUpperCase - Chuyển thành chữ hoa
     toLowerCase - Chuyển thành chữ thường
     capitalizeFirst - Viết hoa chữ cái đầu
     capitalizeWords - Viết hoa chữ cái đầu mỗi từ
     reverseString - Đảo ngược chuỗi
     isPalindrome - Kiểm tra palindrome
     countOccurrences - Đếm số lần xuất hiện ký tự
     countWords - Đếm số từ
     trimSpaces - Xóa khoảng trắng thừa
     replaceSubstring - Thay thế chuỗi con
     contains - Kiểm tra chứa chuỗi con
     indexOf - Tìm vị trí chuỗi con
     substring - Cắt chuỗi con
     joinStrings - Nối chuỗi với dấu phân cách
     splitString - Tách chuỗi
     isEmpty - Kiểm tra rỗng
     isBlank - Kiểm tra rỗng/chỉ khoảng trắng
     countVowels - Đếm nguyên âm
     countConsonants - Đếm phụ âm
     removeDigits - Xóa các ký tự số
     keepDigitsOnly - Chỉ giữ ký tự số
     ========XỬ LÝ SỐ (NUMBER PROCESSING) - 28 phương thức:
     isEven - Kiểm tra số chẵn
     isOdd - Kiểm tra số lẻ
     isPrime - Kiểm tra số nguyên tố
     factorial - Tính giai thừa
     power - Tính lũy thừa
     findMax - Tìm số lớn nhất
     findMin - Tìm số nhỏ nhất
     sum - Tính tổng mảng
     average - Tính trung bình
     abs - Giá trị tuyệt đối
     gcd - Ước chung lớn nhất
     lcm - Bội chung nhỏ nhất
     reverseNumber - Đảo ngược số
     countDigits - Đếm chữ số
     sumOfDigits - Tổng các chữ số
     isNumberPalindrome - Kiểm tra số palindrome
     isArmstrong - Kiểm tra số Armstrong
     numberToString - Chuyển số thành chuỗi
     stringToNumber - Chuyển chuỗi thành số
     round - Làm tròn số thực
     isPerfectNumber - Kiểm tra số hoàn hảo
     fibonacci - Tính số Fibonacci
     sortAscending - Sắp xếp tăng dần
     sortDescending - Sắp xếp giảm dần

     */

    /**
     * Đếm số ký tự trong chuỗi
     */
    public int countCharacters(String str) {
        if (str == null) return 0;
        return str.length();
    }

    /**
     * Chuyển chuỗi thành chữ hoa
     */
    public String toUpperCase(String str) {
        if (str == null) return null;
        return str.toUpperCase();
    }

    /**
     * Chuyển chuỗi thành chữ thường
     */
    public String toLowerCase(String str) {
        if (str == null) return null;
        return str.toLowerCase();
    }

    /**
     * Viết hoa chữ cái đầu tiên
     */
    public String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Viết hoa chữ cái đầu mỗi từ
     */
    public String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                result.append(words[i].substring(0, 1).toUpperCase())
                        .append(words[i].substring(1).toLowerCase());
                if (i < words.length - 1) result.append(" ");
            }
        }
        return result.toString();
    }

    /**
     * Đảo ngược chuỗi
     */
    public String reverseString(String str) {
        if (str == null) return null;
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Kiểm tra chuỗi đối xứng (palindrome)
     */
    public boolean isPalindrome(String str) {
        if (str == null) return false;
        String cleaned = str.replaceAll("\\s+", "").toLowerCase();
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    /**
     * Đếm số lần xuất hiện của ký tự trong chuỗi
     */
    public int countOccurrences(String str, char ch) {
        if (str == null) return 0;
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) count++;
        }
        return count;
    }

    /**
     * Đếm số từ trong chuỗi
     */
    public int countWords(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        return str.trim().split("\\s+").length;
    }

    /**
     * Xóa khoảng trắng thừa
     */
    public String trimSpaces(String str) {
        if (str == null) return null;
        return str.trim().replaceAll("\\s+", " ");
    }

    /**
     * Thay thế chuỗi con
     */
    public String replaceSubstring(String str, String oldStr, String newStr) {
        if (str == null) return null;
        return str.replace(oldStr, newStr);
    }

    /**
     * Kiểm tra chuỗi có chứa chuỗi con
     */
    public boolean contains(String str, String substring) {
        if (str == null || substring == null) return false;
        return str.contains(substring);
    }

    /**
     * Tìm vị trí đầu tiên của chuỗi con
     */
    public int indexOf(String str, String substring) {
        if (str == null || substring == null) return -1;
        return str.indexOf(substring);
    }

    /**
     * Cắt chuỗi con
     */
    public String substring(String str, int start, int end) {
        if (str == null || start < 0 || end > str.length() || start > end) {
            return null;
        }
        return str.substring(start, end);
    }

    /**
     * Nối các chuỗi với dấu phân cách
     */
    public String joinStrings(String[] strings, String delimiter) {
        if (strings == null || strings.length == 0) return "";
        return String.join(delimiter, strings);
    }

    /**
     * Tách chuỗi theo dấu phân cách
     */
    public String[] splitString(String str, String delimiter) {
        if (str == null) return new String[0];
        return str.split(delimiter);
    }

    /**
     * Kiểm tra chuỗi rỗng
     */
    public boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Kiểm tra chuỗi rỗng hoặc chỉ có khoảng trắng
     */
    public boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Đếm số nguyên âm trong chuỗi
     */
    public int countVowels(String str) {
        if (str == null) return 0;
        int count = 0;
        String vowels = "aeiouAEIOU";
        for (int i = 0; i < str.length(); i++) {
            if (vowels.indexOf(str.charAt(i)) != -1) count++;
        }
        return count;
    }

    /**
     * Đếm số phụ âm trong chuỗi
     */
    public int countConsonants(String str) {
        if (str == null) return 0;
        int count = 0;
        String vowels = "aeiouAEIOU";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isLetter(ch) && vowels.indexOf(ch) == -1) count++;
        }
        return count;
    }

    /**
     * Xóa tất cả các ký tự số khỏi chuỗi
     */
    public String removeDigits(String str) {
        if (str == null) return null;
        return str.replaceAll("\\d", "");
    }

    /**
     * Xóa tất cả các ký tự không phải số khỏi chuỗi
     */
    public String keepDigitsOnly(String str) {
        if (str == null) return null;
        return str.replaceAll("\\D", "");
    }

    // ==================== NUMBER PROCESSING METHODS ====================

    /**
     * Kiểm tra số chẵn
     */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     * Kiểm tra số lẻ
     */
    public boolean isOdd(int number) {
        return number % 2 != 0;
    }

    /**
     * Kiểm tra số nguyên tố
     */
    public boolean isPrime(int number) {
        if (number < 2) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }

    /**
     * Tính giai thừa
     */
    public long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Số phải không âm");
        if (n == 0 || n == 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Tính lũy thừa
     */
    public double power(double base, int exponent) {
        return Math.pow(base, exponent);
    }

    /**
     * Tìm số lớn nhất trong mảng
     */
    public int findMax(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Mảng không được rỗng");
        }
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) max = numbers[i];
        }
        return max;
    }

    /**
     * Tìm số nhỏ nhất trong mảng
     */
    public int findMin(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Mảng không được rỗng");
        }
        int min = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < min) min = numbers[i];
        }
        return min;
    }

    /**
     * Tính tổng các số trong mảng
     */
    public int sum(int[] numbers) {
        if (numbers == null) return 0;
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }

    /**
     * Tính trung bình cộng
     */
    public double average(int[] numbers) {
        if (numbers == null || numbers.length == 0) return 0;
        return (double) sum(numbers) / numbers.length;
    }

    /**
     * Tính giá trị tuyệt đối
     */
    public int abs(int number) {
        return Math.abs(number);
    }

    /**
     * Tìm ước chung lớn nhất (GCD)
     */
    public int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * Tìm bội chung nhỏ nhất (LCM)
     */
    public int lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(a * b) / gcd(a, b);
    }

    /**
     * Đảo ngược số nguyên
     */
    public int reverseNumber(int number) {
        int reversed = 0;
        int temp = Math.abs(number);
        while (temp > 0) {
            reversed = reversed * 10 + temp % 10;
            temp /= 10;
        }
        return number < 0 ? -reversed : reversed;
    }

    /**
     * Đếm số chữ số
     */
    public int countDigits(int number) {
        if (number == 0) return 1;
        return String.valueOf(Math.abs(number)).length();
    }

    /**
     * Tính tổng các chữ số
     */
    public int sumOfDigits(int number) {
        int sum = 0;
        number = Math.abs(number);
        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }

    /**
     * Kiểm tra số đối xứng (palindrome)
     */
    public boolean isNumberPalindrome(int number) {
        return number == reverseNumber(number);
    }

    /**
     * Kiểm tra số Armstrong (số bằng tổng lũy thừa các chữ số)
     */
    public boolean isArmstrong(int number) {
        int original = Math.abs(number);
        int sum = 0;
        int digits = countDigits(original);
        int temp = original;

        while (temp > 0) {
            int digit = temp % 10;
            sum += Math.pow(digit, digits);
            temp /= 10;
        }
        return sum == original;
    }

    /**
     * Chuyển số thành chuỗi
     */
    public String numberToString(int number) {
        return String.valueOf(number);
    }

    /**
     * Chuyển chuỗi thành số
     */
    public int stringToNumber(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Chuỗi không hợp lệ: " + str);
        }
    }

    /**
     * Làm tròn số thực
     */
    public double round(double number, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(number * scale) / scale;
    }

    /**
     * Kiểm tra số hoàn hảo (bằng tổng các ước số của nó)
     */
    public boolean isPerfectNumber(int number) {
        if (number < 1) return false;
        int sum = 0;
        for (int i = 1; i <= number / 2; i++) {
            if (number % i == 0) sum += i;
        }
        return sum == number;
    }

    /**
     * Tính số Fibonacci thứ n
     */
    public long fibonacci(int n) {
        if (n < 0) throw new IllegalArgumentException("n phải không âm");
        if (n == 0) return 0;
        if (n == 1) return 1;

        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    /**
     * Sắp xếp mảng tăng dần (Bubble Sort)
     */
    public int[] sortAscending(int[] numbers) {
        if (numbers == null) return null;
        int[] sorted = numbers.clone();
        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = 0; j < sorted.length - i - 1; j++) {
                if (sorted[j] > sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }
        return sorted;
    }

    /**
     * Sắp xếp mảng giảm dần
     */
    public int[] sortDescending(int[] numbers) {
        if (numbers == null) return null;
        int[] sorted = numbers.clone();
        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = 0; j < sorted.length - i - 1; j++) {
                if (sorted[j] < sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }
        return sorted;
    }

}
