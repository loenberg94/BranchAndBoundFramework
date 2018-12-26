package bb_framework.interfaces;

import bb_framework.types.Coefficient;

public interface Dataset {
    Coefficient get(int index);
    int size();
}
