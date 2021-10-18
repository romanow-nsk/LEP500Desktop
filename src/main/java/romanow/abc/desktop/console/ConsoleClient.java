package romanow.abc.desktop.console;

import com.google.gson.Gson;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.API.RestAPILEP500;
import romanow.abc.core.DBRequest;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.artifacts.Artifact;
import romanow.abc.core.entity.baseentityes.JEmpty;
import romanow.abc.core.entity.baseentityes.JString;
import romanow.abc.core.entity.subjectarea.WorkSettings;
import romanow.abc.core.entity.users.User;
import romanow.abc.core.utils.Pair;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class ConsoleClient {
    ConsoleFactory factory = new ConsoleFactory();
    //------------------------------------------------------------------------------------
    Gson gson = new Gson();
    BufferedReader reader;
    String clientIP="localhost";
    int clientPort=4567;
    String sysPassword="pi31415926";
    RestAPIBase service = null;
    RestAPILEP500 service2 = null;
    String debugToken=null;
    User user=null;
    boolean connected=false;
    WorkSettings workSettings=null;
    boolean localUser=false;
    //------------------------------------------------------------------------------------
    public RestAPIBase getService() {
        return service; }
    public void setClientIP(String clientIP) {
        this.clientIP = clientIP; }
    public void setClientPort(int clientPort) {
        this.clientPort = clientPort; }
    public boolean loggedOn(){
        return user!=null;
        }
    public ArrayList<String> getCommandString() throws IOException {
        ArrayList<String> out = new ArrayList<>();
        System.out.print("ess> ");
        String ss = reader.readLine();
        //System.out.println(ss);
        StringTokenizer st = new StringTokenizer(ss, " \t");
        while (st.hasMoreTokens()) {
            out.add(st.nextToken());
            }
        return out;
        }
    public ConsoleClient(){
        reader = new BufferedReader(new InputStreamReader(System.in));
        }
    public void exec(){
        Values.init();
            try {
                System.out.print(new ConsoleHelp().exec(this,null));
                } catch (Exception ee){ System.out.println(ee.toString());}
        while(true){
            try {
                ArrayList<String> ss = getCommandString();
                if (ss.size()==0)
                    continue;
                String out = factory.exec(this,ss);
                if (out!=null)
                    System.out.print(out);
                } catch (Exception ee){ System.out.println(ee.toString());}
            }
        }
    public void getWorkSettings() throws UniException {
        DBRequest request = new APICallC<DBRequest>(){
            @Override
            public Call<DBRequest> apiFun() {
                return service.workSettings(debugToken);
                }
            }.call();
        workSettings = (WorkSettings)request.get(new Gson());
        }
    //---------------------------------------------------------------------------------------------------
    public void loadFile(Artifact art, String fspec) throws UniException {
        ResponseBody body = new APICallC<ResponseBody>(){
            @Override
            public Call apiFun() {
                return service.downLoad(debugToken,art.getOid());
            }
        }.call();
        long fileSize = body.contentLength();
        InputStream in = body.byteStream();
        try {
            FileOutputStream out = new FileOutputStream(fspec);
            while (fileSize-- != 0)
                out.write(in.read());
                in.close();
                out.flush();
                out.close();
            } catch (IOException ee) {
                throw UniException.io(ee);
                }
        }
    public void loadFileAndDelete(Artifact art, String fullName) throws UniException {
        loadFile(art, fullName);
        new APICallC<JEmpty>(){
            @Override
            public Call<JEmpty> apiFun() {
                return service.removeArtifact(debugToken,art.getOid());
                }
            }.call();
        }
    private Pair<RestAPIBase,String> startOneClient() throws UniException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Values.HTTPTimeOut, TimeUnit.SECONDS)
                .connectTimeout(Values.HTTPTimeOut, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+clientIP+":"+clientPort)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        RestAPIBase service = (RestAPIBase)retrofit.create(RestAPIBase.class);
        localUser = clientIP.equals("localhost") || clientIP.equals("127.0.0.1");
        JString ss = new APICallC<JString>(){
            @Override
            public Call<JString> apiFun() {
                return service.debugToken(Values.DebugTokenPass);
            }
        }.call();
        return new Pair<RestAPIBase,String>(service,ss.getValue());
        }
    private RestAPILEP500 startSecondClient() throws UniException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(Values.HTTPTimeOut, TimeUnit.SECONDS)
                .connectTimeout(Values.HTTPTimeOut, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+clientIP+":"+clientPort)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        RestAPILEP500 service = (RestAPILEP500)retrofit.create(RestAPILEP500.class);
        localUser = clientIP.equals("localhost") || clientIP.equals("127.0.0.1");
        return service;
        }
    public void startClient() throws UniException{
        try {
            Pair<RestAPIBase,String> res = startOneClient();
            service = res.o1;
            debugToken = res.o2;
            service2 = startSecondClient();
            } catch (UniException e) {
                throw UniException.io("Ошибка ключа отладки "+e.toString()+"\n");
                }
        }
    public static void main(String aa[]){
        new ConsoleClient().exec();
        }
}
