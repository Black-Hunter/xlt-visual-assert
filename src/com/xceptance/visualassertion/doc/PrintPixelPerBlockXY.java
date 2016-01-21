package com.xceptance.visualassertion.doc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.SystemUtils;

import com.xceptance.xlt.visualassertion.ImageComparison;
import com.xceptance.xlt.visualassertion.ImageComparison.Algorithm;

public class PrintPixelPerBlockXY
{

    /**
     * Should illustrate how the pixelPerBlockXY parameter works. Creates a single picture. That picture is a table, to
     * the right the line shifts more, to the bottom the pixelPerBlockXY parameter grows. The current pixelPerBlock
     * value is also printed to the right of the blocks. //TODO
     * 
     * @param args
     */

    private final static File tempDirectory = SystemUtils.getJavaIoTmpDir();

    private static File fileOutput = new File(tempDirectory + "/showPixelsPerBlockP.png");

    private static File fileMarked = new File(tempDirectory + "/marked.png");

    private static File fileTrash = new File(tempDirectory + "/trash.png");

    private static int lineWidth = 50;

    private static int lineShift = 5;

    private static Color backgroundColor = Color.WHITE;

    private static Color lineColor = Color.BLACK;

    final static double pixTolerance = 0.2;

    final static int markingX = 1;

    final static int markingY = 1;

    final static int shiftPixelPerBlock = 5;

    // 100 * 100 pixels per block
    final static int blockWidthAndHeight = 100;

    // 11 blocks per row and column
    final static int blockPerImage = 11;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException
    {

        final BufferedImage outputImage = new BufferedImage(blockWidthAndHeight * blockPerImage, blockWidthAndHeight * blockPerImage,
                                                            BufferedImage.TYPE_INT_ARGB);
        final BufferedImage[] imagesInARow = new BufferedImage[blockPerImage];
        final BufferedImage[] rows = new BufferedImage[blockPerImage];
        final BufferedImage referenceImage = initializeReferenceImage();

        // Do your thing
        for (int iRow = 0; iRow < blockPerImage; iRow++)
        {

            for (int iColumn = 0; iColumn < blockPerImage; iColumn++)
            {

                // create the image to compare
                final BufferedImage imgToComp = initializeImgToCompare(iColumn);

                // Get the marked image and save it to the right position
                final int pixelPerBlockXY = shiftPixelPerBlock * iRow + 1;
                final ImageComparison imagecomparison = new ImageComparison(Algorithm.FUZZY, markingX, markingX, pixelPerBlockXY, 0.00,
                                                                            pixTolerance, false, false, 3, 3, false);
                final boolean result = imagecomparison.isEqual(referenceImage, imgToComp, fileTrash, fileMarked, fileTrash);
                if (!result)
                {
                    imagesInARow[iColumn] = ImageIO.read(fileMarked);
                }
                else
                {
                    imagesInARow[iColumn] = imgToComp;
                }

                imagesInARow[iColumn] = drawBlockBorders(imagesInARow[iColumn]);
                if (pixelPerBlockXY != 1)
                {
                    imagesInARow[iColumn] = drawPixelPerBlockRect(imagesInARow[iColumn], pixelPerBlockXY);
                }
            }

            // merge the images in a row
            rows[iRow] = new BufferedImage(blockWidthAndHeight * blockPerImage, 100, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < imagesInARow.length; i++)
            {
                final Graphics graphicsRow = rows[iRow].getGraphics();
                graphicsRow.drawImage(imagesInARow[i], blockWidthAndHeight * i, 0, null);
                graphicsRow.dispose();
            }
        }

        // merge the rows
        final Graphics graphicsOutputImage = outputImage.getGraphics();
        for (int i = 0; i < rows.length; i++)
        {
            graphicsOutputImage.drawImage(rows[i], 0, blockWidthAndHeight * i, null);
        }
        graphicsOutputImage.dispose();

        ImageIO.write(outputImage, "PNG", fileOutput);
        fileMarked.delete();
        fileTrash.delete();

    }

    private static BufferedImage drawPixelPerBlockRect(final BufferedImage img, final int pixelPerBlockXY)
    {
        final Graphics graphics = img.getGraphics();
        graphics.setColor(Color.GREEN);

        for (int h = 0; h < img.getHeight(); h = h + pixelPerBlockXY)
        {
            for (int w = 0; w < img.getWidth(); w = w + pixelPerBlockXY)
            {

                graphics.drawRect(w, h, pixelPerBlockXY, pixelPerBlockXY);
            }
        }
        graphics.dispose();

        return img;
    }

    private static BufferedImage drawBlockBorders(final BufferedImage img)
    {

        // Paint a rectangle around the block to make it easier to read
        final Graphics graphics = img.getGraphics();
        graphics.setColor(Color.BLUE);
        graphics.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
        graphics.dispose();

        return img;
    }

    private static BufferedImage initializeReferenceImage()
    {
        final BufferedImage reference = new BufferedImage(blockWidthAndHeight, blockWidthAndHeight, BufferedImage.TYPE_INT_ARGB);

        for (int w = 0; w < reference.getWidth(); w++)
        {
            for (int h = 0; h < reference.getHeight(); h++)
            {
                if (h < lineWidth)
                {
                    reference.setRGB(w, h, lineColor.getRGB());
                }
                else
                {
                    reference.setRGB(w, h, backgroundColor.getRGB());
                }
            }
        }

        return reference;
    }

    private static BufferedImage initializeImgToCompare(final int iColumn)
    {
        final BufferedImage imageToCompare = new BufferedImage(blockWidthAndHeight, blockWidthAndHeight, BufferedImage.TYPE_INT_ARGB);

        for (int w = 0; w < imageToCompare.getWidth(); w++)
        {
            for (int h = 0; h < imageToCompare.getHeight(); h++)
            {
                if (iColumn * lineShift <= h && h <= iColumn * lineShift + lineWidth)
                {
                    imageToCompare.setRGB(w, h, lineColor.getRGB());
                }
                else
                {
                    imageToCompare.setRGB(w, h, backgroundColor.getRGB());
                }
            }
        }

        return imageToCompare;
    }
}
