package bb_framework.interfaces;

import bb_framework.utils.Constraint;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import javafx.scene.control.ProgressBar;

import java.util.HashMap;

/**
 *  Interface for user to specify lower- and upperbound funcions
 */
public interface Bound {

    /**
     * @param node used to get current state.
     * @param set can be used if necessary by user
     * @return lowerbound Double value for current node state
     * */
    double Lowerbound(Node node, double[] set, Problem problem);

    /**
     * @param node used to get current state.
     * @param set can be used if necessary by user
     * @return upperbound Double value for current node state
     * */
    double Upperbound(Node node, double[] set, Problem problem);
}
