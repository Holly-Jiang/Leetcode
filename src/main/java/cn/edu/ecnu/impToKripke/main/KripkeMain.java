package cn.edu.ecnu.impToKripke.main;

import cn.edu.ecnu.impToKripke.util.FileUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class KripkeMain {

    public static void main(String[] args) {
        IMPToKripke impToKripke = new IMPToKripke();
        //输入文件路径
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入文件绝对路径：");
        String filePath = sc.nextLine();
        System.out.println("文件位于：" + filePath);

        System.out.println("请输入最大终止执行次数：");
        String count = sc.nextLine();

        List<String> codeList = FileUtil.toArrayByInputStreamReader1(filePath);
        //构造节点关系并画图
        impToKripke.structNodeRelation(impToKripke,count,codeList);

    }
}
