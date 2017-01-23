// Copyright © Diego Catalano, 2015
// diego.catalano at live.com
//
// Copyright © Andrew Kirillov, 2007-2008
// andrew.kirillov at gmail.com
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

package com.xceptance.xlt.ai.machine_learning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.xceptance.xlt.ai.image.AverageMetric;
import com.xceptance.xlt.ai.image.FastBitmap;
import com.xceptance.xlt.ai.image.PatternHelper;
import com.xceptance.xlt.ai.util.Constants;
import com.xceptance.xlt.ai.util.Helper;

/**
 * Base neural network class.
 * @author Diego Catalano edited by Thomas Volkmann
 */
public abstract class Network implements Serializable
{    
    /**
	 * Auto generated serial number.
	 */
	private static final long serialVersionUID = 1L;

    /**
     * Network's layers count.
     */
    protected int layersCount;
    
    /**
     * Network's layers.
     */
    public Layer layer;

    /**
     * Get Network's inputs count.
     * @return Network's inputs count.
     */
    public int getInputsCount() 
    {
        return inputsCount;
    }
    
    /***
     * Get the average Metric which is saved in the network for further use.
     * @return Map average Metric
     */
    public Map<Integer, AverageMetric> getAverageMetric()
    {
    	return averMet;
    }
    
    /**
     * Get Network's layers count.
     * @return Network's layers count.
     */
    public Layer getLayers() 
    {
        return layer;
    }

    /**
     * Get Network's output vector.
     * @return Network's output vector.
     */
    public ArrayList<Double> getOutput() 
    {
        return output;
    }
    
    /**
     * Initializes a new instance of the Network class.
     * @param inputsCount Network's inputs count.
     */
    protected Network( int inputsCount)
    {    	
        this.inputsCount = Math.max( 1, inputsCount );        
        // create collection of layers
        this.layer 				= Layer.getInstance(inputsCount);
        internalList 			= new ArrayList<>();
        overwatchList 			= new ArrayList<>();
        selfTest 				= true;
        useColor				= Constants.USE_COLOR_FOR_COMPARISON;
        useOriginSize			= Constants.USE_ORIGINAL_SIZE;
        referenceImageHeight 	= Constants.IMAGE_HEIGHT;
        referenceImageWidth 	= Constants.IMAGE_WIDTH;
    }
    
    /**
     * Compute output vector of the network.
     * @param input Input vector.
     * @return Returns network's output vector.
     */
    public ArrayList<Double> Compute(ArrayList<Integer> input)
    {
        // local variable to avoid mutlithread conflicts
		ArrayList<Double> output = new ArrayList<>();

        output.addAll(layer.Compute(input));

        return output;
    }
    
    /**
     * Compute output vector of the network.
     * @param input Input vector.
     * @return String network's output vector summarized.
     */
    public String checkForRecognitionAsString(ArrayList<Integer> input)
    {
		return Helper.numberConverter(layer.computeSum(input));
    }
    
    /**
     * Compute output vector of the network.
     * @param input Input vector.
     * @return double network's output vector summarized.
     */
    public double checkForRecognitionAsDouble(ArrayList<Integer> input)
    {
		return layer.computeSum(input);
    }
    
    /**
     * Set the internal used constants. 
     */
    public void setConstants()
    {
    	Constants.IMAGE_HEIGHT 				= referenceImageHeight;
    	Constants.IMAGE_WIDTH 				= referenceImageWidth;
    	Constants.USE_COLOR_FOR_COMPARISON 	= useColor;
    	Constants.USE_ORIGINAL_SIZE 		= useOriginSize;
    }
    
    /**
     * Check the neural network with already seen pattern for self test, if the {@link Constants #INTENDED_PERCENTAGE_MATCH} is reached the network stop Learning.
     * After finished training mode the network will categorize the screenshots in recognized and unrecognized.
     * @param intendedPercentageMatch Destination value which should the network reach in comparison. 
     * @return selfTest Boolean flag for training or not.
     */
    public boolean onSelfTest(double intendedPercentageMatch, ArrayList<PatternHelper> validationList, boolean flag)
    { 
	    if (internalList.size() > 2 && flag)
	    {
	    	double resultVerfication = 0.0;	    	
		   	for (PatternHelper element : internalList)
		   	{
		   		resultVerfication += layer.computeSum(element.getPatternList());		   		
		   	}	
	   		
	   		double resultValidation = 0.0;
		   	for (PatternHelper element : validationList)
		   	{
		   		resultValidation += layer.computeSum(element.getPatternList());		   		
		   	}		   		
		  			   	
		   	System.out.println("Selftest Value training dir: " + (resultVerfication / internalList.size()));
		   	System.out.println("Selftest Value Validation dir: " + (resultValidation / internalList.size()));
		   	
		   	if ((resultVerfication / internalList.size()) >= intendedPercentageMatch &&
		   		 resultValidation  / validationList.size() < intendedPercentageMatch)
	    	{
		   		internalList.clear();
	    		selfTest = false;
	    		return selfTest;
	    	}  
	    }
	    return selfTest;
    }
    
    /**
     * Scan the given parameter path for images which are allowed after {@link Helper#IMAGE_FILTER} and compare all of them with the internal list.
     * If the images are in the list nothing is done otherwise the HashCode of the images will be added to the list.
     * This method is used for folder oversee.
     * @param path String to the folder to scan
     * @param screenshotName current screenshot ge added before saving in the folder, important for the next use of the network.
     * @return result list with all loaded images.
     */
    public ArrayList<FastBitmap> scanFolderForChanges(String path, String screenshotName)
    {
    	ArrayList<FastBitmap> result = new ArrayList<>();
    	
		File[] list = Helper.scanFolder(path);
		ArrayList<Integer> tempList = new ArrayList<>();
		
		for (File element : list)
		{
			if (!overwatchList.contains(element.getName().hashCode()))
			{
				result.add(Helper.loadImageScaled_FastBitmap(element.getAbsolutePath(), Constants.IMAGE_HEIGHT, Constants.IMAGE_WIDTH));
			}
			tempList.add(element.getName().hashCode());
		}		
		
		overwatchList.clear();		
		overwatchList.add(screenshotName.hashCode());
		overwatchList.addAll(tempList);
		
    	return result;
    }
    
    public ArrayList<FastBitmap> scanFolderForChanges(String path)
    {
    	ArrayList<FastBitmap> result = new ArrayList<>();   
		File[] list = Helper.scanFolder(path);
		
		for (File element : list)
		{
			result.add(Helper.loadImageScaled_FastBitmap(element.getAbsolutePath(), Constants.IMAGE_HEIGHT, Constants.IMAGE_WIDTH));

		}	
    	return result;
    }
    
    /**
     * Internal list for self test.
     * @param list with all processed images.
     */
    public void setInternalList(ArrayList<PatternHelper> list)
    { 
    	if (selfTest)
    	{
		   	for (PatternHelper element : list)
		   	{
		   		if (!internalList.contains(element))
		   		{
		   			internalList.add(element);
		   		}
		    }
    	}
    }
    
    /**
     * Save network to specified file.
     * @param fileName File name to save network into.
     * @param averMetric average metric to save
     */
    public void Save(String fileName, Map<Integer, AverageMetric> averMetric)
    {
    	this.averMet 			= averMetric;
    	useColor 				= Constants.USE_COLOR_FOR_COMPARISON;
    	useOriginSize 			= Constants.USE_ORIGINAL_SIZE;
    	referenceImageHeight 	= Constants.IMAGE_HEIGHT;
    	referenceImageWidth 	= Constants.IMAGE_WIDTH;
    	
        try 
        {        	
        	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(this);            
            out.close();
        } 
        catch (FileNotFoundException ex) 
        {
            ex.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Load network from specified file.
     * @param fileName File name to load network from.
     * @return Returns instance of Network class with all properties initialized from file.
     */
    public Network Load(String fileName)
    {
        Network network = null;
        try 
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            network = (Network)in.readObject();
            in.close();
        } 
        catch (FileNotFoundException ex) 
        {
            ex.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        return network;
    }
    	
    /**
     * Flag for image comparison.
     */
    private boolean useColor;
    
    /**
     * Flag for image scaling.
     */
    private boolean useOriginSize;
    /**
     * Saved average metric of all seen Images.
     */
	private Map<Integer, AverageMetric> averMet;	
    
	/**
	 * Flag if the network is already proper trained.
	 */
	private boolean selfTest;
	
	/**
	 * Folder list which contains Hashvalues from already seen images, in the folder corresponding to the network.
	 */
	private ArrayList<Integer> overwatchList;
	
	/**
	 * Internal Pattern list for self test.
	 */
    private ArrayList<PatternHelper> internalList;
	
    /**
	 * Set image weight for all images in this network, for a better comparing.
	 */
	private int referenceImageWidth;
	
	/**
	 * Set image height for all images in this network, for a better comparing.
	 */
	private int referenceImageHeight;

	/**
     * Network's inputs count.
     */	
    private int inputsCount;

    /**
     * Network's output vector.
     */
    private ArrayList<Double> output;
}