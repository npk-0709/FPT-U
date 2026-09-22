import java.util.ArrayList;
import java.util.List;

// 1. Lớp Animal đại diện cho thông tin động vật
class Animal {
    private String code;
    private String name;
    private double weight;

    public Animal(String code, String name, double weight) {
        this.code = code;
        this.name = name;
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public void display() {
        // Định dạng hiển thị số thực (ví dụ 10.0 -> 10, 12.5 -> 12.5)
        String weightStr = (weight == (long) weight) ? String.format("%d", (long) weight) : String.valueOf(weight);
        System.out.println(code + "," + name + "," + weightStr);
    }
}

// 2. Lớp NodeAnimal làm nút trong Danh sách liên kết đôi
class NodeAnimal {
    Animal data;
    NodeAnimal next;
    NodeAnimal prev;

    public NodeAnimal(Animal data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public void display() {
        if (data != null) {
            data.display();
        }
    }
}

// 3. Lớp AnimalManager quản lý danh sách bằng Doubly Linked List
class AnimalManager {
    private NodeAnimal head;
    private NodeAnimal tail;

    public AnimalManager() {
        this.head = null;
        this.tail = null;
    }

    // Thêm một con vật vào đầu danh sách
    public void addHead(String code, String name, double weight) {
        Animal animal = new Animal(code, name, weight);
        NodeAnimal newNode = new NodeAnimal(animal);

        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
    }

    // Thêm một con vật vào cuối danh sách
    public void addTail(String code, String name, double weight) {
        Animal animal = new Animal(code, name, weight);
        NodeAnimal newNode = new NodeAnimal(animal);

        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    // Xóa một con vật khỏi đầu danh sách
    public void removeHead() {
        if (head == null) return;

        if (head == tail) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
    }

    // Xóa một con vật khỏi cuối danh sách
    public void removeTail() {
        if (tail == null) return;

        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
    }

    // In ra danh sách tính từ đầu đến cuối
    public void display() {
        NodeAnimal current = head;
        while (current != null) {
            current.display();
            current = current.next;
        }
    }

    // Đếm số lượng con vật
    public int count() {
        int count = 0;
        NodeAnimal current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    // Lấy ra node chứa con vật có mã số (code), nếu không có trả về null
    public NodeAnimal getAnimalById(String code) {
        NodeAnimal current = head;
        while (current != null) {
            if (current.data.getCode().equals(code)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // Tìm các con vật có tên chứa x và cân nặng > y, trả về danh sách các Node
    public List<NodeAnimal> search(String x, double y) {
        List<NodeAnimal> result = new ArrayList<>();
        NodeAnimal current = head;
        while (current != null) {
            if (current.data.getName().contains(x) && current.data.getWeight() > y) {
                result.add(current);
            }
            current = current.next;
        }
        return result.isEmpty() ? null : result;
    }

    // Thêm một node vào sau node có mã là code
    public void insertBehind(String code, String newCode, String newName, double newWeight) {
        NodeAnimal targetNode = getAnimalById(code);
        if (targetNode == null) return;

        Animal newAnimal = new Animal(newCode, newName, newWeight);
        NodeAnimal newNode = new NodeAnimal(newAnimal);

        newNode.next = targetNode.next;
        newNode.prev = targetNode;

        if (targetNode.next != null) {
            targetNode.next.prev = newNode;
        } else {
            tail = newNode; // Nếu chèn sau tail thì newNode thành tail mới
        }
        targetNode.next = newNode;
    }
}

// 4. Lớp Main chạy thử nghiệm theo đúng kịch bản trong ảnh
public class Main {
    public static void main(String[] args) {
        AnimalManager alist = new AnimalManager();

        alist.addHead("A01", "PE", 12.5);
        alist.addTail("A02", "KEY", 12.5);
        alist.addHead("A03", "KEY", 10);
        alist.addTail("A04", "MIC", 12);
        alist.addHead("A05", "KEY", 15);

        alist.display();
        System.out.println("-------------------");

        alist.removeHead();
        System.out.println(alist.count());

        List<NodeAnimal> searchResult = alist.search("KEY", 5);
        if (searchResult != null) {
            for (NodeAnimal item : searchResult) {
                item.display();
            }
        }

        System.out.println("-------------------");

        NodeAnimal tmp = alist.getAnimalById("A02");
        if (tmp != null) {
            if (tmp.prev != null) tmp.prev.display();
            tmp.display();
            if (tmp.next != null) tmp.next.display();
        }

        System.out.println("-------------------");

        alist.insertBehind("A02", "A015", "PETER", 17.5);
        alist.display();

        System.out.println("-------------------");

        alist.removeTail();
        alist.display();
    }
}