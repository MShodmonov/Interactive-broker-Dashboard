package TestJavaClient.utils;


import com.ib.client.Bar;
import com.ib.client.Decimal;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class BarList {

    private Integer reqId;

    private List<Bar> barList;

    public BarList() {
        reqId = 0;
        barList = new LinkedList<>();
    }

    public Integer getReqId() {
        return reqId;
    }

    public void setReqId(Integer reqId) {
        this.reqId = reqId;
    }

    public List<Bar> getBarList() {
        return barList;
    }

    public void setBarList(List<Bar> barList) {
        this.barList = barList;
    }

    public void getAverageMoving() {
        double m_open = 0;
        double m_high = 0;
        double m_low = 0;
        double m_close = 0;
        Decimal m_volume = Decimal.get(0);
        int m_count = 0;

        LinkedList<Bar> list = new LinkedList<>();
        for (int i = 0; i < barList.size(); i++) {
            m_open = m_open + barList.get(i).open();
            m_high = m_high + barList.get(i).high();
            m_low = m_low + barList.get(i).low();
            m_close = m_close + barList.get(i).close();
            m_volume = m_volume.add(barList.get(i).volume());
            m_count = m_count + barList.get(i).count();
            if (i % 5 == 0 && (i != 0)) {
                list.add(new Bar(barList.get(i).time(), m_open / 5, m_high / 5, m_low / 5, m_close / 5, m_volume.divide(Decimal.get(5)), m_count / 5, Decimal.get(0)));
                m_open = 0;
                m_high = 0;
                m_low = 0;
                m_close = 0;
                m_volume = Decimal.get(0);
                m_count = 0;
            }
        }
        barList = list;
    }
}
