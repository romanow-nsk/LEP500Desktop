package romanow.abc.desktop;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import romanow.abc.core.Pair;

public abstract class DLConfiguration {
    public abstract String getName();
    public abstract MultiLayerConfiguration create(int hiddenLayerCount, int numInputs, int numOutputs, int seed, int l1, int l2, double ll);
}
