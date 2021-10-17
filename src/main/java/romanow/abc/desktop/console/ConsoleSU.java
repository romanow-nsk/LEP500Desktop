package romanow.abc.desktop.console;

import romanow.abc.core.UniException;

import java.util.ArrayList;

public class ConsoleSU extends ConsoleCommand {
    public ConsoleSU() {
        super("su");
        }
    @Override
    public String exec(ConsoleClient client, ArrayList<String> command) throws UniException {
        ArrayList<String> xx = new ArrayList<>();
        xx.add("login");
        xx.add("9130000000");
        xx.add("pi31415926");
        return new ConsoleLogin().exec(client,xx);
        }

    @Override
    public String help() {
        return cmd+"\t- вход под SU\n";
    }
}
