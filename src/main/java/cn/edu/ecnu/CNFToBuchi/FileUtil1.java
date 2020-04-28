package cn.edu.ecnu.CNFToBuchi;

import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil1 {

    public static CNF toArrayByInputStreamReader1(String name) {

        CNF cnf=new CNF();
        // 使用ArrayList来存储每行读取到的字符串
        List<List<String>> arrayList = new ArrayList<>();

        Map<String, List<List<String>>> stateList=new HashMap<>();
        cnf.setState(stateList);
        try {
            File file = new File(name);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            //如果是R就U-free 终止状态是S, 定义标志flag=false，U就是R-free，终止状态是True 定义标志flag=true
            Boolean flag = false;
            cnf.setFlag(flag);
            String str;
            while ((str = bf.readLine()) != null
            &&str.length()>2) {
                if (str.contains("U")) {
                    flag = true;
                    cnf.setFlag(flag);
                }
                List<String> clause= new ArrayList<>(); ;

                String state=str.substring(0,2);
                if (!Strings.isEmpty(state)){
                    stateList.put(state,arrayList);
                }
                str=str.substring(4);
                //String sub[] = str.split("#");
                StringBuffer sb=new StringBuffer();
                for (int i = 0; i < str.length(); i++) {
                    Character c = str.charAt(i);
                    if (c=='('||c==')'||c==' '){
                        continue;
                    }
                    //如果遇到或就把一个括号的加进去
                    if (c=='#'){
                        clause.add(sb.toString());
                        sb=new StringBuffer();
                        arrayList.add(clause);
                        clause=new ArrayList<>();
                        continue;
                    }
                    //如果遇到且 就加一个文字
                    if (c=='&'){
                        clause.add(sb.toString());
                        sb=new StringBuffer();
                        continue;
                    }
                    //其他情况就拼凑文字
                    sb.append(c);


                }
                clause.add(sb.toString());
                arrayList.add(clause);
                System.out.println(state+"toArrayByInputStreamReader1=======arrayList:"+arrayList);
                sb=new StringBuffer();
                clause=new ArrayList<>();
                arrayList = new ArrayList<>();
            }
            bf.close();
            inputReader.close();
            return cnf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cnf;
    }

    public static CNF readNodeRelation(String name) {

        CNF cnf=new CNF();

        Map<String, String> stateList=new HashMap<>();
        cnf.setMap(stateList);
        try {
            File file = new File(name);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);

            String str;
            while ((str = bf.readLine()) != null&& !str.equals("")) {
                String [] relation =str.split("=");
                stateList.put(relation[0],relation[1]);
            }
            System.out.println("节点对应关系："+stateList);
            bf.close();
            inputReader.close();
            return cnf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cnf;
    }
//
//    public static CNF readNodeRelation(String name) {
//
//        CNF cnf=new CNF();
//
//        Map<String, String> stateList=new HashMap<>();
//        cnf.setMap(stateList);
//        try {
//            File file = new File(name);
//            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
//            BufferedReader bf = new BufferedReader(inputReader);
//
//            String str;
//            while ((str = bf.readLine()) != null&& !str.equals("")) {
//                String [] relation =str.split("=");
//                stateList.put(relation[0],relation[1]);
//            }
//            System.out.println("节点对应关系："+stateList);
//            bf.close();
//            inputReader.close();
//            return cnf;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return cnf;
//    }

    //test
    public static void main(String[] args) {
        System.out.println(toArrayByInputStreamReader1("/Users/jiangqianxi/Desktop/aa"));
    }
}