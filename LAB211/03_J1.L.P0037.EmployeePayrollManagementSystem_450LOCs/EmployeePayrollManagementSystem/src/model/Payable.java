package model;

/**
 * Hợp đồng tính lương cho mọi đối tượng có thể nhận lương.
 * Tách riêng hành vi tính lương ra interface giúp minh hoạ tính đa hình:
 * client chỉ cần biết tới {@code Payable} mà không phụ thuộc lớp cụ thể.
 */
public interface Payable {

    /** Tính lương tháng của đối tượng. */
    double calculateSalary();
}
