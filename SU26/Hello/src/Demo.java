
public class Demo {

    private int age; // 30

    public Demo(int age) {
        this.age = age;
    }
    
   
    public String getAge() {
        return "toi " + age + " tuoi ";
    }

    public void setAge(int age) {
        if ((age > 0) & (age < 150)) {
            this.age = age;
        } else {
            System.out.println("Nhap sai tuoi roi, vui long nhap lai !");
        }

    }

    @Override
    public String toString() {
        return "Demo{" + "age=" + age + '}';
    }

}
