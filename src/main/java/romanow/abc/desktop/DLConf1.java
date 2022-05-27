package romanow.abc.desktop;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import romanow.abc.core.Pair;

import static romanow.abc.desktop.LEP500NNPanel.*;

public class DLConf1 extends DLConfiguration{
    @Override
    public MultiLayerConfiguration create(int hiddenCount, int numInputs, int numOutputs, int seed) {
        MultiLayerConfiguration conf1 = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(0.1))
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(hiddenCount)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(hiddenCount).nOut(hiddenCount)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        //Переопределить глобальную активацию TANH с помощью softmax для этого слоя
                        .activation(Activation.SOFTMAX)
                        .nIn(hiddenCount).nOut(numOutput).build())
                .build();
        return conf1;
        }

    @Override
    public String getName() {
        return "Модель 1";
        }
}
