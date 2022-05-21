package romanow.abc.desktop;

import romanow.abc.core.constants.Values;

public class LEP500BasePanel extends BasePanel{
    public int EventGraph=6;
    LEP500Client main2;
    public LEP500BasePanel() {}
    @Override
    public void refresh() {}
    @Override
    public void eventPanel(int code, int par1, long par2, String par3, Object oo) { }
    @Override
    public void shutDown() {}
    public void initPanel(MainBaseFrame main0){
        super.initPanel(main0);
        main2 = (LEP500Client)main0;
        }
    public void runInGUI(Runnable code){
        java.awt.EventQueue.invokeLater(code);
        }
}
