package romanow.abc.desktop;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import static romanow.abc.desktop.LEP500NNPanel.numOutput;

public class DLConf3 extends DLConfiguration{
    @Override
    public MultiLayerConfiguration create(int hiddenCount, int numInputs, int numOutputs, int seed) {
        MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.SIGMOID_UNIFORM)
                .updater(new Sgd(0.1))
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(50)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(50).nOut(25)
                        .build())
                .layer(2, new DenseLayer.Builder().nIn(25).nOut(hiddenCount)
                        .build())
                .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        //Переопределить глобальную активацию  с помощью softmax для этого слоя
                        .activation(Activation.SOFTMAX)
                        .nIn(hiddenCount).nOut(numOutput).build())
                .build();
        return conf2;
    }
    @Override
    public String getName() {
        return "Модель 3";
        }

}
