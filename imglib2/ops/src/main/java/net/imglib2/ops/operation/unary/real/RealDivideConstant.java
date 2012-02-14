/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
 */

package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.ComplexType;

/**
 * Sets the real component of an output complex number to the division of
 * the real component of an input complex number by a constant value. The
 * constant value is specified in the constructor. In the case of division
 * by zero the value is set to a value also specified in the constructor.
 * 
 * @author Barry DeZonia
 * 
 */
public final class RealDivideConstant<I extends ComplexType<I>, O extends ComplexType<O>>
	implements UnaryOperation<I,O>
{
	private final double constant;
	private final double dbzVal;

	public RealDivideConstant(double constant, double dbzVal) {
		this.constant = constant;
		this.dbzVal = dbzVal;
	}

	@Override
	public O compute(I x, O output) {
		if (constant == 0) {
			output.setReal(dbzVal);
		} else { // not dividing by zero
			double value = x.getRealDouble() / constant;
			output.setReal(value);
		}
		return output;
	}

	@Override
	public RealDivideConstant<I,O> copy() {
		return new RealDivideConstant<I,O>(constant, dbzVal);
	}

}