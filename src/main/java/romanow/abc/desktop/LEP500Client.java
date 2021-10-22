package romanow.abc.desktop;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPILEP500;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.subjectarea.*;
import romanow.abc.core.entity.users.User;

import java.util.concurrent.TimeUnit;

import static romanow.abc.core.constants.Values.*;
import static romanow.abc.core.constants.ValuesBase.UserSuperAdminType;


public class LEP500Client extends Client{
    public LEP500Client(){
        this(true);
        }
    public LEP500Client(boolean setLog){
        super(setLog);
        Values.init();
        }
    public void initPanels() {
        super.initPanels();
        panelDescList.add(new PanelDescriptor("Настройки", LEP500WorkSettingsPanel.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        panelDescList.add(new PanelDescriptor("Параметры", LEP500ParamsPanel.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        panelDescList.add(new PanelDescriptor("Измерения", LEP500Example.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        panelDescList.add(new PanelDescriptor("Графики", LEP500TrendPanel.class,new int[]
                {UserSuperAdminType,UserLEP500Analytic}));
        }
    //-------------------------------------------------------------------------------------------------------
    RestAPILEP500 service2;
    @Override
    public void onLoginSuccess(){
        try {
            service2 = startSecondClient(getServerIP(),""+getServerPort());
            getWorkSettings();
            } catch (UniException e) {
                popup("Ошибка соединения: " +e.toString());
                }
    }

    public void setExternalData(String token, User uu, WorkSettings ws0, RestAPIBase service0, RestAPILEP500 service20, boolean localUser0){
        debugToken = token;
        loginUser = uu;
        workSettings = ws0;
        service = service0;
        service2 = service20;
        localUser = localUser0;
        }
    public RestAPILEP500 startSecondClient(String ip, String port) throws UniException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Values.HTTPTimeOut, TimeUnit.SECONDS)
                .connectTimeout(Values.HTTPTimeOut, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+ip+":"+port)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        RestAPILEP500 service = (RestAPILEP500)retrofit.create(RestAPILEP500.class);
        localUser = ip.equals("localhost") || port.equals("127.0.0.1");
        return service;
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
                new LEP500Client();
            }
        });
    }
}
