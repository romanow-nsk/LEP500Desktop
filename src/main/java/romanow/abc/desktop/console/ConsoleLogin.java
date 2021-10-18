package romanow.abc.desktop.console;

import romanow.abc.core.UniException;
import romanow.abc.core.entity.baseentityes.JBoolean;
import romanow.abc.core.entity.users.User;

import retrofit2.Call;

import java.util.ArrayList;

public class ConsoleLogin extends ConsoleCommand {
    public ConsoleLogin() {
        super("login");
        }
    @Override
    public String exec(ConsoleClient client, ArrayList<String> command) throws UniException {
        int portIdx=2;
        int ipIdx=2;
        int sz=command.size();
        if (sz==1)
            return "Не хватает параметров\n";
        if (sz>3)
            return "Лишние параметры\n";
        String login = command.get(1);
        String pass="";
        if (sz==3)
            pass = command.get(2);
        else{
            System.out.print("password:");
            try {
                pass = client.reader.readLine();
                } catch (Exception ee){
                    throw UniException.io(ee.toString()+"\n");
                    }
            }
        client.startClient();
        String finalPass = pass;
        client.user = new APICallC<User>(){
            @Override
            public Call<User> apiFun() {
                return client.service.login(login, finalPass);
            }
        }.call();
        client.debugToken = client.user.getSessionToken();
        client.getWorkSettings();
        client.connected = true;
        String out = "";
        return out;
        }
    @Override
    public String help() {
        return "login phone <password>\t- авторизация\n";
        }
}
