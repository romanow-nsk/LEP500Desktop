package romanow.abc.desktop;


import com.google.gson.Gson;
import lombok.Getter;
import romanow.abc.core.ErrorList;
import romanow.abc.core.constants.Values;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.utils.Pair;
import romanow.lep500.FFTAudioTextFile;
import romanow.lep500.FileDescription;
import romanow.lep500.LEP500LocalData;
import romanow.lep500.LEP500Params;

import java.awt.*;
import java.io.*;

import static romanow.abc.core.constants.Values.*;
import static romanow.abc.core.constants.ValuesBase.UserSuperAdminType;


public class LEP500LocalClient extends LEP500Client{
    @Getter private LEP500LocalData localData = new LEP500LocalData();
    //------------------------------------------------------------------------------------------------------------------
    public LEP500LocalClient(){
        super(true,true,true);
        }
    @Override
    public void onLoginSuccess(){
        try {
            loadConstants(true);
            setTitle(ValuesBase.env().applicationName(AppNameTitle)+": "+loginUser().getHeader());
            debugToken = loginUser().getSessionToken();
            setBounds(X0, Y0, ShortView, ViewHight);
            getPanelList().setBounds(10,10,PanelW,PanelH);
            getShowLog().setSelected(false);
            getPanelList().removeAll();
            getPanels().clear();
            for(PanelDescriptor pp : panelDescList){
                    BasePanel panel = (BasePanel) pp.view.newInstance();
                    if (panel instanceof LogPanel){
                        setLogPanel((LogPanel)panel);
                        setMES(getLogPanel().mes(),getLogView(),getMESLOC());
                        }
                    panel.editMode = true;
                    try {
                        panel.initPanel(this);
                        getPanels().add(panel);
                        getPanelList().add(pp.name, panel);
                        } catch (Exception ee){
                            System.out.println("Ошибка открытия панели "+pp.name+"\n"+ee.toString());
                            }
                }
            setMES(getLogPanel().mes(),getLogView(),getMESLOC());
            loadLocalData();
            refresh();
            } catch(Exception ee){
                System.out.println(ee.toString());
                ee.printStackTrace();
                }
        setVisible(true);
        }
    public ErrorList createLocalDataDescription(){
        ErrorList out = new ErrorList();
        localData = new LEP500LocalData();
        LEP500Params params = new LEP500Params();
        params.setName("Стандартные");
        localData.getLep500ParamList().add(params);
        FileDialog dlg=new FileDialog(this,"Каталог файлов измерений",FileDialog.SAVE);
        dlg.setFile("a.dat");
        dlg.show();
        if (dlg.getDirectory()==null){
            out.addError("Каталог файлов измерений не выбран");
            return out;
            }
        localData.setMeasureFilesDir(dlg.getDirectory());
        File ff = new File(dlg.getDirectory());
        if (!ff.isDirectory()){
            out.addError("Ошибка выбора каталога файлов измерений");
            return out;
            }
        String files[] = ff.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
                }
            });
        int fcount=0;
        for(String fname :  files){
            FileDescription fd = new FileDescription(fname);
            String error = fd.getFormatError();
            if (error.length()!=0){
                out.addError("Ошибка "+fname+": "+error);
                continue;
                }
            String ss = dlg.getDirectory()+"/"+fname;
            BufferedReader reader = null;
            try{
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(ss),"Windows-1251"));
                FFTAudioTextFile file = new FFTAudioTextFile();
                file.readData(fd,reader);
                error = fd.getFormatError();
                if (error.length()!=0){
                    out.addError("Ошибка "+fname+": "+error);
                    continue;
                    }
                reader.close();
                localData.getFiles().add(fd);
                out.addInfo("Импортирован: "+fd.toOneString());
                fcount++;
                } catch (IOException ex){
                    if (reader!=null){
                        try {
                            reader.close();
                            } catch (Exception ee){}
                        }
                }
            }
        out.addInfo("Импортировано "+fcount+", c ошибками "+out.getErrCount());
        localData.createPowerLines();
        return  out;
        }
    public void loadLocalData(){
        String fname = LEP500LocalData.class.getSimpleName()+".json";
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(fname), "Windows-1251");
            localData = (LEP500LocalData) gson.fromJson(reader,LEP500LocalData.class);
            reader.close();
            localData.createPowerLines();
            } catch (Exception ee){
                System.out.println("Файл данных "+fname+" не прочитан, создан заново");
                ErrorList res = createLocalDataDescription();
                System.out.println(res);
                saveLocalData();
                }
        }
    public void saveLocalData(){
        String fname = LEP500LocalData.class.getSimpleName()+".json";
        try {
            String ss = gson.toJson(localData);
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fname), "Windows-1251");
            out.write(ss);
            out.flush();
            out.close();
            } catch (Exception e2){
                System.out.println("Файл данных "+fname+" не записан, фатальная ошибка");
                }
        }
    public void initPanels() {
        panelDescList.clear();
        panelDescList.add(new PanelDescriptor("Трассировка", LogPanel.class,new int[]
                {UserSuperAdminType, UserAdminType}));
        panelDescList.add(new PanelDescriptor("Параметры", LEP500ParamsPanel.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        panelDescList.add(new PanelDescriptor("Измерения", LEP500LocalExperience.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        panelDescList.add(new PanelDescriptor("Графики", LEP500TrendPanel.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        panelDescList.add(new PanelDescriptor("Анализ", LEP500LocalNNPanel.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        }
    //-------------------------------------------------------------------------------------------------------
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Values.init();
                new LEP500LocalClient();
        }   });
    }
}
