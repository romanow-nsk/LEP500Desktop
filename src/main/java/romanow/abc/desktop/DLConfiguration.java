package romanow.abc.desktop;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import romanow.abc.core.Pair;

public abstract class DLConfiguration {
    public abstract Pair<String, MultiLayerConfiguration> create();
}
