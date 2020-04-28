package cn.edu.ecnu.impToKripke.main;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class LabelFunctionModel {
    private List<Map<String, String>> labelProgram;
    private Integer labelCount;
    private ArrayList<String> contentList;
    private ArrayList<Integer> wordList;


    public ArrayList<Integer> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<Integer> wordList) {
        this.wordList = wordList;
    }

    public ArrayList<String> getContentList() {
        return contentList;
    }

    public void setContentList(ArrayList<String> contentList) {
        this.contentList = contentList;
    }

    public List<Map<String, String>> getLabelProgram() {
        return labelProgram;
    }

    public void setLabelProgram(List<Map<String, String>> labelProgram) {
        this.labelProgram = labelProgram;
    }

    public Integer getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(Integer labelCount) {
        this.labelCount = labelCount;
    }
}
