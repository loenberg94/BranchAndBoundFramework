package bb_framework.interfaces;

import bb_framework.utils.Node;

public interface Constraint {
    boolean CheckConstraint(Node node, double[] set);
}
