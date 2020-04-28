package cn.edu.ecnu.CNFToBuchi;

import cn.edu.ecnu.impToKripke.main.GraphViz;

import java.util.*;

public class CnfToBuchi {
    public static void main(String[] args) {
        CnfToBuchi buchi = new CnfToBuchi();
//        //输入文件路径
//        Scanner sc = new Scanner(System.in);
//        System.out.println("请输入文件绝对路径：");
//        String filePath = sc.nextLine();
//        System.out.println("文件位于：" + filePath);
        String relationPath = "/Users/jiangqianxi/Desktop/bb.txt";
        CNF cnf1 = FileUtil1.readNodeRelation(relationPath);

        String filePath = "/Users/jiangqianxi/Desktop/aa.txt";
        CNF cnf2 = FileUtil1.toArrayByInputStreamReader1(filePath);
        cnf2.setMap(cnf1.getMap());
        Node node = buchi.translateCNFToBuchi(cnf2.getMap(), cnf2.getState());
        node.setNode(cnf1.getMap());
        node.setFlag(cnf2.getFlag());
        buchi.outputGraphViz(node);
        buchi.outputBuchi(node);

    }

    public void outputGraphViz(Node entity) {
        GraphViz dot = new GraphViz("/Users/jiangqianxi/Desktop/test", "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe");
        dot.start_graph();
        Map<String, String> node = entity.getNode();
        List<List<String>> edge = entity.getEdge();
        if (entity.getFlag()) {
            for (Map.Entry<String, String> entry : node.entrySet()) {
                if (entry.getValue().equals("True")) {
                    dot.endNode(entry.getValue());
                } else {
                    dot.addNode(entry.getValue());
                }
            }
        } else {
            for (Map.Entry<String, String> entry : node.entrySet()) {
                dot.endNode(entry.getValue());
            }
        }
        for (int i = 0; i < edge.size(); i++) {
            StringBuffer sb = new StringBuffer();
            sb.append(edge.get(i).get(0)).append("->");
            sb.append(edge.get(i).get(1)).append("[label=\"");
            sb.append(edge.get(i).get(2)).append("\"]");
            dot.addln(sb.toString());

        }
        dot.end_graph();
//        try {
//            dot.run();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


    public void outputBuchi(Node entity) {
        //构造buchi五元组
        Buchi buchi = new Buchi();
        List<String> alphabet = new ArrayList<>();

        List<String> state = new ArrayList<>();

        List<List<String>> ts = new ArrayList<>();

        String finalState ;

        String iniState;

        Map<String, String> node = entity.getNode();
        List<List<String>> edge = entity.getEdge();
        for (Map.Entry<String, String> entry : node.entrySet()) {
            String value = entry.getValue();
            state.add(value);
        }

        for (List<String> list : edge) {
            List<String> list1 = new ArrayList<>();
            list1.add(list.get(0));
            list1.add(list.get(1));
            list1.add(list.get(2));
            ts.add(list1);

        }

        if (entity.getFlag()){
            finalState="{True}";
        }else{
            finalState="S";
        }
        iniState = edge.get(0).get(0);
        buchi.setAlphabet(alphabet);
        buchi.setState(state);
        buchi.setTs(ts);
        buchi.setFinalState(finalState);
        buchi.setIniState(iniState);
        System.out.println("A=(S,Alphabet,TS,S0,F)");
        StringBuffer sb =new StringBuffer();

        sb.append("Alphabet={").append(outputBuchi(alphabet,new StringBuffer())).append("},\n S={" )
                .append(outputBuchi(state,new StringBuffer())).append("},\n TS:" ).append(outputTS(ts,new StringBuffer()) )
                .append(",\n F=").append(finalState).append(",\n S0={")
                .append(iniState).append("}");

        System.out.println(sb.toString());
        }

        public String outputTS(List<List<String> >list,StringBuffer sb){
            if (list.size()<=0){
                return sb.toString();
            }
            for (int i=0;i<list.size();i++){
                if (list.get(i)!=null){
                   sb.append(list.get(i).get(0)).append("(").append(list.get(i).get(2))
                           .append(")->").append(list.get(i).get(1)).append(",");
                }
            }
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();

        }

    public String outputBuchi(List<String> list,StringBuffer sb){
        if (list.size()<=0){
            return sb.toString();
        }
        for (int i=0;i<list.size();i++){
            if (list.get(i)!=""){
                sb.append(list.get(i)).append(",");
            }
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();

    }
    public Node translateCNFToBuchi(Map<String, String> node1, Map<String, List<List<String>>> codeList) {

        Node result = new Node();
        // Map<String, String> node = new HashMap<>();
        List<List<String>> edge = new ArrayList<>();
        result.setEdge(edge);
        // result.setNode(node);
        List<String> sub = new ArrayList<>();
        for (Map.Entry<String, List<List<String>>> entry : codeList.entrySet()) {
            String key = entry.getKey();
            List<List<String>> value = entry.getValue();
            //从哪个节点出去
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < value.size(); i++) {
                sub.add(node1.get(key));
                for (int j = 0; j < value.get(i).size(); j++) {
                    if (value.get(i).get(j).contains("X")) {
                        //node.put(value.get(i).get(j), key);
                        //到某个节点
                        sub.add(value.get(i).get(j).substring(1));
                    } else {
                        //转移动作
                        sb.append(value.get(i).get(j) + "&");
                    }
                }
                sb.deleteCharAt(sb.length() - 1);
                sub.add(sb.toString());
                edge.add(sub);
                sb = new StringBuffer();
                sub = new ArrayList<>();
            }

        }
        System.out.println("边的关系:" + result);
        return result;
    }

}




