package mpicbg.imglib.algorithm.roi;


import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.NumericType;

public class DirectCrossCorr
	<T extends NumericType<T>, R extends NumericType<R>, S extends NumericType<S>>
		extends ROIAlgorithm<T, S>
{

	
	
	private final Image<R> kernel;
	private final int[] kernelSize;
	private LocalizableByDimCursor<S> outputImageCursor;
	private final LocalizableByDimCursor<R> kernelCursor;
	
	public DirectCrossCorr(final S type, final Image<T> inputImage, final Image<R> kernel)
	{
		this(type, inputImage, kernel, null);
	}
	
	
	public DirectCrossCorr(final S type, final Image<T> inputImage, final Image<R> kernel,
			final OutsideStrategyFactory<T> outsideFactory) {
		super(type, inputImage, kernel.getDimensions(), outsideFactory);
		this.kernel = kernel;
		outputImageCursor = null;
		kernelSize = kernel.getDimensions();
		kernelCursor = kernel.createLocalizableByDimCursor();
		
		setName(inputImage.getName() + " * " + kernel.getName());
	}
	
	private LocalizableByDimCursor<S> getOutputCursor()
	{
		if (outputImageCursor == null)
		{
			outputImageCursor = getOutputImage().createLocalizableByDimCursor();
		}		
		return outputImageCursor;
	}
	
	
	@Override
	protected boolean patchOperation(final int[] position, final RegionOfInterestCursor<T> roiCursor) {
		final LocalizableByDimCursor<S> outCursor = getOutputCursor();
		final int[] pos = new int[outCursor.getNumDimensions()];
		float conv = 0;
		//final S type = outCursor.getImage().createType();
		
		outCursor.setPosition(position);
		
		while(roiCursor.hasNext())
		{
			roiCursor.fwd();
			roiCursor.getPosition(pos);
			kernelCursor.setPosition(pos);
			conv += roiCursor.getType().getReal() * kernelCursor.getType().getReal();					
		}
				
		outCursor.getType().setReal(conv);
		
		return true;
	}

	@Override
	public boolean checkInput() {
		if (kernel.getNumDimensions() == getOutputImage().getNumActiveCursors())
		{
			setErrorMessage("Kernel has different dimensionality than the Image");
			return false;
		}
		else
		{
			return true;
		}
	}

}
