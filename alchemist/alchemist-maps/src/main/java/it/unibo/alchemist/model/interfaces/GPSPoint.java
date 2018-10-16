/*******************************************************************************
 * Copyright (C) 2010-2018, Danilo Pianini and contributors listed in the main
 * project's alchemist/build.gradle file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of the
 * GNU General Public License, with a linking exception, as described in the file
 * LICENSE in the Alchemist distribution's top directory.
 ******************************************************************************/
package it.unibo.alchemist.model.interfaces;

/**
 */
public interface GPSPoint extends GeoPosition, Comparable<GPSPoint> {

    /**
     * @return the time
     */
    Time getTime();

    /**
     * 
     * @param t time to minus to the point's time
     * @return new GPSPoint with the new time
     */
    GPSPoint subtractTime(Time t);

    /**
     * 
     * @param t time to plus to the point's time
     * @return new GPSPoint with the new time
     */
    GPSPoint addTime(Time t);
}
