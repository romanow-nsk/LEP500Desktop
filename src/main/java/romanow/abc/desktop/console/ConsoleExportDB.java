package romanow.abc.desktop.console;

import romanow.abc.core.UniException;
import romanow.abc.core.entity.artifacts.Artifact;
import retrofit2.Call;

import java.util.ArrayList;

public class ConsoleExportDB extends ConsoleCommand {
    private boolean xlsx=false;
    public ConsoleExportDB(boolean xlsx0) {
        super("expdb"+(xlsx0 ? "x":""));
        xlsx = xlsx0;
        }
    @Override
    public String exec(ConsoleClient client, ArrayList<String> command) throws UniException {
        if (command.size() != 2)
            return "Не хватает параметров\n";
        String fname = command.get(1);
        String fullName = fname + ".xls";
        Artifact art = new APICallC<Artifact>() {
            @Override
            public Call<Artifact> apiFun() {
                return client.service.exportDBxlsx(client.debugToken, xlsx, 1000);
                }
            }.call();
        client.loadFile(art,fullName);
        return "Экспорт БД в файл "+fullName+"\n";
        }
    @Override
    public String help() {
        return cmd+" file\t- экспорт БД (имя без .xls"+(xlsx ? "x":"")+")\n";
    }
}
