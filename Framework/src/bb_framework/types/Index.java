package bb_framework.types;

public class Index extends Coefficient<Integer> {
    public Index(int i){
        super(i);
    }

    @Override
    public String toString() {
        return "Index";
    }
}
