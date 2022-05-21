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

public class DLConf2 extends DLConfiguration{
    @Override
    public Pair<String, MultiLayerConfiguration> create() {
        MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(0.1))
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(25)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(25).nOut(15)
                        .build())
                .layer(2, new DenseLayer.Builder().nIn(15).nOut(numHiddenLayers)
                        .build())
                .layer(3, new DenseLayer.Builder().nIn(numHiddenLayers).nOut(numHiddenLayers)
                        .build())
                .layer(4, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        //Переопределить глобальную активацию TANH с помощью softmax для этого слоя
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenLayers).nOut(numOutput).build())
                .build();
        return new Pair("Модель 2",conf2);
    }
}