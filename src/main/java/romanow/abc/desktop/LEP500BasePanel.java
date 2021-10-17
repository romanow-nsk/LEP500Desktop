package romanow.abc.desktop;

public class LEP500BasePanel extends BasePanel{
    LEP500Client main2;
    public LEP500BasePanel() {}
    @Override
    public void refresh() {}
    @Override
    public void eventPanel(int code, int par1, long par2, String par3) { }
    @Override
    public void shutDown() {}
    public void initPanel(MainBaseFrame main0){
        super.initPanel(main0);
        main2 = (LEP500Client)main0;
        }
}
