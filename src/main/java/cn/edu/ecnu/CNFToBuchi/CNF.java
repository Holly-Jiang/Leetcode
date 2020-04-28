package cn.edu.ecnu.CNFToBuchi;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.Map;

public class CNF {
    private Map<String,List<List<String>>> state;
    private Map<String,String> map;

    private Boolean flag;

    public  Map<String,List<List<String>>>  getState() {
        return state;
    }

    public void setState( Map<String,List<List<String>>> state) {
        this.state = state;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "CNF{" +
                "\nstate=" + state +
                ",\n flag=" + flag +
                '}';
    }
}
