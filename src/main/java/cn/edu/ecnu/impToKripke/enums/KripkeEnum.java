package cn.edu.ecnu.impToKripke.enums;

import lombok.*;

public enum KripkeEnum {
    Plus(1),
    Minus(2),
    Star(3),
    GE(4),
    GT(5),
    EQ(6),
    LE(7),
    LT(8),
    SemiColon(9),
    LeftParen(10),
    RightParen(11),
    Assignment(12),
    If(13),
    Else(14),
    And(15),
    Or(16),
    Not(17),
    Skip(18),
    Wait(19),
    Then(20),
    While(21),
    Do(22),
    True(1),
    False(24),
    // cobegin & coend
    Cobegin(26),
    Coend(27),
    //a~z
    Identifier(28),
    // 0,1,2
    IntLiteral(29),
    // sth forget
    Endwhile ( 30),
    Endif ( 31);

    @Setter
    @Getter
    private Integer Code;

    KripkeEnum(int code) {
        this.Code = code;
    }

    public Integer getCode() {
        return Code;
    }

}
