package romanow.abc.desktop.console;

import romanow.abc.core.UniException;

import java.util.ArrayList;

public class ConsoleHelp extends ConsoleCommand {
    public ConsoleHelp() {
        super("help");
        }
    @Override
    public String exec(ConsoleClient client, ArrayList<String> command) throws UniException {
        return client.factory.help();
        }

    @Override
    public String help() {
        return cmd+"\t- эта команда\n";
    }
}
