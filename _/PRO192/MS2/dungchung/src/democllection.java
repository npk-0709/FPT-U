import java.util.*;

public class JavaCollectionsCheatSheet {

    public static void main(String[] args) {
        System.out.println("====== TÀI LIỆU ÔN TẬP JAVA COLLECTIONS ======\n");

        demoList();
        System.out.println("------------------------------------------------");
        demoSet();
        System.out.println("------------------------------------------------");
        demoQueueAndDeque();
        System.out.println("------------------------------------------------");
        demoMap();
        System.out.println("------------------------------------------------");
        demoCollectionsUtility();
    }

    /**
     * 1. LIST INTERFACE
     * Đặc điểm: Có thứ tự (insertion order), cho phép các phần tử trùng lặp (duplicates).
     * Truy cập phần tử thông qua index.
     */
    public static void demoList() {
        System.out.println("1. DEMO LIST (ArrayList, LinkedList, Vector, Stack)");

        // 1.1 ArrayList: Mảng động.
        // Ưu điểm: Truy xuất cực nhanh (O(1)).
        // Nhược điểm: Thêm/Xóa ở giữa mảng chậm (O(n)) vì phải dồn phần tử.
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Java");
        arrayList.add("Python");
        arrayList.add("Java"); // Cho phép trùng
        System.out.println("ArrayList: " + arrayList);

        // 1.2 LinkedList: Danh sách liên kết kép.
        // Ưu điểm: Thêm/Xóa ở đầu/cuối/giữa rất nhanh (O(1) nếu đã có node).
        // Nhược điểm: Truy xuất chậm (O(n)) vì phải duyệt từ đầu/cuối.
        List<String> linkedList = new LinkedList<>(Arrays.asList("C++", "C#"));
        linkedList.add(1, "Go");
        System.out.println("LinkedList: " + linkedList);

        // 1.3 Vector: Giống ArrayList nhưng Thread-Safe (Đồng bộ hóa).
        // Nhược điểm: Chậm hơn ArrayList trong môi trường đơn luồng (Single-thread). Thường ít dùng hiện nay.
        List<String> vector = new Vector<>();
        vector.add("Ruby");

        // 1.4 Stack: Kế thừa Vector. Hoạt động theo nguyên tắc LIFO (Last In, First Out).
        // Các hàm đặc trưng: push(), pop(), peek().
        Stack<String> stack = new Stack<>();
        stack.push("Sách Toán");
        stack.push("Sách Văn");
        System.out.println("Stack (LIFO) lấy ra: " + stack.pop()); // Lấy "Sách Văn" ra
    }

    /**
     * 2. SET INTERFACE
     * Đặc điểm: KHÔNG có thứ tự (tùy loại), KHÔNG cho phép các phần tử trùng lặp.
     */
    public static void demoSet() {
        System.out.println("2. DEMO SET (HashSet, LinkedHashSet, TreeSet)");

        // 2.1 HashSet: Dùng Băm (Hashing).
        // Đặc điểm: Nhanh nhất (O(1) cho thêm/xóa/tìm kiếm). KHÔNG duy trì thứ tự chèn.
        Set<Integer> hashSet = new HashSet<>();
        hashSet.add(10);
        hashSet.add(5);
        hashSet.add(20);
        hashSet.add(10); // Sẽ bị bỏ qua vì trùng
        System.out.println("HashSet (Không thứ tự): " + hashSet);

        // 2.2 LinkedHashSet: Kế thừa HashSet, có thêm danh sách liên kết.
        // Đặc điểm: Duy trì THỨ TỰ CHÈN (Insertion Order). Chậm hơn HashSet một chút.
        Set<Integer> linkedHashSet = new LinkedHashSet<>(Arrays.asList(10, 5, 20));
        System.out.println("LinkedHashSet (Thứ tự chèn): " + linkedHashSet);

        // 2.3 TreeSet: Cài đặt dựa trên Red-Black Tree.
        // Đặc điểm: Tự động SẮP XẾP tăng dần (hoặc theo Comparator). O(log n) cho các thao tác.
        Set<Integer> treeSet = new TreeSet<>(Arrays.asList(10, 5, 20));
        System.out.println("TreeSet (Tự động sắp xếp): " + treeSet);
    }

    /**
     * 3. QUEUE & DEQUE INTERFACE
     * Queue: Hàng đợi FIFO (First In, First Out).
     * Deque: Hàng đợi 2 đầu (Double Ended Queue).
     */
    public static void demoQueueAndDeque() {
        System.out.println("3. DEMO QUEUE & DEQUE (PriorityQueue, ArrayDeque)");

        // 3.1 PriorityQueue: Hàng đợi ưu tiên.
        // Đặc điểm: Các phần tử được sắp xếp theo mức độ ưu tiên (mặc định là tăng dần).
        Queue<Integer> pQueue = new PriorityQueue<>();
        pQueue.add(100);
        pQueue.add(10);
        pQueue.add(50);
        System.out.println("PriorityQueue (peek phần tử nhỏ nhất): " + pQueue.peek()); // In ra 10

        // 3.2 ArrayDeque: Hàng đợi 2 đầu dùng mảng.
        // Đặc điểm: Nhanh hơn Stack khi làm LIFO, nhanh hơn LinkedList khi làm FIFO. Khuyên dùng!
        Deque<String> deque = new ArrayDeque<>();
        deque.addFirst("Đầu hàng");
        deque.addLast("Cuối hàng");
        System.out.println("ArrayDeque: " + deque);
    }

    /**
     * 4. MAP INTERFACE
     * Đặc điểm: Lưu trữ theo cặp Key-Value. Key là duy nhất (không trùng), Value có thể trùng.
     * Lưu ý: Map KHÔNG kế thừa Collection interface.
     */
    public static void demoMap() {
        System.out.println("4. DEMO MAP (HashMap, LinkedHashMap, TreeMap, Hashtable)");

        // 4.1 HashMap: Lưu trữ không thứ tự.
        // Đặc điểm: Nhanh nhất cho tìm kiếm/thêm/xóa O(1). Cho phép 1 Key null.
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Apple", 10);
        hashMap.put("Banana", 5);
        hashMap.put("Apple", 20); // Ghi đè value của key "Apple"
        System.out.println("HashMap: " + hashMap);

        // 4.2 LinkedHashMap: Duy trì thứ tự chèn.
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Apple", 10);
        linkedHashMap.put("Banana", 5);
        System.out.println("LinkedHashMap (Thứ tự chèn): " + linkedHashMap);

        // 4.3 TreeMap: Sắp xếp theo Key (Tăng dần).
        // Đặc điểm: O(log n), không cho phép Key null.
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Zebra", 1);
        treeMap.put("Apple", 2);
        System.out.println("TreeMap (Sắp xếp theo Key): " + treeMap);
        
        // 4.4 Hashtable: Giống HashMap nhưng Thread-safe và KHÔNG cho phép Key/Value null. (Legacy, ít dùng)
    }

    /**
     * 5. COLLECTIONS UTILITY CLASS
     * Các hàm tiện ích dùng để thao tác trên các Collection.
     */
    public static void demoCollectionsUtility() {
        System.out.println("5. DEMO LỚP TIỆN ÍCH COLLECTIONS");

        List<Integer> list = new ArrayList<>(Arrays.asList(5, 1, 9, 3));
        
        // Sắp xếp tăng dần
        Collections.sort(list);
        System.out.println("Sort: " + list);
        
        // Đảo ngược
        Collections.reverse(list);
        System.out.println("Reverse: " + list);
        
        // Tìm kiếm nhị phân (Yêu cầu list phải được sort tăng dần trước)
        Collections.sort(list); 
        int index = Collections.binarySearch(list, 5);
        System.out.println("Vị trí của số 5 trong mảng đã sort: " + index);
        
        // Tìm Max, Min
        System.out.println("Max: " + Collections.max(list) + ", Min: " + Collections.min(list));
    }
}

