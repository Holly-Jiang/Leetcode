package cn.edu.ecnu.CNFToBuchi;

import java.util.List;
import java.util.Map;

public class Buchi {

    private List<String> alphabet;

    private List<String> state;

    private List<List<String>> ts ;

    private String finalState ;

    private String iniState;

    public List<String> getAlphabet() {
        return alphabet;
    }

    public List<String> getState() {
        return state;
    }

    public List<List<String>> getTs() {
        return ts;
    }

    public String getFinalState() {
        return finalState;
    }

    public void setAlphabet(List<String> alphabet) {
        this.alphabet = alphabet;
    }

    public void setState(List<String> state) {
        this.state = state;
    }

    public void setTs(List<List<String>> ts) {
        this.ts = ts;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public String getIniState() {
        return iniState;
    }

    public void setIniState(String iniState) {
        this.iniState = iniState;
    }

    @Override
    public String toString() {
        return "Buchi{" +
                "alphabet=" + alphabet +
                ", state=" + state +
                ", ts=" + ts +
                ", finalState='" + finalState + '\'' +
                ", iniState='" + iniState + '\'' +
                '}';
    }
}
