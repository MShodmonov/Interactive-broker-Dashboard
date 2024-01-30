package TestJavaClient.utils;


import com.ib.client.Bar;

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
}
