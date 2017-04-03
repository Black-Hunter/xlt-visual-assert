// Copyright © Diego Catalano, 2012-2016
// diego.catalano at live.com
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// Copyright 2017 Thomas Volkmann
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this
// software and associated documentation files (the "Software"), 
// to deal in the Software without restriction, including without limitation the rights 
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
// and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all 
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
// BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
// ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.xceptance.xlt.ai.image;

import com.xceptance.xlt.ai.image.FastBitmap;
import com.xceptance.xlt.ai.image.IApplyInPlace;

/**
 * Base class for image grayscaling.
 * 
 * Supported types: RGB.
 * Coordinate System: Independent.
 * 
 * @author Diego Catalano edited by Thomas Volkmann
 */
public class Grayscale implements IApplyInPlace
{    
    double redCoefficient = 0.2125, greenCoefficient = 0.7154, blueCoefficient = 0.0721;
    
    /**
     * Three methods for grayscale.
     */
    public static enum Algorithm 
    {
        /**
         * (Max(red, green, blue) + Min(red, green, blue)) / 2
         */
        Lightness,
        
        /**
         * (red + green + blue) / 3
         */
        Average,
        
        /**
         * (red * green * blue) ^ 1/3
         */
        GeometricMean,
        
        /**
         * 0.2125R + 0.7154G + 0.0721B
         */
        Luminosity,
        
        /**
         * Min(red, green, max)
         */
        MinimumDecomposition,
        
        /**
         * Max(red, green, blue)
         */
        MaximumDecomposition
    };
    
    /**
     * Initializes a new instance of the Grayscale class.
     * In this constructor, will be 0.2125B + 0.7154G + 0.0721B.
     */
    public Grayscale() 
    {    	
    }

    /**
     * Initializes a new instance of the Grayscale class. 
     * @param redCoefficient Portion of red channel's value to use during conversion from RGB to grayscale. 
     * @param greenCoefficient Portion of green channel's value to use during conversion from RGB to grayscale. 
     * @param blueCoefficient Portion of blue channel's value to use during conversion from RGB to grayscale. 
     */
    public Grayscale(double redCoefficient, double greenCoefficient, double blueCoefficient) 
    {
        this.redCoefficient = redCoefficient;
        this.greenCoefficient = greenCoefficient;
        this.blueCoefficient = blueCoefficient;
        this.isAlgorithm = false;
    }
    
    /**
     * Initializes a new instance of the Grayscale class. 
     * @param grayscaleMethod Methods for grayscaling.
     */
    public Grayscale(Algorithm grayscaleMethod)
    {
        this.grayscaleMethod = grayscaleMethod;
        this.isAlgorithm = true;
    }

    /**
     * Get red coefficient
     * @return red coefficient
     */
    public double getRedCoefficient() 
    {
        return redCoefficient;
    }

    /**
     * Set red coefficient
     * @param redCoefficient red coefficient
     */
    public void setRedCoefficient(double redCoefficient) 
    {
        this.redCoefficient = redCoefficient;
    }

    /**
     * Get green coefficient
     * @return green coefficient
     */
    public double getGreenCoefficient() 
    {
        return greenCoefficient;
    }

    /**
     * Set green coefficient
     * @param greenCoefficient green coefficient
     */
    public void setGreenCoefficient(double greenCoefficient) 
    {
        this.greenCoefficient = greenCoefficient;
    }

    /**
     * Get blue coefficient
     * @return blue coefficient
     */
    public double getBlueCoefficient() 
    {
        return blueCoefficient;
    }

    /**
     * Set blue coefficient
     * @param blueCoefficient blue coefficient
     */
    public void setBlueCoefficient(double blueCoefficient) 
    {
        this.blueCoefficient = blueCoefficient;
    }

    /**
     * Get Grayscale Method
     * @return Grayscale Method
     */
    public Algorithm getGrayscaleMethod()
    {
        return grayscaleMethod;
    }

    /**
     * Set Grayscale Method
     * @param grayscaleMethod Grayscale Method
     */
    public void setGrayscaleMethod(Algorithm grayscaleMethod) 
    {
        this.grayscaleMethod = grayscaleMethod;
    }
    
    /**
     * Apply filter to a FastBitmap.
     * @param fastBitmap Image to be processed.
     */
    @Override
    public void applyInPlace(FastBitmap fastBitmap)
    {
    	if(!isAlgorithm)
    	{
    		double r,g,b,gray;
            FastBitmap fb = new FastBitmap(fastBitmap.getWidth(), fastBitmap.getHeight(), FastBitmap.ColorSpace.Grayscale);                
            int[] pixelsRGB = fastBitmap.getRGBData();
            byte[] pixelsG = fb.getGrayData();
            
            for (int i = 0; i < pixelsG.length; i++) 
            {
            	r = pixelsRGB[i] >> 16 & 0xFF;
                g = pixelsRGB[i] >> 8 & 0xFF;
                b = pixelsRGB[i] & 0xFF;
                    
                gray = (r*redCoefficient+g*greenCoefficient+b*blueCoefficient);                    
                pixelsG[i] = (byte)gray;
            }
                
            fb.setGrayData(pixelsG);
            fastBitmap.setImage(fb);
    	}
        else
        {
        	Apply(fastBitmap, this.grayscaleMethod);
        }
    }
    
    /***
     * Used the chosen algorithm on the image.
     * @param fastBitmap used image where the transformation is applied on
     * @param grayMethod choose of the enum Algorithm 
     */
    private void Apply(FastBitmap fastBitmap, Algorithm grayMethod)
    {
    	double r,g,b,gray;
    	FastBitmap fb = new FastBitmap(fastBitmap.getWidth(), fastBitmap.getHeight(), FastBitmap.ColorSpace.Grayscale);
    	int[] pixelsRGB = fastBitmap.getRGBData();
    	byte[] pixelsG = fb.getGrayData();

    	switch(grayMethod)
    	{
    		case Lightness:
    			double max,min;
                for (int i = 0; i < pixelsG.length; i++) 
                {
                	r = pixelsRGB[i] >> 16 & 0xFF;
                    g = pixelsRGB[i] >> 8 & 0xFF;
                    b = pixelsRGB[i] & 0xFF;

                    max = Math.max(r, g);
                    max = Math.max(max, b);
                    min = Math.min(r, g);
                    min = Math.min(min, b);
                    gray = (max+min)/2;

                    pixelsG[i] = (byte)gray;
                }
                break;

    		case Average:
    			for (int i = 0; i < pixelsG.length; i++) 
                {
                  	r = pixelsRGB[i] >> 16 & 0xFF;
                    g = pixelsRGB[i] >> 8 & 0xFF;
                    b = pixelsRGB[i] & 0xFF;
                        
                    gray = (r+g+b) / 3;
                    pixelsG[i] = (byte)gray;
                }
                break;
                    
    		case GeometricMean:
    			for (int i = 0; i < pixelsG.length; i++) 
    			{
    				r = pixelsRGB[i] >> 16 & 0xFF;
                    g = pixelsRGB[i] >> 8 & 0xFF;
                    b = pixelsRGB[i] & 0xFF;

                    gray = Math.pow(r*g*b,0.33);
                    pixelsG[i] = (byte)gray;
    			}
                break;

    		case Luminosity:
    			for (int i = 0; i < pixelsG.length; i++) 
    			{
    				r = pixelsRGB[i] >> 16 & 0xFF;
                    g = pixelsRGB[i] >> 8 & 0xFF;
                    b = pixelsRGB[i] & 0xFF;

                    gray = (r*0.2125+g*0.7154+b*0.0721);
                    pixelsG[i] = (byte)gray;
                }
                break;
                    
    		case MinimumDecomposition:
    			for (int i = 0; i < pixelsG.length; i++) 
    			{
    				gray = Math.min(pixelsRGB[i] >> 16 & 0xFF, pixelsRGB[i] >> 8 & 0xFF);
                    gray = Math.min(gray, pixelsRGB[i] & 0xFF);

                    pixelsG[i] = (byte)gray;
    			}
    			break;
                    
    		case MaximumDecomposition:
    			for (int i = 0; i < pixelsG.length; i++) 
    			{
    				gray = Math.max(pixelsRGB[i] >> 16 & 0xFF, pixelsRGB[i] >> 8 & 0xFF);
    				gray = Math.max(gray, pixelsRGB[i] & 0xFF);

    				pixelsG[i] = (byte)gray;
    			}
                break;
    	}            
    	fastBitmap.setImage(fb);
    }
    
    /***
     * Enum which holds the gray scale method.
     */
    private Algorithm grayscaleMethod;
    
    /***
     * Flag for algorithm choose.
     */
    private boolean isAlgorithm = false;
}