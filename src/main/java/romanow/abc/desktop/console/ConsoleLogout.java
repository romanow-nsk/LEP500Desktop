package romanow.abc.desktop.console;

import romanow.abc.core.UniException;
import romanow.abc.core.entity.baseentityes.JEmpty;
import retrofit2.Call;

import java.util.ArrayList;

public class ConsoleLogout extends ConsoleCommand {
    public ConsoleLogout() {
        super("logout");
        }
    @Override
    public String exec(ConsoleClient client, ArrayList<String> command) throws UniException {
        if (client.loggedOn()){
            new APICallC<JEmpty>() {
                @Override
                public Call<JEmpty> apiFun() {
                    return client.service.logoff(client.debugToken);
                    }
                }.call();
            client.user=null;
            }
        return null;
        }

    @Override
    public String help() {
        return cmd+"\t- выход из сеанса\n";
    }
}
