
public class SpecCala extends Cala {

    private int color;

    public SpecCala(int color, String owner, int price) {
        super(owner, price);
        this.color = color;
    }

    public int getValue() {
        if (color % 2 != 0) {  
            return getPrice() - 3;
        } else {  
            return getPrice() + 7;
        }
    }

    public void setData() {
        String owner = getOwner();
        if (owner != null && owner.length() >= 2) {
            owner = owner.charAt(0) + "XX" + owner.substring(2);
        }
        setOwner(owner);
    }

    @Override
    public String toString() {
        return tooString() +"\n"+ tooString() + ", " + color;
    }
    
    public String tooString(){
        return super.toString();
    }

}
