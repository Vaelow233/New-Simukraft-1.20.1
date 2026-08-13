package common.cn.kafei.simukraft.compat.ldlib.gui.ui.style;

import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.FlexWrap;
import dev.vfyjxf.taffy.style.TaffyPosition;

public final class LayoutStyle {
    public float width = Float.NaN;
    public float height = Float.NaN;
    public float widthPercent = Float.NaN;
    public float heightPercent = Float.NaN;
    public float minWidth = Float.NaN;
    public float minHeight = Float.NaN;
    public float maxWidth = Float.NaN;
    public float maxHeight = Float.NaN;
    public float left = Float.NaN;
    public float right = Float.NaN;
    public float top = Float.NaN;
    public float bottom = Float.NaN;
    public float paddingLeft;
    public float paddingRight;
    public float paddingTop;
    public float paddingBottom;
    public float marginLeft;
    public float marginRight;
    public float marginTop;
    public float marginBottom;
    public float rowGap;
    public float columnGap;
    public float flexGrow;
    public float flexShrink = 1.0F;
    public float aspectRatio = Float.NaN;
    public FlexDirection flexDirection = FlexDirection.ROW;
    public FlexWrap flexWrap = FlexWrap.NO_WRAP;
    public AlignItems alignItems = AlignItems.STRETCH;
    public AlignItems alignSelf;
    public AlignContent alignContent = AlignContent.FLEX_START;
    public AlignContent justifyContent = AlignContent.FLEX_START;
    public TaffyPosition positionType = TaffyPosition.RELATIVE;

    public LayoutStyle width(float value) { width = value; return this; }
    public LayoutStyle height(float value) { height = value; return this; }
    public LayoutStyle widthPercent(float value) { widthPercent = value; return this; }
    public LayoutStyle heightPercent(float value) { heightPercent = value; return this; }
    public LayoutStyle minWidth(float value) { minWidth = value; return this; }
    public LayoutStyle minHeight(float value) { minHeight = value; return this; }
    public LayoutStyle maxWidth(float value) { maxWidth = value; return this; }
    public LayoutStyle maxHeight(float value) { maxHeight = value; return this; }
    public LayoutStyle left(float value) { left = value; return this; }
    public LayoutStyle right(float value) { right = value; return this; }
    public LayoutStyle top(float value) { top = value; return this; }
    public LayoutStyle bottom(float value) { bottom = value; return this; }
    public LayoutStyle paddingAll(float value) { paddingLeft = paddingRight = paddingTop = paddingBottom = value; return this; }
    public LayoutStyle paddingLeft(float value) { paddingLeft = value; return this; }
    public LayoutStyle paddingRight(float value) { paddingRight = value; return this; }
    public LayoutStyle paddingTop(float value) { paddingTop = value; return this; }
    public LayoutStyle paddingBottom(float value) { paddingBottom = value; return this; }
    public LayoutStyle paddingHorizontal(float value) { paddingLeft = paddingRight = value; return this; }
    public LayoutStyle paddingVertical(float value) { paddingTop = paddingBottom = value; return this; }
    public LayoutStyle marginAll(float value) { marginLeft = marginRight = marginTop = marginBottom = value; return this; }
    public LayoutStyle marginLeft(float value) { marginLeft = value; return this; }
    public LayoutStyle marginRight(float value) { marginRight = value; return this; }
    public LayoutStyle marginTop(float value) { marginTop = value; return this; }
    public LayoutStyle marginBottom(float value) { marginBottom = value; return this; }
    public LayoutStyle marginHorizontal(float value) { marginLeft = marginRight = value; return this; }
    public LayoutStyle marginVertical(float value) { marginTop = marginBottom = value; return this; }
    public LayoutStyle gapAll(float value) { rowGap = columnGap = value; return this; }
    public LayoutStyle gap(float row, float column) { rowGap = row; columnGap = column; return this; }
    public LayoutStyle flex(float value) { flexGrow = value; flexShrink = value; return this; }
    public LayoutStyle flexGrow(float value) { flexGrow = value; return this; }
    public LayoutStyle flexShrink(float value) { flexShrink = value; return this; }
    public LayoutStyle setAspectRatio(float value) { aspectRatio = value; return this; }
    public LayoutStyle flexDirection(FlexDirection value) { flexDirection = value; return this; }
    public LayoutStyle flexWrap(FlexWrap value) { flexWrap = value; return this; }
    public LayoutStyle alignItems(AlignItems value) { alignItems = value; return this; }
    public LayoutStyle alignSelf(AlignItems value) { alignSelf = value; return this; }
    public LayoutStyle alignContent(AlignContent value) { alignContent = value; return this; }
    public LayoutStyle justifyContent(AlignContent value) { justifyContent = value; return this; }
    public LayoutStyle positionType(TaffyPosition value) { positionType = value; return this; }
}
