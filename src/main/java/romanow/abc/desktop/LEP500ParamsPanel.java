/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.abc.desktop;

import romanow.abc.core.DBRequest;
import romanow.abc.core.entity.Entity;
import romanow.abc.core.entity.EntityList;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.subjectarea.WorkSettings;
import retrofit2.Response;
import romanow.lep500.LEP500Params;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class LEP500ParamsPanel extends LEP500BasePanel {
    private WorkSettings ws;
    EntityPanelUni paramsList;
    EntityList<LEP500Params> list = new EntityList<>();
    public LEP500ParamsPanel() {
        initComponents();
        }
    public void initPanel(MainBaseFrame main0){
        super.initPanel(main0);
        paramsList  = new EntityPanelUni(10, 15, list, "LEP500Params", main,true,0,5) {
            @Override
            public EntityList<Entity> getLazy(){
                return  null;
                }
            @Override
            public boolean isRecordSelected(Entity ent) {
                return true;
                }
            @Override
            public void showRecord() {
                showParams();
            }
            @Override
            public void updateRecord() {
                updateParams();
            }
        };
        //=================================================================================================================
        add(paramsList);        
        }
    public void showParams(){}
    public void updateParams(){}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        GUIrefreshPeriod = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        ArchiveDepthInDay = new javax.swing.JTextField();
        StreamDataPeriod = new javax.swing.JTextField();
        StreamDataLongPeriod = new javax.swing.JTextField();
        FailureTestPeriod = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        EventsPeriod = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        RegisterAge = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        PLMEmulator = new javax.swing.JCheckBox();
        PLMPort = new javax.swing.JTextField();
        PLMIP = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        PLMTimeOut = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        PLMGroupSize = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        FileScanPeriod = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        MainServerPeriod = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        StreamDataLimit = new javax.swing.JTextField();
        PLMReady = new javax.swing.JCheckBox();
        jLabel36 = new javax.swing.JLabel();
        StreamDataCompressMode = new javax.swing.JTextField();
        Refresh = new javax.swing.JButton();
        UserSilenceTime = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        MainServer = new javax.swing.JCheckBox();
        jLabel28 = new javax.swing.JLabel();

        setLayout(null);
        add(jSeparator1);
        jSeparator1.setBounds(410, 80, 240, 10);

        GUIrefreshPeriod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GUIrefreshPeriodKeyPressed(evt);
            }
        });
        add(GUIrefreshPeriod);
        GUIrefreshPeriod.setBounds(310, 210, 70, 25);

        jLabel17.setText("Регистров в блоке");
        add(jLabel17);
        jLabel17.setBounds(420, 220, 110, 14);

        jLabel18.setText("Верхняя граница частоты диапазона макс.");
        add(jLabel18);
        jLabel18.setBounds(20, 130, 250, 14);

        jLabel19.setText("Вид компрессии потоковых данных");
        add(jLabel19);
        jLabel19.setBounds(20, 400, 260, 14);

        jLabel20.setText("Цикл обнаружения аварий (сек)");
        add(jLabel20);
        jLabel20.setBounds(20, 190, 230, 14);

        ArchiveDepthInDay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ArchiveDepthInDayKeyPressed(evt);
            }
        });
        add(ArchiveDepthInDay);
        ArchiveDepthInDay.setBounds(310, 90, 70, 25);

        StreamDataPeriod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StreamDataPeriodKeyPressed(evt);
            }
        });
        add(StreamDataPeriod);
        StreamDataPeriod.setBounds(310, 120, 70, 25);

        StreamDataLongPeriod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StreamDataLongPeriodKeyPressed(evt);
            }
        });
        add(StreamDataLongPeriod);
        StreamDataLongPeriod.setBounds(310, 150, 70, 25);

        FailureTestPeriod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FailureTestPeriodKeyPressed(evt);
            }
        });
        add(FailureTestPeriod);
        FailureTestPeriod.setBounds(310, 180, 70, 25);

        jLabel21.setText("Период обновления форм ЧМИ (сек)");
        add(jLabel21);
        jLabel21.setBounds(20, 220, 210, 14);

        EventsPeriod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EventsPeriodKeyPressed(evt);
            }
        });
        add(EventsPeriod);
        EventsPeriod.setBounds(310, 240, 70, 25);

        jLabel22.setText("\"Возраст\" регистра в кэше (мс)");
        add(jLabel22);
        jLabel22.setBounds(20, 280, 250, 14);

        RegisterAge.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RegisterAgeKeyPressed(evt);
            }
        });
        add(RegisterAge);
        RegisterAge.setBounds(310, 270, 70, 25);

        jLabel23.setText("Цикл опроса дискретных   событий (сек)");
        add(jLabel23);
        jLabel23.setBounds(20, 250, 250, 14);

        PLMEmulator.setText("Эмулятор ПЛК");
        PLMEmulator.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                PLMEmulatorItemStateChanged(evt);
            }
        });
        add(PLMEmulator);
        PLMEmulator.setBounds(420, 90, 150, 23);

        PLMPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PLMPortKeyPressed(evt);
            }
        });
        add(PLMPort);
        PLMPort.setBounds(530, 150, 60, 25);

        PLMIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PLMIPKeyPressed(evt);
            }
        });
        add(PLMIP);
        PLMIP.setBounds(530, 120, 110, 25);

        jLabel25.setText("IP ПЛМ");
        add(jLabel25);
        jLabel25.setBounds(420, 130, 60, 14);

        PLMTimeOut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PLMTimeOutKeyPressed(evt);
            }
        });
        add(PLMTimeOut);
        PLMTimeOut.setBounds(530, 180, 60, 25);

        jLabel26.setText("Порт ПЛМ");
        add(jLabel26);
        jLabel26.setBounds(420, 160, 60, 14);

        jLabel27.setText("Тайм-аут (с)");
        add(jLabel27);
        jLabel27.setBounds(420, 190, 90, 14);

        PLMGroupSize.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PLMGroupSizeKeyPressed(evt);
            }
        });
        add(PLMGroupSize);
        PLMGroupSize.setBounds(530, 210, 60, 25);
        add(jSeparator2);
        jSeparator2.setBounds(20, 80, 360, 10);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("ПЛК");
        add(jLabel4);
        jLabel4.setBounds(660, 70, 50, 14);

        FileScanPeriod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FileScanPeriodKeyPressed(evt);
            }
        });
        add(FileScanPeriod);
        FileScanPeriod.setBounds(310, 300, 70, 25);

        jLabel33.setText("Длинный цикл опроса потоковых данных (сек)");
        add(jLabel33);
        jLabel33.setBounds(20, 160, 260, 14);

        MainServerPeriod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MainServerPeriodKeyPressed(evt);
            }
        });
        add(MainServerPeriod);
        MainServerPeriod.setBounds(310, 330, 70, 25);

        jLabel34.setText("Цикл опроса источников файлов (сек)");
        add(jLabel34);
        jLabel34.setBounds(20, 310, 260, 14);

        jLabel35.setText("Цикл снятия данных сервером СМУ (сек)");
        add(jLabel35);
        jLabel35.setBounds(20, 340, 260, 14);

        StreamDataLimit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StreamDataLimitKeyPressed(evt);
            }
        });
        add(StreamDataLimit);
        StreamDataLimit.setBounds(310, 360, 70, 25);

        PLMReady.setText("Соединение с ПЛК");
        PLMReady.setEnabled(false);
        add(PLMReady);
        PLMReady.setBounds(420, 250, 180, 23);

        jLabel36.setText("Лимит наборов потоковых данных в периоде");
        add(jLabel36);
        jLabel36.setBounds(20, 370, 260, 14);

        StreamDataCompressMode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StreamDataCompressModeKeyPressed(evt);
            }
        });
        add(StreamDataCompressMode);
        StreamDataCompressMode.setBounds(310, 390, 70, 25);

        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });
        add(Refresh);
        Refresh.setBounds(640, 230, 40, 40);

        UserSilenceTime.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UserSilenceTimeKeyPressed(evt);
            }
        });
        add(UserSilenceTime);
        UserSilenceTime.setBounds(310, 420, 70, 25);

        jLabel1.setText("Время \"молчания\" оператора (мин)");
        add(jLabel1);
        jLabel1.setBounds(20, 430, 240, 14);

        MainServer.setText("Сервер СМУ СНЭ");
        add(MainServer);
        MainServer.setBounds(420, 280, 170, 23);

        jLabel28.setText("Нижняя граница частоты диапазона макс.");
        add(jLabel28);
        jLabel28.setBounds(20, 100, 240, 14);
    }// </editor-fold>//GEN-END:initComponents

    private void GUIrefreshPeriodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GUIrefreshPeriodKeyPressed
        procPressedInt(evt, GUIrefreshPeriod,"GUIrefreshPeriod");
    }//GEN-LAST:event_GUIrefreshPeriodKeyPressed

    private void ArchiveDepthInDayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ArchiveDepthInDayKeyPressed
        procPressedInt(evt, ArchiveDepthInDay,"archiveDepthInDay");
    }//GEN-LAST:event_ArchiveDepthInDayKeyPressed

    private void StreamDataPeriodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StreamDataPeriodKeyPressed
        procPressedInt(evt, StreamDataPeriod,"streamDataPeriod");
    }//GEN-LAST:event_StreamDataPeriodKeyPressed

    private void StreamDataLongPeriodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StreamDataLongPeriodKeyPressed
        procPressedInt(evt, StreamDataLongPeriod,"streamDataLongPeriod");
    }//GEN-LAST:event_StreamDataLongPeriodKeyPressed

    private void FailureTestPeriodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FailureTestPeriodKeyPressed
        procPressedInt(evt, FailureTestPeriod,"failureTestPeriod");
    }//GEN-LAST:event_FailureTestPeriodKeyPressed

    private void EventsPeriodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EventsPeriodKeyPressed
        procPressedInt(evt, EventsPeriod,"eventTestPeriod");
    }//GEN-LAST:event_EventsPeriodKeyPressed

    private void RegisterAgeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RegisterAgeKeyPressed
        procPressedInt(evt, RegisterAge,"maxRegisterAge");
    }//GEN-LAST:event_RegisterAgeKeyPressed

    private void setIPPortVisible(){
        }

    private void PLMEmulatorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_PLMEmulatorItemStateChanged
        procPressedBoolean(PLMEmulator,"emulated");
        setIPPortVisible();
    }//GEN-LAST:event_PLMEmulatorItemStateChanged

    private void PLMPortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PLMPortKeyPressed
        procPressedInt(evt, PLMPort,"plmPort");
    }//GEN-LAST:event_PLMPortKeyPressed

    private void PLMIPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PLMIPKeyPressed
        procPressedString(evt, PLMIP,"plmIP");
    }//GEN-LAST:event_PLMIPKeyPressed

    private void PLMTimeOutKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PLMTimeOutKeyPressed
        procPressedInt(evt, PLMTimeOut,"plmTimeOut");
    }//GEN-LAST:event_PLMTimeOutKeyPressed

    private void FileScanPeriodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FileScanPeriodKeyPressed
        procPressedInt(evt, FileScanPeriod,"fileScanPeriod");
    }//GEN-LAST:event_FileScanPeriodKeyPressed

    private void MainServerPeriodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MainServerPeriodKeyPressed
        procPressedInt(evt, MainServerPeriod,"mainServerPeriod");
    }//GEN-LAST:event_MainServerPeriodKeyPressed

    private void StreamDataLimitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StreamDataLimitKeyPressed
        procPressedInt(evt, StreamDataLimit,"streamDataPeriodLimit");
    }//GEN-LAST:event_StreamDataLimitKeyPressed


    private void StreamDataCompressModeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StreamDataCompressModeKeyPressed
        procPressedInt(evt, StreamDataCompressMode,"compressMode");
    }//GEN-LAST:event_StreamDataCompressModeKeyPressed

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        refresh();
    }//GEN-LAST:event_RefreshActionPerformed

    private void UserSilenceTimeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UserSilenceTimeKeyPressed
        procPressedInt(evt, UserSilenceTime,"userSilenceTime");
    }//GEN-LAST:event_UserSilenceTimeKeyPressed

    private void PLMGroupSizeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PLMGroupSizeKeyPressed
        procPressedInt(evt, PLMGroupSize,"plmRegGroupSize");
    }//GEN-LAST:event_PLMGroupSizeKeyPressed

    private void procPressedInt(KeyEvent evt, JTextField text, String name){
        if(evt.getKeyCode()!=10) return;
        int vv=0;
        try {
            vv = Integer.parseInt(text.getText());
        } catch (Exception ee){
            popup("Недопустимый формат целого");
            return;
        }
        updateSettings(evt,name,vv);
        refresh();
        }
    private void procPressedString(KeyEvent evt, JTextField text, String name){
        if(evt.getKeyCode()!=10) return;
        updateSettings(evt,name,text.getText());
        refresh();
        }
    private void procPressedBoolean(JCheckBox box, String name){
        updateSettings(null,name,box.isSelected());
        refresh();
        }

    @Override
    public void refresh() {
        try {
            if (!main.getWorkSettings())
                return;
            ws = (WorkSettings)main.workSettings;
        } catch (Exception e) { popup(e.toString()); }
    }

    @Override
    public void eventPanel(int code, int par1, long par2, String par3) {
        if (code==EventRefreshSettings){
            refresh();
            main.sendEventPanel(EventRefreshSettingsDone,0,0,"");
            }
        }

    @Override
    public void shutDown() {
    }

    private void updateSettings(){
        updateSettings(null);
        }
    private void updateSettings(KeyEvent evt){
        Response<JEmpty> wsr = null;
        try {
            wsr = main.service.updateWorkSettings(main.debugToken,new DBRequest(ws,main.gson)).execute();
            if (!wsr.isSuccessful()){
                popup("Ошибка обновления настроек  " + httpError(wsr));
                return;
                }
            popup("Настройки обновлены");
            if (evt!=null)
                main.viewUpdate(evt,true);
            main.sendEventPanel(EventRefreshSettings,0,0,"");
            } catch (IOException e) {
                main.viewUpdate(evt,false);
                popup(e.toString());
                }
        }
    private void updateSettings(KeyEvent evt, String name, int val){
        Response<JEmpty> wsr = null;
        try {
            wsr = main.service.updateWorkSettings(main.debugToken,name,val).execute();
            if (!wsr.isSuccessful()){
                popup("Ошибка обновления настроек  " + httpError(wsr));
                return;
                }
            popup("Настройки обновлены");
            if (evt!=null)
                main.viewUpdate(evt,true);
            main.sendEventPanel(EventRefreshSettings,0,0,"");
            } catch (IOException e) {
                main.viewUpdate(evt,false);
                popup(e.toString());
                }
        }
    private void updateSettings(KeyEvent evt, String name, boolean val){
        Response<JEmpty> wsr = null;
        try {
            wsr = main.service.updateWorkSettings(main.debugToken,name,val).execute();
            if (!wsr.isSuccessful()){
                popup("Ошибка обновления настроек  " + httpError(wsr));
                return;
            }
            popup("Настройки обновлены");
            if (evt!=null)
                main.viewUpdate(evt,true);
            main.sendEventPanel(EventRefreshSettings,0,0,"");
            } catch (IOException e) {
                main.viewUpdate(evt,false);
                popup(e.toString());
            }
        }
    private void updateSettings(KeyEvent evt, String name, String val){
        Response<JEmpty> wsr = null;
        try {
            wsr = main.service.updateWorkSettings(main.debugToken,name,val).execute();
            if (!wsr.isSuccessful()){
                popup("Ошибка обновления настроек  " + httpError(wsr));
                return;
                }
            popup("Настройки обновлены");
            if (evt!=null)
                main.viewUpdate(evt,true);
            main.sendEventPanel(EventRefreshSettings,0,0,"");
        } catch (IOException e) {
            main.viewUpdate(evt,false);
            popup(e.toString());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ArchiveDepthInDay;
    private javax.swing.JTextField EventsPeriod;
    private javax.swing.JTextField FailureTestPeriod;
    private javax.swing.JTextField FileScanPeriod;
    private javax.swing.JTextField GUIrefreshPeriod;
    private javax.swing.JCheckBox MainServer;
    private javax.swing.JTextField MainServerPeriod;
    private javax.swing.JCheckBox PLMEmulator;
    private javax.swing.JTextField PLMGroupSize;
    private javax.swing.JTextField PLMIP;
    private javax.swing.JTextField PLMPort;
    private javax.swing.JCheckBox PLMReady;
    private javax.swing.JTextField PLMTimeOut;
    private javax.swing.JButton Refresh;
    private javax.swing.JTextField RegisterAge;
    private javax.swing.JTextField StreamDataCompressMode;
    private javax.swing.JTextField StreamDataLimit;
    private javax.swing.JTextField StreamDataLongPeriod;
    private javax.swing.JTextField StreamDataPeriod;
    private javax.swing.JTextField UserSilenceTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
