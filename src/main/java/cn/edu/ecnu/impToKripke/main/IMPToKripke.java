package cn.edu.ecnu.impToKripke.main;

import cn.edu.ecnu.impToKripke.config.KripkeConfig;
import cn.edu.ecnu.impToKripke.enums.DfaEnum;
import cn.edu.ecnu.impToKripke.enums.KripkeEnum;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

public class IMPToKripke {
    // global variables
    private Integer currentState = DfaEnum.Initial.getCode();
    private ArrayList<Integer> wordList = new ArrayList<>();
    private ArrayList<String> contentList = new ArrayList<>();
    // record the if & else & endif position
    private Map<String, Integer> ifSegment = new HashMap<>();
    // whether in the if-else code segment
    private Boolean inIf = false;
    // counter of sentences between if & else
    private Integer countIfToElse = 0;
    // record the while & endwhile position
    private Map<String, Integer> whileSegment = new HashMap<>();
    // whether in the while-do code segment
    private Boolean inWhile = false;
    // counter of sentences between while & endwhile
    private Integer countWhileToEnd = 0;
    // record the variables
    private ArrayList<String> varList = new ArrayList<>();

    public void getWhilePosition(Integer startPostion, Integer whileCount) {
        Integer whileToEnd = 0;
        Integer localLength = wordList.size();
        whileSegment.put("startCount", whileCount);
        while (startPostion < localLength) {
            if (wordList.get(startPostion).equals(KripkeEnum.Endwhile.getCode())) {
                break;
            }
            if (wordList.get(startPostion).equals(KripkeEnum.SemiColon.getCode()) ||
                    wordList.get(startPostion).equals(KripkeEnum.If.getCode())) {
                whileToEnd += 1;
            }
            startPostion += 1;
        }
        whileSegment.put(KripkeConfig.whileToEndCount, whileToEnd);
        inWhile = true;
        countWhileToEnd = 0;
    }

    public void getIfPosition(Integer startPostion) {
        Integer ifToElse = 0, elseToEnd = 0;
        Integer localLength = wordList.size();
        while (startPostion < localLength) {
            if (wordList.get(startPostion).equals(KripkeEnum.Else.getCode())) {
                break;
            }
            if (wordList.get(startPostion) == KripkeEnum.SemiColon.getCode()) {
                ifToElse += 1;
            }
            startPostion += 1;
        }
        ifSegment.put(KripkeConfig.ifToElseCount, ifToElse);
        while (startPostion < localLength) {
            if (wordList.get(startPostion).equals(KripkeEnum.SemiColon.getCode())) {
                elseToEnd += 1;
            }
            if (wordList.get(startPostion).equals(KripkeEnum.Endif.getCode())) {
                break;
            }
            startPostion += 1;
        }
        ifSegment.put(KripkeConfig.elseToEndCount, elseToEnd);
        inIf = true;
        countIfToElse = 0;
    }

    //初始化
    public Integer handleInitialState(Character c) {
        Integer localState = DfaEnum.Initial.getCode();
        if (c >= 65 && c <= 124) {
            if (c == 'i')
                localState = DfaEnum.Id_if1.getCode();
            else if (c == 'e')
                localState = DfaEnum.Id_e1.getCode();
            else if (c == 'a')
                localState = DfaEnum.Id_and1.getCode();
            else if (c == 'o')
                localState = DfaEnum.Id_or1.getCode();
            else if (c == 's')
                localState = DfaEnum.Id_skip1.getCode();
            else if (c == 't')
                localState = DfaEnum.Id_t1.getCode();
            else if (c == 'w')
                localState = DfaEnum.Id_w1.getCode();
            else if (c == 'd')
                localState = DfaEnum.Id_do1.getCode();
            else if (c == 'f')
                localState = DfaEnum.Id_false1.getCode();
            else if (c == 'n')
                localState = DfaEnum.Id_not1.getCode();
            else if (c == 'c')
                localState = DfaEnum.Id_c1.getCode();
            else {
                wordList.add(KripkeEnum.Identifier.getCode());
                contentList.add(c.toString());
            }
        } else if (c == '>')
            localState = DfaEnum.GT.getCode();
        else if (c == '<')
            localState = DfaEnum.LT.getCode();
        else if (c == '+') {
            wordList.add(KripkeEnum.Plus.getCode());
            contentList.add("+");
        }

        // localState = DfaEnum.Initial
        else if (c == '-') {
            wordList.add(KripkeEnum.Minus.getCode());
            contentList.add("-");
        }

        // localState = DfaEnum.Initial
        else if (c == ';') {
            wordList.add(KripkeEnum.SemiColon.getCode());
            contentList.add(";");
        }

        // localState = DfaEnum.Initial
        else if (c == '*') {
            wordList.add(KripkeEnum.Star.getCode());
            contentList.add("*");
        }
        // localState = DfaEnum.Initial
        else if (c == '(') {
            wordList.add(KripkeEnum.LeftParen.getCode());
            contentList.add("(");
        }
        // localState = DfaEnum.Initial
        else if (c == ')') {
            wordList.add(KripkeEnum.RightParen.getCode());
            contentList.add(")");
        }
        // localState = DfaEnum.Initial
        else if (c == '=') {
            localState = DfaEnum.Assignment.getCode();
        } else if (c >= 48 && c <= 57) {
            wordList.add(KripkeEnum.IntLiteral.getCode());
            contentList.add(c.toString());
        } else {
            System.out.println("something unexpected has happend!");
        }
        return localState;
    }

    //生成label function
    public LabelFunctionModel translateToLabelFunction(Integer upToNowCount,Integer startPosition) {
        // get all the variables
        LabelFunctionModel response = new LabelFunctionModel();
        for (int i = upToNowCount; i < wordList.size(); i++) {
            if (wordList.get(i).equals(KripkeEnum.Identifier.getCode())) {
                varList.add(contentList.get(i));
            }
        }
        Stack<Integer> whileStartPosition=new Stack<>();
        char labelStr = 'L';
        // key value
        Integer labelCount = upToNowCount;
        // System.out.println(labelCount)
        List<Map<String, String>> labelProgram = new ArrayList<>();
        Integer length = wordList.size();
        Integer index = startPosition;
        while (index < length) {
            Map<String, String> segment = new HashMap<>();
            // useless key words todo 优化
            if (wordList.get(index).equals(KripkeEnum.Else.getCode()) ||
                    wordList.get(index).equals(KripkeEnum.Endif.getCode()) ||
                    wordList.get(index).equals(KripkeEnum.Endwhile.getCode())) {
                if (wordList.get(index).equals(KripkeEnum.Else.getCode())) {
                    segment.put("type", "else");
                }
                if (wordList.get(index).equals(KripkeEnum.Endif.getCode())) {
                    segment.put("type", "endif");
                }
                if (wordList.get(index).equals(KripkeEnum.Endwhile.getCode())) {
                    segment.put("type", "endwhile");
                    labelProgram.get(labelProgram.size()-1).put(KripkeConfig.backLabel,"L"+whileStartPosition.pop());
                }
                labelProgram.add(segment);
                index += 1;
                continue;
            } else if (wordList.get(index).equals(KripkeEnum.Identifier.getCode()) ||
                    wordList.get(index).equals(KripkeEnum.Skip.getCode()) ||
                    wordList.get(index).equals(KripkeEnum.Wait.getCode())) {
                // skip ;
                if (wordList.get(index).equals(KripkeEnum.Skip.getCode())) {
                    segment.put(KripkeConfig.frontLabel, labelStr + String.valueOf(labelCount));
                    segment.put(KripkeConfig.backLabel, labelStr + String.valueOf(labelCount + 1));
                    segment.put("type", "5");
                    index += 1;
                }
                //wait
                else if (wordList.get(index).equals(KripkeEnum.Wait.getCode())) {
                    segment.put(KripkeConfig.frontLabel, labelStr + String.valueOf(labelCount));
                    segment.put(KripkeConfig.backLabel, labelStr + String.valueOf(labelCount + 1));
                    segment.put("type", "6");
                    index += 1;
                    StringBuffer expression = new StringBuffer("");
                    //todo 容易死循环 优化
                    while (true) {
                        expression.append(contentList.get(index));
                        index += 1;
                        if (contentList.get(index).equals(";")) {
                            break;
                        }

                    }
                    segment.put(KripkeConfig.conditionExp, expression.toString());
                } else {
                    segment.put(KripkeConfig.frontLabel, labelStr + String.valueOf(labelCount));
                    segment.put(KripkeConfig.backLabel, labelStr + String.valueOf(labelCount + 1));
                    segment.put(KripkeConfig.identifier1, contentList.get(index));
                    segment.put(KripkeConfig.identifier2, contentList.get(index + 2));
                    index += 3;
                    // eg: a = 1 / a = b
                    if (wordList.get(index).equals(KripkeEnum.SemiColon.getCode())) {
                        segment.put("type", "1");
                    }
                    // eg: a = 2 * b / a = b + c
                    else {
                        segment.put(KripkeConfig.operator, contentList.get(index));
                        segment.put(KripkeConfig.identifier3, contentList.get(index + 1));
                        index += 2;
                        segment.put("type", "2");
                    }
                }

                if (inIf) {
                    countIfToElse += 1;
                    if (countIfToElse.equals(ifSegment.get(KripkeConfig.ifToElseCount))) {
                        segment.put(KripkeConfig.backLabel, labelStr +
                                String.valueOf(labelCount + ifSegment.get(KripkeConfig.elseToEndCount) + 1));
                        inIf = false;
                        countIfToElse = 0;
                    }

                }

                if (inWhile) {
                    countIfToElse += 1;
                    if (countWhileToEnd.equals(ifSegment.get(KripkeConfig.whileToEndCount))) {
                        segment.put(KripkeConfig.backLabel, labelStr +
                                String.valueOf(labelCount + whileSegment.get(KripkeConfig.startCount) + 1));
                        inWhile = false;
                        countWhileToEnd = 0;
                    }
                }
            } else if (wordList.get(index).equals(KripkeEnum.If.getCode())) {
                // if occurs in the while part
                if (inWhile) {
                    countWhileToEnd += 1;
                }
                getIfPosition(index);
                segment.put(KripkeConfig.frontLabel, labelStr + String.valueOf(labelCount));
                segment.put(KripkeConfig.ifTrueLabel, labelStr + String.valueOf(labelCount + 1));
                segment.put(KripkeConfig.ifFalseLabel, labelStr + String.valueOf(labelCount + ifSegment.get(KripkeConfig.ifToElseCount) + 1));
                StringBuffer expression = new StringBuffer("( ");
                index += 1;
                while (true) {
                    if (wordList.get(index).equals(KripkeEnum.Then.getCode())) {
                        break;
                    }
                    //多个参数之间用空格隔开
                    expression.append(contentList.get(index)).append(" ");
                    index += 1;
                }
                expression.append(")");
                segment.put(KripkeConfig.conditionExp, expression.toString());
                segment.put("type", "3");

            } else if (wordList.get(index).equals(KripkeEnum.While.getCode())) {
                whileStartPosition.push(labelCount);
                getWhilePosition(index, labelCount);
                segment.put(KripkeConfig.frontLabel, labelStr + String.valueOf(labelCount));
                segment.put(KripkeConfig.whileTrueLabel, labelStr + String.valueOf(labelCount + 1));
                segment.put(KripkeConfig.whileFalseLabel, labelStr + String.valueOf(labelCount + whileSegment.get(KripkeConfig.whileToEndCount) + 1));
                StringBuffer expression = new StringBuffer("(");
                index += 1;
                while (true) {
                    if (wordList.get(index).equals(KripkeEnum.Do.getCode())) {
                        break;
                    }
                    expression.append(contentList.get(index));
                    index += 1;
                }
                expression.append(")");
                segment.put(KripkeConfig.conditionExp, expression.toString());
                segment.put("type", "4");
            }
            labelProgram.add(segment);
            labelCount += 1;
            index += 1;

        }
        response.setLabelCount(labelCount);
        response.setLabelProgram(labelProgram);
        return response;
    }

    //输出label function
    public void outputKripkeStructure(List<Map<String, String>> labelProgram) {

        // System.out.println(labelProgram)
        System.out.println("*********************Label Program***************************");
        for (Map<String, String> currentPart : labelProgram) {
            if (currentPart.get("type").equals("1")) {
                System.out.println(currentPart.get(KripkeConfig.frontLabel) + ": " +
                        currentPart.get(KripkeConfig.identifier1) + " = " + currentPart.get(KripkeConfig.identifier2));
            } else if (currentPart.get("type").equals("2")) {
                System.out.println(currentPart.get(KripkeConfig.frontLabel) + ": " +
                        currentPart.get(KripkeConfig.identifier1) + " = " + currentPart.get(KripkeConfig.identifier2) +
                        " " + currentPart.get(KripkeConfig.operator) + " " + currentPart.get(KripkeConfig.identifier3));
            } else if (currentPart.get("type").equals("3")) {
                System.out.println(currentPart.get(KripkeConfig.frontLabel) + ": if " +
                        currentPart.get(KripkeConfig.conditionExp) + " then ");
            } else if (currentPart.get("type").equals("4")) {
                System.out.println(currentPart.get(KripkeConfig.frontLabel) + ": while " +
                        currentPart.get(KripkeConfig.conditionExp) + " do ");
            } else if (currentPart.get("type").equals("5")) {
                System.out.println(currentPart.get(KripkeConfig.frontLabel) + ": skip ");
            } else if (currentPart.get("type").equals("6")) {
                System.out.println(currentPart.get(KripkeConfig.frontLabel) + ": wait " +
                        currentPart.get(KripkeConfig.conditionExp));
            } else {
                System.out.println(currentPart.get("type"));
            }
        }
        System.out.println("*********************Label Formula***************************");
        for (Map<String, String> currentPart : labelProgram) {
            if (currentPart.get("type").equals("1")) {
                System.out.println(String.format("pc = %s ^ pc' = %s ^ %s' = %s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.backLabel), currentPart.get(KripkeConfig.identifier1), currentPart.get(KripkeConfig.identifier2)));
            } else if (currentPart.get("type").equals("2")) {
                System.out.println(String.format("pc = %s ^ pc' = %s ^ %s' = %s %s %s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.backLabel), currentPart.get(KripkeConfig.identifier1), currentPart.get(KripkeConfig.identifier2)
                        , currentPart.get(KripkeConfig.operator), currentPart.get(KripkeConfig.identifier3)));
            } else if (currentPart.get("type").equals("3")) {
                System.out.println(String.format("pc = %s ^ pc' = %s ^ %s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.ifTrueLabel), currentPart.get(KripkeConfig.conditionExp)));
                System.out.println(String.format("pc = %s ^ pc' = %s ^ ¬%s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.ifFalseLabel), currentPart.get(KripkeConfig.conditionExp)));
            } else if (currentPart.get("type").equals("4")) {
                System.out.println(String.format("pc = %s ^ pc' = %s ^ %s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.whileTrueLabel), currentPart.get(KripkeConfig.conditionExp)));
                System.out.println(String.format("pc = %s ^ pc' = %s ^ ¬%s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.whileFalseLabel), currentPart.get(KripkeConfig.conditionExp)));
            } else if (currentPart.get("type").equals("5")) {
                System.out.println(String.format("pc = %s ^ pc' = %s ^ Same(U)", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.backLabel), currentPart.get(KripkeConfig.conditionExp)));
            } else if (currentPart.get("type").equals("6")) {
                System.out.println(String.format("pc = %s ^ pc' = %s ^ %s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.backLabel), currentPart.get(KripkeConfig.conditionExp)));
                System.out.println(String.format("pc = %s ^ pc' = %s ^ ¬%s", currentPart.get(KripkeConfig.frontLabel),
                        currentPart.get(KripkeConfig.frontLabel), currentPart.get(KripkeConfig.conditionExp)));
            }
        }
    }
    //endregion
    //region构造一阶逻辑公式
    public LabelFunctionModel translateToKripke(String codeContent, Integer upToNowCount, Integer flag) {
        //记录当前wordlist是从哪里开始
        Integer length=wordList.size();
        String newCodeContent = codeContent;
        //预处理文本
        List<String> content = new ArrayList<>();
        StringBuilder co = new StringBuilder();
        for (int i = 0; i < newCodeContent.length(); i++) {
            char c = newCodeContent.charAt(i);
            if (newCodeContent.charAt(i) == ' '
                    || newCodeContent.charAt(i) == ';'
                    || c == '(' || c == ')') {
                if (co.length() > 0) {
                    content.add(co.toString());
                    co = new StringBuilder();
                }
            }
            if (c == ';') {
                content.add(c + "");
                co = new StringBuilder();
            } else if (c != ' ') {
                co.append(newCodeContent.charAt(i));
            }
        }
        content.add(co.toString());
        for (int j = 0; j < content.size(); j++) {
            for (int i = 0; i < content.get(j).length(); i++) {
                char c = content.get(j).charAt(i);
                if (c == '\n' || c == ' ') {
                    continue;
                } else if (currentState.equals(DfaEnum.GT.getCode())) {
                    if (c == '=') {
                        // >=
                        wordList.add(KripkeEnum.GE.getCode());
                        contentList.add(">=");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        //>
                        wordList.add(KripkeEnum.GT.getCode());
                        contentList.add(">");
                        currentState = handleInitialState(c);
                    }
                } else if (currentState.equals(DfaEnum.LT.getCode())) {
                    if (c == '=') {
                        // >=
                        wordList.add(KripkeEnum.LE.getCode());
                        contentList.add("<=");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        //<
                        wordList.add(KripkeEnum.LT.getCode());
                        contentList.add("<");
                        currentState = handleInitialState(c);
                    }
                } else if (currentState.equals(DfaEnum.Assignment.getCode())) {
                    if (c == '=') {
                        // ==
                        wordList.add(KripkeEnum.EQ.getCode());
                        contentList.add("==");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        //=
                        wordList.add(KripkeEnum.Assignment.getCode());
                        contentList.add("=");
                        currentState = handleInitialState(c);
                    }
                }
                //65
                else if (currentState.equals(DfaEnum.Id_c1.getCode())) {
                    if (c == 'o') {
                        // co
                        currentState = DfaEnum.Id_c2.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("c");
                        currentState = handleInitialState(c);
                    }
                }
                //co
                else if (currentState.equals(DfaEnum.Id_c2.getCode())) {
                    if (c == 'b') {
                        // cob
                        currentState = DfaEnum.Id_cobegin3.getCode();
                    } else if (c == 'e') {
                        //coe
                        currentState = DfaEnum.Id_coend3.getCode();

                    } else {
                        System.out.println("co error");
                    }
                }
                //co
                else if (currentState.equals(DfaEnum.Id_cobegin3.getCode())) {
                    if (c == 'e') {
                        // cob
                        currentState = DfaEnum.Id_cobegin4.getCode();
                    } else {
                        System.out.println("cob error");
                    }
                }
                //cobe
                else if (currentState.equals(DfaEnum.Id_cobegin4.getCode())) {
                    if (c == 'g') {
                        // cobeg
                        currentState = DfaEnum.Id_cobegin5.getCode();
                    } else {
                        System.out.println("cobe error");
                    }
                }  //cobeg
                else if (currentState.equals(DfaEnum.Id_cobegin5.getCode())) {
                    if (c == 'i') {
                        // cob
                        currentState = DfaEnum.Id_cobegin6.getCode();
                    } else {
                        System.out.println("cobe error");
                    }
                }
                //cobegi
                else if (currentState.equals(DfaEnum.Id_cobegin6.getCode())) {
                    if (c == 'n') {
                        // cobegin
                        currentState = DfaEnum.Id_cobegin7.getCode();
                        wordList.add(KripkeEnum.Cobegin.getCode());
                        contentList.add("cobegin");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("cobegi error");
                    }
                }
                //coe
                else if (currentState.equals(DfaEnum.Id_coend3.getCode())) {
                    if (c == 'n') {
                        // coen
                        currentState = DfaEnum.Id_coend4.getCode();
                    } else {
                        System.out.println("coe error");
                    }
                }

                //coen
                else if (currentState.equals(DfaEnum.Id_coend4.getCode())) {
                    if (c == 'd') {
                        // coen
                        currentState = DfaEnum.Id_coend5.getCode();
                        wordList.add(KripkeEnum.Coend.getCode());
                        contentList.add("coend");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("coen error");
                    }
                }

                //i
                else if (currentState.equals(DfaEnum.Id_if1.getCode())) {
                    if (c == 'f') {
                        // coen
                        currentState = DfaEnum.Id_if2.getCode();
                        wordList.add(KripkeEnum.If.getCode());
                        contentList.add("if");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("i");
                        currentState = handleInitialState(c);
                    }
                }
                //e
                else if (currentState.equals(DfaEnum.Id_e1.getCode())) {
                    if (c == 'l') {
                        // el
                        currentState = DfaEnum.Id_else2.getCode();
                    } else if (c == 'n') {
                        // en
                        currentState = DfaEnum.Id_end2.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("e");
                        currentState = handleInitialState(c);
                    }
                }
                //el
                else if (currentState.equals(DfaEnum.Id_else2.getCode())) {
                    if (c == 's') {
                        // els
                        currentState = DfaEnum.Id_else3.getCode();
                    } else if (c == 'n') {
                        // en
                        System.out.println("el error");
                    }
                }
                //els
                else if (currentState.equals(DfaEnum.Id_else3.getCode())) {
                    if (c == 'e') {
                        // el
                        currentState = DfaEnum.Id_else4.getCode();
                        wordList.add(KripkeEnum.Else.getCode());
                        contentList.add("else");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("els error");
                    }
                }
                //en
                else if (currentState.equals(DfaEnum.Id_end2.getCode())) {
                    if (c == 'd') {
                        // el
                        currentState = DfaEnum.Id_end3.getCode();
                    } else {
                        System.out.println("en error");
                    }
                }
                //end
                else if (currentState.equals(DfaEnum.Id_end3.getCode())) {
                    if (c == 'w') {
                        // el
                        currentState = DfaEnum.Id_endwhile4.getCode();
                    } else if (c == 'i') {
                        // el
                        currentState = DfaEnum.Id_endif4.getCode();
                    } else {
                        System.out.println("end error");
                    }
                }
                //endw
                else if (currentState.equals(DfaEnum.Id_endwhile4.getCode())) {
                    if (c == 'h') {
                        // el
                        currentState = DfaEnum.Id_endwhile5.getCode();
                    } else {
                        System.out.println("endw error");
                    }
                }
                //endwh
                else if (currentState.equals(DfaEnum.Id_endwhile5.getCode())) {
                    if (c == 'i') {
                        // el
                        currentState = DfaEnum.Id_endwhile6.getCode();
                    } else {
                        System.out.println("endwh error");
                    }
                }
                //endwhi
                else if (currentState.equals(DfaEnum.Id_endwhile6.getCode())) {
                    if (c == 'l') {
                        // el
                        currentState = DfaEnum.Id_endwhile7.getCode();
                    } else {
                        System.out.println("endwhi error");
                    }
                }

                //endwhil
                else if (currentState.equals(DfaEnum.Id_endwhile7.getCode())) {
                    if (c == 'e') {
                        // el
                        currentState = DfaEnum.Id_endwhile8.getCode();
                        wordList.add(KripkeEnum.Endwhile.getCode());
                        contentList.add("endwhile");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("endwhil error");
                    }
                }
                //endi
                else if (currentState.equals(DfaEnum.Id_endif4.getCode())) {
                    if (c == 'f') {
                        // el
                        currentState = DfaEnum.Id_endif5.getCode();
                        wordList.add(KripkeEnum.Endif.getCode());
                        contentList.add("endif");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("endi error");
                    }
                }
                //a
                else if (currentState.equals(DfaEnum.Id_and1.getCode())) {
                    if (c == 'n') {
                        // an
                        currentState = DfaEnum.Id_and2.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("a");
                        currentState = handleInitialState(c);
                    }
                }
                //an
                else if (currentState.equals(DfaEnum.Id_and2.getCode())) {
                    if (c == 'd') {
                        // and
                        currentState = DfaEnum.Id_and3.getCode();
                        wordList.add(KripkeEnum.And.getCode());
                        contentList.add("and");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("endi error");
                    }
                }
                //o
                else if (currentState.equals(DfaEnum.Id_or1.getCode())) {
                    if (c == 'r') {
                        // or
                        currentState = DfaEnum.Id_or2.getCode();
                        wordList.add(KripkeEnum.Or.getCode());
                        contentList.add("or");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("o");
                        currentState = handleInitialState(c);
                    }
                }
                //n
                else if (currentState.equals(DfaEnum.Id_not1.getCode())) {
                    if (c == 'o') {
                        // no
                        currentState = DfaEnum.Id_not2.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("n");
                        currentState = handleInitialState(c);
                    }
                }
                //no
                else if (currentState.equals(DfaEnum.Id_not2.getCode())) {
                    if (c == 't') {
                        // not
                        currentState = DfaEnum.Id_not3.getCode();
                        wordList.add(KripkeEnum.Not.getCode());
                        contentList.add("not");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("no error");
                    }
                }
                //s
                else if (currentState.equals(DfaEnum.Id_skip1.getCode())) {
                    if (c == 'k') {
                        // sk
                        currentState = DfaEnum.Id_skip2.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("s");
                        currentState = handleInitialState(c);

                    }
                }
                //sk
                else if (currentState.equals(DfaEnum.Id_skip2.getCode())) {
                    if (c == 'i') {
                        // ski
                        currentState = DfaEnum.Id_skip3.getCode();
                    } else {
                        System.out.println("sk error");
                    }
                }
                //ski
                else if (currentState.equals(DfaEnum.Id_skip3.getCode())) {
                    if (c == 'p') {
                        // skip
                        currentState = DfaEnum.Id_skip4.getCode();
                        wordList.add(KripkeEnum.Skip.getCode());
                        contentList.add("skip");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("ski error");
                    }
                }

                //t
                else if (currentState.equals(DfaEnum.Id_t1.getCode())) {
                    if (c == 'r') {
                        // tr
                        currentState = DfaEnum.Id_true2.getCode();
                    } else if (c == 'h') {
                        // th
                        currentState = DfaEnum.Id_then2.getCode();

                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("t");
                        currentState = handleInitialState(c);
                    }
                }
                //tr
                else if (currentState.equals(DfaEnum.Id_true2.getCode())) {
                    if (c == 'u') {
                        // tru
                        currentState = DfaEnum.Id_true3.getCode();

                    } else {
                        System.out.println("tr error");
                    }
                }
                //tru
                else if (currentState.equals(DfaEnum.Id_true3.getCode())) {
                    if (c == 'e') {
                        // true
                        currentState = DfaEnum.Id_true4.getCode();
                        wordList.add(KripkeEnum.True.getCode());
                        contentList.add("true");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("tru error");
                    }
                }
                //th
                else if (currentState.equals(DfaEnum.Id_then2.getCode())) {
                    if (c == 'e') {
                        // the
                        currentState = DfaEnum.Id_then3.getCode();
                    } else {
                        System.out.println("th error");
                    }
                }
                //the
                else if (currentState.equals(DfaEnum.Id_then3.getCode())) {
                    if (c == 'n') {
                        // then
                        currentState = DfaEnum.Id_then4.getCode();
                        wordList.add(KripkeEnum.Then.getCode());
                        contentList.add("then");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("the error");
                    }
                }

                //w
                else if (currentState.equals(DfaEnum.Id_w1.getCode())) {
                    if (c == 'h') {
                        // or
                        currentState = DfaEnum.Id_while2.getCode();
                    } else if (c == 'a') {
                        // or
                        currentState = DfaEnum.Id_wait2.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("w");
                        currentState = handleInitialState(c);
                    }
                }
                //wh
                else if (currentState.equals(DfaEnum.Id_while2.getCode())) {
                    if (c == 'i') {
                        // whi
                        currentState = DfaEnum.Id_while3.getCode();

                    } else {
                        System.out.println("wh error");
                    }
                }

                //wh
                else if (currentState.equals(DfaEnum.Id_while3.getCode())) {
                    if (c == 'l') {
                        // whil
                        currentState = DfaEnum.Id_while4.getCode();

                    } else {
                        System.out.println("whi error");
                    }
                }

                //whil
                else if (currentState.equals(DfaEnum.Id_while4.getCode())) {
                    if (c == 'e') {
                        // while
                        currentState = DfaEnum.Id_while5.getCode();
                        wordList.add(KripkeEnum.While.getCode());
                        contentList.add("while");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("Whil error");
                    }
                }
                //wa
                else if (currentState.equals(DfaEnum.Id_wait2.getCode())) {
                    if (c == 'i') {
                        // wai
                        currentState = DfaEnum.Id_wait3.getCode();
                    } else {
                        System.out.println("wa error");
                    }
                }
                //wai
                else if (currentState.equals(DfaEnum.Id_wait3.getCode())) {
                    if (c == 't') {
                        // wait
                        currentState = DfaEnum.Id_wait4.getCode();
                        wordList.add(KripkeEnum.Wait.getCode());
                        contentList.add("wait");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("wai error");
                    }
                }
                //d
                else if (currentState.equals(DfaEnum.Id_do1.getCode())) {
                    if (c == 'o') {
                        // or
                        currentState = DfaEnum.Id_do2.getCode();
                        wordList.add(KripkeEnum.Do.getCode());
                        contentList.add("do");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("d");
                        currentState = handleInitialState(c);
                    }
                }
                //f
                else if (currentState.equals(DfaEnum.Id_false1.getCode())) {
                    if (c == 'a') {
                        // ski
                        currentState = DfaEnum.Id_false2.getCode();
                    } else {
                        wordList.add(KripkeEnum.Identifier.getCode());
                        contentList.add("f");
                        currentState = handleInitialState(c);
                    }
                }
                //fa
                else if (currentState.equals(DfaEnum.Id_false2.getCode())) {
                    if (c == 'l') {
                        // fal
                        currentState = DfaEnum.Id_false3.getCode();
                    } else {
                        System.out.println("fa error");
                    }
                }
                //fal
                else if (currentState.equals(DfaEnum.Id_false3.getCode())) {
                    if (c == 's') {
                        // fals
                        currentState = DfaEnum.Id_false4.getCode();

                    } else {
                        System.out.println("fal error");
                    }
                }
                //fals
                else if (currentState.equals(DfaEnum.Id_false4.getCode())) {
                    if (c == 'e') {
                        // false
                        currentState = DfaEnum.Id_false5.getCode();
                        wordList.add(KripkeEnum.False.getCode());
                        contentList.add("false");
                        currentState = DfaEnum.Initial.getCode();
                    } else {
                        System.out.println("fals error");
                    }
                }
                //
                else if (currentState.equals(DfaEnum.Initial.getCode())) {
                    currentState = handleInitialState(c);
                }
            }
        }

        LabelFunctionModel response = new LabelFunctionModel();
        //计算表达式 直接返回内容
        if (flag == 1) {
            response.setWordList(wordList);
            response.setContentList(contentList);
            return response;
        }
        //构造一阶逻辑表达式
        response = translateToLabelFunction(upToNowCount,length);
        return response;
    }
    //endregion
    public void structNodeRelation(IMPToKripke impToKripke,String count,List<String> codeList){

        Integer nodeCount = Integer.parseInt(count);
        //初始化节点
        Map<String, String> initialNode = new HashMap<>();
        initialNode.put("pc0", "L1");
        initialNode.put("pc1", "L1");
        initialNode.put("node", "1");
        Integer currentCount = 1;
        List<Map<String, String>> finalResult = new ArrayList<>();
        List<Integer> len=new ArrayList<>();
        //region将程序转换成一阶逻辑式子
        for (Integer i=0;i< codeList.size();i++) {
            String s=codeList.get(i);
            if (s == null || s == "") {
                continue;
            }
            initialNode.put("pc1", "L" + (currentCount ));
            LabelFunctionModel model = impToKripke.translateToKripke(s, currentCount , 0);
            List<Map<String, String>> labelProgram = model.getLabelProgram();
            if (model.getLabelProgram().size() > 0)
                impToKripke.outputKripkeStructure(labelProgram);
            currentCount = model.getLabelCount();
            len.add(i,model.getLabelCount());

            for (Map<String, String> temp : labelProgram) {
                if (temp.get("type").length() > 2) {
                    continue;
                }
                finalResult.add(temp);
            }
        }
        //endregion
        //region将变量加入节点内容
        Set<String> allvariableSet = new HashSet<>(impToKripke.varList);
        List<String> allvariableList = new ArrayList<>(allvariableSet);
        Collections.sort(allvariableList);
        for (String s : allvariableList) {
            initialNode.put(s, 0 + "");
        }
        //endregion
        //System.out.println(initialNode);
        //region初始化边
        List<List<String>> edge = new ArrayList<>();
        for (int i = 0; i < nodeCount +10; i++) {
            edge.add(new ArrayList<>());
        }
        //endregion
        nodeCount = 1;
        List<Map<String, String>> nodeList = new ArrayList<>();
        nodeList.add(initialNode);
        LinkedList<Map<String, String>> queue = new LinkedList<>();
        queue.push(initialNode);
        //region 构造节点关系
        while (!queue.isEmpty()) {
            Map<String, String> currentNode = queue.pop();
            for (Map<String, String> tranCondition : finalResult) {
                //pc0 or pc1 =frontLabel
                if (!currentNode.get("pc0").equals(tranCondition.get(KripkeConfig.frontLabel)) &&
                        !currentNode.get("pc1").equals(tranCondition.get(KripkeConfig.frontLabel))) {
                    continue;
                }
                Map<String, String> newNode = new HashMap<>(currentNode);
                //wait
                if (tranCondition.get("type").equals("6")) {
                    if (impToKripke.judgeExpression(tranCondition.get(KripkeConfig.conditionExp), currentCount, currentNode))
                        if (currentNode.get("pc0").equals(tranCondition.get(KripkeConfig.frontLabel))) {
                            newNode.put("pc0", tranCondition.get(KripkeConfig.backLabel));
                        } else {
                            newNode.put("pc1", tranCondition.get(KripkeConfig.backLabel));
                        }
                } else if (tranCondition.get("type").equals("3")) {
                    if (impToKripke.judgeExpression(tranCondition.get(KripkeConfig.conditionExp), currentCount, currentNode)) {
                        if (currentNode.get("pc0").equals(tranCondition.get(KripkeConfig.frontLabel))) {
                            newNode.put("pc0", tranCondition.get(KripkeConfig.ifTrueLabel));
                        } else {
                            newNode.put("pc1", tranCondition.get(KripkeConfig.ifTrueLabel));
                        }
                    } else {
                        if (currentNode.get("pc0").equals(tranCondition.get(KripkeConfig.frontLabel))) {
                            newNode.put("pc0", tranCondition.get(KripkeConfig.ifFalseLabel));
                        } else {
                            newNode.put("pc1", tranCondition.get(KripkeConfig.ifFalseLabel));
                        }
                    }
                    //while
                } else if (tranCondition.get("type").equals("4")) {
                    if (impToKripke.judgeExpression(tranCondition.get(KripkeConfig.conditionExp), currentCount, currentNode)) {
                        if (currentNode.get("pc0").equals(tranCondition.get(KripkeConfig.frontLabel))) {
                            newNode.put("pc0", tranCondition.get                                                                        (KripkeConfig.whileTrueLabel));
                        } else {
                            newNode.put("pc1", tranCondition.get(KripkeConfig.whileTrueLabel));
                        }
                    } else {
                        if (currentNode.get("pc0").equals(tranCondition.get(KripkeConfig.frontLabel))) {
                            newNode.put("pc0", tranCondition.get(KripkeConfig.whileFalseLabel));
                        } else {
                            newNode.put("pc1", tranCondition.get(KripkeConfig.whileFalseLabel));
                        }
                    }

                } else {
                    if (tranCondition.get("type").equals("1")) {
                        //b
                        String modifyVariable = tranCondition.get(KripkeConfig.identifier1);
                        //eval("1")
                        newNode.put(modifyVariable, impToKripke.toString(tranCondition.get(KripkeConfig.identifier2), currentNode));
                    } else if (tranCondition.get("type").equals("2")) {
                        String modifyVariable = tranCondition.get(KripkeConfig.identifier1);
                        String exper = tranCondition.get(KripkeConfig.identifier2) +
                                tranCondition.get(KripkeConfig.operator) + tranCondition.get(KripkeConfig.identifier3);
                        newNode.put(modifyVariable, impToKripke.toString(exper, currentNode));
                    } else if (tranCondition.get("type").equals("5")) {

                    }
                    if (currentNode.get("pc0").equals(tranCondition.get(KripkeConfig.frontLabel))) {
                        newNode.put("pc0", tranCondition.get(KripkeConfig.backLabel));
                    } else {
                        newNode.put("pc1", tranCondition.get(KripkeConfig.backLabel));
                    }
                }

                Integer flag = 1;

                for (Map<String, String> entry : nodeList) {
                    if (impToKripke.isEqual(entry, newNode)) {
                        Integer index1 = Integer.parseInt(currentNode.get("node"));
                        if (index1 > edge.size()) {
                            List<String> list = new ArrayList<>();
                            list.add(entry.get("node"));
                            edge.add(index1, list);
                            System.out.println("-------------------");
                            System.out.println(index1 + "------");
                            System.out.println(list);
                            System.out.println("--------------------");
                        } else {
                            edge.get(index1).add(entry.get("node"));
                        }
                        flag = 0;
                    }
                }
                if (flag == 1) {
                    nodeCount += 1;
                    newNode.put("node", nodeCount + "");
                    //将当前节点连接到currentNode节点
                    edge.get(Integer.parseInt(currentNode.get("node"))).add(nodeCount + "");
                    nodeList.add(newNode);
                    queue.addLast(newNode);
                    System.out.println(newNode);
                }
            }
            if (nodeCount >= Integer.parseInt(count)) {
                break;
            }

        }
        //endregion
        // 画图
        impToKripke.drawKripke(edge,nodeCount,nodeList,allvariableList);
    }
    //画图
    public void drawKripke( List<List<String>> edge,Integer nodeCount,List<Map<String,String>> nodeList,List<String>allvariableList) {
        GraphViz dot = new GraphViz("/Users/jiangqianxi/Desktop/test", "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe");
        dot.start_graph();
        for (int i=0;i<nodeCount;i++){
            Map<String ,String> t=nodeList.get(i);
            StringBuffer sb=new StringBuffer();
            for (String var:allvariableList){
                sb.append(t.get(var)).append(",");
            }
            sb.append(t.get("pc0")).append(",").append(t.get("pc1"));
            dot.label(t.get("node"),sb.toString());
        }
        for (int i=1;i<nodeCount+1;i++){
            for (int j=0;j<edge.get(i).size();j++){
                dot.addln(i+"->"+edge.get(i).get(j));
            }
        }
        dot.end_graph();
        System.out.println(dot.getGraph());
        try {
            dot.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //判断两个节点除了node其他内容是否相同
    public Boolean isEqual(Map<String, String> node1, Map<String, String> node2) {
        if (node1 == null || node2 == null || node1.size() != node2.size()) {
            return false;
        }
        for (Map.Entry<String, String> entry : node1.entrySet()) {
            if (entry.getKey().equals("node") ||
                    entry.getValue().equals(node2.get(entry.getKey()))) {
                continue;
            }
            return false;
        }
        return true;
    }
    //计算表达式的值
    public Boolean judgeExpression(String expression, Integer currentCount, Map<String, String> currentNode) {
        IMPToKripke imk = new IMPToKripke();
        LabelFunctionModel model = imk.translateToKripke(expression, currentCount, 1);
        List<Integer> wordList = model.getWordList();
        Integer localLength = wordList.size();
        String newExpe = "";
        for (int i = 0; i < localLength; i++) {
            if (wordList.get(i).equals(KripkeEnum.Identifier.getCode())) {
                //imk namespace
                //赋值情况 找到变量的值
//                String temp = imk.contentList.get(i);
//                while (!temp.matches("[0-9]+")) {
//                    temp = currentNode.get(temp);
//                }
//                newExpe += temp+" ";
                newExpe += currentNode.get(imk.contentList.get(i));
            } else if (wordList.get(i).equals(KripkeEnum.True.getCode())) {
                newExpe += "true ";
            } else if (wordList.get(i).equals(KripkeEnum.False.getCode())) {
                newExpe += "false ";
            } else if (wordList.get(i).equals(KripkeEnum.Not.getCode())) {
                newExpe += "! ";
            } else {
                newExpe += imk.contentList.get(i) + " ";
            }
        }
        return Boolean.parseBoolean(eval(newExpe));

    }
    //计算字符串表达式的值
    public String eval(String newExpe) {
        //compute newExpe  优化将此提取成一个函数
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
        try {
            return String.valueOf(scriptEngine.eval(newExpe));
        } catch (ScriptException exception) {
            exception.printStackTrace();
            return " ";
        }

    }

    //判断条件语句是否为真
    public Boolean parseBoolean(String s) {
        if (s == null || s.equals(" ")) {
            return false;
        }
        //匹配数字
        if (s.matches("[0-9]+")) {
            Integer s1 = Integer.parseInt(s);
            if (s1 != 0) {
                return true;
            }
            return false;
        } //匹配字母
        else if (s.matches("[a-z]*|[A-Z]*")) {
            if (s.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }
    //计算s的数值
    public String toString(String s, Map<String, String> currentNode) {
        if (s.length() > 1) {
            //计算表达式的值
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                if (Character.isLetter(s.charAt(i))) {
                    sb.append(currentNode.get(s.charAt(i)+""));
                }else {
                    sb.append(s.charAt(i));
                }
            }
            return eval(sb.toString());
        } else if (s.matches("[a-z]|[A-Z]")) {
            return currentNode.get(s);
        } else {
            return s;
        }
    }
}
