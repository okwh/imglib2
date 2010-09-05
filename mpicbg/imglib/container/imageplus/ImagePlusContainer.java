/**
 * Copyright (c) 2009--2010, Funke, Preibisch, Saalfeld & Schindelin
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.container.imageplus;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.DirectAccessContainerImpl;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableByDimCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableByDimOutOfBoundsCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizablePlaneCursor;
import mpicbg.imglib.exception.ImgLibException;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

/**
 * A {@link Container} that stores data in an aray of 2d-slices each as a
 * linear array of basic types.  For types that are supported by ImageJ (byte,
 * short, int, float), an actual ImagePlus is created or used to store the
 * data.  Alternatively, an {@link ImagePlusContainer} can be created using
 * an already existing {@link ImagePlus} instance. 
 * 
 * {@link ImagePlusContainer ImagePlusContainers} provides a legacy layer to
 * apply imglib-based algorithm implementations directly on the data stored in
 * an ImageJ {@link ImagePlus}.  For all types that are suported by ImageJ, the
 * {@link ImagePlusContainer} provides access to the pixels of an
 * {@link ImagePlus} instance that can be accessed ({@see #getImagePlus()}.
 * 
 * @author Jan Funke, Stephan Preibisch, Stephan Saalfeld, Johannes Schindelin
 */
public class ImagePlusContainer<T extends Type<T>, A extends ArrayDataAccess<A>> extends DirectAccessContainerImpl<T,A>
{
	final protected ImagePlusContainerFactory factory;
	final protected int width, height, depth, frames, channels, slices;

	final ArrayList< A > mirror;

	ImagePlusContainer( final ImagePlusContainerFactory factory, final int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );
		
		if ( dim.length > 0 )
			width = dim[ 0 ];
		else
			width = 1;

		if ( dim.length > 1 )
			height = dim[ 1 ];
		else
			height = 1;

		if ( dim.length > 2 )
			depth = dim[ 2 ];
		else
			depth = 1;

		if ( dim.length > 3 )
			frames = dim[ 3 ];
		else
			frames = 1;

		if ( dim.length > 4 )
			channels = dim[ 4 ];
		else
			channels = 1;
		
		slices = depth * frames * channels;
			
		this.factory = factory;
		
		mirror = new ArrayList<A>( slices );
	}
	
	ImagePlusContainer( final ImagePlusContainerFactory factory, final A creator, final int[] dim, final int entitiesPerPixel ) 
	{
		this( factory, dim, entitiesPerPixel );				
		
		for ( int i = 0; i < slices; ++i )
			mirror.add( creator.createArray( width * height * entitiesPerPixel ));
	}

	public ImagePlus getImagePlus() throws ImgLibException 
	{ 
		throw new ImgLibException( this, "has no ImagePlus instance, it is not a standard type of ImagePlus" ); 
	}

	@Override
	public A update( final Cursor< ? > c )
	{
		return mirror.get( c.getStorageIndex() );
	}
	
	/**
	 * Estimate the minimal required number of dimensions, whereas width and
	 * height are always first.
	 * 
	 * E.g. a gray-scale 2d time series would have three dimensions
	 * [width,height,frames], a gray-scale 3d stack [width,height,depth] and a
	 * 2d composite image [width,height,channels] as well.  A composite 3d
	 * stack has four dimensions [width,height,depth,channels], as a time
	 * series five [width,height,depth,frames,channels].
	 * 
	 * <em>Note that the order of the dimensions is slightly different from
	 * that as used in the data stack in {@link ImagePlus}.  We considered this
	 * more logical since interleaving the color channel in the middle of the
	 * spatial dimensions makes sense only for display purposes but complicates
	 * algorithm logics.  Note as well that this has no consequences for import,
	 * export and processing of the data,
	 * {@link ImagePlusContainer ImagePlusContainers}, internally, work on a
	 * re-ordered list of the {@link ImagePlus} slices.</em> 
	 * 
	 * @param imp
	 * @return
	 */
	protected static int[] reduceDimensions( final ImagePlus imp )
	{
		/* ImagePlus is at least 2d, x,y are mapped to an index on a stack slice */
		int n = 2;
		final int[] impDimensions = imp.getDimensions();
		for ( int d = 2; d < impDimensions.length; ++d )
			if ( impDimensions[ d ] > 1 ) ++n;
		
		final int[] dim = new int[ n ];
		dim[ 0 ] = impDimensions[ 0 ];
		dim[ 1 ] = impDimensions[ 1 ];
		
		n = 1;
		for ( int d = 2; d < impDimensions.length; ++d )
			if ( impDimensions[ d ] > 1 )
				dim[ ++n ] = impDimensions[ d ];
		
		return dim;
	}

	public int getWidth() { return width; }
	
	public int getHeight() { return height; }
	
	public int getChannels() { return channels; }
	
	public int getDepth() { return depth; }
	
	public int getFrames() { return frames; }

	/**
	 * Note: this is NOT the number of z-slices!
	 * 
	 * @return depth * frames * channels which reflects the number of slices
	 * of the {@link ImageStack} used to store pixels in {@link ImagePlus}.
	 */
	public int getSlices() { return slices; }

	/**
	 * For a given >=2d location, estimate the pixel index in the stack slice.
	 * 
	 * @param l
	 * @return
	 */
	public final int getIndex( final int[] l ) 
	{
		if ( numDimensions > 1 )
			return l[ 1 ] * width + l[ 0 ];
		else
			return l[ 0 ];
	}

	@Override
	public Cursor<T> createCursor( final Image<T> image ) 
	{
		return new ImagePlusCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableCursor<T> createLocalizableCursor( final Image<T> image ) 
	{
		return new ImagePlusLocalizableCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor( final Image<T> image ) 
	{
		return new ImagePlusLocalizablePlaneCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( final Image<T> image ) 
	{
		return new ImagePlusLocalizableByDimCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( final Image<T> image, OutOfBoundsStrategyFactory<T> outOfBoundsFactory ) 
	{
		return new ImagePlusLocalizableByDimOutOfBoundsCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer(), outOfBoundsFactory );
	}
	
	public ImagePlusContainerFactory getFactory() { return factory; }

	@Override
	public void close()
	{
		for ( final A array : mirror )
			array.close();
	}
}
