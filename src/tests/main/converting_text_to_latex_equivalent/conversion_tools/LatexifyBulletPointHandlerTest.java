package main.converting_text_to_latex_equivalent.conversion_tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class LatexifyBulletPointHandlerTest {
    LatexifyBulletPointHandler inst;

    String DASH_BULLET_POINT = "- ";
    String DOT_BULLET_POINT = "• ";

    @BeforeEach
    void setUp() {
        inst = new LatexifyBulletPointHandler();
    }

    @Test
    @DisplayName("same as previous bullet; style = dot")
    void samePrevStyleDot() throws IOException {
        //prep
        String line = "• this is the line";
        String bulletPointStyle = DOT_BULLET_POINT;
        String previousBulletPointStyle = DOT_BULLET_POINT;
        //result
        BulletPointOperationOut operationOut = inst.handleBulletPointOperation(line, bulletPointStyle, previousBulletPointStyle);
        String modifiedLine = operationOut.modifiedLine();
        String previousBulletStyle = operationOut.previousBulletStyle();
        //test
        assertEquals("  \\item this is the line", modifiedLine);
        assertEquals(DOT_BULLET_POINT,previousBulletStyle);
    }

    @Test
    @DisplayName("no previous bullet point; style = dot")
    void noPrevStyleDot() throws IOException {
        //prep
        String line = "• this is the line";
        String bulletPointStyle = DOT_BULLET_POINT;
        String previousBulletPointStyle = null;
        //result
        BulletPointOperationOut operationOut = inst.handleBulletPointOperation(line, bulletPointStyle, previousBulletPointStyle);
        String modifiedLine = operationOut.modifiedLine();
        String previousBulletStyle = operationOut.previousBulletStyle();
        //test
        String expected = "\\begin{itemize}\n  \\item this is the line";
        assertEquals(expected, modifiedLine);
        assertEquals(bulletPointStyle,previousBulletStyle);
    }

}