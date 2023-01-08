/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.abc.desktop;

import retrofit2.Call;
import retrofit2.Response;
import romanow.abc.core.API.APICallSynch;
import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.ConstValue;
import romanow.abc.core.constants.OidList;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.*;
import romanow.abc.core.entity.baseentityes.JBoolean;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.baseentityes.JLong;
import romanow.abc.core.entity.subjectarea.MFSelection;
import romanow.abc.core.entity.subjectarea.MeasureFile;
import romanow.abc.core.entity.subjectarea.PowerLine;
import romanow.abc.core.entity.subjectarea.Support;
import romanow.abc.core.entity.users.User;
import romanow.abc.core.mongo.DBQueryInt;
import romanow.abc.core.mongo.DBQueryList;
import romanow.abc.core.mongo.DBXStream;
import romanow.abc.core.mongo.I_DBQuery;
import romanow.lep500.AnalyseResult;
import romanow.lep500.AnalyseResultList;
import romanow.lep500.LEP500Params;
import romanow.lep500.PeakPlace;
import romanow.lep500.fft.ExtremeFacade;
import romanow.lep500.fft.ExtremeList;
import romanow.lep500.fft.ExtremeNull;
import romanow.lep500.fft.FFTStatistic;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author romanow0
 */
public class LEP500LocalExperience extends LEP500BasePanel {
    public final static HashMap<Integer,String> StateColors=new HashMap<>();{
        StateColors.put(Values.MSUndefined,"/drawable/status_gray.png");
        StateColors.put(Values.MSNormal,"/drawable/status_green.png");
        StateColors.put(Values.MSNormalMinus,"/drawable/status_light_green.png");
        StateColors.put(Values.MSNoise,"/drawable/status_red.png");
        StateColors.put(Values.MSLowLevel,"/drawable/status_red.png");
        StateColors.put(Values.MSNoPeak,"/drawable/status_gray.png");
        StateColors.put(Values.MSSecond1,"/drawable/status_red.png");
        StateColors.put(Values.MSSecond2,"/drawable/status_yellow.png");
        StateColors.put(Values.MSSumPeak1,"/drawable/status_light_red.png");
        StateColors.put(Values.MSSumPeak2,"/drawable/status_light_yellow.png");
        }
    private ArrayList<ConstValue> resultStates = new ArrayList<>();
    private HashMap<Integer,ConstValue> expertNoteMap;                  // Оценка эксперта по коду
    private HashMap<Integer,ConstValue> algResultMap;                   // Результаты алгоритмов по коду
    //private HashMap<Integer,String> analyseStateList = new HashMap<>();
    private ArrayList<String> criterisList= new ArrayList<>();
    private ArrayList<MeasureFile> measureFiles = new ArrayList<>();
    private ArrayList<MeasureFile> selection = new ArrayList<>();
    private ArrayList<MeasureFile> tempSelection = new ArrayList<>();
    private boolean selectionChanged=false;
    private ArrayList<MFSelection> selectionList = new ArrayList<>();
    private ArrayList<PowerLine> lines = new ArrayList<>();
    private ArrayList<Support> supports = new ArrayList<>();
    private ArrayList<LEP500Params> params = new ArrayList<>();
    private ArrayList<AnalyseResult> results = new ArrayList<>();
    private AnalyseResult selectedResult=null;
    private HashMap<Long,User> userMap = new HashMap<>();
    private EntityList<User> userList = new EntityList<>();
    private boolean lineSupportState = false;                           // Режим - линия/опора
    /**
     * Creates new form LEP500Example
     */
    public LEP500LocalExperience() {
        initComponents();
        algResultMap = Values.constMap().getGroupMapByValue("MState");
        expertNoteMap =Values.constMap().getGroupMapByValue("EState");
        }
    //---------------------------------------------------------------------------------
    private ArrayList<JTextField> criteriaResults = new ArrayList<>();
    private ArrayList<JButton> criteriaResultLamps = new ArrayList<>();
    private void initResultsCriteriaList(){
        int idx=0,dy=40;
        for(String ss : criterisList) {
            JLabel criteriaName = new JLabel();
            criteriaName.setBounds(10, 260+idx*dy, 150, 14);
            criteriaName.setText(ss);
            add(criteriaName);
            JTextField criteriaResult = new JTextField();
            criteriaResult.setEnabled(false);
            criteriaResult.setBounds(180, 260+idx*dy, 190, 25);
            criteriaResults.add(criteriaResult);
            add(criteriaResult);
            JButton criteriaResultLamp = new JButton();
            criteriaResultLamp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/status_gray.png"))); // NOI18N
            criteriaResultLamp.setBounds(380, 250+idx*dy, 40, 40);
            criteriaResultLamp.setBorderPainted(false);
            criteriaResultLamp.setContentAreaFilled(false);
            criteriaResultLamps.add(criteriaResultLamp);
            add(criteriaResultLamp);
            idx++;
            }
        }
    private void clearResultsCriteriaList(){
        for(int i=0;i<criterisList.size();i++){
            criteriaResults.get(i).setText("");
            criteriaResultLamps.get(i).setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/status_gray.png")));
            }
        }

    @Override
    public void initPanel(MainBaseFrame main0) {
        super.initPanel(main0);
        lineSupportState=false;
        ExpertNoteSelector.setEnabled(false);
        SelectionName.setEnabled(false);
        SelectionSave.setEnabled(false);
        resultStates = main.filter(main.constList,"EState");
        resultStates.sort(new Comparator<ConstValue>() {
            @Override
            public int compare(ConstValue o1, ConstValue o2) {
                return o1.value()-o2.value();
                }
            });
        criterisList.clear();
        for (int mode = 0; mode < Values.extremeFacade.length; mode++) {
            ExtremeFacade facade;
            try {
                facade = (ExtremeFacade)Values.extremeFacade[mode].newInstance();
                } catch (Exception e) {
                    facade = new ExtremeNull();
                    }
                criterisList.add(facade.getTitle());
            }
        ExpertNoteSelector.removeAll();
        ExpertNoteSelector.add("Все определенные");
        for (ConstValue cc : resultStates){
            ExpertNoteSelector.add(cc.title());
            }
        initResultsCriteriaList();
        /*
        new APICall<EntityList<User>>(main) {
            @Override
            public Call<EntityList<User>> apiFun() {
                return main.service.getUserList(main.debugToken,Values.GetAllModeActual,0);
                }
            @Override
            public void onSucess(EntityList<User> oo) {
                userList = oo;
                userMap.clear();
                OwnerSelector.removeAll();
                OwnerSelector.add("Все ");
                for(User user : oo){
                    userMap.put(user.getOid(),user);
                    OwnerSelector.add("["+user.getOid()+"] "+user.getTitle());
                    }
                refreshAll();
                }
            };
         */
        refreshSelectionList();
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        choice1 = new java.awt.Choice();
        AddMeasure = new javax.swing.JButton();
        SelectionMeasureList = new java.awt.Choice();
        LineSupportState = new java.awt.Checkbox();
        PowerLineList = new java.awt.Choice();
        jLabel3 = new javax.swing.JLabel();
        SupportList = new java.awt.Choice();
        AnalyseSelection = new javax.swing.JButton();
        Refresh = new javax.swing.JButton();
        SelectuionAdd = new javax.swing.JButton();
        MeasureList = new java.awt.Choice();
        jLabel4 = new javax.swing.JLabel();
        AnalyseParams = new java.awt.Choice();
        jLabel5 = new javax.swing.JLabel();
        Results = new java.awt.Choice();
        jLabel6 = new javax.swing.JLabel();
        AnaluseResultRemove = new javax.swing.JButton();
        ShowGraph = new javax.swing.JButton();
        AnaluseResultsCrear = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ExpertNoteList = new java.awt.Choice();
        ExpertNoteSave = new javax.swing.JButton();
        ResultData = new java.awt.TextArea();
        DeleteFile = new javax.swing.JButton();
        ExpertNoteSelector = new java.awt.Choice();
        jSeparator1 = new javax.swing.JSeparator();
        ExpertNote = new javax.swing.JTextField();
        Owner = new javax.swing.JTextField();
        ShowTree = new javax.swing.JButton();
        OwnerSelector = new java.awt.Choice();
        SelectionList = new java.awt.Choice();
        jLabel9 = new javax.swing.JLabel();
        SelectionSave = new javax.swing.JButton();
        SelectionFileRemove = new javax.swing.JButton();
        SelectionName = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        SelectionRemove = new javax.swing.JButton();
        SelectionCount = new javax.swing.JTextField();
        MeasuresCount = new javax.swing.JTextField();
        SkipTimeMS = new javax.swing.JTextField();
        StartOver = new javax.swing.JTextField();
        StartLevelProc = new javax.swing.JTextField();
        SplitMeasure = new javax.swing.JButton();
        Size32768 = new javax.swing.JCheckBox();
        ResultCommonData = new java.awt.TextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        WithFiles = new javax.swing.JCheckBox();
        ShowTree1 = new javax.swing.JButton();

        setLayout(null);

        AddMeasure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/upload.png"))); // NOI18N
        AddMeasure.setBorderPainted(false);
        AddMeasure.setContentAreaFilled(false);
        AddMeasure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddMeasureActionPerformed(evt);
            }
        });
        add(AddMeasure);
        AddMeasure.setBounds(745, 20, 40, 30);
        add(SelectionMeasureList);
        SelectionMeasureList.setBounds(90, 170, 680, 20);

        LineSupportState.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LineSupportStateItemStateChanged(evt);
            }
        });
        add(LineSupportState);
        LineSupportState.setBounds(60, 30, 30, 20);

        PowerLineList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                PowerLineListItemStateChanged(evt);
            }
        });
        add(PowerLineList);
        PowerLineList.setBounds(90, 30, 190, 20);

        jLabel3.setText("Опора");
        add(jLabel3);
        jLabel3.setBounds(10, 60, 70, 16);

        SupportList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SupportListItemStateChanged(evt);
            }
        });
        add(SupportList);
        SupportList.setBounds(90, 60, 190, 20);

        AnalyseSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/lep500button.png"))); // NOI18N
        AnalyseSelection.setBorderPainted(false);
        AnalyseSelection.setContentAreaFilled(false);
        AnalyseSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnalyseSelectionActionPerformed(evt);
            }
        });
        add(AnalyseSelection);
        AnalyseSelection.setBounds(810, 230, 35, 35);

        Refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/refresh.png"))); // NOI18N
        Refresh.setBorderPainted(false);
        Refresh.setContentAreaFilled(false);
        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });
        add(Refresh);
        Refresh.setBounds(660, 20, 30, 30);

        SelectuionAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/updown.png"))); // NOI18N
        SelectuionAdd.setBorderPainted(false);
        SelectuionAdd.setContentAreaFilled(false);
        SelectuionAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectuionAddActionPerformed(evt);
            }
        });
        add(SelectuionAdd);
        SelectuionAdd.setBounds(710, 55, 40, 35);

        MeasureList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MeasureListItemStateChanged(evt);
            }
        });
        add(MeasureList);
        MeasureList.setBounds(90, 90, 680, 20);

        jLabel4.setText("Пауза (мс)");
        add(jLabel4);
        jLabel4.setBounds(780, 140, 70, 20);
        add(AnalyseParams);
        AnalyseParams.setBounds(560, 240, 160, 20);

        jLabel5.setText("Выборка");
        add(jLabel5);
        jLabel5.setBounds(10, 130, 70, 16);

        Results.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ResultsItemStateChanged(evt);
            }
        });
        add(Results);
        Results.setBounds(90, 200, 680, 20);

        jLabel6.setText("Измерения");
        add(jLabel6);
        jLabel6.setBounds(10, 170, 70, 16);

        AnaluseResultRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/remove.png"))); // NOI18N
        AnaluseResultRemove.setBorderPainted(false);
        AnaluseResultRemove.setContentAreaFilled(false);
        AnaluseResultRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnaluseResultRemoveActionPerformed(evt);
            }
        });
        add(AnaluseResultRemove);
        AnaluseResultRemove.setBounds(730, 240, 30, 30);

        ShowGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/graph.png"))); // NOI18N
        ShowGraph.setBorderPainted(false);
        ShowGraph.setContentAreaFilled(false);
        ShowGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowGraphActionPerformed(evt);
            }
        });
        add(ShowGraph);
        ShowGraph.setBounds(850, 230, 40, 40);

        AnaluseResultsCrear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/clear.png"))); // NOI18N
        AnaluseResultsCrear.setBorderPainted(false);
        AnaluseResultsCrear.setContentAreaFilled(false);
        AnaluseResultsCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnaluseResultsCrearActionPerformed(evt);
            }
        });
        add(AnaluseResultsCrear);
        AnaluseResultsCrear.setBounds(770, 240, 30, 30);

        jLabel7.setText("Результаты");
        add(jLabel7);
        jLabel7.setBounds(10, 200, 70, 16);

        jLabel8.setText("Оценка эксперта");
        add(jLabel8);
        jLabel8.setBounds(10, 230, 110, 16);
        add(ExpertNoteList);
        ExpertNoteList.setBounds(120, 230, 170, 20);

        ExpertNoteSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/save.png"))); // NOI18N
        ExpertNoteSave.setBorderPainted(false);
        ExpertNoteSave.setContentAreaFilled(false);
        ExpertNoteSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExpertNoteSaveActionPerformed(evt);
            }
        });
        add(ExpertNoteSave);
        ExpertNoteSave.setBounds(300, 230, 30, 30);
        add(ResultData);
        ResultData.setBounds(420, 270, 480, 390);

        DeleteFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/remove.png"))); // NOI18N
        DeleteFile.setBorderPainted(false);
        DeleteFile.setContentAreaFilled(false);
        DeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteFileActionPerformed(evt);
            }
        });
        add(DeleteFile);
        DeleteFile.setBounds(750, 55, 30, 30);

        ExpertNoteSelector.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ExpertNoteSelectorItemStateChanged(evt);
            }
        });
        add(ExpertNoteSelector);
        ExpertNoteSelector.setBounds(340, 30, 150, 20);
        add(jSeparator1);
        jSeparator1.setBounds(10, 123, 750, 0);

        ExpertNote.setEnabled(false);
        add(ExpertNote);
        ExpertNote.setBounds(340, 60, 150, 25);

        Owner.setEnabled(false);
        add(Owner);
        Owner.setBounds(500, 60, 150, 25);

        ShowTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/question.png"))); // NOI18N
        ShowTree.setBorderPainted(false);
        ShowTree.setContentAreaFilled(false);
        ShowTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowTreeActionPerformed(evt);
            }
        });
        add(ShowTree);
        ShowTree.setBounds(290, 20, 40, 40);

        OwnerSelector.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                OwnerSelectorItemStateChanged(evt);
            }
        });
        add(OwnerSelector);
        OwnerSelector.setBounds(500, 30, 150, 20);

        SelectionList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SelectionListItemStateChanged(evt);
            }
        });
        add(SelectionList);
        SelectionList.setBounds(90, 130, 310, 20);

        jLabel9.setText("Измерения");
        add(jLabel9);
        jLabel9.setBounds(10, 90, 70, 16);

        SelectionSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/save.png"))); // NOI18N
        SelectionSave.setBorderPainted(false);
        SelectionSave.setContentAreaFilled(false);
        SelectionSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectionSaveActionPerformed(evt);
            }
        });
        add(SelectionSave);
        SelectionSave.setBounds(500, 120, 30, 30);

        SelectionFileRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/remove.png"))); // NOI18N
        SelectionFileRemove.setBorderPainted(false);
        SelectionFileRemove.setContentAreaFilled(false);
        SelectionFileRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectionFileRemoveActionPerformed(evt);
            }
        });
        add(SelectionFileRemove);
        SelectionFileRemove.setBounds(850, 170, 30, 30);
        add(SelectionName);
        SelectionName.setBounds(540, 130, 230, 25);

        jLabel10.setText("Линия");
        add(jLabel10);
        jLabel10.setBounds(10, 30, 70, 16);

        jLabel11.setText("Оценка");
        add(jLabel11);
        jLabel11.setBounds(340, 10, 70, 16);

        jLabel2.setText("Параметры анализа");
        add(jLabel2);
        jLabel2.setBounds(430, 245, 140, 16);

        SelectionRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/remove.png"))); // NOI18N
        SelectionRemove.setBorderPainted(false);
        SelectionRemove.setContentAreaFilled(false);
        SelectionRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectionRemoveActionPerformed(evt);
            }
        });
        add(SelectionRemove);
        SelectionRemove.setBounds(460, 120, 30, 30);

        SelectionCount.setEnabled(false);
        add(SelectionCount);
        SelectionCount.setBounds(780, 170, 50, 25);

        MeasuresCount.setEnabled(false);
        add(MeasuresCount);
        MeasuresCount.setBounds(410, 130, 40, 25);

        SkipTimeMS.setText("50");
        add(SkipTimeMS);
        SkipTimeMS.setBounds(850, 140, 40, 25);

        StartOver.setText("3");
        add(StartOver);
        StartOver.setBounds(850, 80, 40, 25);

        StartLevelProc.setText("60");
        StartLevelProc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartLevelProcActionPerformed(evt);
            }
        });
        add(StartLevelProc);
        StartLevelProc.setBounds(850, 110, 40, 25);

        SplitMeasure.setText("Нарезка");
        SplitMeasure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SplitMeasureActionPerformed(evt);
            }
        });
        add(SplitMeasure);
        SplitMeasure.setBounds(810, 20, 90, 25);

        Size32768.setText("32768");
        add(Size32768);
        Size32768.setBounds(850, 50, 60, 20);
        add(ResultCommonData);
        ResultCommonData.setBounds(20, 460, 390, 200);

        jLabel12.setText("Собственник");
        add(jLabel12);
        jLabel12.setBounds(500, 6, 130, 20);

        jLabel13.setText("Перепад");
        add(jLabel13);
        jLabel13.setBounds(780, 85, 60, 20);

        jLabel14.setText("% от макс.");
        add(jLabel14);
        jLabel14.setBounds(780, 115, 70, 20);

        WithFiles.setText("с файлами");
        add(WithFiles);
        WithFiles.setBounds(460, 150, 100, 20);

        ShowTree1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/question.png"))); // NOI18N
        ShowTree1.setBorderPainted(false);
        ShowTree1.setContentAreaFilled(false);
        ShowTree1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowTree1ActionPerformed(evt);
            }
        });
        add(ShowTree1);
        ShowTree1.setBounds(330, 225, 40, 40);
    }// </editor-fold>//GEN-END:initComponents

    private void AddMeasureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMeasureActionPerformed
        new UploadPanel(400, 300, main, new I_OK() {
            @Override
            public void onOK(Entity ent) {
                new APICall<MeasureFile>(main){
                    @Override
                    public Call<MeasureFile> apiFun() {
                        return main2.service2.addMeasure(main.debugToken,ent.getOid());
                        }
                    @Override
                    public void onSucess(MeasureFile oo) {
                        System.out.println(oo);
                    }
                };
            }
        });
    }//GEN-LAST:event_AddMeasureActionPerformed

    public void askForSaveChanges(Runnable code){
        if (!selectionChanged)
            code.run();
        else
            new OKFull(200, 200, "Несохраненные изменения, продолжить?", new I_ButtonFull() {
                @Override
                public void onPush(boolean yes) {
                    if (!yes)
                        return;
                    selectionChanged=false;
                    SelectionSave.setEnabled(false);
                    code.run();
                }
            });
        }

    public void refreshSelectionAsk(){
        askForSaveChanges(new Runnable() {
            @Override
            public void run() {
                refreshSelection();
                }
            });
        }

    public void wasChanged(){
        selectionChanged=true;
        SelectionSave.setEnabled(true);
        SelectionList.setEnabled(false);
        SelectionName.setEnabled(true);
        }
    public void clearChanges(){
        selectionChanged=false;
        SelectionSave.setEnabled(false);
        SelectionList.setEnabled(true);
        SelectionName.setEnabled(false);
        }

    public void loadSelection(MFSelection mfs){
        new APICall<DBRequest>(main){
            @Override
            public Call<DBRequest> apiFun() {
                return main.service.getEntity(main.debugToken,"MFSelection", mfs.getOid(), 2);
                }
            @Override
            public void onSucess(DBRequest oo) {
                    try {
                        MFSelection mfSelection = (MFSelection) oo.get(main.gson);
                        selection.clear();
                        SelectionCount.setText(""+mfSelection.getFiles().size());
                        SelectionMeasureList.removeAll();
                        for(EntityLink<MeasureFile> file : mfSelection.getFiles()){
                            selection.add(file.getRef());
                            SelectionMeasureList.add(file.getTitle());
                            }
                        } catch (UniException e) {
                            System.out.println(e);
                            }
                }
            };
        }

    public void refreshSelection(){
        int idx = SelectionList.getSelectedIndex();
        if (idx!=0){
            loadSelection(selectionList.get(idx-1));
            }
        else{
            tempSelection = selection;
            SelectionCount.setText(""+tempSelection.size());
            SelectionMeasureList.removeAll();
            for(MeasureFile file : selection){
                SelectionMeasureList.add("["+file.getOid()+"] "+file.getTitle());
                }
            }
        }

    private void LineSupportStateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LineSupportStateItemStateChanged
        refreshAll();
    }//GEN-LAST:event_LineSupportStateItemStateChanged

    private void SupportListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SupportListItemStateChanged
        if (supports.size()==0)
            return;
        measureFiles = supports.get(SupportList.getSelectedIndex()).getFiles();
        refreshMeasureList();
    }//GEN-LAST:event_SupportListItemStateChanged

    private void PowerLineListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_PowerLineListItemStateChanged
        refreshSupport();
    }//GEN-LAST:event_PowerLineListItemStateChanged


    private void AnalyseSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalyseSelectionActionPerformed
        if (selection.size()==0 || params.size()==0)
            return;
        OidList  list = new OidList();
        for(MeasureFile ss : selection){
            list.oids.add(ss.getOid());
            }
        new APICall<ArrayList<AnalyseResult>>(main){
            @Override
            public Call<ArrayList<AnalyseResult>> apiFun() {
                return main2.service2.analyse(main.debugToken,params.get(AnalyseParams.getSelectedIndex()).getOid(),list);
                }
            @Override
            public void onSucess(ArrayList<AnalyseResult> oo) {
                for(AnalyseResult dd : oo){
                    results.add(dd);
                }
            refreshResults();
            }
        };

    }//GEN-LAST:event_AnalyseSelectionActionPerformed

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        refreshAll();
    }//GEN-LAST:event_RefreshActionPerformed

    private void SelectuionAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectuionAddActionPerformed
        if(measureFiles.size()==0)
            return;
        MeasureFile file = measureFiles.remove(MeasureList.getSelectedIndex());
        MeasureList.remove(MeasureList.getSelectedIndex());
        MeasuresCount.setText(""+measureFiles.size());
        selection.add(file);
        SelectionCount.setText(""+selection.size());
        SelectionMeasureList.add("["+file.getOid()+"] "+file.getTitle());
        wasChanged();

    }//GEN-LAST:event_SelectuionAddActionPerformed

    private void AnaluseResultRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnaluseResultRemoveActionPerformed
        if(results.size()==0)
            return;
        results.remove(Results.getSelectedIndex());
        refreshResults();
    }//GEN-LAST:event_AnaluseResultRemoveActionPerformed

    private void AnaluseResultsCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnaluseResultsCrearActionPerformed
        results.clear();
        refreshResults();
    }//GEN-LAST:event_AnaluseResultsCrearActionPerformed

    private void ResultsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ResultsItemStateChanged
        showOneResult();
    }//GEN-LAST:event_ResultsItemStateChanged

    private void ShowGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowGraphActionPerformed
        if (results.size()==0)
            return;
        main.sendEventPanel(EventGraph,0,0,"",results);
    }//GEN-LAST:event_ShowGraphActionPerformed

    private void ExpertNoteSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExpertNoteSaveActionPerformed
        if (selectedResult==null)
            return;
        selectedResult.measure.setExpertResult(ExpertNoteList.getSelectedIndex());
        try {
            Response<JEmpty> wsr = main2.service2.setExpertNote(main.debugToken,selectedResult.measure.getOid(),ExpertNoteList.getSelectedIndex()).execute();
            if (!wsr.isSuccessful()){
                popup("Ошибка обновления параметров  " + httpError(wsr));
                return;
                }
            else
                popup("Оценка эксперта обновлена");
            } catch (IOException e) {
                popup(e.toString());
                }
    }//GEN-LAST:event_ExpertNoteSaveActionPerformed


    private void refreshSelectionList(){
        if (!selectionChanged)
            refreshSelectionListForced();
        else{
            new OK(200, 200, "Есть несохранные изменения, продолжить?", new I_Button() {
                @Override
                public void onPush() {
                    refreshSelectionListForced();
                    }
                });
            }
        }

    private void refreshSelectionListForced(){
        SelectionList.removeAll();
        selectionList.clear();
        tempSelection.clear();
        /*
        new APICall<ArrayList<DBRequest>>(main) {
            @Override
            public Call<ArrayList<DBRequest>> apiFun() {
                return main.service.getEntityList(main.debugToken,"MFSelection",Values.GetAllModeActual,1);
            }
            @Override
            public void onSucess(ArrayList<DBRequest> oo) {
                SelectionList.add("Временная...");
                try{
                    for(DBRequest request : oo){
                        MFSelection selection = (MFSelection) request.get(main.gson);
                        selectionList.add(selection);
                        SelectionList.add(selection.getTitle());
                    }
                refreshSelection();
                } catch (UniException ee){
                    System.out.println("");
                }
            }
        };
         */
    }


    private void DeleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteFileActionPerformed
        if (measureFiles.size()==0)
            return;
        final MeasureFile file = measureFiles.get(MeasureList.getSelectedIndex());
        new OK(200, 200, "Удалить " + file.toString(), new I_Button() {
            @Override
            public void onPush() {
                new APICall<JBoolean>(main){
                    @Override
                    public Call<JBoolean> apiFun() {
                        return main.service.deleteById(main.debugToken,"Измерение",file.getOid());
                        }
                    @Override
                    public void onSucess(JBoolean oo) {
                        refreshAll();
                        System.out.println("Измерение удалено");
                        new APICall<JEmpty>(main){
                            @Override
                            public Call<JEmpty> apiFun() {
                                return main.service.removeArtifact(main.debugToken,file.getArtifact().getOid());
                                }
                            @Override
                            public void onSucess(JEmpty oo) {
                                System.out.println("Файл удален");
                            }
                        };
                    }
                };
            }
        });
    }//GEN-LAST:event_DeleteFileActionPerformed

    private void MeasureListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_MeasureListItemStateChanged
        refreshMeasure();
    }//GEN-LAST:event_MeasureListItemStateChanged

    private void ExpertNoteSelectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ExpertNoteSelectorItemStateChanged
        refreshAll();
    }//GEN-LAST:event_ExpertNoteSelectorItemStateChanged

    private void ShowTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowTreeActionPerformed
        if (lineSupportState){
            System.out.println("______________________________________________________________________________");
            for(PowerLine line : lines){
                System.out.println("Линия "+line.getTitle());
                for(Support support : line.getGroup()){
                    System.out.println("______________ Опора "+support.getTitle());
                    for(MeasureFile file : support.getFiles())
                        System.out.println(".............."+file.toString()+" "+expertNoteMap.get(file.getExpertResult()).title());
                    }
                }
            }
        new APICall<ArrayList<DBRequest>>(main) {
                @Override
                public Call<ArrayList<DBRequest>> apiFun() {
                    return main.service.getEntityList(main.debugToken,"MFSelection",Values.GetAllModeActual,2);
                    }
                @Override
                public void onSucess(ArrayList<DBRequest> oo) {
                    System.out.println("______________________________________________________________________________");
                    try{
                        for(DBRequest request : oo){
                            MFSelection selection = (MFSelection) request.get(main.gson);
                            System.out.println("______________ Выборка "+selection.getTitle());
                            for(EntityLink<MeasureFile> file : selection.getFiles())
                                System.out.println(".............."+file.toString()+" "+expertNoteMap.get(file.getRef().getExpertResult()).title());
                                }
                        } catch (UniException ee){
                            System.out.println("");
                             }
                }
            };
    }//GEN-LAST:event_ShowTreeActionPerformed

    private void OwnerSelectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_OwnerSelectorItemStateChanged
        refreshAll();
    }//GEN-LAST:event_OwnerSelectorItemStateChanged

    private void SelectionSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectionSaveActionPerformed
        if (!selectionChanged)
            return;
        int idx = SelectionList.getSelectedIndex();
        if (idx==0){
            new OKFull(200, 200, "Сохранить выборку?", new I_ButtonFull() {
                @Override
                public void onPush(boolean yes) {
                    if (!yes){
                        clearChanges();
                        tempSelection.clear();
                        refreshSelection();
                        return;
                        }
                    String name = SelectionName.getText();
                    if (name.length()==0){
                        popup("Название выборки?");
                        return;
                        }
                    final MFSelection mfSelection = new MFSelection();
                    mfSelection.setName(name);
                    mfSelection.getUser().setOidRef(main.loginUser);
                    for(MeasureFile file : selection)
                        mfSelection.getFiles().addOidRef(file);
                    clearChanges();
                    new APICall<JLong>(main) {
                        @Override
                        public Call<JLong> apiFun() {
                            return main.service.addEntity(main.debugToken,new DBRequest(mfSelection,main.gson),0);
                            }
                        @Override
                        public void onSucess(JLong oo) {
                            refreshSelectionListForced();
                            }
                        };
                    }
                });
            }
        else{
            MFSelection mfSelection = selectionList.get(idx-1);
            new OKFull(200, 200, "Обновить выборку?", new I_ButtonFull() {
                @Override
                public void onPush(boolean yes) {
                    if (!yes){
                        clearChanges();
                        refreshSelection();
                        return;
                        }
                    mfSelection.getFiles().clear();
                    for(MeasureFile file : selection)
                        mfSelection.getFiles().addOidRef(file);
                    clearChanges();
                    new APICall<JEmpty>(main) {
                        @Override
                        public Call<JEmpty> apiFun() {
                            return main.service.updateEntity(main.debugToken,new DBRequest(mfSelection,main.gson));
                            }
                        @Override
                        public void onSucess(JEmpty oo) {
                            refreshSelectionListForced();
                            }
                        };
                    }
            });
        }
    }//GEN-LAST:event_SelectionSaveActionPerformed

    private void SelectionFileRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectionFileRemoveActionPerformed
        if (SelectionMeasureList.getItemCount()==0)
            return;
        int idx= SelectionMeasureList.getSelectedIndex();
        selection.remove(idx);
        SelectionCount.setText(""+selection.size());
        SelectionMeasureList.remove(idx);
        wasChanged();
    }//GEN-LAST:event_SelectionFileRemoveActionPerformed

    private void SelectionListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SelectionListItemStateChanged
        refreshSelection();
    }//GEN-LAST:event_SelectionListItemStateChanged

    private void SelectionRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectionRemoveActionPerformed
        int idx = SelectionList.getSelectedIndex();
        new OK(200, 200, "Удалить выборку"+(WithFiles.isSelected() ? " c файлами?":"?"), new I_Button() {
            @Override
            public void onPush() {
                final int idx = SelectionList.getSelectedIndex();
                if (idx==0){
                    tempSelection.clear();
                    clearChanges();
                    refreshSelectionListForced();
                    }
                else
                    new APICall<JBoolean>(main) {
                        @Override
                        public Call<JBoolean> apiFun() {
                            return main.service.removeEntity(main.debugToken,"MFSelection",selectionList.get(idx-1).getOid());
                            }
                        @Override
                        public void onSucess(JBoolean oo) {
                            clearChanges();
                            try{
                                MFSelection removeList = selectionList.get(idx-1);
                                if (WithFiles.isSelected()) {
                                    for (EntityLink<MeasureFile> mfile : removeList.getFiles()) {
                                        new APICallSynch<JBoolean>() {
                                            @Override
                                            public Call<JBoolean> apiFun() {
                                                return main.service.removeEntity(main.debugToken, "MeasureFile", mfile.getOid());
                                                }
                                            }.call();
                                        new APICallSynch<JEmpty>() {
                                            @Override
                                            public Call<JEmpty> apiFun() {
                                                return main.service.removeArtifact(main.debugToken, mfile.getRef().getArtifact().getOid());
                                                }
                                            }.call();
                                        }
                                    }
                                refreshAll();
                                } catch (UniException ee){
                                    System.out.println(ee.toString());
                                    }
                            }
                        };
                    }
            });
    }//GEN-LAST:event_SelectionRemoveActionPerformed

    private void SplitMeasureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SplitMeasureActionPerformed
        if (measureFiles.size()==0)
            return;
        final MeasureFile file = measureFiles.get(MeasureList.getSelectedIndex());
        new OK(200, 200, "Нарезать по ударам", new I_Button() {
            @Override
            public void onPush() {
                int dd1=0,dd2=0,dd3=0;
                try {
                    dd1 = Integer.parseInt(StartOver.getText());
                    dd2 = Integer.parseInt(StartLevelProc.getText());
                    dd3 = Integer.parseInt(SkipTimeMS.getText());
                    } catch (Exception ee){
                        System.out.println("Недопустимый формат целого - нарезка");
                        return;
                        }
                final int kk1 = dd1, kk2 = dd2, kk3=dd3;
                new APICall<MFSelection>(main) {
                    @Override
                    public Call<MFSelection> apiFun() {
                        return main2.service2.splitMeasure(main.debugToken,file.getOid(),Size32768.isSelected(),kk1,kk2,kk3);
                        }
                    @Override
                    public void onSucess(MFSelection selection) {
                        refreshAll();
                        }
                    };
                }
            });

    }//GEN-LAST:event_SplitMeasureActionPerformed

    private void StartLevelProcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartLevelProcActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StartLevelProcActionPerformed

    private void ShowTree1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowTree1ActionPerformed
        evaluateOneResult();
    }//GEN-LAST:event_ShowTree1ActionPerformed

    private void refreshMeasure(){
        ExpertNote.setVisible(measureFiles.size()!=0);
        Owner.setVisible(measureFiles.size()!=0);
        if (measureFiles.size()==0) return;
        MeasureFile file  = measureFiles.get(MeasureList.getSelectedIndex());
        ExpertNote.setText(expertNoteMap.get(file.getExpertResult()).title());
        if (file.getUserID()==0){
            Owner.setText("");
            }
        else{
            User user = userMap.get(file.getUserID());
            Owner.setText("["+user.getOid()+"] "+user.getLastName());
            }
        }

    private void refreshResults(){
        Results.removeAll();
        for(AnalyseResult result : results){
            Results.add(result.getTitle());
            }
        showPeakPlaces();
        showOneResult();
        }
    private void crearOneResult(){
        clearResultsCriteriaList();
        URL url = getClass().getResource("/drawable/status_gray.png");
        selectedResult=null;
        ExpertNoteList.removeAll();
        }
    private void showOneResultByCriteria(int idx){
        if (selectedResult==null)
            return;
        ExtremeList extreme = selectedResult.data.get(idx);
        int res = extreme.getTestResult();
        ResultData.append(res+"----------------------------------------------\n");
        ConstValue ss = algResultMap.get(res);
        criteriaResults.get(idx).setText(ss == null ? "Недопустимый результат" : ss.title());
        String icon = StateColors.get(res);
        if (icon==null){
            System.out.println("Не найден результат анализа: "+res);
            }
        criteriaResultLamps.get(idx).setIcon(new javax.swing.ImageIcon(getClass().getResource(icon)));
        ResultData.append(extreme.getTestComment()+"\n");
        ResultData.append(extreme.showExtrems(selectedResult.firstFreq,selectedResult.lastFreq,selectedResult.dFreq));
        }

    public void showPeakPlaces(){
        ResultCommonData.setText("");
        for(MeasureFile ss : selection){
            ResultCommonData.append(ss.toString()+"\n");
            }
        AnalyseResultList list = new AnalyseResultList(results);
        ArrayList<PeakPlace> res = list.calcPeakPlaces();
        ResultCommonData.append("Суммарный вес пиков:\n");
        ResultCommonData.append("частота кол-во ∑мест ∑значений ∑мест*знач. декремент\n");
        for(PeakPlace pp : res)
            ResultCommonData.append(pp.toString()+"\n");
        }

    private void showOneResult(){
        crearOneResult();
        if (results.size()==0)
            return;
        selectedResult = results.get(Results.getSelectedIndex());
        if (!selectedResult.valid){
            ResultData.setText("Результат с ошибкой:\n"+selectedResult.message);
            return;
            }
        ResultData.setText(selectedResult.message);
        for (ConstValue cc : resultStates)
            ExpertNoteList.add(cc.title());
        ExpertNoteList.select(selectedResult.measure.getExpertResult());
        for(int idx=0;idx<criterisList.size();idx++){
            showOneResultByCriteria(idx);
            }
        }

    private void evaluateOneResult(){
        if (results.size()==0)
            return;
        main.sendEventPanel(EventNetwork,0,0,"",results.get(Results.getSelectedIndex()));
        }

    private void refreshMeasureList(){
        MeasureList.removeAll();
        MeasuresCount.setText(""+measureFiles.size());
        for(MeasureFile ss : measureFiles){
            MeasureList.add(ss.getTitle());
            }
        refreshMeasure();
        }
    private void refreshSupport(){
        SupportList.removeAll();
        if(lines.size()==0)
            return;
        supports = lines.get(PowerLineList.getSelectedIndex()).getGroup();
        for(Support ss : supports){
            SupportList.add(ss.getName());
            }
        measureFiles = supports.get(0).getFiles();
        refreshMeasureList();
        }

    private void refreshParams(){
        AnalyseParams.removeAll();
        params.clear();
        new APICall<ArrayList<DBRequest>>(main){
            @Override
            public Call<ArrayList<DBRequest>> apiFun() {
                return main.service.getEntityList(main.debugToken,"LEP500Params", Values.GetAllModeActual,0);
                }
            @Override
            public void onSucess(ArrayList<DBRequest> oo) {
                params.clear();
                for(DBRequest dd : oo){
                    try {
                        LEP500Params param = (LEP500Params) dd.get(main.gson);
                        AnalyseParams.add("["+param.getOid()+"] "+param.getTitle());
                        params.add(param);
                        } catch (UniException e) {
                            System.out.println(e);
                            }
                        }
                }
            };
        }

    private void refreshAll(){
        lineSupportState = LineSupportState.getState();
        refreshParams();
        refreshSelectionListForced();
        clearResultsCriteriaList();
        ExpertNoteList.removeAll();
        SelectionMeasureList.removeAll();
        MeasureList.removeAll();
        PowerLineList.removeAll();
        SupportList.removeAll();
        supports.clear();
        measureFiles.clear();
        lines.clear();
        selection.clear();
        OwnerSelector.setEnabled(!lineSupportState);
        ExpertNoteSelector.setEnabled(!lineSupportState);
        PowerLineList.setEnabled(lineSupportState);
        SupportList.setEnabled(lineSupportState);
        if (!lineSupportState){
            int idx= ExpertNoteSelector.getSelectedIndex();
            int note = idx==0 ? 0 :  resultStates.get(idx-1).value();
            idx = OwnerSelector.getSelectedIndex();
            long userId = idx==0 ? 0 : userList.get(idx-1).getOid();
            new APICall<ArrayList<MeasureFile>>(main){
                @Override
                public Call<ArrayList<MeasureFile>> apiFun() {
                    return main2.service2.getMeasureSelection(main.debugToken,note,userId,"","");
                    }
                @Override
                public void onSucess(ArrayList<MeasureFile> oo) {
                    measureFiles.clear();
                    measureFiles = oo;
                    MeasuresCount.setText(""+measureFiles.size());
                    for(MeasureFile ss : oo){
                        MeasureList.add(ss.getTitle());
                        }
                    refreshMeasure();
                    }
                };
            return;
            }
        new APICall<ArrayList<DBRequest>>(main){
            @Override
            public Call<ArrayList<DBRequest>> apiFun() {
                return main.service.getEntityList(main.debugToken,"PowerLine", Values.GetAllModeActual,3);
                }
            @Override
            public void onSucess(ArrayList<DBRequest> oo) {
                lines.clear();
                for(DBRequest dd : oo){
                    try {
                        PowerLine line = (PowerLine) dd.get(main.gson);
                        lines.add(line);
                        PowerLineList.add(line.getName());
                        } catch (UniException e) {
                             System.out.println(e);
                            }
                        }
                    refreshSupport();
                    }
                };
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddMeasure;
    private javax.swing.JButton AnaluseResultRemove;
    private javax.swing.JButton AnaluseResultsCrear;
    private java.awt.Choice AnalyseParams;
    private javax.swing.JButton AnalyseSelection;
    private javax.swing.JButton DeleteFile;
    private javax.swing.JTextField ExpertNote;
    private java.awt.Choice ExpertNoteList;
    private javax.swing.JButton ExpertNoteSave;
    private java.awt.Choice ExpertNoteSelector;
    private java.awt.Checkbox LineSupportState;
    private java.awt.Choice MeasureList;
    private javax.swing.JTextField MeasuresCount;
    private javax.swing.JTextField Owner;
    private java.awt.Choice OwnerSelector;
    private java.awt.Choice PowerLineList;
    private javax.swing.JButton Refresh;
    private java.awt.TextArea ResultCommonData;
    private java.awt.TextArea ResultData;
    private java.awt.Choice Results;
    private javax.swing.JTextField SelectionCount;
    private javax.swing.JButton SelectionFileRemove;
    private java.awt.Choice SelectionList;
    private java.awt.Choice SelectionMeasureList;
    private javax.swing.JTextField SelectionName;
    private javax.swing.JButton SelectionRemove;
    private javax.swing.JButton SelectionSave;
    private javax.swing.JButton SelectuionAdd;
    private javax.swing.JButton ShowGraph;
    private javax.swing.JButton ShowTree;
    private javax.swing.JButton ShowTree1;
    private javax.swing.JCheckBox Size32768;
    private javax.swing.JTextField SkipTimeMS;
    private javax.swing.JButton SplitMeasure;
    private javax.swing.JTextField StartLevelProc;
    private javax.swing.JTextField StartOver;
    private java.awt.Choice SupportList;
    private javax.swing.JCheckBox WithFiles;
    private java.awt.Choice choice1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
