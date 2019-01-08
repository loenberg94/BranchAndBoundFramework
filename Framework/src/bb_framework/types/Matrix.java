package bb_framework.types;

import bb_framework.interfaces.Dataset;

public class Matrix implements Dataset {

    int n,m;
    Coefficient[][] elements;

    @Override
    public Coefficient get(int index) {
        return elements[index%n][index/n];
    }

    @Override
    public int size() {
        return n * m;
    }

    public Matrix(Coefficient[][] coefficients){
        this.n = coefficients.length;
        this.m = coefficients[0].length;
        this.elements = coefficients;
    }
}
