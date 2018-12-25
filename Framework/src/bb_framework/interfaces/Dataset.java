package bb_framework.interfaces;

import bb_framework.types.Coefficient;
import bb_framework.types.DatasetIndex;

public interface Dataset {
    Coefficient get(DatasetIndex index);
    DatasetIndex next(DatasetIndex index);
    int size();
}
