package bb_framework.types;

import bb_framework.interfaces.Dataset;

public class Vector implements Dataset {

    int n;
    Coefficient[] elements;

    @Override
    public Coefficient get(DatasetIndex index) {
        return elements[((DatasetVectorIndex) index).i];
    }

    @Override
    public DatasetIndex next(DatasetIndex index) {
        return new DatasetVectorIndex(((DatasetVectorIndex) index).i + 1);
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
