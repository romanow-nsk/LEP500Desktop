package romanow.abc.desktop.console;

import romanow.abc.core.UniException;

import java.util.ArrayList;

public abstract class ConsoleCommand {
    public final String cmd;
    public ConsoleCommand(String cmd) {
        this.cmd = cmd;
        }
    public abstract String exec(ConsoleClient client, ArrayList<String> command) throws UniException;
    public abstract String help();
}
