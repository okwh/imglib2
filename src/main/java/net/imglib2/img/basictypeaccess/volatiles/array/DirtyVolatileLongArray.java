/*
 * #%L
 * BigDataViewer core classes with minimal dependencies
 * %%
 * Copyright (C) 2012 - 2016 Tobias Pietzsch, Stephan Saalfeld, Stephan Preibisch,
 * Jean-Yves Tinevez, HongKee Moon, Johannes Schindelin, Curtis Rueden, John Bogovic
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.img.basictypeaccess.volatiles.array;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.array.AbstractLongArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileLongAccess;

/**
 * A {@link LongArray} with an {@link #isDirty()} and an {@link #isValid()}
 * flag.
 *
 * @author Stephan Saalfeld
 */
public class DirtyVolatileLongArray extends AbstractLongArray< DirtyVolatileLongArray > implements VolatileLongAccess, Dirty
{
	final protected boolean isValid;

	protected boolean dirty = false;

	public DirtyVolatileLongArray( final int numEntities, final boolean isValid )
	{
		super( numEntities );
		this.isValid = isValid;
		this.data = new long[ numEntities ];
	}

	public DirtyVolatileLongArray( final long[] data, final boolean isValid )
	{
		super( data );
		this.isValid = isValid;
	}

	@Override
	public void setValue( final int index, final long value )
	{
		dirty = true;
		data[ index ] = value;
	}

	@Override
	public DirtyVolatileLongArray createArray( final int numEntities )
	{
		return new DirtyVolatileLongArray( numEntities, true );
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}
}
