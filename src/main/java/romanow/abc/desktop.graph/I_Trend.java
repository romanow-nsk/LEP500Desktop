package romanow.abc.desktop.graph;

import romanow.lep500.I_TrendData;

/**
 * Created by romanow on 09.04.2018.
 */
public interface I_Trend {
    public void toFront();
    public void addTrend(I_TrendData hh);
    public void addTrendView(I_TrendData hh);
    public void close();
    }
