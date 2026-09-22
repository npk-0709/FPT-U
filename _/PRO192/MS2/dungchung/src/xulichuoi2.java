/**
 * ============================================================
 *   JAVA PRO - CHEAT SHEET ĐẦY ĐỦ CHO THI
 *   Bao gồm: Xử lý chuỗi, Số, Mảng 2D, OOP, Exception
 * ============================================================
 *
 * ===== MỤC LỤC =====
 * 1. XỬ LÝ CHUỖI (STRING PROCESSING)          - dòng ~40
 * 2. XỬ LÝ SỐ   (NUMBER PROCESSING)           - dòng ~280
 * 3. XỬ LÝ MẢNG (ARRAY PROCESSING)            - dòng ~460
 * 4. XỬ LÝ MẢNG 2 CHIỀU (2D ARRAY)            - dòng ~560
 * 5. OOP - CLASS MẪU ĐẦY ĐỦ                   - dòng ~650
 * 6. EXCEPTION HANDLING                        - dòng ~780
 * 7. QUICK REFERENCE                           - dòng ~850
 */

public class JavaPRO_CheatSheet {

    // ==============================================================
    // PHẦN 1: XỬ LÝ CHUỖI (STRING PROCESSING) — 30 phương thức
    // ==============================================================

    /** Đếm số ký tự trong chuỗi */
    public int countCharacters(String str) {
        if (str == null) return 0;
        return str.length();
    }

    /** Chuyển chuỗi thành chữ hoa */
    public String toUpperCase(String str) {
        if (str == null) return null;
        return str.toUpperCase();
    }

    /** Chuyển chuỗi thành chữ thường */
    public String toLowerCase(String str) {
        if (str == null) return null;
        return str.toLowerCase();
    }

    /** Viết hoa chữ cái đầu tiên */
    public String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /** Viết hoa chữ cái đầu mỗi từ */
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

    /** Đảo ngược chuỗi */
    public String reverseString(String str) {
        if (str == null) return null;
        return new StringBuilder(str).reverse().toString();
    }

    /** Kiểm tra chuỗi đối xứng (palindrome) */
    public boolean isPalindrome(String str) {
        if (str == null) return false;
        String cleaned = str.replaceAll("\\s+", "").toLowerCase();
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    /** Đếm số lần xuất hiện của ký tự trong chuỗi */
    public int countOccurrences(String str, char ch) {
        if (str == null) return 0;
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) count++;
        }
        return count;
    }

    /** Đếm số từ trong chuỗi */
    public int countWords(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        return str.trim().split("\\s+").length;
    }

    /** Xóa khoảng trắng thừa (giữ nguyên 1 khoảng giữa từ) */
    public String trimSpaces(String str) {
        if (str == null) return null;
        return str.trim().replaceAll("\\s+", " ");
    }

    /** Xóa toàn bộ khoảng trắng */
    public String removeAllSpaces(String str) {
        if (str == null) return null;
        return str.replaceAll("\\s", "");
    }

    /** Thay thế chuỗi con */
    public String replaceSubstring(String str, String oldStr, String newStr) {
        if (str == null) return null;
        return str.replace(oldStr, newStr);
    }

    /** Thay thế ký tự tại vị trí index bằng chuỗi khác
     *  Ví dụ: replaceAt("Hello", 1, "XX") → "HXXllo" */
    public String replaceAt(String str, int index, String replacement) {
        if (str == null || index < 0 || index >= str.length()) return str;
        return str.substring(0, index) + replacement + str.substring(index + 1);
    }

    /** Kiểm tra chuỗi có chứa chuỗi con */
    public boolean contains(String str, String substring) {
        if (str == null || substring == null) return false;
        return str.contains(substring);
    }

    /** Tìm vị trí đầu tiên của chuỗi con */
    public int indexOf(String str, String substring) {
        if (str == null || substring == null) return -1;
        return str.indexOf(substring);
    }

    /** Cắt chuỗi con */
    public String substring(String str, int start, int end) {
        if (str == null || start < 0 || end > str.length() || start > end) return null;
        return str.substring(start, end);
    }

    /** Nối các chuỗi với dấu phân cách */
    public String joinStrings(String[] strings, String delimiter) {
        if (strings == null || strings.length == 0) return "";
        return String.join(delimiter, strings);
    }

    /** Tách chuỗi theo dấu phân cách */
    public String[] splitString(String str, String delimiter) {
        if (str == null) return new String[0];
        return str.split(delimiter);
    }

    /** Kiểm tra chuỗi rỗng */
    public boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /** Kiểm tra chuỗi rỗng hoặc chỉ có khoảng trắng */
    public boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /** Đếm số nguyên âm (a, e, i, o, u) trong chuỗi */
    public int countVowels(String str) {
        if (str == null) return 0;
        int count = 0;
        String vowels = "aeiouAEIOU";
        for (int i = 0; i < str.length(); i++) {
            if (vowels.indexOf(str.charAt(i)) != -1) count++;
        }
        return count;
    }

    /** Đếm số phụ âm trong chuỗi */
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

    /** Xóa tất cả ký tự số khỏi chuỗi */
    public String removeDigits(String str) {
        if (str == null) return null;
        return str.replaceAll("\\d", "");
    }

    /** Chỉ giữ lại ký tự số */
    public String keepDigitsOnly(String str) {
        if (str == null) return null;
        return str.replaceAll("\\D", "");
    }

    /** Kiểm tra chuỗi có chứa chữ hoa không */
    public boolean hasUpperCase(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray())
            if (Character.isUpperCase(c)) return true;
        return false;
    }

    /** Đổi chữ hoa thành thường và ngược lại */
    public String toggleCase(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray())
            sb.append(Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c));
        return sb.toString();
    }

    /** Đếm số chữ cái (letter) trong chuỗi */
    public int countLetters(String str) {
        if (str == null) return 0;
        int count = 0;
        for (char ch : str.toCharArray())
            if (Character.isLetter(ch)) count++;
        return count;
    }

    /** Đếm ký tự đặc biệt (không phải chữ, số, khoảng trắng) */
    public int countSpecialChars(String str) {
        if (str == null) return 0;
        int count = 0;
        for (char ch : str.toCharArray())
            if (!Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch)) count++;
        return count;
    }

    /** Xóa các ký tự trùng nhau, giữ lần xuất hiện đầu */
    public String removeDuplicateChars(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray())
            if (sb.indexOf(String.valueOf(c)) == -1) sb.append(c);
        return sb.toString();
    }


    // ==============================================================
    // PHẦN 2: XỬ LÝ SỐ (NUMBER PROCESSING) — 30 phương thức
    // ==============================================================

    /** Kiểm tra số chẵn */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /** Kiểm tra số lẻ */
    public boolean isOdd(int number) {
        return number % 2 != 0;
    }

    /** Kiểm tra số nguyên tố */
    public boolean isPrime(int number) {
        if (number < 2) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }

    /** Tính giai thừa */
    public long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Số phải không âm");
        if (n == 0 || n == 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    /** Tính lũy thừa */
    public double power(double base, int exponent) {
        return Math.pow(base, exponent);
    }

    /** Tìm số lớn nhất trong mảng */
    public int findMax(int[] numbers) {
        if (numbers == null || numbers.length == 0)
            throw new IllegalArgumentException("Mảng không được rỗng");
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++)
            if (numbers[i] > max) max = numbers[i];
        return max;
    }

    /** Tìm số nhỏ nhất trong mảng */
    public int findMin(int[] numbers) {
        if (numbers == null || numbers.length == 0)
            throw new IllegalArgumentException("Mảng không được rỗng");
        int min = numbers[0];
        for (int i = 1; i < numbers.length; i++)
            if (numbers[i] < min) min = numbers[i];
        return min;
    }

    /** Tính tổng các số trong mảng */
    public int sum(int[] numbers) {
        if (numbers == null) return 0;
        int total = 0;
        for (int num : numbers) total += num;
        return total;
    }

    /** Tính trung bình cộng */
    public double average(int[] numbers) {
        if (numbers == null || numbers.length == 0) return 0;
        return (double) sum(numbers) / numbers.length;
    }

    /** Giá trị tuyệt đối */
    public int abs(int number) {
        return Math.abs(number);
    }

    /** Ước chung lớn nhất (GCD) */
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

    /** Bội chung nhỏ nhất (LCM) */
    public int lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(a * b) / gcd(a, b);
    }

    /** Đảo ngược số nguyên */
    public int reverseNumber(int number) {
        int reversed = 0;
        int temp = Math.abs(number);
        while (temp > 0) {
            reversed = reversed * 10 + temp % 10;
            temp /= 10;
        }
        return number < 0 ? -reversed : reversed;
    }

    /** Đếm số chữ số */
    public int countDigits(int number) {
        if (number == 0) return 1;
        return String.valueOf(Math.abs(number)).length();
    }

    /** Tính tổng các chữ số */
    public int sumOfDigits(int number) {
        int sum = 0;
        number = Math.abs(number);
        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }

    /** Kiểm tra số đối xứng (palindrome) */
    public boolean isNumberPalindrome(int number) {
        return number == reverseNumber(number);
    }

    /** Kiểm tra số Armstrong (tổng lũy thừa các chữ số = chính số đó)
     *  Ví dụ: 153 = 1³ + 5³ + 3³ */
    public boolean isArmstrong(int number) {
        int original = Math.abs(number);
        int sum = 0;
        int digits = countDigits(original);
        int temp = original;
        while (temp > 0) {
            int digit = temp % 10;
            sum += (int) Math.pow(digit, digits);
            temp /= 10;
        }
        return sum == original;
    }

    /** Chuyển số thành chuỗi */
    public String numberToString(int number) {
        return String.valueOf(number);
    }

    /** Chuyển chuỗi thành số */
    public int stringToNumber(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Chuỗi không hợp lệ: " + str);
        }
    }

    /** Làm tròn số thực đến n chữ số thập phân */
    public double round(double number, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(number * scale) / scale;
    }

    /** Kiểm tra số hoàn hảo (bằng tổng các ước số, trừ chính nó)
     *  Ví dụ: 6 = 1 + 2 + 3 */
    public boolean isPerfectNumber(int number) {
        if (number < 1) return false;
        int sum = 0;
        for (int i = 1; i <= number / 2; i++)
            if (number % i == 0) sum += i;
        return sum == number;
    }

    /** Tính số Fibonacci thứ n */
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

    /** Sắp xếp mảng tăng dần (Bubble Sort) */
    public int[] sortAscending(int[] numbers) {
        if (numbers == null) return null;
        int[] sorted = numbers.clone();
        for (int i = 0; i < sorted.length - 1; i++)
            for (int j = 0; j < sorted.length - i - 1; j++)
                if (sorted[j] > sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
        return sorted;
    }

    /** Sắp xếp mảng giảm dần */
    public int[] sortDescending(int[] numbers) {
        if (numbers == null) return null;
        int[] sorted = numbers.clone();
        for (int i = 0; i < sorted.length - 1; i++)
            for (int j = 0; j < sorted.length - i - 1; j++)
                if (sorted[j] < sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
        return sorted;
    }

    /** Kiểm tra số chính phương */
    public boolean isPerfectSquare(int n) {
        if (n < 0) return false;
        int sq = (int) Math.sqrt(n);
        return sq * sq == n;
    }

    /** Kiểm tra số nguyên tố đôi (p và p+2 đều là số nguyên tố) */
    public boolean isTwinPrime(int n) {
        return isPrime(n) && isPrime(n + 2);
    }

    /** Lấy danh sách ước số của n (không kể n) */
    public java.util.List<Integer> getDivisors(int n) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int i = 1; i <= n / 2; i++)
            if (n % i == 0) list.add(i);
        return list;
    }

    /** Đếm số ước của n */
    public int countDivisors(int n) {
        int count = 0;
        for (int i = 1; i <= Math.sqrt(n); i++)
            if (n % i == 0)
                count += (i == n / i) ? 1 : 2;
        return count;
    }

    /** Chuyển số thập phân sang nhị phân */
    public String toBinary(int n) {
        return Integer.toBinaryString(n);
    }

    /** Chuyển số thập phân sang hệ 16 */
    public String toHex(int n) {
        return Integer.toHexString(n);
    }

    /** Chuyển số thập phân sang hệ 8 */
    public String toOctal(int n) {
        return Integer.toOctalString(n);
    }

    /** Chuyển chuỗi nhị phân sang số thập phân */
    public int fromBinary(String bin) {
        return Integer.parseInt(bin, 2);
    }


    // ==============================================================
    // PHẦN 3: XỬ LÝ MẢNG 1 CHIỀU (ARRAY PROCESSING)
    // ==============================================================

    /** Tìm kiếm tuyến tính (Linear Search) — trả về index, -1 nếu không có */
    public int linearSearch(int[] arr, int target) {
        if (arr == null) return -1;
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == target) return i;
        return -1;
    }

    /** Tìm kiếm nhị phân (Binary Search) — mảng phải đã sắp xếp tăng dần */
    public int binarySearch(int[] arr, int target) {
        if (arr == null) return -1;
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    /** Đảo ngược mảng (tạo mảng mới) */
    public int[] reverseArray(int[] arr) {
        if (arr == null) return null;
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
            result[i] = arr[arr.length - 1 - i];
        return result;
    }

    /** Đảo ngược mảng tại chỗ (in-place) */
    public void reverseInPlace(int[] arr) {
        int l = 0, r = arr.length - 1;
        while (l < r) {
            int tmp = arr[l]; arr[l++] = arr[r]; arr[r--] = tmp;
        }
    }

    /** Đếm số phần tử chẵn trong mảng */
    public int countEven(int[] arr) {
        int count = 0;
        for (int x : arr) if (x % 2 == 0) count++;
        return count;
    }

    /** Đếm số phần tử dương trong mảng */
    public int countPositive(int[] arr) {
        int count = 0;
        for (int x : arr) if (x > 0) count++;
        return count;
    }

    /** Lọc chỉ giữ số dương trong mảng */
    public int[] filterPositive(int[] arr) {
        int count = 0;
        for (int x : arr) if (x > 0) count++;
        int[] result = new int[count];
        int idx = 0;
        for (int x : arr) if (x > 0) result[idx++] = x;
        return result;
    }

    /** Kiểm tra mảng có chứa phần tử target không */
    public boolean containsElement(int[] arr, int target) {
        for (int x : arr) if (x == target) return true;
        return false;
    }

    /** Xóa phần tử tại vị trí index */
    public int[] removeAt(int[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) return arr;
        int[] result = new int[arr.length - 1];
        for (int i = 0, j = 0; i < arr.length; i++)
            if (i != index) result[j++] = arr[i];
        return result;
    }

    /** Chèn phần tử vào vị trí index */
    public int[] insertAt(int[] arr, int index, int value) {
        int[] result = new int[arr.length + 1];
        for (int i = 0; i < index; i++) result[i] = arr[i];
        result[index] = value;
        for (int i = index; i < arr.length; i++) result[i + 1] = arr[i];
        return result;
    }

    /** Kiểm tra mảng là tăng dần */
    public boolean isAscending(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++)
            if (arr[i] > arr[i + 1]) return false;
        return true;
    }

    /** Xóa phần tử trùng trong mảng */
    public int[] removeDuplicates(int[] arr) {
        java.util.LinkedHashSet<Integer> set = new java.util.LinkedHashSet<>();
        for (int x : arr) set.add(x);
        int[] result = new int[set.size()];
        int i = 0;
        for (int x : set) result[i++] = x;
        return result;
    }


    // ==============================================================
    // PHẦN 4: XỬ LÝ MẢNG 2 CHIỀU (2D ARRAY PROCESSING)
    // ==============================================================

    /** In mảng 2 chiều */
    public void print2DArray(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) System.out.printf("%4d", val);
            System.out.println();
        }
    }

    /** Tính tổng toàn bộ mảng 2 chiều */
    public int sumAll(int[][] matrix) {
        int total = 0;
        for (int[] row : matrix) for (int val : row) total += val;
        return total;
    }

    /** Tính tổng một hàng */
    public int sumRow(int[][] matrix, int row) {
        int sum = 0;
        for (int val : matrix[row]) sum += val;
        return sum;
    }

    /** Tính tổng một cột */
    public int sumCol(int[][] matrix, int col) {
        int sum = 0;
        for (int[] row : matrix) sum += row[col];
        return sum;
    }

    /** Tính tổng đường chéo chính (i == j) */
    public int sumMainDiagonal(int[][] matrix) {
        int sum = 0;
        for (int i = 0; i < matrix.length; i++) sum += matrix[i][i];
        return sum;
    }

    /** Tính tổng đường chéo phụ (i + j == n-1) */
    public int sumAntiDiagonal(int[][] matrix) {
        int sum = 0, n = matrix.length;
        for (int i = 0; i < n; i++) sum += matrix[i][n - 1 - i];
        return sum;
    }

    /** Tìm giá trị lớn nhất trong mảng 2 chiều */
    public int max2D(int[][] matrix) {
        int max = matrix[0][0];
        for (int[] row : matrix)
            for (int val : row)
                if (val > max) max = val;
        return max;
    }

    /** Tìm giá trị nhỏ nhất trong mảng 2 chiều */
    public int min2D(int[][] matrix) {
        int min = matrix[0][0];
        for (int[] row : matrix)
            for (int val : row)
                if (val < min) min = val;
        return min;
    }

    /** Kiểm tra ma trận đối xứng (matrix[i][j] == matrix[j][i]) */
    public boolean isSymmetric(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < matrix[i].length; j++)
                if (matrix[i][j] != matrix[j][i]) return false;
        return true;
    }

    /** Chuyển vị ma trận (transpose) */
    public int[][] transpose(int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int[][] result = new int[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result[j][i] = matrix[i][j];
        return result;
    }

    /** Nhân 2 ma trận */
    public int[][] multiplyMatrix(int[][] a, int[][] b) {
        int n = a.length, m = b[0].length, k = b.length;
        int[][] result = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                for (int p = 0; p < k; p++)
                    result[i][j] += a[i][p] * b[p][j];
        return result;
    }

    /** Đếm phần tử âm trong mảng 2 chiều */
    public int countNegative2D(int[][] matrix) {
        int count = 0;
        for (int[] row : matrix)
            for (int val : row)
                if (val < 0) count++;
        return count;
    }


    // ==============================================================
    // PHẦN 5: OOP — CLASS MẪU ĐẦY ĐỦ (Student)
    // ==============================================================

    /**
     * CLASS MẪU: Student với đầy đủ constructor, getter/setter, toString, equals
     *
     *  public class Student {
     *      private String name;
     *      private int age;
     *      private double gpa;
     *
     *      // Constructor không tham số
     *      public Student() {}
     *
     *      // Constructor đầy đủ
     *      public Student(String name, int age, double gpa) {
     *          this.name = name;
     *          this.age  = age;
     *          this.gpa  = gpa;
     *      }
     *
     *      // Getter
     *      public String getName() { return name; }
     *      public int getAge()     { return age; }
     *      public double getGpa()  { return gpa; }
     *
     *      // Setter với validation
     *      public void setName(String name) { this.name = name; }
     *      public void setAge(int age) {
     *          if (age < 0) throw new IllegalArgumentException("Tuổi không hợp lệ");
     *          this.age = age;
     *      }
     *      public void setGpa(double gpa) {
     *          if (gpa < 0 || gpa > 4.0) throw new IllegalArgumentException("GPA không hợp lệ");
     *          this.gpa = gpa;
     *      }
     *
     *      @Override
     *      public String toString() {
     *          return "Student{name='" + name + "', age=" + age + ", gpa=" + gpa + "}";
     *      }
     *
     *      @Override
     *      public boolean equals(Object o) {
     *          if (this == o) return true;
     *          if (!(o instanceof Student)) return false;
     *          Student s = (Student) o;
     *          return age == s.age && name.equals(s.name);
     *      }
     *  }
     *
     * -------------------------------------------------------
     * KẾ THỪA (INHERITANCE):
     *
     *  public class GradStudent extends Student {
     *      private String major;
     *
     *      public GradStudent(String name, int age, double gpa, String major) {
     *          super(name, age, gpa);   // gọi constructor cha TRƯỚC TIÊN
     *          this.major = major;
     *      }
     *
     *      public String getMajor() { return major; }
     *
     *      @Override
     *      public String toString() {
     *          return super.toString() + ", major=" + major;
     *      }
     *  }
     *
     * -------------------------------------------------------
     * ABSTRACT CLASS:
     *
     *  public abstract class Shape {
     *      protected String color;
     *      public Shape(String color) { this.color = color; }
     *      public abstract double area();        // bắt buộc override
     *      public String getColor() { return color; }  // dùng được luôn
     *  }
     *
     *  public class Circle extends Shape {
     *      private double radius;
     *      public Circle(String color, double r) { super(color); this.radius = r; }
     *      @Override
     *      public double area() { return Math.PI * radius * radius; }
     *  }
     *
     * -------------------------------------------------------
     * INTERFACE:
     *
     *  public interface Printable {
     *      void print();                         // abstract
     *      default String format() {             // có sẵn, có thể override
     *          return "Default";
     *      }
     *  }
     *
     *  public class Report implements Printable {
     *      @Override
     *      public void print() { System.out.println("Printing..."); }
     *  }
     */


    // ==============================================================
    // PHẦN 6: EXCEPTION HANDLING
    // ==============================================================

    /**
     * CÁCH DÙNG TRY-CATCH-FINALLY:
     *
     *  public int divide(int a, int b) {
     *      try {
     *          return a / b;
     *      } catch (ArithmeticException e) {
     *          System.out.println("Lỗi: " + e.getMessage());
     *          return 0;
     *      } finally {
     *          System.out.println("Luôn chạy dù có lỗi hay không");
     *      }
     *  }
     *
     * -------------------------------------------------------
     * BẮT NHIỀU LOẠI EXCEPTION:
     *
     *  try {
     *      // code
     *  } catch (ArithmeticException e) {
     *      System.out.println("Lỗi chia 0");
     *  } catch (NullPointerException e) {
     *      System.out.println("Null pointer");
     *  } catch (Exception e) {
     *      System.out.println("Lỗi khác: " + e.getMessage());
     *  }
     *
     * -------------------------------------------------------
     * CUSTOM EXCEPTION:
     *
     *  public class InvalidAgeException extends Exception {
     *      public InvalidAgeException(String msg) { super(msg); }
     *  }
     *
     *  // Ném exception tự định nghĩa
     *  public void setAge(int age) throws InvalidAgeException {
     *      if (age < 0 || age > 150)
     *          throw new InvalidAgeException("Tuổi không hợp lệ: " + age);
     *      this.age = age;
     *  }
     *
     *  // Gọi phương thức có throws
     *  try {
     *      obj.setAge(-5);
     *  } catch (InvalidAgeException e) {
     *      System.out.println(e.getMessage());
     *  }
     *
     * -------------------------------------------------------
     * CÁC EXCEPTION HAY GẶP:
     *   NullPointerException          — gọi method trên null
     *   ArrayIndexOutOfBoundsException — vượt index mảng
     *   NumberFormatException          — Integer.parseInt chuỗi sai
     *   ArithmeticException            — chia cho 0
     *   ClassCastException             — ép kiểu sai
     *   StackOverflowError             — đệ quy vô tận
     *   IllegalArgumentException       — tham số không hợp lệ
     */


    // ==============================================================
    // PHẦN 7: QUICK REFERENCE — PATTERN HAY DÙNG
    // ==============================================================

    /**
     * ===== DUYỆT TỪNG KÝ TỰ TRONG CHUỖI =====
     *   for (char c : str.toCharArray()) { ... }
     *   for (int i = 0; i < str.length(); i++) { char c = str.charAt(i); ... }
     *
     * ===== XÂY DỰNG CHUỖI KẾT QUẢ → DÙNG StringBuilder =====
     *   StringBuilder sb = new StringBuilder();
     *   sb.append(c);           // thêm vào cuối
     *   sb.insert(0, c);        // thêm vào đầu
     *   sb.delete(i, j);        // xóa từ i đến j
     *   sb.reverse();           // đảo ngược
     *   String result = sb.toString();
     *
     * ===== SWAP 2 PHẦN TỬ =====
     *   int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
     *
     * ===== ĐỆ QUY — LUÔN CÓ BASE CASE =====
     *   public int sum(int n) {
     *       if (n == 0) return 0;       // base case — bắt buộc
     *       return n + sum(n - 1);      // recursive case
     *   }
     *
     * ===== ARRAYLIST =====
     *   import java.util.ArrayList;
     *   import java.util.Collections;
     *   ArrayList<Integer> list = new ArrayList<>();
     *   list.add(3);
     *   list.remove(Integer.valueOf(2));         // xóa giá trị
     *   list.remove(0);                          // xóa theo index
     *   Collections.sort(list);                  // tăng dần
     *   Collections.sort(list, Collections.reverseOrder()); // giảm dần
     *   list.contains(3);  list.size();  list.get(0);
     *
     * ===== String METHODS HAY DÙNG =====
     *   s.length()              s.charAt(i)         s.indexOf("x")
     *   s.substring(a, b)       s.replace("a","b")  s.toUpperCase()
     *   s.trim()                s.split(",")         s.equals(t)
     *   s.equalsIgnoreCase(t)   s.startsWith("x")   s.endsWith("x")
     *   s.contains("x")         String.valueOf(n)    s.isEmpty()
     *
     * ===== Math METHODS =====
     *   Math.abs(n)    Math.max(a,b)   Math.min(a,b)
     *   Math.pow(b,e)  Math.sqrt(n)    Math.round(d)
     *   Math.floor(d)  Math.ceil(d)    Math.PI
     *   Math.random()  — trả về [0.0, 1.0)
     *   (int)(Math.random() * n)  — random 0 đến n-1
     *
     * ===== Character METHODS =====
     *   Character.isLetter(c)       Character.isDigit(c)
     *   Character.isUpperCase(c)    Character.isLowerCase(c)
     *   Character.isWhitespace(c)   Character.isLetterOrDigit(c)
     *   Character.toUpperCase(c)    Character.toLowerCase(c)
     *   (int) c    — lấy mã ASCII
     *   (char) n   — đổi số thành ký tự
     *   'A'=65  'Z'=90  'a'=97  'z'=122  '0'=48  '9'=57
     *
     * ===== KIỂM TRA NULL/RỖNG TRƯỚC KHI XỬ LÝ =====
     *   if (str == null || str.isEmpty()) return ...;
     *   if (arr == null || arr.length == 0) return ...;
     *
     * ===== CHUYỂN ĐỔI KIỂU =====
     *   int    → String : String.valueOf(n)  hoặc  n + ""
     *   String → int    : Integer.parseInt(s)
     *   int    → double : (double) n
     *   double → int    : (int) d   (cắt bỏ phần thập phân)
     *   char   → int    : (int) c   (lấy mã ASCII)
     *   int    → char   : (char) n
     */

    // ============================================================
    //  HẾT FILE — Chúc thi tốt! 🎯
    // ============================================================
}
