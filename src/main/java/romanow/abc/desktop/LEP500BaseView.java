package romanow.abc.desktop;

import romanow.abc.core.constants.ValuesBase;

import javax.swing.*;
import java.awt.*;

public class LEP500BaseView extends javax.swing.JFrame {
    protected int winHigh=250;
    protected int winWidth=280;
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.drawRect(2,2,winWidth-5,winHigh-5);
        }
    public LEP500BaseView(int ww, int hh){
        winWidth=ww;
        winHigh=hh;
        setUndecorated(true);
        }
    public void setWH(int ww, int hh){
        winWidth=ww;
        winHigh=hh;
        }
    public void positionOn(int x0, int y0){
        setBounds(x0,y0,winWidth,winHigh);
        setVisible(true);
        }
    private Thread thread=null;
    public void delayIt(){
        delayIt(ValuesBase.PopupMessageDelay);
        }
    public void delayIt(final  int delay){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay*1000);
                } catch (InterruptedException e) {
                    return;
                }
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        dispose();
                        }
                    });
                }
            });
            thread.start();
        }
    public void closeView(){
        if(thread!=null){
            thread.interrupt();
            thread=null;
            }
        dispose();
        }
    public void retryLongDelay(){
        if(thread!=null){
            thread.interrupt();
            thread=null;
            }
        delayIt(ValuesBase.PopupLongDelay);
        }
}
