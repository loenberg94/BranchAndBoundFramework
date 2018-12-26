package bb_framework.types;

import bb_framework.interfaces.Dataset;

public class Vector implements Dataset {

    int n;
    Coefficient[] elements;

    @Override
    public Coefficient get(int index) {
        return elements[index];
    }

    @Override
    public int size() {
        return n;
    }


    public Vector(Coefficient[] coefficients, int n){
        this.n = n;
        elements = coefficients;
    }
}
