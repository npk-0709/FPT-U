# TÀI LIỆU HỆ THỐNG QUẢN LÝ KHO HÀNG (WAREHOUSE INVENTORY MANAGEMENT)
> **Môn học:** CSD201 - Data Structures and Algorithms  
> **Ngôn ngữ:** Java (JDK 8 trở lên)  
> **Kiến trúc:** Cây nhị phân tìm kiếm (BST), Hàng đợi (Queue - FIFO), Ngăn xếp (Stack - LIFO)

---

## 1. TỔNG QUAN HỆ THỐNG & TÍNH NĂNG CHÍNH

Hệ thống mô phỏng việc quản lý kho hàng thực tế kết hợp giải thuật và cấu trúc dữ liệu:
- **Quản lý danh mục sản phẩm bằng Binary Search Tree (BST):** Quản lý sản phẩm dựa trên khóa chính là mã vạch (`barcode`), cho phép tìm kiếm, thêm, xóa với độ phức tạp trung bình $O(\log n)$ và duyệt danh mục đã sắp xếp theo thứ tự mã vạch tăng dần (In-Order Traversal - $O(n)$).
- **Quản lý xuất/nhập tồn kho theo lô (Batch):** Mỗi sản phẩm có các lô hàng với số lượng, ngày nhập và hạn sử dụng riêng biệt:
  - **Chiến lược FIFO (First In, First Out) sử dụng Queue:** Áp dụng cho các mặt hàng có hạn sử dụng (thực phẩm, đồ uống, sữa...). Lô nào nhập trước hoặc hết hạn sớm sẽ được xuất trước để tránh tồn đọng hàng quá hạn.
  - **Chiến lược LIFO (Last In, First Out) sử dụng Stack:** Áp dụng cho các mặt hàng không yêu cầu hạn sử dụng ngắn, hàng xếp chồng theo khối hoặc linh kiện điện tử, vật liệu (lấy từ đỉnh xuống).
- **Tự động kiểm tra hạn sử dụng:** Trong quá trình xuất kho, hệ thống tự phát hiện các lô đã hết hạn (`isExpired()`) để hủy bỏ và cảnh báo, tiếp tục xuất từ các lô còn hạn.
- **Chế độ Demo trực quan & Tương tác:** Cung cấp sẵn các kịch bản demo cho FIFO, LIFO, BST và menu tương tác nhập xuất bằng bàn phím.

---

## 2. BẢNG PHÂN TÍCH CẤU TRÚC DỮ LIỆU & ĐỘ PHỨC TẠP (CSD201)

| Cấu trúc dữ liệu | Lớp đại diện | Mục đích sử dụng | Thao tác | Độ phức tạp |
|---|---|---|---|---|
| **Binary Search Tree (BST)** | `ProductBST` | Quản lý danh mục sản phẩm theo mã vạch (`barcode`) | Tìm kiếm (`search`)<br>Chèn (`insert`)<br>Xóa (`delete`)<br>Duyệt In-Order | $O(\log n)$ (TB), $O(n)$ (Xấu nhất)<br>$O(\log n)$ (TB), $O(n)$ (Xấu nhất)<br>$O(\log n)$ (TB), $O(n)$ (Xấu nhất)<br>$O(n)$ |
| **Queue (Hàng đợi)** | `FIFOInventory` (`LinkedList`) | Quản lý các lô hàng theo chiến lược Nhập trước - Xuất trước | Nhập lô (`offer`)<br>Xuất lô (`poll`)<br>Xem đỉnh (`peek`) | $O(1)$<br>$O(1)$<br>$O(1)$ |
| **Stack (Ngăn xếp)** | `LIFOInventory` (`Stack`) | Quản lý các lô hàng theo chiến lược Nhập sau - Xuất trước | Nhập lô (`push`)<br>Xuất lô (`pop`)<br>Xem đỉnh (`peek`) | $O(1)$<br>$O(1)$<br>$O(1)$ |

---

## 3. CHI TIẾT CÁC LỚP (CLASSES) VÀ PHƯƠNG THỨC (METHODS)

### 3.1. Lớp `Product` (`warehouse.Product`)
Đại diện cho thông tin định danh của một mặt hàng trong danh mục chung.

#### Thuộc tính (Attributes)
- `private String barcode`: Mã vạch của sản phẩm (khóa duy nhất dùng làm key trong cây BST).
- `private String name`: Tên sản phẩm.
- `private String category`: Danh mục/ngành hàng (Ví dụ: "Thuc pham", "Dien tu").
- `private double price`: Đơn giá sản phẩm (VND).

#### Phương thức (Methods)
- `Product(String barcode, String name, String category, double price)`: Constructor khởi tạo sản phẩm.
- `String getBarcode()`: Trả về mã vạch sản phẩm.
- `String getName()`: Trả về tên sản phẩm.
- `String getCategory()`: Trả về danh mục.
- `double getPrice()`: Trả về giá bán.
- `String toString()`: Định dạng chuỗi hiển thị gồm mã vạch, tên, danh mục và giá tiền theo cột chuẩn.

---

### 3.2. Lớp `ProductBatch` (`warehouse.ProductBatch`)
Đại diện cho một lô hàng cụ thể của sản phẩm được nhập vào kho.

#### Thuộc tính (Attributes)
- `private String batchId`: Mã định danh lô hàng (ví dụ: `B001`, `B002`).
- `private int quantity`: Số lượng sản phẩm còn lại trong lô.
- `private LocalDate importDate`: Ngày nhập lô hàng vào kho.
- `private LocalDate expiryDate`: Ngày hết hạn sử dụng của lô hàng.

#### Phương thức (Methods)
- `ProductBatch(String batchId, int quantity, LocalDate importDate, LocalDate expiryDate)`: Constructor khởi tạo thông tin lô hàng.
- `String getBatchId()`: Lấy mã lô hàng.
- `int getQuantity()`: Lấy số lượng hàng hiện có trong lô.
- `void setQuantity(int q)`: Cập nhật số lượng hàng trong lô sau khi xuất bớt.
- `LocalDate getImportDate()`: Lấy ngày nhập kho.
- `LocalDate getExpiryDate()`: Lấy hạn sử dụng.
- `boolean isExpired()`: So sánh `expiryDate` với ngày hiện tại (`LocalDate.now()`). Trả về `true` nếu lô hàng đã hết hạn.
- `String toString()`: Chuỗi hiển thị định dạng lô hàng, kèm cảnh báo `<<HET HAN!>>` nếu đã quá date.

---

### 3.3. Lớp `FIFOInventory` (`warehouse.FIFOInventory`)
Quản lý luồng xuất/nhập kho cho một sản phẩm theo chiến lược **FIFO** bằng cấu trúc **Queue**.

#### Thuộc tính (Attributes)
- `private Queue<ProductBatch> queue`: Hàng đợi chứa các lô hàng (`LinkedList` implement `Queue`).
- `private String productBarcode`: Mã vạch sản phẩm áp dụng.
- `private int totalQuantity`: Tổng số lượng tồn kho của sản phẩm qua các lô.

#### Phương thức (Methods)
- `FIFOInventory(String productBarcode)`: Constructor khởi tạo hàng đợi rỗng cho sản phẩm.
- `void importBatch(ProductBatch batch)`: Enqueue (`queue.offer(batch)`) lô hàng vào cuối hàng đợi; cộng dồn `totalQuantity`.
- `int exportGoods(int requestedQty)`: Thực hiện xuất hàng theo FIFO:
  - Lấy phần tử ở đầu hàng đợi (`queue.peek()`).
  - Nếu lô bị hết hạn (`isExpired()`), thông báo và loại bỏ khỏi hàng đợi (`queue.poll()`).
  - Nếu số lượng trong lô $\le$ số lượng cần xuất: xuất toàn bộ lô và dequeue (`queue.poll()`).
  - Nếu số lượng trong lô $>$ số lượng cần xuất: trừ trực tiếp số lượng lô hiện tại và hoàn tất.
  - Lặp lại cho đến khi đủ số lượng yêu cầu hoặc rỗng kho. Trả về số lượng thực tế xuất được.
- `void display()`: Duyệt qua toàn bộ Queue từ đầu đến cuối để hiển thị danh sách các lô hàng đang tồn.
- `int getTotalQuantity()`: Trả về tổng tồn kho.
- `String getProductBarcode()`: Trả về mã vạch sản phẩm.
- `boolean isEmpty()`: Kiểm tra hàng đợi tồn kho có rỗng không.

---

### 3.4. Lớp `LIFOInventory` (`warehouse.LIFOInventory`)
Quản lý luồng xuất/nhập kho cho một sản phẩm theo chiến lược **LIFO** bằng cấu trúc **Stack**.

#### Thuộc tính (Attributes)
- `private Stack<ProductBatch> stack`: Ngăn xếp chứa các lô hàng.
- `private String productBarcode`: Mã vạch sản phẩm.
- `private int totalQuantity`: Tổng số lượng tồn kho.

#### Phương thức (Methods)
- `LIFOInventory(String productBarcode)`: Constructor khởi tạo ngăn xếp rỗng.
- `void importBatch(ProductBatch batch)`: Push (`stack.push(batch)`) lô hàng lên đỉnh ngăn xếp; tăng `totalQuantity`.
- `int exportGoods(int requestedQty)`: Thực hiện xuất hàng theo LIFO:
  - Lấy lô hàng trên đỉnh (`stack.peek()`).
  - Nếu lô hết hạn, loại bỏ khỏi stack (`stack.pop()`).
  - Nếu số lượng trong lô $\le$ số lượng cần: xuất hết và pop khỏi ngăn xếp (`stack.pop()`).
  - Nếu số lượng trong lô $>$ số lượng cần: trừ bớt số lượng của lô đỉnh.
  - Lặp lại đến khi đủ số lượng hoặc rỗng ngăn xếp. Trả về số lượng thực tế đã xuất.
- `void display()`: Duyệt từ đỉnh stack xuống đáy để hiển thị các lô hàng (đánh dấu lô ở ĐỈNH sẽ được xuất tiếp theo).
- `int getTotalQuantity()`: Trả về tổng số lượng tồn.
- `String getProductBarcode()`: Trả về mã vạch.
- `boolean isEmpty()`: Kiểm tra ngăn xếp có rỗng không.

---

### 3.5. Lớp `ProductBST` (`warehouse.ProductBST`)
Cài đặt cấu trúc **Binary Search Tree (Cây nhị phân tìm kiếm)** thuần để quản lý danh mục sản phẩm theo mã vạch.

#### Lớp lồng nhau (Inner Class) `Node`
- `Product product`: Dữ liệu sản phẩm lưu tại node.
- `Node left`: Con trỏ tới node con bên trái (mã vạch nhỏ hơn).
- `Node right`: Con trỏ tới node con bên phải (mã vạch lớn hơn).

#### Thuộc tính (Attributes)
- `private Node root`: Node gốc của cây BST.
- `private int size`: Tổng số lượng sản phẩm trong cây.

#### Phương thức (Methods)
- `ProductBST()`: Khởi tạo cây rỗng (`root = null`, `size = 0`).
- `boolean insert(Product product)`: Thêm sản phẩm vào cây. Kiểm tra trùng mã trước khi gọi `insertRec`.
- `private Node insertRec(Node node, Product product)`: Đệ quy so sánh chuỗi `barcode` bằng `compareTo` để tìm vị trí thích hợp (nhỏ sang trái, lớn sang phải).
- `Product search(String barcode)`: Tìm kiếm sản phẩm theo mã vạch. Gọi `searchRec`.
- `private Node searchRec(Node node, String barcode)`: Đệ quy tìm kiếm trên cây nhị phân: so khớp mã vạch để rẽ trái hoặc phải tương ứng.
- `boolean delete(String barcode)`: Xóa sản phẩm khỏi cây. Gọi `deleteRec`.
- `private Node deleteRec(Node node, String barcode)`: Đệ quy xóa node trên BST xử lý cả 3 trường hợp:
  1. Node lá (không có con): Trả về `null`.
  2. Node có 1 con: Nối trực tiếp con duy nhất lên vị trí node bị xóa.
  3. Node có 2 con: Tìm node nhỏ nhất ở cây con bên phải (`in-order successor` thông qua `findMin`), thay thế giá trị và xóa node successor đó.
- `private Node findMin(Node node)`: Tìm node có giá trị nhỏ nhất (đi tận cùng về phía bên trái).
- `void inOrderDisplay()`: Duyệt cây theo thứ tự giữa (In-Order: Trái $\rightarrow$ Gốc $\rightarrow$ Phải), in bảng danh mục sản phẩm tự động sắp xếp theo thứ tự mã vạch tăng dần.
- `private void inOrderRec(Node node)`: Hàm đệ quy hỗ trợ duyệt In-Order.
- `List<Product> searchByCategory(String category)`: Tìm tất cả sản phẩm thuộc một danh mục cụ thể bằng cách duyệt cây.
- `private void searchByCategoryRec(Node node, String category, List<Product> result)`: Hàm đệ quy gom sản phẩm theo danh mục.
- `void displayTree()`: Vẽ sơ đồ cấu trúc hình cây phân cấp trực quan dạng ASCII lên màn hình console.
- `private void displayTreeRec(Node node, String prefix, boolean isLeft)`: Đệ quy in cấu trúc cây kèm đường nối nét vẽ.
- `int getSize()`: Lấy số lượng node hiện tại trong cây.
- `boolean isEmpty()`: Kiểm tra cây có rỗng hay không.

---

### 3.6. Lớp `WarehouseSystem` (`warehouse.WarehouseSystem`)
Lớp điều phối trung tâm tích hợp toàn bộ hệ thống kho hàng.

#### Thuộc tính (Attributes)
- `private ProductBST productCatalog`: Cây BST quản lý danh mục sản phẩm theo mã vạch.
- `private Map<String, FIFOInventory> fifoInventories`: Bảng ánh xạ từ mã vạch sang kho hàng FIFO tương ứng.
- `private Map<String, LIFOInventory> lifoInventories`: Bảng ánh xạ từ mã vạch sang kho hàng LIFO tương ứng.
- `private Map<String, String> strategyMap`: Lưu chiến lược xuất hàng đã đăng ký cho từng mã vạch (`"FIFO"` hoặc `"LIFO"`).
- `private int batchCounter`: Biến đếm sinh mã lô hàng tự động (`B001`, `B002`,...).

#### Phương thức (Methods)
- `WarehouseSystem()`: Khởi tạo hệ thống và các cấu trúc liên quan.
- `boolean registerProduct(Product product, String strategy)`: Thêm sản phẩm vào danh mục BST và khởi tạo kho hàng (Queue hoặc Stack) tương ứng với chiến lược được chọn.
- `void importGoods(String barcode, int quantity, LocalDate expiryDate)`: Tìm sản phẩm trong BST. Nếu tồn tại, tạo lô hàng mới và nhập vào kho FIFO/LIFO tương ứng.
- `void exportGoods(String barcode, int quantity)`: Tìm sản phẩm và gọi xuất hàng theo đúng chiến lược (FIFO qua Queue hoặc LIFO qua Stack).
- `void searchProduct(String barcode)`: Tìm kiếm thông tin sản phẩm từ BST và hiển thị số lượng tồn kho hiện tại.
- `void displayCatalog()`: Gọi duyệt In-Order cây BST để in danh mục hàng hóa.
- `void displayBSTStructure()`: In sơ đồ phân nhánh của cây BST.
- `void displayInventory(String barcode)`: In chi tiết từng lô hàng đang có của sản phẩm tương ứng.
- `void displayAllInventory()`: Tổng hợp và in bảng tình trạng tồn kho của tất cả các sản phẩm trong hệ thống.
- `void removeProduct(String barcode)`: Xóa sản phẩm khỏi cây BST và loại bỏ dữ liệu kho liên quan.

---

### 3.7. Lớp `Main` (`warehouse.Main`)
Điểm khởi chạy ứng dụng (Entry point), chứa hàm `main` và giao diện điều khiển dạng Console Menu.

#### Thuộc tính (Attributes)
- `static Scanner scanner`: Đối tượng đọc dữ liệu từ bàn phím.
- `static WarehouseSystem warehouse`: Thể hiện hệ thống kho dùng chung.

#### Phương thức (Methods)
- `public static void main(String[] args)`: Vòng lặp điều hướng menu chính.
- `static void initSampleData()`: Nạp sẵn dữ liệu mẫu (sản phẩm thực phẩm, đồ điện tử và các lô hàng).
- `static void demoFIFO()`: Kịch bản chạy tự động chứng minh nguyên lý Queue: xuất các lô sữa nhập trước và gần hết hạn trước.
- `static void demoLIFO()`: Kịch bản chạy tự động chứng minh nguyên lý Stack: xuất các lô bóng đèn mới nhất ở đỉnh stack trước.
- `static void demoBST()`: Kịch bản chạy tự động kiểm chứng các thao tác trên BST (Duyệt In-order, hiển thị cây, tìm kiếm, thêm mới, xóa node có con).
- `static void interactiveMode()`: Mở menu cho người dùng tự tay nhập các thao tác.
- `static void interactiveRegister()`: Đọc dữ liệu từ bàn phím để đăng ký sản phẩm mới.
- `static void interactiveImport()`: Đọc dữ liệu nhập kho lô hàng.
- `static void interactiveExport()`: Đọc dữ liệu xuất kho.
- `static void printMainMenu()`: In giao diện menu chính.
- `static void printSeparator(String title)`: In dòng phân cách tiêu đề.
- `static void pause()`: Tạm dừng màn hình chờ bấm Enter.
- `static int readInt(String prompt)`: Nhập số nguyên an toàn (bắt lỗi `NumberFormatException`).
- `static double readDouble(String prompt)`: Nhập số thực an toàn.
- `static String readString(String prompt)`: Đọc chuỗi văn bản không rỗng.

---

## 4. HƯỚNG DẪN BIÊN DỊCH VÀ CHẠY ỨNG DỤNG

### Chạy bằng NetBeans
1. Mở NetBeans IDE $\rightarrow$ **Open Project** $\rightarrow$ Chọn thư mục `WarehouseInventoryManagement`.
2. Mở file `src/warehouse/Main.java`.
3. Nhấn tổ hợp phím **Shift + F6** (hoặc chuột phải chọn **Run File**).

### Chạy bằng Command Line / PowerShell
```powershell
# Di chuyển đến thư mục dự án
cd "c:\Users\Khuong\Desktop\FPTU\CSD201\WarehouseInventoryManagement"

# Biên dịch tất cả file java vào thư mục out
javac -encoding UTF-8 -d out src\warehouse\*.java

# Chạy chương trình
java -cp out warehouse.Main
```
