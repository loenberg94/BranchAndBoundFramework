package com.mal.interfaces;

import com.mal.Node;
import com.mal.abstract_classes.Dataset;

public interface Constraint {
    boolean CheckConstraint(Node node, Dataset set);
}
