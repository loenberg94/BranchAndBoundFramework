package com.mal.framework.interfaces;

import com.mal.framework.utils.Node;
import com.mal.framework.abstract_classes.Dataset;

public interface Constraint {
    boolean CheckConstraint(Node node, double[] set);
}
