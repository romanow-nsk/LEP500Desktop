/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.abc.desktop;

import com.google.gson.Gson;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import retrofit2.Call;
import romanow.abc.core.API.APICallSynch;
import romanow.abc.core.DBRequest;
import romanow.abc.core.Pair;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.ConstValue;
import romanow.abc.core.constants.OidList;
import romanow.abc.core.constants.Values;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.EntityLink;
import romanow.abc.core.entity.EntityList;
import romanow.abc.core.entity.subjectarea.MFSelection;
import romanow.abc.core.entity.subjectarea.MeasureFile;
import romanow.abc.core.entity.users.User;
import romanow.abc.core.mongo.*;
import romanow.abc.core.utils.FileNameExt;
import romanow.abc.core.utils.OwnDateTime;
import romanow.lep500.AnalyseResult;
import romanow.lep500.LEP500Params;
import romanow.lep500.fft.Extreme;
import romanow.lep500.fft.ExtremeFacade;
import romanow.lep500.fft.ExtremeList;
import romanow.lep500.fft.ExtremeNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Admin
 */
public class LEP500NNPanel extends LEP500BasePanel {
    public final static int ExtremeCount=5;
    public final static int ExtremeTypesCount=5;
    //public static final int labelIndex = ExtremeCount*ExtremeTypesCount*2;  //сколько значений в каждой строке CSV-файла
    //public static final int numInputs = ExtremeCount*ExtremeTypesCount*2;   //кол-во нейронов входной слой
    public final int numClasses = Values.EState2Count;                      //сколько классов в наборе данных
    public static final int numOutput = Values.EState2Count;                //кол-во нейронов выходной слой
    public final static String tmpCsv="temp.csv";
    //-------------------------------------------------------------------------------------------------------------------
    private int batchSize = 0;                                              //сколько всего примеров (0 - все из файла)
    private int numHiddenLayers = 5;                                        //кол-во нейронов скрытый слой
    private int nEpoch = 1000;                                              //кол-во эпох
    private ArrayList<MeasureFile> measureFilesU = new ArrayList<>();
    private ArrayList<MeasureFile> measureFilesS = new ArrayList<>();
    private ArrayList<MFSelection> selections = new ArrayList<>();
    private ArrayList<LEP500Params> params = new ArrayList<>();
    private ArrayList<AnalyseResult> results = new ArrayList<>();
    private EntityList<User> users = new EntityList<>();
    private HashMap<Integer, ConstValue> state2Map = new HashMap<>();
    private ArrayList<DLConfiguration> configs = new ArrayList<>();
    private boolean working=false;
    private int seed=6;
    private boolean interrupted=false;
    public LEP500NNPanel() {
        initComponents();
    }
    @Override
    public void refresh() {}

    @Override
    public void shutDown() {}
    public void initPanel(MainBaseFrame main0){
        super.initPanel(main0);
        Seed.setText(""+seed);
        state2Map = Values.constMap().getGroupMapByValue("EState2");
        AnalyseMode.removeAll();
        AnalyseMode.add("Пики");
        AnalyseMode.add("Спектр");
        refreshModels();
        refreshAll();
        }
    public void refreshModels(){
        Models.removeAll();
        configs.clear();
        configs.add(new DLConf1());
        configs.add(new DLConf2());
        configs.add(new DLConf3());
        for(int i=0;i<configs.size();i++)
            Models.add(configs.get(i).getName());
        }
    public boolean getModelParams(){
        try {
            nEpoch = Integer.parseInt(EpochCount.getText());
            numHiddenLayers = Integer.parseInt(HiddenLayersCount.getText());
            batchSize = Integer.parseInt(BatchSize.getText());
            seed = Integer.parseInt(Seed.getText());
            } catch (Exception ee){
                popup("Недопустимый формат параметра модели");
                return false;
                }
        return true;
        }
    public void loadParamsList(){
        params.clear();
        Params.removeAll();
        new APICall<ArrayList<DBRequest>>(null){
            @Override
            public Call<ArrayList<DBRequest>> apiFun() {
                return main.getService().getEntityList(main.debugToken,"LEP500Params", Values.GetAllModeActual,0);
                }
            @Override
            public void onSucess(ArrayList<DBRequest> oo) {
                params.clear();
                for(DBRequest dd : oo){
                    try {
                        LEP500Params param = (LEP500Params) dd.get(new Gson());
                        params.add(param);
                        Params.add(param.getTitle());
                        } catch (UniException e) {
                            System.out.println(e);
                            }
                }
            }
        };
    }

    private void stopAnalyse(){
        Interrupt.setEnabled(false);
        interrupted=false;
        }
    void analyseInThread(String title,final ArrayList<MeasureFile> files, final long paramOid, final int groupSize,final int analyseMode){
        Interrupt.setEnabled(true);
        interrupted=false;
        for(int idx = 0; idx< files.size(); idx+=groupSize) {
            OidList list = new OidList();
            for (int ii = idx; ii< files.size() && ii-idx < groupSize; ii++)
                list.oids.add(files.get(ii).getOid());
            final int idx2=idx;
            runInGUI(new Runnable() {
                @Override
                public void run() {
                    MeasuresNumProceed.setText(""+idx2);
                    }
                });
            revalidate();
            repaint();
            try{
                ArrayList<AnalyseResult> oo = new APICallSynch<ArrayList<AnalyseResult>>() {
                    @Override
                    public Call<ArrayList<AnalyseResult>> apiFun() {
                        return main2.service2.analyse(main.debugToken, paramOid, list);
                    }
                }.call();
                for (AnalyseResult dd : oo) {
                    results.add(dd);
                    }
                if (interrupted){
                    stopAnalyse();
                    setWorking(false);
                    return;
                    }
            } catch (UniException e) {
                System.out.println("Ошибка чтения данных анализа: " + e.toString());
                stopAnalyse();
                }
            }
        popup("Анализ завершен");
        stopAnalyse();
        runInGUI(new Runnable() {
            @Override
            public void run() {
                setWorking(false);
                FileNameExt fileNameExt = main.getOutputFileName("Результаты анализа (каталог)","a.csv","a.csv");
                if (fileNameExt==null)
                    return;
                ArrayList<String> list=null;
                String fname="";
                switch (analyseMode){
case 0:             list = createTeachDataPeaks(results,ExtremeTypesCount,ExtremeCount);
                    fname ="P_"+(list.size()-1)+"_"+title;
                    break;
case 1:             list = createTeachDataSpectrum(results);
                    fname ="S_"+(list.size()-1)+"_"+title;
                    break;
                    }
                String ss = "_"+params.get(Params.getSelectedIndex()).getTitle()+"_"+new OwnDateTime().toString2()+".csv";;
                String dd = fileNameExt.getPath()+fname+ss;
                saveFile(dd,list);
                }
            });
        }

    public int getTeachValuesNum(String fname){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fname),"Windows-1251"));
            in.readLine();
            String ss = in.readLine();
            in.close();
            int idx=-1;
            int count=0;
            while((idx=ss.indexOf(",",idx+1))!=-1){
                count++;
                }
            return count;
        } catch (Exception ee){
            System.out.println("Ошибка записи файла: "+ee.toString());
            return 0;
            }
    }

    public boolean saveFile(String fname, ArrayList<String> list){
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname),"Windows-1251"));
            for(String ss :list){
                out.write(ss);
                out.newLine();
                }
            out.flush();
            out.close();
            } catch (Exception ee){
                System.out.println("Ошибка записи файла: "+ee.toString());
                return false;
                }
        return true;
        }

    public void setWorking(boolean bb){
        working = bb;
        Working.setSelected(bb);
        Education.setEnabled(!bb);
        EducationAndTest.setEnabled(!bb);
        AnalyseUserData.setEnabled(!bb);
        LoadModel.setEnabled(!bb);
        }

    public void analyseUserData(final int groupSize){
        if (params.size()==0){
            popup("Нет наборов параметров анализа");
            }
        if (measureFilesU.size()==0){
            popup("Нет выборки файлов измерений");
            }
        setWorking(true);
        MeasuresNumProceed.setText("0");
        final long paramOid = params.get(Params.getSelectedIndex()).getOid();
        results.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                analyseInThread(users.get(Users.getSelectedIndex()-1).getTitle(),measureFilesU, paramOid,groupSize,AnalyseMode.getSelectedIndex());
                }
            }).start();
        }

    public void analyseSelectionData(final int groupSize){
        if (params.size()==0){
            popup("Нет наборов параметров анализа");
        }
        if (measureFilesS.size()==0){
            popup("Нет выборки файлов измерений");
            }
        setWorking(true);
        MeasuresNumProceed.setText("0");
        final long paramOid = params.get(Params.getSelectedIndex()).getOid();
        results.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                analyseInThread(selections.get(Selection.getSelectedIndex()-1).getTitle(),measureFilesS,paramOid,groupSize,AnalyseMode.getSelectedIndex());
                }
            }).start();
    }

    @Override
    public void eventPanel(int code, int par1, long par2, String par3, Object oo) {
        if (code==EventNetwork){
            if (main2.network==null){
                popup("Модель не загружена");
                return;
                }
            evaluateOne((AnalyseResult) oo);
            }
        }

    public void evaluateOne(AnalyseResult result){
        DataSet dataSet = new DataSet();
        ArrayList<AnalyseResult> xx = new ArrayList<>();
        xx.add(result);
        ArrayList<String> ss = createTeachDataPeaks(xx,ExtremeTypesCount,ExtremeCount);
        if (!saveFile(tmpCsv,ss));
        DataSet set = loadData(tmpCsv);
        INDArray output = main2.network.output(set.getFeatures());
        Evaluation eval = new Evaluation(numOutput);
        eval.eval(set.getLabels(), output);
        int actual = set.get(0).outcome();
        int prediction = Integer.parseInt(String.valueOf(output.getRow(0).argMax()));
        popup("Эксперт: "+state2Map.get(actual).title() +  " Предсказание: "+state2Map.get(prediction).title());
        }


    public ArrayList<String> createTeachDataPeaks(ArrayList<AnalyseResult> resList, int typesCount, int extremeCount){
        int stateCounts[] = new int[Values.EState2Count];
        int totalCount=0;
        for(int i=0;i<Values.EState2Count;i++)
            stateCounts[i]=0;
        int size = Values.extremeFacade.length;
        ExtremeFacade facades[] = new ExtremeFacade[size];
        for(int i=0;i<size;i++){
            try {
                facades[i] = (ExtremeFacade)Values.extremeFacade[i].newInstance();
                } catch (Exception e) {
                    facades[i] = new ExtremeNull();
                    }
                }
        ArrayList<String> out = new ArrayList<>();
        String header="";
        for(int i=0;i<ExtremeTypesCount;i++){
            String bb = Values.extremeFacade[i].getSimpleName()+"Mode";
            for(int j=0;j<ExtremeCount;j++) {
                String bb2 = bb+(j+1);
                header+=bb2+"-Value,"+bb2+"-freq,";
                }
            header+="ExpertResult";
            }
        out.add(header);
        for (int i=0;i<resList.size();i++){
            StringBuffer ss = new StringBuffer();
            AnalyseResult result = resList.get(i);
            if (!result.valid){
                System.out.println("Ошибка анализа: "+result.getTitle()+"\n"+result.message);
                continue;
                }
            for(int k=0;k<typesCount && k<result.data.size();k++){
                ExtremeFacade facade = facades[k];
                ExtremeList list = result.data.get(k);
                for (int j=0;j<extremeCount;j++) {
                    if (k != 0 || j != 0)
                        ss.append(",");
                    if (j>=list.data().size())
                        ss.append("0,0");
                    else{
                        Extreme extreme = list.data().get(j);
                        facade.setExtreme(extreme);
                        ss.append(replace(facade.getValue())+","+replace(extreme.idx*result.dFreq));
                        }
                    }
                }
                //int state2 = Values.stateToState2.get(measureFiles.get(i).getExpertResult());
                int state2 = Values.stateToState2.get(result.measure.getExpertResult());
                stateCounts[state2]++;
                totalCount++;
                ss.append(","+state2);
                out.add(ss.toString());
                }
            DLLog.append("Файлов в выборке "+totalCount+",по состояниям:\n");
            for(int i=0;i<stateCounts.length;i++){
                DLLog.append(""+state2Map.get(i).title()+": "+stateCounts[i]+"\n");
                }
            return out;
        }

    public ArrayList<String> createTeachDataSpectrum(ArrayList<AnalyseResult> resList){
        int stateCounts[] = new int[Values.EState2Count];
        int totalCount=0;
        for(int i=0;i<Values.EState2Count;i++)
            stateCounts[i]=0;
        ArrayList<String> out = new ArrayList<>();
        AnalyseResult result = resList.get(0);
        int dataSize = result.spectrum.length-result.nFirst;
        String header="";
        for(int i=result.nFirst;i<result.spectrum.length;i++){
            header+="A"+(i-result.nFirst+1)+",";
            }
            header+="ExpertResult";
        out.add(header);
        for (int i=0;i<resList.size();i++){
            result = resList.get(i);
            if (!result.valid){
                System.out.println("Ошибка анализа: "+result.getTitle()+"\n"+result.message);
                continue;
                }
            if (dataSize!=result.spectrum.length-result.nFirst){
                System.out.println("Несовпадение размернстей: "+result.getTitle()+" "+(result.spectrum.length-result.nFirst)+" вместо "+dataSize);
                }
            String ss="";
            for(int k=0;k<dataSize;k++){
                if (k+result.nFirst>=result.spectrum.length)
                    ss+="0.0,";
                else
                    ss+=replace(result.spectrum[k+result.nFirst])+",";
                }
            int state2 = Values.stateToState2.get(result.measure.getExpertResult());
            stateCounts[state2]++;
            totalCount++;
            ss+=state2;
            out.add(ss);
            }
        DLLog.append("Файлов в выборке "+totalCount+",по состояниям:\n");
        for(int i=0;i<stateCounts.length;i++){
            DLLog.append(""+state2Map.get(i).title()+": "+stateCounts[i]+"\n");
        }
        return out;
    }



    public static String replace(double vv){
        return String.format("%6.3f",vv).replace(",",".");
        }

    public void refreshAll(){
        DLLog.setText("");
        loadUsers();
        loadSelections();
        loadParamsList();
        }
    public void loadUsers(){
        try {
            users = new APICallSynch<EntityList<User>>() {
                @Override
                public Call<EntityList<User>> apiFun() {
                    return main.getService().getUserList(main.debugToken, ValuesBase.GetAllModeActual, 1);
                    }
                }.call();
            Users.removeAll();
            Users.add("...");
            for (User user : users)
                Users.add(user.getTitle());
            } catch (UniException e) {
                System.out.println("Ошибка чтения User: " + e.toString());
               }
        }
    public void loadSelections(){
        try {
            ArrayList<DBRequest> list = new APICallSynch<ArrayList<DBRequest>>() {
                @Override
                public Call<ArrayList<DBRequest>> apiFun() {
                    return main.getService().getEntityList(main.debugToken, "MFSelection",ValuesBase.GetAllModeActual, 1);
                    }
            }.call();
            selections.clear();
            Selection.removeAll();
            Selection.add("...");
            for (DBRequest request : list){
                MFSelection selection = (MFSelection)request.get(main.gson);
                selections.add(selection);
                Selection.add(selection.getTitle());
                }
            } catch (UniException e) {
                System.out.println("Ошибка чтения MFSelection: " + e.toString());
                }
    }

    public DataSet loadData(FileNameExt fileNameExt){
        String ss = fileNameExt.getPath()+fileNameExt.fileName();
        return loadData(ss);
        }
    public DataSet loadData(String ss){
        try {
            int valuesCount= getTeachValuesNum(ss);
            RecordReader recordReader3 = new CSVRecordReader(1, ',');
            recordReader3.initialize(new FileSplit(new File(ss)));
            //DataSetIterator управляет обходом набора данных и подготовкой данные для нейронной сети.
            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader3, batchSize!=0 ? batchSize : 100000, valuesCount, numClasses);
            DataSet allData = iterator.next();
            //Перетасовать набор данных, чтобы избавиться от порядка классов в исходном файле
            allData.shuffle(123456789);
            return allData;
            } catch (Exception ee){
                System.out.println("Ошибка загрузки данных:\n"+ss.toString());
                return new DataSet();
                }
        }
    public DataSet loadOnlyTraining(FileNameExt fileNameExt){
        DataSet dataset = loadData(fileNameExt);
        DataNormalization normalizer = new NormalizerStandardize();
        //Собираем статистику (среднее/стандартное отклонение) из обучающих данных
        normalizer.fit(dataset);
        //Применяем нормализацию к обучающим данным
        normalizer.transform(dataset);
        return dataset;
        }
    public Pair<DataSet,DataSet> loadTrainingAndTest(FileNameExt fileNameExt,double percent){
        DataSet dataset = loadData(fileNameExt);
        //Разделяем выборку на тестовую и обучающую в соответсвии 75% на обучение
            SplitTestAndTrain testAndTrain = dataset.splitTestAndTrain(percent);
            //Получаем тестовую и обучающую выборки
            DataSet trainingData = testAndTrain.getTrain();
            DataSet testData = testAndTrain.getTest();
            //Нормализация данных
            DataNormalization normalizer = new NormalizerStandardize();
            //Собираем статистику (среднее/стандартное отклонение) из обучающих данных
            normalizer.fit(trainingData);
            //Применяем нормализацию к обучающим данным
            normalizer.transform(trainingData);
            //Применяем нормализацию к тестовым данным
            normalizer.transform(testData);
        return new Pair<>(trainingData,testData);
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Users = new java.awt.Choice();
        Refresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        MeasuresNumProceed = new javax.swing.JTextField();
        AnalyseUserData = new javax.swing.JButton();
        Education = new javax.swing.JButton();
        DLLog = new java.awt.TextArea();
        LoadModel = new javax.swing.JButton();
        TestPercent = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Params = new java.awt.Choice();
        MeasuresUserNum = new javax.swing.JTextField();
        Working = new javax.swing.JCheckBox();
        EducationAndTest = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        Models = new java.awt.Choice();
        EpochCount = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        HiddenLayersCount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        BatchSize = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        Interrupt = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        Selection = new java.awt.Choice();
        AnalyseSelection = new javax.swing.JButton();
        MeasuresSelectionNum = new javax.swing.JTextField();
        AnalyseMode = new java.awt.Choice();
        Seed = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setLayout(null);

        Users.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                UsersItemStateChanged(evt);
            }
        });
        add(Users);
        Users.setBounds(60, 30, 170, 20);

        Refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/refresh.png"))); // NOI18N
        Refresh.setBorderPainted(false);
        Refresh.setContentAreaFilled(false);
        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });
        add(Refresh);
        Refresh.setBounds(20, 20, 30, 30);

        jLabel1.setText("Модель (конфигурация НС)");
        add(jLabel1);
        jLabel1.setBounds(60, 130, 180, 16);

        jLabel2.setText("Измерений с оценкой");
        add(jLabel2);
        jLabel2.setBounds(150, 10, 130, 16);

        MeasuresNumProceed.setEnabled(false);
        add(MeasuresNumProceed);
        MeasuresNumProceed.setBounds(310, 130, 50, 25);

        AnalyseUserData.setText("Анализ");
        AnalyseUserData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnalyseUserDataActionPerformed(evt);
            }
        });
        add(AnalyseUserData);
        AnalyseUserData.setBounds(310, 30, 130, 22);

        Education.setText("Обучение");
        Education.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EducationActionPerformed(evt);
            }
        });
        add(Education);
        Education.setBounds(310, 240, 130, 20);
        add(DLLog);
        DLLog.setBounds(470, 20, 430, 540);

        LoadModel.setText("Загрузка модели");
        LoadModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadModelActionPerformed(evt);
            }
        });
        add(LoadModel);
        LoadModel.setBounds(310, 270, 130, 20);

        TestPercent.setText("75");
        add(TestPercent);
        TestPercent.setBounds(310, 170, 40, 25);

        jLabel3.setText("% обучения");
        add(jLabel3);
        jLabel3.setBounds(360, 180, 70, 10);

        jLabel4.setText("Выборка");
        add(jLabel4);
        jLabel4.setBounds(60, 50, 130, 16);
        add(Params);
        Params.setBounds(60, 110, 170, 20);

        MeasuresUserNum.setEnabled(false);
        add(MeasuresUserNum);
        MeasuresUserNum.setBounds(250, 30, 50, 25);

        Working.setText("в работе");
        Working.setEnabled(false);
        add(Working);
        Working.setBounds(370, 130, 90, 20);

        EducationAndTest.setText("Обучение+тест");
        EducationAndTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EducationAndTestActionPerformed(evt);
            }
        });
        add(EducationAndTest);
        EducationAndTest.setBounds(310, 210, 130, 20);

        jLabel5.setText("Параметры анализа");
        add(jLabel5);
        jLabel5.setBounds(60, 90, 130, 16);
        add(Models);
        Models.setBounds(60, 150, 170, 20);

        EpochCount.setText("1000");
        add(EpochCount);
        EpochCount.setBounds(60, 210, 50, 25);

        jLabel6.setText("Seed");
        add(jLabel6);
        jLabel6.setBounds(120, 275, 70, 16);

        HiddenLayersCount.setText("5");
        add(HiddenLayersCount);
        HiddenLayersCount.setBounds(60, 180, 50, 25);

        jLabel7.setText("Нейронов скрытого слоя");
        add(jLabel7);
        jLabel7.setBounds(120, 185, 160, 16);

        BatchSize.setText("0");
        add(BatchSize);
        BatchSize.setBounds(60, 240, 50, 25);

        jLabel8.setText("Эпох обучения");
        add(jLabel8);
        jLabel8.setBounds(120, 215, 110, 16);

        Interrupt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/problem.png"))); // NOI18N
        Interrupt.setBorderPainted(false);
        Interrupt.setContentAreaFilled(false);
        Interrupt.setEnabled(false);
        Interrupt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InterruptActionPerformed(evt);
            }
        });
        add(Interrupt);
        Interrupt.setBounds(260, 120, 35, 35);

        jLabel9.setText("Собственник");
        add(jLabel9);
        jLabel9.setBounds(60, 10, 130, 16);

        Selection.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SelectionItemStateChanged(evt);
            }
        });
        add(Selection);
        Selection.setBounds(60, 70, 170, 20);

        AnalyseSelection.setText("Анализ");
        AnalyseSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnalyseSelectionActionPerformed(evt);
            }
        });
        add(AnalyseSelection);
        AnalyseSelection.setBounds(310, 60, 130, 22);

        MeasuresSelectionNum.setEnabled(false);
        add(MeasuresSelectionNum);
        MeasuresSelectionNum.setBounds(250, 70, 50, 25);
        add(AnalyseMode);
        AnalyseMode.setBounds(310, 100, 130, 20);

        Seed.setText("0");
        add(Seed);
        Seed.setBounds(60, 270, 50, 25);

        jLabel10.setText("Выборка (0 - все)");
        add(jLabel10);
        jLabel10.setBounds(120, 245, 140, 16);
    }// </editor-fold>//GEN-END:initComponents

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        refreshAll();
        }//GEN-LAST:event_RefreshActionPerformed


    public void loadFilesBySelection(long oid){
        new APICall<DBRequest>(null){
            @Override
            public Call<DBRequest> apiFun() {
                return main.getService().getEntity(main.getDebugToken(),"MFSelection",oid,2);
                }
            @Override
            public void onSucess(DBRequest oo) {
                measureFilesS.clear();
                try {
                    MFSelection selection = (MFSelection)oo.get(new Gson());
                    for(EntityLink<MeasureFile> fileLink : selection.getFiles())
                        measureFilesS.add(fileLink.getRef());

                    } catch (UniException e) {
                        System.out.println(e);
                        }
                    }
                };
        }

    private void UsersItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_UsersItemStateChanged
        MeasuresNumProceed.setText("");
        measureFilesU.clear();
        if (Users.getSelectedIndex()==0)
            return;
        DBQueryList query =  new DBQueryList().
                add(new DBQueryInt(I_DBQuery.ModeNEQ,"expertResult", Values.ESNotSupported)).
                add(new DBQueryInt(I_DBQuery.ModeNEQ,"expertResult",Values.ESNotSet)).
                add(new DBQueryLong("userId",users.get(Users.getSelectedIndex()-1).getOid())).
                add(new DBQueryBoolean("valid",true));
        final String xmlQuery = new DBXStream().toXML(query);
        new APICall<ArrayList<DBRequest>>(null){
            @Override
            public Call<ArrayList<DBRequest>> apiFun() {
                return main.getService().getEntityListByQuery(main.debugToken,"MeasureFile",xmlQuery,1);
                }
            @Override
            public void onSucess(ArrayList<DBRequest> oo) {
                measureFilesU.clear();
                MeasuresUserNum.setText(""+oo.size());
                for(DBRequest dd : oo){
                    try {
                        MeasureFile ss = (MeasureFile) dd.get(new Gson());
                        measureFilesU.add(ss);
                        } catch (UniException e) {
                            System.out.println(e);
                            }
                }
            }
        };
    }//GEN-LAST:event_UsersItemStateChanged

    private void AnalyseUserDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalyseUserDataActionPerformed
        analyseUserData(10);
    }//GEN-LAST:event_AnalyseUserDataActionPerformed

    private void EducationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EducationActionPerformed
        //Инициализация модели
        FileNameExt fileNameExt = main.getInputFileName("Файл данных НС","*.csv","");
        if (fileNameExt==null)
            return;
        int valuesCount=getTeachValuesNum(fileNameExt.fullName());
        DataSet trainingData = loadOnlyTraining(fileNameExt);
        if (trainingData==null)
            return;
        if (!getModelParams())
            return;
        MultiLayerNetwork model = new MultiLayerNetwork(configs.get(Models.getSelectedIndex()).create(numHiddenLayers,valuesCount,numOutput,seed));
        model.init();
        //Записываем оценку один раз каждые 100 итераций
        model.setListeners(new ScoreIterationListener(100));
        for(int i=0; i<nEpoch; i++ ) {
            model.fit(trainingData);
            }
        //Сохранение модели в файл
        try {
            String fname = Models.getSelectedItem()+"_"+fileNameExt.getName()+".model";
            String path = fileNameExt.getPath()+"/"+fname;
            model.save(new File(path));
            popup("Модель сохранена: "+fname);
            } catch (Exception ee){
                System.out.println("Ошибка сохранения НС: "+ee.toString());
                }
        }//GEN-LAST:event_EducationActionPerformed

    private void LoadModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadModelActionPerformed
        try {
            FileNameExt fileNameExt = main.getInputFileName("Файл модели НС","*.model","");
            if (fileNameExt==null)
                return;
            main2.network = MultiLayerNetwork.load(new File(fileNameExt.fullName()),true);
            popup("Модель загружена");
            } catch (Exception e) {
                popup("Ошибка загрузки модели");
                System.out.println("Ошибка загрузки модели: "+e.toString());
                }
    }//GEN-LAST:event_LoadModelActionPerformed

    private void EducationAndTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EducationAndTestActionPerformed
        //Инициализация модели
        FileNameExt fileNameExt = main.getInputFileName("Файл данных НС","*.csv","");
        if (fileNameExt==null)
            return;
        int valuesCount=getTeachValuesNum(fileNameExt.fullName());
        double percent=0;
        try {
            percent = Integer.parseInt(TestPercent.getText())/100.;
            } catch (Exception ee){
                System.out.println("Ошибка формата целого");
                return;
                }
        Pair<DataSet,DataSet>  data = loadTrainingAndTest(fileNameExt,percent);
        if (data==null)
            return;
        if (!getModelParams())
            return;
        MultiLayerNetwork model = new MultiLayerNetwork(configs.get(Models.getSelectedIndex()).create(numHiddenLayers,valuesCount,numOutput,seed));
        model.init();
        //Записываем оценку один раз каждые 100 итераций
        model.setListeners(new ScoreIterationListener(100));
        for(int i=0; i<nEpoch; i++ ) {
            model.fit(data.o1);
            }
        evaluate(model,data.o2);
    }//GEN-LAST:event_EducationAndTestActionPerformed

    private void InterruptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InterruptActionPerformed
        interrupted=true;
        popup("Анализ будет прерван");
    }//GEN-LAST:event_InterruptActionPerformed

    private void SelectionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SelectionItemStateChanged
        MeasuresSelectionNum.setText("");
        if (Selection.getItemCount()==1 || Selection.getSelectedIndex()==0)
        return;
        MFSelection selection = selections.get(Selection.getSelectedIndex()-1);
        loadFilesBySelection(selection.getOid());
        MeasuresSelectionNum.setText(""+ measureFilesS.size());
    }//GEN-LAST:event_SelectionItemStateChanged

    private void AnalyseSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalyseSelectionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AnalyseSelectionActionPerformed


    //Метод для вывода результата обучения на тестовой выборке
    public void evaluate(MultiLayerNetwork model, DataSet testData){
        //Оцениваем модель на тестовом наборе
        INDArray output = model.output(testData.getFeatures());
        Evaluation eval = new Evaluation(numOutput);
        eval.eval(testData.getLabels(), output);
        //Вывод оценки модели
        DLLog.append("\"-----------------------------------\n");
        DLLog.append(eval.stats()+"\n"); //confusion matrix, evaluation metrics
        //DLLog.append("-----------------------------------\n");
        //DLLog.append(eval.confusionToString()+"\n");
        //DLLog.append("-----------------------------------\n");
        //DLLog.append(output+"\n");
        //DLLog.append("-----------------------------------\n");
        //Вывод результата по каждому экземпляру данных
        int count=0;
        DLLog.append("Эксперт -> Предсказание\n");
        for(int i = 0;i<output.rows();i++) {
            int actual = testData.get(i).outcome();
            int prediction = Integer.parseInt(String.valueOf(output.getRow(i).argMax()));
            DLLog.append((i+1) + " "+state2Map.get(actual).title() + (prediction==actual ? "" : "->"+state2Map.get(prediction).title())+"\n");
            if (prediction==actual)
                count++;
            }
        DLLog.append("Процент верных предсказаний: "+count*100/output.rows());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Choice AnalyseMode;
    private javax.swing.JButton AnalyseSelection;
    private javax.swing.JButton AnalyseUserData;
    private javax.swing.JTextField BatchSize;
    private java.awt.TextArea DLLog;
    private javax.swing.JButton Education;
    private javax.swing.JButton EducationAndTest;
    private javax.swing.JTextField EpochCount;
    private javax.swing.JTextField HiddenLayersCount;
    private javax.swing.JButton Interrupt;
    private javax.swing.JButton LoadModel;
    private javax.swing.JTextField MeasuresNumProceed;
    private javax.swing.JTextField MeasuresSelectionNum;
    private javax.swing.JTextField MeasuresUserNum;
    private java.awt.Choice Models;
    private java.awt.Choice Params;
    private javax.swing.JButton Refresh;
    private javax.swing.JTextField Seed;
    private java.awt.Choice Selection;
    private javax.swing.JTextField TestPercent;
    private java.awt.Choice Users;
    private javax.swing.JCheckBox Working;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables
}
