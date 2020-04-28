package cn.edu.ecnu.impToKripke.main;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class GraphViz {
    private String runPath = "";
    private String dotPath = "";
    private String runOrder = "";
    private String dotCodeFile = "code" + System.currentTimeMillis() + ".txt";
    private String resultGif = "gif" + System.currentTimeMillis();
    private StringBuilder graph = new StringBuilder();

    Runtime runtime = Runtime.getRuntime();

    public void run() {
        File file = new File(runPath);
        file.mkdirs();
        writeGraphToFile(graph.toString(), runPath);
        creatOrder();
        try {
            runtime.exec(runOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getGraph() {
        return this.graph.toString();
    }

    public void creatOrder() {
        runOrder += dotPath + " ";
        runOrder += runPath;
        runOrder += "\\" + dotCodeFile + " ";
        runOrder += "-T gif ";
        runOrder += "-o ";
        runOrder += runPath;
        runOrder += "\\" + resultGif + ".gif";
        System.out.println(runOrder);
    }

    public void writeGraphToFile(String dotcode, String filename) {
        try {
            File file = new File(filename + "\\" + dotCodeFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(dotcode.getBytes());
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public GraphViz(String runPath, String dotPath) {
        this.runPath = runPath;
        this.dotPath = dotPath;
    }

    public void add(String line) {
        graph.append("\t" + line);
    }

    public void node(String line) {
        graph.append("\t" + line + ";\n");
    }

    public void addln(String line) {
        graph.append("\t" + line + ";\n");
    }

    public void addln() {
        graph.append('\n');
    }

    public void start_graph() {
        graph.append("digraph G {\n");
    }

    public void label(String name, String label) {
        graph.append("\t" + name + "[label=\"" + label + "\"];\n");
    }

    public void addNode(String name) {
        graph.append("\t" + name + ";\n");
    }
    public void endNode(String name) {
        graph.append("\t" + name + "[ shape = doublecircle ];\n");
    }
    public void end_graph() {
        graph.append("}");
        System.out.println(graph);
    }

    public void graph() {
        graph.append("digraph G {\n" +
                "\t1[label=\"0,L1,L4\"];\n" +
                "\t2[label=\"0,L2,L4\"];\n" +
                "\t3[label=\"0,L1,L5\"];\n" +
                "\t4[label=\"0,L3,L4\"];\n" +
                "\t5[label=\"0,L2,L5\"];\n" +
                "\t6[label=\"1,L1,L4\"];\n" +
                "\t7[label=\"0,L3,L5\"];\n" +
                "\t8[label=\"1,L2,L4\"];\n" +
                "\t9[label=\"1,L1,L5\"];\n" +
                "\t10[label=\"1,L2,L5\"];\n" +
                "\t11[label=\"1,L1,L6\"];\n" +
                "\t12[label=\"1,L2,L6\"];\n" +
                "\t1->2;\n" +
                "\t1->3;\n" +
                "\t2->4;\n" +
                "\t2->5;\n" +
                "\t3->5;\n" +
                "\t3->3;\n" +
                "\t4->6;\n" +
                "\t4->7;\n" +
                "\t5->7;\n" +
                "\t5->5;\n" +
                "\t6->8;\n" +
                "\t6->9;\n" +
                "\t7->9;\n" +
                "\t7->7;\n" +
                "\t8->8;\n" +
                "\t8->10;\n" +
                "\t9->10;\n" +
                "\t9->11;\n" +
                "\t10->10;\n" +
                "\t10->12;\n" +
                "\t11->12;\n" +
                "\t11->1;\n" +
                "\t12->12;\n" +
                "\t12->2;\n" +
                "}");
    }
}
