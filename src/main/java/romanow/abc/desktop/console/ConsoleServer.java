package romanow.abc.desktop.console;

import romanow.abc.core.UniException;

import java.util.ArrayList;

public class ConsoleServer extends ConsoleCommand {
    public ConsoleServer() {
        super("server");
        }
    @Override
    public String exec(ConsoleClient client, ArrayList<String> command) throws UniException {
        int sz = command.size();
        if (sz==1)
            return "Не хватает параметров\n";
        if (sz==3)
            client.clientIP = command.get(2);
        if (sz>2) {
            int port = 0;
            try {
                port = Integer.parseInt(command.get(1));
                client.clientPort = port;
                } catch (Exception ee) {
                    return "Формат: номер порта - " + command.get(1) + "\n";
                }
            }
            return null;
        }
    @Override
    public String help() {
        return cmd+" port <ip> - ip-порт сервера\n";
    }
}
