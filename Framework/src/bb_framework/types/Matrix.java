package bb_framework.types;

import bb_framework.interfaces.Dataset;

public class Matrix implements Dataset {

    int n,m;
    Coefficient[][] elements;

    @Override
    public Coefficient get(DatasetIndex index) {
        DatasetMatrixIndex dmi = (DatasetMatrixIndex) index;
        return elements[dmi.i][dmi.j];
    }

    @Override
    public DatasetIndex next(DatasetIndex index) {
        DatasetMatrixIndex dmi = (DatasetMatrixIndex) index;
        if(dmi.i == n - 1){
            return new DatasetMatrixIndex(0,dmi.j + 1);
        }
        return new DatasetMatrixIndex(dmi.i + 1,dmi.j);
    }

    @Override
    public int size() {
        return n * m;
    }

    public Matrix(Coefficient[][] coefficients, int n, int m){
        this.n = n;
        this.m = m;
        this.elements = coefficients;
    }
}
