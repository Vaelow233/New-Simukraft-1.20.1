package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.Horizontal;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.TextWrap;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.Vertical;

public final class TextStyle {
    int textColor = 0xFFFFFFFF;
    boolean textShadow = true;
    float fontSize = 9.0F;
    TextWrap textWrap = TextWrap.HIDE;
    Horizontal horizontal = Horizontal.CENTER;
    Vertical vertical = Vertical.CENTER;
    float rollSpeed;

    public TextStyle textColor(int value) { textColor = value; return this; }
    public TextStyle textShadow(boolean value) { textShadow = value; return this; }
    public TextStyle fontSize(float value) { fontSize = value; return this; }
    public TextStyle textWrap(TextWrap value) { textWrap = value; return this; }
    public TextStyle textAlignHorizontal(Horizontal value) { horizontal = value; return this; }
    public TextStyle textAlignVertical(Vertical value) { vertical = value; return this; }
    public TextStyle rollSpeed(float value) { rollSpeed = value; return this; }
    public float fontSize() { return fontSize; }
}
