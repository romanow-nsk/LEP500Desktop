package romanow.abc.desktop;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPILEP500;
import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.constants.ValuesBase;
import romanow.abc.core.entity.base.WorkSettingsBase;
import romanow.abc.core.entity.subjectarea.*;
import romanow.abc.core.entity.users.User;

import java.util.concurrent.TimeUnit;

import static romanow.abc.core.constants.Values.*;
import static romanow.abc.core.constants.ValuesBase.UserSuperAdminType;


public class LEP500LocalClient extends LEP500Client{
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
            BasePanel pn;
            refresh();
            } catch(Exception ee){
                System.out.println(ee.toString());
                ee.printStackTrace();
                }
        setVisible(true);
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
