package romanow.abc.desktop.console;

import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.EntityList;
import romanow.abc.core.entity.baseentityes.JBoolean;
import romanow.abc.core.entity.baseentityes.JLong;
import romanow.abc.core.entity.users.User;
import retrofit2.Call;

import java.util.ArrayList;

public class ConsoleUser extends ConsoleCommand {
    public ConsoleUser() {
        super("user");
        }
    @Override
    public String exec(ConsoleClient client, ArrayList<String> command) throws UniException {
        try {
            //   1    2     3    4  5  6  7
            // type login pass phone F I O
            int sz = command.size();
            if (sz==1){
                EntityList<User> ss = new APICallC<EntityList<User>>(){
                    @Override
                    public Call<EntityList<User>> apiFun() {
                        return client.service.getUserList(client.debugToken,Values.GetAllModeActual,0);
                    }}.call();
                String out="";
                for(User uu : ss)
                    out+=uu.toFullString()+"\n";
                return out;
                }
            if (sz==2){
                long id = Long.parseLong(command.get(1));
                JBoolean ss = new APICallC<JBoolean>(){
                    @Override
                    public Call<JBoolean> apiFun() {
                        return client.service.removeEntity(client.debugToken,"User",id);
                    }}.call();
                return "Удален пользователь, oid="+id+"\n";
                }
            if (sz<5)
                return "Не хватает параметров\n";
            if (sz>8)
                return "Много параметров\n";
            int type = Integer.parseInt(command.get(1));
            if (type<0 || type>5)
                return "Недопустимый тип пользователя\n";
            String nm3 = (sz > 7 ? command.get(7) : "");
            String nm2 = (sz > 6 ? command.get(6) : "");
            String nm1 = (sz > 5 ? command.get(5) : "");
            User user = new User(type,nm1,nm2,nm3,command.get(2),command.get(3),command.get(4));
            JLong ss = new APICallC<JLong>(){
                @Override
                public Call<JLong> apiFun() {
                    return client.service.addUser(client.debugToken,user);
                }}.call();
            return  "Тип пользователя: "+ Values.title("UserType",type)+" oid="+ss.getValue()+"\n"+user.toString()+"\n";
            }
        catch(UniException e1){
            return "Ошибка ПЛК: "+e1.toString()+"\n";
            }
        catch(Exception ee){
            return "Недопустимое значение параметра\n";
            }
        }
    @Override
    public String help() {
        return cmd+" typeId login pass phone <фамилия> <имя> <отчество> - добавить пользователя\n"+
                cmd+" - список пользователей\n"+cmd+" <oid> - удалить пользователя по oid\n";
        }
}
