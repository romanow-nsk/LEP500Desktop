package romanow.abc.desktop.console;

import romanow.abc.core.UniException;

import java.util.ArrayList;

public class ConsoleFactory {
    private ArrayList<ConsoleCommand> list = new ArrayList<>();
    public ConsoleFactory(){
        list.add(new ConsoleExit());
        list.add(new ConsoleLogin());
        list.add(new ConsoleLogout());
        list.add(new ConsoleHelp());
        list.add(new ConsoleWS());
        list.add(new ConsoleServer());
        list.add(new ConsoleSU());
        list.add(new ConsoleExportDB(false));
        list.add(new ConsoleExportDB(true));
        list.add(new ConsoleUser());
        }
    public String exec(ConsoleClient client, ArrayList<String> tokens) {
        try {
            for(ConsoleCommand command : list)
                if (command.cmd.equals(tokens.get(0)))
                    return command.exec(client,tokens);
            } catch (UniException ee){
                return ee.toString()+"\n";
                }
        return "Команда не найдена\n";
        }
    public String help(){
        String out="";
        for(ConsoleCommand command : list)
            out+=command.help();
        return out;
        }
}
