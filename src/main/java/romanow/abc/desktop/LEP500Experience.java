/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.abc.desktop;

import retrofit2.Call;
import retrofit2.Response;
import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.ConstValue;
import romanow.abc.core.constants.OidList;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.Entity;
import romanow.abc.core.entity.baseentityes.JBoolean;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.subjectarea.MeasureFile;
import romanow.abc.core.entity.subjectarea.PowerLine;
import romanow.abc.core.entity.subjectarea.Support;
import romanow.abc.core.mongo.DBQueryInt;
import romanow.abc.core.mongo.DBQueryList;
import romanow.abc.core.mongo.DBXStream;
import romanow.abc.core.mongo.I_DBQuery;
import romanow.lep500.AnalyseResult;
import romanow.lep500.LEP500Params;
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
public class LEP500Experience extends LEP500BasePanel {
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
    private HashMap<Integer,String> analyseStateList = new HashMap<>();
    private ArrayList<ConstValue> resultStates = new ArrayList<>();
    private ArrayList<String> criterisList= new ArrayList<>();
    private ArrayList<MeasureFile> measureFiles = new ArrayList<>();
    private ArrayList<MeasureFile> selectedFiles = new ArrayList<>();
    private ArrayList<PowerLine> lines = new ArrayList<>();
    private ArrayList<Support> supports = new ArrayList<>();
    private ArrayList<LEP500Params> params = new ArrayList<>();
    private ArrayList<AnalyseResult> results = new ArrayList<>();
    private AnalyseResult selectedResult=null;
    /**
     * Creates new form LEP500Example
     */
    public LEP500Experience() {
        initComponents();
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
        ExpertResult3.setVisible(false);
        resultStates = main.filter(main.constList,"EState");
        resultStates.sort(new Comparator<ConstValue>() {
            @Override
            public int compare(ConstValue o1, ConstValue o2) {
                return o1.value()-o2.value();
                }
            });
        analyseStateList.clear();
        for(ConstValue cc : resultStates)
            analyseStateList.put(cc.value(),cc.title());
        criterisList.clear();
        for (int mode = 0; mode < FFTStatistic.extremeFacade.length; mode++) {
            ExtremeFacade facade;
            try {
                facade = (ExtremeFacade)FFTStatistic.extremeFacade[mode].newInstance();
                } catch (Exception e) {
                    facade = new ExtremeNull();
                    }
                criterisList.add(facade.getTitle());
            }
        ExpertResult2.removeAll();
        ExpertResult3.removeAll();
        ExpertResult3.add("Все определенные");
        for (ConstValue cc : resultStates){
            ExpertResult2.add(cc.title());
            ExpertResult3.add(cc.title());
            }
        initResultsCriteriaList();
        refreshAll();
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
        Selection = new java.awt.Choice();
        jLabel1 = new javax.swing.JLabel();
        FromSelect = new javax.swing.JButton();
        Linked = new java.awt.Checkbox();
        PowerLine = new java.awt.Choice();
        jLabel3 = new javax.swing.JLabel();
        Support = new java.awt.Choice();
        ProcSelection = new javax.swing.JButton();
        Refresh = new javax.swing.JButton();
        ToSelect = new javax.swing.JButton();
        MeasureList = new java.awt.Choice();
        jLabel4 = new javax.swing.JLabel();
        Params = new java.awt.Choice();
        jLabel5 = new javax.swing.JLabel();
        Results = new java.awt.Choice();
        jLabel6 = new javax.swing.JLabel();
        RemoveResults = new javax.swing.JButton();
        ShowGraph = new javax.swing.JButton();
        CrearResults = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ExpertResult = new java.awt.Choice();
        SaveExpert = new javax.swing.JButton();
        ResultData = new java.awt.TextArea();
        DeleteFile = new javax.swing.JButton();
        ExpertResult2 = new java.awt.Choice();
        ExpertSelectionMode = new java.awt.Checkbox();
        ExpertResult3 = new java.awt.Choice();
        jSeparator1 = new javax.swing.JSeparator();

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
        AddMeasure.setBounds(690, 40, 40, 30);
        add(Selection);
        Selection.setBounds(90, 120, 530, 20);

        jLabel1.setText("Параметры");
        add(jLabel1);
        jLabel1.setBounds(10, 150, 70, 14);

        FromSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/up.PNG"))); // NOI18N
        FromSelect.setBorderPainted(false);
        FromSelect.setContentAreaFilled(false);
        FromSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FromSelectActionPerformed(evt);
            }
        });
        add(FromSelect);
        FromSelect.setBounds(580, 75, 40, 40);

        Linked.setLabel("Опора/Линия");
        Linked.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LinkedItemStateChanged(evt);
            }
        });
        add(Linked);
        Linked.setBounds(290, 40, 130, 20);

        PowerLine.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                PowerLineItemStateChanged(evt);
            }
        });
        add(PowerLine);
        PowerLine.setBounds(90, 70, 190, 20);

        jLabel3.setText("Опора");
        add(jLabel3);
        jLabel3.setBounds(10, 40, 70, 14);

        Support.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SupportItemStateChanged(evt);
            }
        });
        add(Support);
        Support.setBounds(90, 40, 190, 20);

        ProcSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/lep500button.png"))); // NOI18N
        ProcSelection.setBorderPainted(false);
        ProcSelection.setContentAreaFilled(false);
        ProcSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProcSelectionActionPerformed(evt);
            }
        });
        add(ProcSelection);
        ProcSelection.setBounds(640, 110, 35, 35);

        Refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/refresh.png"))); // NOI18N
        Refresh.setBorderPainted(false);
        Refresh.setContentAreaFilled(false);
        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });
        add(Refresh);
        Refresh.setBounds(640, 40, 30, 30);

        ToSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/updown.png"))); // NOI18N
        ToSelect.setBorderPainted(false);
        ToSelect.setContentAreaFilled(false);
        ToSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToSelectActionPerformed(evt);
            }
        });
        add(ToSelect);
        ToSelect.setBounds(580, 35, 40, 40);

        MeasureList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MeasureListItemStateChanged(evt);
            }
        });
        add(MeasureList);
        MeasureList.setBounds(90, 10, 530, 20);

        jLabel4.setText("Линия");
        add(jLabel4);
        jLabel4.setBounds(10, 70, 70, 14);
        add(Params);
        Params.setBounds(90, 150, 190, 20);

        jLabel5.setText("Выборка");
        add(jLabel5);
        jLabel5.setBounds(10, 120, 70, 14);

        Results.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ResultsItemStateChanged(evt);
            }
        });
        add(Results);
        Results.setBounds(90, 190, 530, 20);

        jLabel6.setText("Измерения");
        add(jLabel6);
        jLabel6.setBounds(10, 10, 70, 14);

        RemoveResults.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/remove.png"))); // NOI18N
        RemoveResults.setBorderPainted(false);
        RemoveResults.setContentAreaFilled(false);
        RemoveResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveResultsActionPerformed(evt);
            }
        });
        add(RemoveResults);
        RemoveResults.setBounds(640, 180, 30, 30);

        ShowGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/graph.png"))); // NOI18N
        ShowGraph.setBorderPainted(false);
        ShowGraph.setContentAreaFilled(false);
        ShowGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowGraphActionPerformed(evt);
            }
        });
        add(ShowGraph);
        ShowGraph.setBounds(750, 175, 40, 40);

        CrearResults.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/clear.png"))); // NOI18N
        CrearResults.setBorderPainted(false);
        CrearResults.setContentAreaFilled(false);
        CrearResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearResultsActionPerformed(evt);
            }
        });
        add(CrearResults);
        CrearResults.setBounds(695, 180, 30, 30);

        jLabel7.setText("Результаты");
        add(jLabel7);
        jLabel7.setBounds(10, 190, 70, 14);

        jLabel8.setText("Оценка эксперта");
        add(jLabel8);
        jLabel8.setBounds(10, 230, 110, 14);
        add(ExpertResult);
        ExpertResult.setBounds(140, 230, 190, 20);

        SaveExpert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/save.png"))); // NOI18N
        SaveExpert.setBorderPainted(false);
        SaveExpert.setContentAreaFilled(false);
        SaveExpert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveExpertActionPerformed(evt);
            }
        });
        add(SaveExpert);
        SaveExpert.setBounds(340, 220, 30, 30);
        add(ResultData);
        ResultData.setBounds(420, 250, 460, 370);

        DeleteFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable/remove.png"))); // NOI18N
        DeleteFile.setBorderPainted(false);
        DeleteFile.setContentAreaFilled(false);
        DeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteFileActionPerformed(evt);
            }
        });
        add(DeleteFile);
        DeleteFile.setBounds(750, 40, 30, 30);

        ExpertResult2.setEnabled(false);
        add(ExpertResult2);
        ExpertResult2.setBounds(630, 10, 150, 20);

        ExpertSelectionMode.setLabel("Оценка");
        ExpertSelectionMode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ExpertSelectionModeItemStateChanged(evt);
            }
        });
        add(ExpertSelectionMode);
        ExpertSelectionMode.setBounds(290, 70, 70, 20);

        ExpertResult3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ExpertResult3ItemStateChanged(evt);
            }
        });
        add(ExpertResult3);
        ExpertResult3.setBounds(370, 70, 170, 20);
        add(jSeparator1);
        jSeparator1.setBounds(10, 108, 560, 2);
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

    public void refreshSelection(){
        MeasureList.removeAll();
        for(MeasureFile ss : measureFiles)
            MeasureList.add(ss.getTitle());
        Selection.removeAll();
        for(MeasureFile ss : selectedFiles)
            Selection.add(ss.getTitle());
        selectExperienceResult2();
        }

    private void FromSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FromSelectActionPerformed
        if(selectedFiles.size()==0)
            return;
        measureFiles.add(selectedFiles.remove(Selection.getSelectedIndex()));
        refreshSelection();
    }//GEN-LAST:event_FromSelectActionPerformed

    private void LinkedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LinkedItemStateChanged
        ExpertSelectionMode.setState(false);
        refreshAll();
    }//GEN-LAST:event_LinkedItemStateChanged

    private void SupportItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SupportItemStateChanged
        if (Linked.getState())
            refreshMeasure(true);
    }//GEN-LAST:event_SupportItemStateChanged

    private void PowerLineItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_PowerLineItemStateChanged
        if (Linked.getState())
            refreshSupport();
    }//GEN-LAST:event_PowerLineItemStateChanged

    private void ProcSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProcSelectionActionPerformed
        if (selectedFiles.size()==0 || params.size()==0)
            return;
        OidList  list = new OidList();
        for(MeasureFile ss : selectedFiles){
            list.oids.add(ss.getOid());
            }
        new APICall<ArrayList<AnalyseResult>>(main){
            @Override
            public Call<ArrayList<AnalyseResult>> apiFun() {
                return main2.service2.analyse(main.debugToken,params.get(Params.getSelectedIndex()).getOid(),list);
                }
            @Override
            public void onSucess(ArrayList<AnalyseResult> oo) {
                for(AnalyseResult dd : oo){
                    results.add(dd);
                }
            refreshResults();
            }
        };

    }//GEN-LAST:event_ProcSelectionActionPerformed

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        refreshAll();
    }//GEN-LAST:event_RefreshActionPerformed

    private void ToSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToSelectActionPerformed
        if(measureFiles.size()==0)
            return;
        selectedFiles.add(measureFiles.remove(MeasureList.getSelectedIndex()));
        refreshSelection();

    }//GEN-LAST:event_ToSelectActionPerformed

    private void RemoveResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveResultsActionPerformed
        if(results.size()==0)
            return;
        results.remove(Results.getSelectedIndex());
        refreshResults();
    }//GEN-LAST:event_RemoveResultsActionPerformed

    private void CrearResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearResultsActionPerformed
        results.clear();
        refreshResults();
    }//GEN-LAST:event_CrearResultsActionPerformed

    private void ResultsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ResultsItemStateChanged
        showOneResult();
    }//GEN-LAST:event_ResultsItemStateChanged

    private void ShowGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowGraphActionPerformed
        if (results.size()==0)
            return;
        main.sendEventPanel(EventGraph,0,0,"",results);
    }//GEN-LAST:event_ShowGraphActionPerformed

    private void SaveExpertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveExpertActionPerformed
        if (selectedResult==null)
            return;
        selectedResult.measure.setExpertResult(ExpertResult.getSelectedIndex());
        try {
            Response<JEmpty> wsr = main.service.updateEntityField(main.debugToken,"expertResult",new DBRequest(selectedResult.measure,main.gson)).execute();
            if (!wsr.isSuccessful()){
                popup("Ошибка обновления параметров  " + httpError(wsr));
                return;
                }
            else
                popup("Оценка эксперта обновлена");
            } catch (IOException e) {
                popup(e.toString());
                }
    }//GEN-LAST:event_SaveExpertActionPerformed

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
        selectExperienceResult2();
    }//GEN-LAST:event_MeasureListItemStateChanged

    private void ExpertSelectionModeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ExpertSelectionModeItemStateChanged
        Linked.setState(false);
        refreshAll();
    }//GEN-LAST:event_ExpertSelectionModeItemStateChanged

    private void ExpertResult3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ExpertResult3ItemStateChanged
        if (ExpertSelectionMode.getState())
            refreshAll();
    }//GEN-LAST:event_ExpertResult3ItemStateChanged

    private void selectExperienceResult2(){
        ExpertResult2.setVisible(measureFiles.size()!=0);
        if (measureFiles.size()==0) return;
        int resultIdx = measureFiles.get(MeasureList.getSelectedIndex()).getExpertResult();
        ExpertResult2.select(resultIdx);
        }

    private void refreshResults(){
        Results.removeAll();
        for(AnalyseResult result : results){
            Results.add(result.getTitle());
            }
        showOneResult();
        }
    private void crearOneResult(){
        clearResultsCriteriaList();
        URL url = getClass().getResource("/drawable/status_gray.png");
        selectedResult=null;
        ExpertResult.removeAll();
        }
    private void showOneResultByCriteria(int idx){
        if (selectedResult==null)
            return;
        ExtremeList extreme = selectedResult.data.get(idx);
        int res = extreme.getTestResult();
        ResultData.append(res+"----------------------------------------------\n");
        String ss = analyseStateList.get(res);
        criteriaResults.get(idx).setText(ss == null ? "Недопустимый результат" : ss);
        String icon = StateColors.get(res);
        if (icon==null){
            System.out.println("Не найден результат анализа: "+res);
            }
        criteriaResultLamps.get(idx).setIcon(new javax.swing.ImageIcon(getClass().getResource(icon)));
        ResultData.append(extreme.getTestComment()+"\n");
        ResultData.append(extreme.showExtrems(selectedResult.firstFreq,selectedResult.lastFreq,selectedResult.dFreq));
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
            ExpertResult.add(cc.title());
        ExpertResult.select(selectedResult.measure.getExpertResult());
        for(int idx=0;idx<criterisList.size();idx++){
            showOneResultByCriteria(idx);
            }
        }

    private void refreshMeasure(boolean byLine){
        ExpertResult.removeAll();
        MeasureList.removeAll();
        if (byLine)
            measureFiles = supports.get(Support.getSelectedIndex()).getFiles();
        for(MeasureFile ss : measureFiles){
            MeasureList.add(ss.getTitle());
            }
        selectExperienceResult2();
        }
    private void refreshSupport(){
        ExpertResult.removeAll();
        Support.removeAll();
        supports.clear();
        if(lines.size()!=0){
            supports = lines.get(PowerLine.getSelectedIndex()).getGroup();
            for(Support ss : supports){
                Support.add(ss.getName());
                }
            }
        refreshMeasure(true);
        }

    private void refreshParams(){
        Params.removeAll();
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
                        Params.add(param.getTitle());
                        params.add(param);
                        } catch (UniException e) {
                            System.out.println(e);
                            }
                        }
                }
            };
        }

    private void refreshAll(){
        refreshParams();
        clearResultsCriteriaList();
        ExpertResult.removeAll();
        Selection.removeAll();
        MeasureList.removeAll();
        PowerLine.removeAll();
        Support.removeAll();
        supports.clear();
        selectedFiles.clear();
        measureFiles.clear();
        lines.clear();
        ExpertResult3.setVisible(ExpertSelectionMode.getState());
        if (ExpertSelectionMode.getState()){
            int idx= ExpertResult3.getSelectedIndex();
            DBQueryList query = idx!=0 ?
                    new DBQueryList().add(new DBQueryInt("expertResult",idx-1))  :
                    new DBQueryList().add(new DBQueryInt(I_DBQuery.ModeNEQ,"expertResult",0));
            final String xmlQuery = new DBXStream().toXML(query);
            new APICall<ArrayList<DBRequest>>(main){
                @Override
                public Call<ArrayList<DBRequest>> apiFun() {
                    return main.service.getEntityListByQuery(main.debugToken,"MeasureFile",xmlQuery,1);
                    }
                @Override
                public void onSucess(ArrayList<DBRequest> oo) {
                    measureFiles.clear();
                    for(DBRequest dd : oo){
                        try {
                            MeasureFile ss = (MeasureFile) dd.get(main.gson);
                            MeasureList.add(ss.getTitle());
                            measureFiles.add(ss);
                            } catch (UniException e) {
                                System.out.println(e);
                                }
                        }
                    refreshMeasure(false);
                }
            };
            return;
            }
        if (!Linked.getState()){
            new APICall<ArrayList<DBRequest>>(main){
                @Override
                public Call<ArrayList<DBRequest>> apiFun() {
                    return main.service.getEntityList(main.debugToken,"MeasureFile", Values.GetAllModeActual,1);
                    }
                @Override
                public void onSucess(ArrayList<DBRequest> oo) {
                    measureFiles.clear();
                    for(DBRequest dd : oo){
                        try {
                            MeasureFile ss = (MeasureFile) dd.get(main.gson);
                            MeasureList.add(ss.getTitle());
                            measureFiles.add(ss);
                            } catch (UniException e) {
                                System.out.println(e);
                                }
                            }
                    refreshMeasure(false);
                    }
                };
            }
        else{
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
                            PowerLine.add(line.getName());
                            } catch (UniException e) {
                                System.out.println(e);
                                }
                            }
                    refreshSupport();
                    }
                };
            }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddMeasure;
    private javax.swing.JButton CrearResults;
    private javax.swing.JButton DeleteFile;
    private java.awt.Choice ExpertResult;
    private java.awt.Choice ExpertResult2;
    private java.awt.Choice ExpertResult3;
    private java.awt.Checkbox ExpertSelectionMode;
    private javax.swing.JButton FromSelect;
    private java.awt.Checkbox Linked;
    private java.awt.Choice MeasureList;
    private java.awt.Choice Params;
    private java.awt.Choice PowerLine;
    private javax.swing.JButton ProcSelection;
    private javax.swing.JButton Refresh;
    private javax.swing.JButton RemoveResults;
    private java.awt.TextArea ResultData;
    private java.awt.Choice Results;
    private javax.swing.JButton SaveExpert;
    private java.awt.Choice Selection;
    private javax.swing.JButton ShowGraph;
    private java.awt.Choice Support;
    private javax.swing.JButton ToSelect;
    private java.awt.Choice choice1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
