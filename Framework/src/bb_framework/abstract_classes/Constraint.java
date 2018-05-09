package bb_framework.abstract_classes;

import bb_framework.utils.Node;

public abstract class Constraint {
    abstract boolean checkConstraint(Node node, double[] set);
    abstract boolean canBranch();
}
