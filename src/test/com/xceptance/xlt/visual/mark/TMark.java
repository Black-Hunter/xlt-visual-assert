package test.com.xceptance.xlt.visual.mark;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.xceptance.xlt.visualassertion.algorithm.ColorFuzzy;
import com.xceptance.xlt.visualassertion.algorithm.ComparisonAlgorithm;
import com.xceptance.xlt.visualassertion.algorithm.ExactMatch;
import com.xceptance.xlt.visualassertion.util.RectangleMask;

import test.com.xceptance.xlt.visual.ImageTest;
import test.com.xceptance.xlt.visual.TestCompare;

public class TMark extends ImageTest
{
    ComparisonAlgorithm a = new ColorFuzzy(0.1);

    // how should a difference maSked during training
    RectangleMask m = new RectangleMask(10, 10);

    // how difference should maRked in difference file
    int mX = 10;

    int mY = 10;

    TestCompare T;

    @Before
    public void setup()
    {
        T = new TestCompare(a, m, mX, mY);
    }

    /**
     * Test default marking
     * 
     * @throws IOException
     */
    @Test
    public void pixel0x0()
    {
        T.match("mark/white-35x35.png").to("mark/white-35x35-1pixel-0x0.png").isNotEqual().hasMarking("mark/pixel0x0.png");
    }

    /**
     * Test default marking, one pixel in
     * 
     * @throws IOException
     */
    @Test
    public void pixel1x1()
    {
        T.match("mark/white-35x35.png").to("mark/white-35x35-1pixel-1x1.png").isNotEqual().hasMarking("mark/pixel1x1.png");
    }

    /**
     * Test default marking, four pixels of all corners
     * 
     * @throws IOException
     */
    @Test
    public void pixels10x10()
    {
        T.match("mark/white-35x35.png").to("mark/white-35x35-4pixels-10x10.png").isNotEqual().hasMarking("mark/pixels10x10.png");
    }

    /**
     * Pixels in corners and one in the middle, shows smaller rectangles due to image size not being a total of 10.
     * 
     * @throws IOException
     */
    @Test
    public void fivePixelsSmallerRect()
    {
        T.match("mark/white-35x35.png").to("mark/white-35x35-5pixels.png").isNotEqual().hasMarking("mark/fivePixelsSmallerRect.png");
    }

    /**
     * Pixels in corners and one in the middle, shows smaller rectangles due to image size not being a total of 10.
     * 
     * @throws IOException
     */
    @Test
    public void fivePixelsMark5x5()
    {
        T = new TestCompare(new ExactMatch(), m, 5, 5);
        T.match("mark/white-35x35.png").to("mark/white-35x35-5pixels.png").isNotEqual().hasMarking("mark/fivePixelsMark5x5.png");
    }

    /**
     * Pixels in corners and one in the middle, mark every pixel
     * 
     * @throws IOException
     */
    @Test
    public void markPixelsOnly()
    {
        T = new TestCompare(new ExactMatch(), m, 1, 1);
        T.match("mark/white-35x35.png").to("mark/white-35x35-5pixels.png").isNotEqual().hasMarking("mark/markPixelsOnly.png");
    }

    /**
     * Pixels in corners and one in the middle, shows smaller rectangles due to image size not being a total of 10 and
     * not square
     * 
     * @throws IOException
     */
    @Test
    public void markNonSquare()
    {
        T = new TestCompare(new ExactMatch(), m, 5, 3);
        T.match("mark/white-35x35.png").to("mark/white-35x35-5pixels.png").isNotEqual().hasMarking("mark/markNonSquare.png");
    }

    /**
     * Pixels in corners and one in the middle, shows smaller rectangles due to image size not being a total of 10 and
     * not square but very small
     * 
     * @throws IOException
     */
    @Test
    public void markNonSquareClosed()
    {
        T = new TestCompare(new ExactMatch(), m, 2, 2);
        T.match("mark/white-35x35.png").to("mark/white-35x35-5pixels.png").isNotEqual().hasMarking("mark/markNonSquareClosed.png");
    }
}
