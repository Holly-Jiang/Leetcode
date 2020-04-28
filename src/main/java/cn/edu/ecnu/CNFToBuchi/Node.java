package cn.edu.ecnu.CNFToBuchi;

import java.util.List;
import java.util.Map;

public class Node {
    private  Boolean flag ;

    private Map<String,String> node;

    private List<List<String>> edge;

    public Map<String, String> getNode() {
        return node;
    }

    public List<List<String>> getEdge() {
        return edge;
    }

    public void setNode(Map<String, String> node) {
        this.node = node;
    }

    public void setEdge(List<List<String>> edge) {
        this.edge = edge;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Node{" +
                "\nflag=" + flag +
                ",\n node=" + node +
                ",\n edge=" + edge +
                '}';
    }
}
