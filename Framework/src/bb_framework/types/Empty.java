package bb_framework.types;

public class Empty extends Coefficient {
    public Empty() {
        super(new Object());
    }

    @Override
    public String toString() {
        return "Empty";
    }
}