package Utilities;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.plot.DefaultDrawingSupplier;

public class ChartDrawingSupplier extends DefaultDrawingSupplier  {

    public Paint[] paintSequence;
    public int paintIndex;
    public int fillPaintIndex;

    {
        paintSequence =  new Paint[] {
                new Color(226, 219, 204),
                new Color(95, 114, 148),
                new Color(171, 77, 93),
                new Color(146, 186, 230),
                new Color(0, 51, 102),
        };
    }

    @Override
    public Paint getNextPaint() {
        Paint result
        = paintSequence[paintIndex % paintSequence.length];
        paintIndex++;
        return result;
    }


    @Override
    public Paint getNextFillPaint() {
        Paint result
        = paintSequence[fillPaintIndex % paintSequence.length];
        fillPaintIndex++;
        return result;
    }   
}
