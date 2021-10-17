package romanow.abc.desktop.console;

import romanow.abc.core.UniException;
import romanow.abc.core.entity.baseentityes.JEmpty;
import retrofit2.Call;

import java.util.ArrayList;

public class ConsoleExit extends ConsoleCommand {
    public ConsoleExit() {
        super("exit");
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
        System.exit(1);
        return null;
        }

    @Override
    public String help() {
        return cmd+"\t- выход из программы\n";
    }
}
