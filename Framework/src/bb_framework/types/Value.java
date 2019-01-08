package bb_framework.types;

public class Value extends Coefficient<Double> {
    public Value(Double value){
        super(value);
    }

    @Override
    public String toString() {
        return "Value";
    }
}
