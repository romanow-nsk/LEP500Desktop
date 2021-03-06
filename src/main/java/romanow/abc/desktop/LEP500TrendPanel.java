/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.abc.desktop;

import romanow.abc.desktop.graph.TrendPanel;
import romanow.lep500.AnalyseResult;

import java.util.ArrayList;

/**
 *
 * @author romanow
 */
public class LEP500TrendPanel extends LEP500BasePanel{
    private final static String buttonAdd = "/drawable/add.png";
    private TrendPanel trend;
    public LEP500TrendPanel() {
        initComponents();
        }
    private Runnable after = new Runnable() {
        @Override
        public void run() {
            trend.setBounds(0,10,Client.PanelW-10,Client.PanelH);
            add(trend);
            trend.setBack(new I_Success() {
                @Override
                public void onSuccess() {
                    //new ESSStreamDataView(main2, false,new I_Value<DataSet>() {
                    //    @Override
                    //    public void onEnter(DataSet value) {
                    //        trend.addTrendView(value);
                    //        }
                    //});
                }
            });
            /*
            JButton toMain = trend.getRefreshButton();
            toMain.setIcon(new javax.swing.ImageIcon(getClass().getResource(buttonAdd))); // NOI18N
            toMain.setBorderPainted(false);
            toMain.setContentAreaFilled(false);
            toMain.removeActionListener(toMain.getActionListeners()[0]);
            toMain.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ESSStreamDataView(main, false,new I_Value<DataSet>() {
                        @Override
                        public void onEnter(DataSet value) {
                            trend.addTrendView(value);
                            }
                        });
                    }
                });
            */
            }
        };
    public void initPanel(MainBaseFrame main0){
        super.initPanel(main0);
        trend = new TrendPanel();
        trend.init(after);
    }

    public boolean isMainMode(){ return true; }
    public boolean isESSMode(){ return true; }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void refresh() {}

    @Override
    public void eventPanel(int code, int par1, long par2, String par3, Object oo) {
        if (code==EventRefreshSettings){
            refresh();
            main.sendEventPanel(EventRefreshSettingsDone,0,0,"");
            }
        if (code==EventGraph){
            ArrayList<AnalyseResult> results = (ArrayList<AnalyseResult>)oo;
            trend.clearFull();
            for(AnalyseResult result  : results)
                trend.addTrendView(result);
            main.panelToFront(this);
            }
    }

    @Override
    public void shutDown() {

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
