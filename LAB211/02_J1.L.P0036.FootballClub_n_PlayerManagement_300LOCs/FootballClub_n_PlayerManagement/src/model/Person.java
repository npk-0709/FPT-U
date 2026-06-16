package model;

import java.io.Serializable;

/**
 * Lớp trừu tượng đại diện cho một người (Person).
 * Áp dụng Abstraction + Inheritance: Person là gốc, Player kế thừa.
 */
public abstract class Person implements Serializable {

    protected String id;
    protected String name;

    /** Constructor mặc định */
    public Person() {
    }

    /**
     * Constructor có tham số.
     * @param id   mã định danh
     * @param name tên người
     */
    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // ===== Getter / Setter =====
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Phương thức trừu tượng — mỗi lớp con tự định nghĩa cách hiển thị.
     * Áp dụng Polymorphism.
     * @return chuỗi thông tin hiển thị
     */
    public abstract String getDisplayInfo();
}
