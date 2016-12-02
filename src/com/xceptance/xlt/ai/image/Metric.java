package com.xceptance.xlt.ai.image;

import java.io.Serializable;
import java.util.ArrayList;

import com.xceptance.xlt.ai.core.FloatPoint;
import com.xceptance.xlt.ai.core.IntPoint;
import com.xceptance.xlt.ai.util.Constants;
import com.xceptance.xlt.ai.core.FeaturePoint;

/***
 * Represent the computed metric, which is created out of the found group.
 * A group represent all found corners. 
 * @author Thomas Volkmann
 */
public class Metric implements Serializable, Comparable<Metric>
{		
	/**
	 * Auto generated serial.
	 */
	private static final long serialVersionUID = 1L;
	
	/***
	 * Constructor which automatically generate the metric with the given parameter.
	 * Stand for the chosen identification points of the group.
	 * @param group found corners.
	 */
	public Metric(ArrayList<FeaturePoint> group, FastBitmap image)
	{
		groupSize 		= group.size();	
		boundingBox 	= new ArrayList<>();
		centerOfGravity = new FloatPoint();
		coordOrigin		= new IntPoint(0,0);
		histo			= null;	
		imageStat		= null;
		startMetricCalculation(group, image);
	}
	
	/***
	 * Calculate the metric for each group. Calculated metrics are:
	 * boundingBox (distance of the farthest points in the group)
	 * centerOfGravity (most central point of all FeaturePoints in a group) 
	 * @param group identified corners represented by FeaturePoints
	 */
    private void startMetricCalculation(ArrayList<FeaturePoint> group, FastBitmap image)
    { 
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        float xSum = 0, ySum = 0;

        for ( FeaturePoint pt : group )
        {
            int x = pt.x;
            int y = pt.y;
            
            xSum += x;
            ySum += y;       

            // check X coordinate
            if ( x < minX )
                minX = x;
            if ( x > maxX )
                maxX = x;

            // check Y coordinate
            if ( y < minY )
                minY = y;
            if ( y > maxY )
                maxY = y;
        }
        
        boundingBox.add(new FeaturePoint( minX, minY ));
        boundingBox.add(new FeaturePoint( maxX, maxY ));
        
        if (Constants.USE_COLOR_FOR_COMPARISON)
        {
        	imageStat = new ImageStatistics(image, Constants.BINSIZE, boundingBox.get(0), boundingBox.get(1));
        }
        // most central point of all points
        centerOfGravity.x = xSum / group.size();
        centerOfGravity.y = ySum / group.size();
    }

    /***
     * Get the nearest point of the group in compare to the coordinate origin.
     * @return FeaturePoint minimum.
     */
	public FeaturePoint getBoundingBoxMin() 
	{
		return boundingBox.get(0);
	}
	
	/***
	 * Get the farthest point of the group in compare to the coordinate origin.
     * @return FeaturePoint maximum	 
	 */
	public FeaturePoint getBoundingBoxMax()
	{
		return boundingBox.get(1);
	}
	
    /***
     * Vector value of the distance in respect to the coordinate origin, take the nearest point.
     * @return double Euclidean minimum distance.
     */
	public double getMinDistanceToZero() 
	{
		return boundingBox.get(0).toIntPoint().DistanceTo(coordOrigin);
	}
	
    /***
     * Vector value of the distance in respect to the coordinate origin, take the farthest point
     * @return double Euclidean maximum distance.
     */
	public double getMaxDistanceToZero()
	{
		return boundingBox.get(1).toIntPoint().DistanceTo(coordOrigin);
	}
	
	/***
	 * The Distance from one side of the group to the most distant other side. 
	 * @return double Euclidean distance.
	 */
	public double getBoundingBoxDistance()
	{
		return boundingBox.get(0).toIntPoint().DistanceTo(boundingBox.get(1).toIntPoint());
	}

	/**
	 * Get the central point of the group.
	 * @return FloatPoint centerOfGravity
	 */
	public FloatPoint getCenterOfGravity() 
	{
		return centerOfGravity;
	}

	/**
	 * Size of the group.
	 * @return int groupSize
	 */
	public int getGroupSize() 
	{
		return groupSize;
	}
	
	/***
	 * Calculated group statistic.
	 * @return imageStat Statistic of the group
	 */
	public ImageStatistics getImageStatistic()
	{
		return imageStat;
	}
	
	/***
	 * Hold two elements of FeaturePoints (max and min).
	 */
	private ArrayList<FeaturePoint> boundingBox;
	
	/***
	 * Center of the group.
	 */
	private FloatPoint centerOfGravity;
	
	/***
	 * Size of the group.
	 */
	private int groupSize;
	
	/***
	 * Coordination origin in screen coordinates.
	 */
	private final IntPoint coordOrigin;
	
	/***
	 * Contains all statistic calculated information for a group.
	 */
	private ImageStatistics imageStat;	
	
	/***
	 * Holds the histogram informations from the region created by the bounding box.
	 */
	public final ImageStatistics histo;

	/***
	 * Metric comparison in reference to another metric with groupSize.
	 */
	@Override
	public int compareTo(Metric o) 
	{
		if (o.groupSize < this.groupSize) return 1;
	    else if (o.groupSize == this.groupSize) return 0;
	    else return -1;		
	}
}
