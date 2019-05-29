package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jtransforms.fft.DoubleFFT_1D;

public class Dataset {
	public static final String NUMBER_OF_VOXELS_IDENTIFIER_X = "# xnodes: ";
	public static final String NUMBER_OF_VOXELS_IDENTIFIER_Y = "# ynodes: ";
	public static final String NUMBER_OF_VOXELS_IDENTIFIER_Z = "# znodes: ";

	private File directory;
	private int timesteps;
	private int xnodes;
	private int ynodes;
	private int znodes;
	
	private double[][][][][] mListXYZ;
	private double[][][][] mListX;
	private double[][][][] mListY;
	private double[][][][] mListZ;
	
	private double[][][][][] mFFTXYZ;
	private double[][][][] mFFTX;
	private double[][][][] mFFTY;
	private double[][][][] mFFTZ;
	
	private boolean xWasRead = false;
	private boolean yWasRead = false;
	private boolean zWasRead = false;
	private boolean xyzWasRead = false;
	
	private boolean xWasTransformed = false;
	private boolean yWasTransformed = false;
	private boolean zWasTransformed = false;
	private boolean xyzWasTransformed = false;
	
	public Dataset(File directory) {
		super();
		this.directory = directory;
	}

	public Dataset(String directory) {
		super();
		this.directory = new File(directory);

	}
	
	/**
	 * 
	 * @return The Real part of the Fourier transformation for all magnetization components X, Y and Z
	 */
	public double[][][][][] getmFFTReXYZ() {
		double[][][][][] fft = getmFFTXYZ();
		double[][][][][] fftRe = new double[xnodes][ynodes][znodes][3][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftRe[x][y][z][0][i] = fft[x][y][z][0][2*i];
						fftRe[x][y][z][1][i] = fft[x][y][z][1][2*i];
						fftRe[x][y][z][2][i] = fft[x][y][z][1][2*i];
					}
				}
			}
		}
		return fftRe;
	}
	
	/**
	 * 
	 * @return The Imaginary part of the Fourier transformation for all magnetization components X, Y and Z
	 */
	public double[][][][][] getmFFTImXYZ() {
		double[][][][][] fft = getmFFTXYZ();
		double[][][][][] fftIm = new double[xnodes][ynodes][znodes][3][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftIm[x][y][z][0][i] = fft[x][y][z][0][(2*i)+1];
						fftIm[x][y][z][1][i] = fft[x][y][z][1][(2*i)+1];
						fftIm[x][y][z][2][i] = fft[x][y][z][2][(2*i)+1];
					}
				}
			}
		}
		return fftIm;
	}
	
	/**
	 * 
	 * @return The absolute value of the Fourier transformation for all magnetization components X, Y and Z
	 */
	public double[][][][][] getmFFTAbsXYZ() {
		double[][][][][] fft = getmFFTXYZ();
		double[][][][][] fftAbs = new double[xnodes][ynodes][znodes][3][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][0][i] = Math.sqrt((fft[x][y][z][0][2*i]*fft[x][y][z][0][2*i])+(fft[x][y][z][0][(2*i)+1]*fft[x][y][z][0][(2*i)+1]));
						fftAbs[x][y][z][1][i] = Math.sqrt((fft[x][y][z][1][2*i]*fft[x][y][z][1][2*i])+(fft[x][y][z][1][(2*i)+1]*fft[x][y][z][1][(2*i)+1]));
						fftAbs[x][y][z][2][i] = Math.sqrt((fft[x][y][z][2][2*i]*fft[x][y][z][2][2*i])+(fft[x][y][z][2][(2*i)+1]*fft[x][y][z][2][(2*i)+1]));
					}
				}
			}
		}
		return fftAbs;
	}
	

	/**
	 * 
	 * @return The complex Argument of the Fourier transformation for all magnetization components X, Y and Z
	 */
	public double[][][][][] getmFFTArgXYZ() {
		double[][][][][] fft = getmFFTXYZ();
		double[][][][][] fftAbs = new double[xnodes][ynodes][znodes][3][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][0][i] = Math.atan2(fft[x][y][z][0][2*i],fft[x][y][z][0][(2*i)+1]);
						fftAbs[x][y][z][1][i] = Math.atan2(fft[x][y][z][1][2*i],fft[x][y][z][1][(2*i)+1]);
						fftAbs[x][y][z][2][i] = Math.atan2(fft[x][y][z][2][2*i],fft[x][y][z][2][(2*i)+1]);
					}
				}
			}
		}
		return fftAbs;
	}
	
	/**
	 * 
	 * @return The real part of the Fourier transformation for the X component of the magnetization
	 */
	public double[][][][] getmFFTReX() {
		double[][][][] fft = getmFFTX();
		double[][][][] fftRe = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftRe[x][y][z][i] = fft[x][y][z][2*i];
					}
				}
			}
		}
		return fftRe;
	}
	
	/**
	 * 
	 * @return The imaginary part of the Fourier transformation for the X component of the magnetization
	 */
	public double[][][][] getmFFTImX() {
		double[][][][] fft = getmFFTX();
		double[][][][] fftIm = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftIm[x][y][z][i] = fft[x][y][z][(2*i)+1];
					}
				}
			}
		}
		return fftIm;
	}
	
	/**
	 * 
	 * @return The absolute value of the Fourier transformation for the X component of the magnetization
	 */
	public double[][][][] getmFFTAbsX() {
		double[][][][] fft = getmFFTX();
		double[][][][] fftAbs = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][i] = Math.sqrt((fft[x][y][z][2*i]*fft[x][y][z][2*i])+(fft[x][y][z][(2*i)+1]*fft[x][y][z][(2*i)+1]));
}
				}
			}
		}
		return fftAbs;
	}
	

	/**
	 * 
	 * @return The complex Argument of the Fourier transformation for the X component of the magnetization
	 */
	public double[][][][] getmFFTArgX() {
		double[][][][] fft = getmFFTX();
		double[][][][] fftAbs = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][i] = Math.atan2(fft[x][y][z][2*i],fft[x][y][z][(2*i)+1]);
					}
				}
			}
		}
		return fftAbs;
	}
	
	/**
	 * 
	 * @return The Real part of the Fourier transformation for the Y component of the magnetization
	 */
	public double[][][][] getmFFTReY() {
		double[][][][] fft = getmFFTY();
		double[][][][] fftRe = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftRe[x][y][z][i] = fft[x][y][z][2*i];
					}
				}
			}
		}
		return fftRe;
	}
	
	/**
	 * 
	 * @return The Imaginary part of the Fourier transformation for the Y component of the magnetization
	 */
	public double[][][][] getmFFTImY() {
		double[][][][] fft = getmFFTY();
		double[][][][] fftIm = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftIm[x][y][z][i] = fft[x][y][z][(2*i)+1];
					}
				}
			}
		}
		return fftIm;
	}
	
	/**
	 * 
	 * @return The absolute value of the Fourier transformation for the Y component of the magnetization
	 */
	public double[][][][] getmFFTAbsY() {
		double[][][][] fft = getmFFTY();
		double[][][][] fftAbs = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][i] = Math.sqrt((fft[x][y][z][2*i]*fft[x][y][z][2*i])+(fft[x][y][z][(2*i)+1]*fft[x][y][z][(2*i)+1]));
}
				}
			}
		}
		return fftAbs;
	}
	

	/**
	 * 
	 * @return The complex Argument of the Fourier transformation for the Y component of the magnetization
	 */
	public double[][][][] getmFFTArgY() {
		double[][][][] fft = getmFFTY();
		double[][][][] fftAbs = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][i] = Math.atan2(fft[x][y][z][2*i],fft[x][y][z][(2*i)+1]);
					}
				}
			}
		}
		return fftAbs;
	}
	
	/**
	 * 
	 * @return The Real part of the Fourier transformation for the Z component of the magnetization
	 */
	public double[][][][] getmFFTReZ() {
		double[][][][] fft = getmFFTZ();
		double[][][][] fftRe = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftRe[x][y][z][i] = fft[x][y][z][2*i];
					}
				}
			}
		}
		return fftRe;
	}
	
	/**
	 * 
	 * @return The Imaginary part of the Fourier transformation for the Z component of the magnetization
	 */
	public double[][][][] getmFFTImZ() {
		double[][][][] fft = getmFFTZ();
		System.out.println(timesteps);
		double[][][][] fftIm = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftIm[x][y][z][i] = fft[x][y][z][(2*i)+1];
					}
				}
			}
		}
		return fftIm;
	}
	
	/**
	 * 
	 * @return The absolute value of the Fourier transformation for the Z component of the magnetization
	 */
	public double[][][][] getmFFTAbsZ() {
		double[][][][] fft = getmFFTZ();
		System.out.println(timesteps);
		double[][][][] fftAbs = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][i] = Math.sqrt((fft[x][y][z][2*i]*fft[x][y][z][2*i])+(fft[x][y][z][(2*i)+1]*fft[x][y][z][(2*i)+1]));
					}
				}
			}
		}
		return fftAbs;
	}
	

	/**
	 * 
	 * @return The complex Argument of the Fourier transformation for the Z component of the magnetization
	 */
	public double[][][][] getmFFTArgZ() {
		double[][][][] fft = getmFFTZ();
		System.out.println(timesteps);
		double[][][][] fftAbs = new double[xnodes][ynodes][znodes][timesteps];
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					for(int i = 0; i < timesteps; i++)
					{
						fftAbs[x][y][z][i] = Math.atan2(fft[x][y][z][2*i],fft[x][y][z][(2*i)+1]);
					}
				}
			}
		}
		return fftAbs;
	}
	
	public double[][][][][] getmFFTXYZ() {
		if(!xyzWasTransformed)
			transformXYZ();
		return mFFTXYZ;
	}

	public double[][][][] getmFFTX() {
		if(!xWasTransformed)
			transformX();
		return mFFTX;
	}

	public double[][][][] getmFFTY() {
		if(!yWasTransformed)
			transformY();
		return mFFTY;
	}

	public double[][][][] getmFFTZ() {
		if(!zWasTransformed)
			transformZ();
		return mFFTZ;
	}
	
	private void transformXYZ()
	{
		if(!xyzWasRead)
			readXYZ();
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					partTransformXYZ(x,y,z);
				}
			}
		}
		xyzWasTransformed = true;
	}
	
	private void transformX()
	{
		if(!xWasRead)
			readX();
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					partTransformX(x,y,z);
				}
			}
		}
		xWasTransformed = true;
	}
	
	private void transformY()
	{
		if(!yWasRead)
			readY();
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					partTransformY(x,y,z);
				}
			}
		}
		yWasTransformed = true;
	}
	
	private void transformZ()
	{
		if(!zWasRead)
			readZ();
		for(int x = 0; x < xnodes; x++)
		{
			for(int y = 0; y < ynodes; y++)
			{
				for(int z = 0; z < znodes; z++)
				{
					partTransformZ(x,y,z);
				}
			}
		}
		zWasTransformed = true;
	}
	
	private void partTransformXYZ(int x, int y, int z)
	{
		double[] input = mListXYZ[x][y][z][0];
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);
        mFFTXYZ[x][y][z][0] = fft;
        
        double[] input2 = mListXYZ[x][y][z][1];
		DoubleFFT_1D fftDo2 = new DoubleFFT_1D(input2.length);
        double[] fft2 = new double[input2.length * 2];
        System.arraycopy(input2, 0, fft2, 0, input2.length);
        fftDo2.realForwardFull(fft2);
        mFFTXYZ[x][y][z][1] = fft2;
        
		double[] input3 = mListXYZ[x][y][z][2];
		DoubleFFT_1D fftDo3 = new DoubleFFT_1D(input3.length);
        double[] fft3 = new double[input3.length * 2];
        System.arraycopy(input3, 0, fft3, 0, input3.length);
        fftDo3.realForwardFull(fft3);
        mFFTXYZ[x][y][z][2] = fft3;
	}
	
	private void partTransformX(int x, int y, int z)
	{
		double[] input = mListX[x][y][z];
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);
        mFFTX[x][y][z] = fft;
	}
	
	private void partTransformY(int x, int y, int z)
	{
		double[] input = mListY[x][y][z];
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);
        mFFTY[x][y][z] = fft;
	}
	
	public void partTransformZ(int x, int y, int z)
	{
		double[] input = mListZ[x][y][z];
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);
        mFFTZ[x][y][z] = fft;
	}

	private void readXYZ() {
		File[] files = directory.listFiles();
		ArrayList<File> filesList = new ArrayList<File>();
		Pattern p0 = Pattern.compile("m0+0\\.ovf");
		Pattern p = Pattern.compile("m[0-9]+\\.ovf");
		for (File f : files) {
			if (p.matcher(f.getName()).matches() && (!p0.matcher(f.getName()).matches())) {
				filesList.add(f);
			}
		}
		
		int length = filesList.size();
		readHeader(filesList.get(0));
		if (xnodes == 0 || ynodes == 0 || znodes == 0) {
			return;
		} else {
			System.out.println("xnodes: " + xnodes + ", ynodes: " + ynodes + ", znodes: " + znodes + " -- total: " + (xnodes*ynodes*znodes*3*length));
		}
		// xGridposition, yGridposition, zGridposition, Vectorcoordinates, Time
		double[][][][][] mList = new double[xnodes][ynodes][znodes][3][length];
		mFFTXYZ = new double[xnodes][ynodes][znodes][3][2*length];
		timesteps = length;
		// testcode ++
		System.out.println((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
		// testcode --

		int k = 0;
		for (File f : filesList) {
			// testcode ++
			if (k % 100 == 0) {
				System.out.println("index = " + k + ", time = " + (-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
				
				/* Total amount of free memory available to the JVM */
				System.out.println("Free memory (Giga bytes): " + Runtime.getRuntime().freeMemory() / (1024. * 1024. * 1024.));

				/* This will return Long.MAX_VALUE if there is no preset limit */
				long maxMemory = Runtime.getRuntime().maxMemory();
				/* Maximum amount of memory the JVM will attempt to use */
				System.out.println("Maximum memory (Giga bytes): "
						+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / (1024. * 1024. * 1024.)));

				/* Total memory currently in use by the JVM */
				System.out.println("Total memory (Giga bytes): " + Runtime.getRuntime().totalMemory() / (1024. * 1024. * 1024.));
			}
			// testcode --
			BufferedReader br = null;
			String line = "";
			String splitBy = " ";

			try {
				int x = 0;
				int y = 0;
				int z = 0;
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#")) {
						// TODO: Check if Header machtes
						String[] splitLine = line.split(splitBy);
						double mx = Double.parseDouble(splitLine[0]);
						double my = Double.parseDouble(splitLine[1]);
						double mz = Double.parseDouble(splitLine[2]);
						mList[x][y][z][0][k] = mx;
						mList[x][y][z][1][k] = my;
						mList[x][y][z][2][k] = mz;

						x++;
						if (x == xnodes) {
							x = 0;
							y++;
						}
						if (y == ynodes) {
							y = 0;
							z++;
						}
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			k++;
		}
		mListXYZ = mList;
		xyzWasRead = true;
		System.out.println(
				((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis())) + " Seconds, ListSize:" + mList.length);
	}
	
	private void readX() {
		File[] files = directory.listFiles();
		ArrayList<File> filesList = new ArrayList<File>();
		Pattern p0 = Pattern.compile("m0+0\\.ovf");
		Pattern p = Pattern.compile("m[0-9]+\\.ovf");
		for (File f : files) {
			if (p.matcher(f.getName()).matches() && (!p0.matcher(f.getName()).matches())) {
				filesList.add(f);
			}
		}
		
		int length = filesList.size();
		readHeader(filesList.get(0));
		if (xnodes == 0 || ynodes == 0 || znodes == 0) {
			return;
		} else {
			System.out.println("xnodes: " + xnodes + ", ynodes: " + ynodes + ", znodes: " + znodes + " -- total: " + (xnodes*ynodes*znodes*3*length));
		}
		// xGridposition, yGridposition, zGridposition, Vectorcoordinates, Time
		double[][][][] mList = new double[xnodes][ynodes][znodes][length];
		mFFTX = new double[xnodes][ynodes][znodes][2*length];
		timesteps = length;
		// testcode ++
		System.out.println((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
		// testcode --

		int k = 0;
		for (File f : filesList) {
			// testcode ++
			if (k % 100 == 0) {
				System.out.println("index = " + k + ", time = " + (-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
				
				/* Total amount of free memory available to the JVM */
				System.out.println("Free memory (Giga bytes): " + Runtime.getRuntime().freeMemory() / (1024. * 1024. * 1024.));

				/* This will return Long.MAX_VALUE if there is no preset limit */
				long maxMemory = Runtime.getRuntime().maxMemory();
				/* Maximum amount of memory the JVM will attempt to use */
				System.out.println("Maximum memory (Giga bytes): "
						+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / (1024. * 1024. * 1024.)));

				/* Total memory currently in use by the JVM */
				System.out.println("Total memory (Giga bytes): " + Runtime.getRuntime().totalMemory() / (1024. * 1024. * 1024.));
			}
			// testcode --
			BufferedReader br = null;
			String line = "";
			String splitBy = " ";

			try {
				int x = 0;
				int y = 0;
				int z = 0;
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#")) {
						// TODO: Check if Header machtes
						String[] splitLine = line.split(splitBy);
						double mx = Double.parseDouble(splitLine[0]);
						mList[x][y][z][k] = mx;

						x++;
						if (x == xnodes) {
							x = 0;
							y++;
						}
						if (y == ynodes) {
							y = 0;
							z++;
						}
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			k++;
		}
		mListX = mList;
		xWasRead = true;
		System.out.println(
				((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis())) + " Seconds, ListSize:" + mList.length);
	}

	private void readY() {
		File[] files = directory.listFiles();
		ArrayList<File> filesList = new ArrayList<File>();
		Pattern p0 = Pattern.compile("m0+0\\.ovf");
		Pattern p = Pattern.compile("m[0-9]+\\.ovf");
		for (File f : files) {
			if (p.matcher(f.getName()).matches() && (!p0.matcher(f.getName()).matches())) {
				filesList.add(f);
			}
		}
		
		int length = filesList.size();
		readHeader(filesList.get(0));
		if (xnodes == 0 || ynodes == 0 || znodes == 0) {
			return;
		} else {
			System.out.println("xnodes: " + xnodes + ", ynodes: " + ynodes + ", znodes: " + znodes + " -- total: " + (xnodes*ynodes*znodes*3*length));
		}
		// xGridposition, yGridposition, zGridposition, Vectorcoordinates, Time
		double[][][][] mList = new double[xnodes][ynodes][znodes][length];
		mFFTY = new double[xnodes][ynodes][znodes][2*length];
		timesteps = length;
		// testcode ++
		System.out.println((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
		// testcode --

		int k = 0;
		for (File f : filesList) {
			// testcode ++
			if (k % 100 == 0) {
				System.out.println("index = " + k + ", time = " + (-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
				
				/* Total amount of free memory available to the JVM */
				System.out.println("Free memory (Giga bytes): " + Runtime.getRuntime().freeMemory() / (1024. * 1024. * 1024.));

				/* This will return Long.MAX_VALUE if there is no preset limit */
				long maxMemory = Runtime.getRuntime().maxMemory();
				/* Maximum amount of memory the JVM will attempt to use */
				System.out.println("Maximum memory (Giga bytes): "
						+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / (1024. * 1024. * 1024.)));

				/* Total memory currently in use by the JVM */
				System.out.println("Total memory (Giga bytes): " + Runtime.getRuntime().totalMemory() / (1024. * 1024. * 1024.));
			}
			// testcode --
			BufferedReader br = null;
			String line = "";
			String splitBy = " ";

			try {
				int x = 0;
				int y = 0;
				int z = 0;
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#")) {
						// TODO: Check if Header machtes
						String[] splitLine = line.split(splitBy);
						double my = Double.parseDouble(splitLine[1]);
						mList[x][y][z][k] = my;

						x++;
						if (x == xnodes) {
							x = 0;
							y++;
						}
						if (y == ynodes) {
							y = 0;
							z++;
						}
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			k++;
		}
		mListY = mList;
		yWasRead = true;
		//System.gc();
		System.out.println(
				((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis())) + " Seconds, ListSize:" + mList.length);

	}
	
	private void readZ() {
		File[] files = directory.listFiles();
		ArrayList<File> filesList = new ArrayList<File>();
		Pattern p0 = Pattern.compile("m0+0\\.ovf");
		Pattern p = Pattern.compile("m[0-9]+\\.ovf");
		for (File f : files) {
			if (p.matcher(f.getName()).matches() && (!p0.matcher(f.getName()).matches())) {
				filesList.add(f);
			}
		}
		
		int length = filesList.size();
		readHeader(filesList.get(0));
		if (xnodes == 0 || ynodes == 0 || znodes == 0) {
			return;
		} else {
			System.out.println("xnodes: " + xnodes + ", ynodes: " + ynodes + ", znodes: " + znodes + " -- total: " + (xnodes*ynodes*znodes*3*length));
		}
		// xGridposition, yGridposition, zGridposition, Vectorcoordinates, Time
		double[][][][] mList = new double[xnodes][ynodes][znodes][length];
		mFFTZ = new double[xnodes][ynodes][znodes][2*length];
		timesteps = length;
		// testcode ++
		System.out.println((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
		// testcode --

		int k = 0;
		for (File f : filesList) {
			// testcode ++
			if (k % 100 == 0) {
				System.out.println("index = " + k + ", time = " + (-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis()));
				
				/* Total amount of free memory available to the JVM */
				System.out.println("Free memory (Giga bytes): " + Runtime.getRuntime().freeMemory() / (1024. * 1024. * 1024.));

				/* This will return Long.MAX_VALUE if there is no preset limit */
				long maxMemory = Runtime.getRuntime().maxMemory();
				/* Maximum amount of memory the JVM will attempt to use */
				System.out.println("Maximum memory (Giga bytes): "
						+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / (1024. * 1024. * 1024.)));

				/* Total memory currently in use by the JVM */
				System.out.println("Total memory (Giga bytes): " + Runtime.getRuntime().totalMemory() / (1024. * 1024. * 1024.));
			}
			// testcode --
			BufferedReader br = null;
			String line = "";
			String splitBy = " ";

			try {
				int x = 0;
				int y = 0;
				int z = 0;
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#")) {
						// TODO: Check if Header machtes
						String[] splitLine = line.split(splitBy);
						double mz = Double.parseDouble(splitLine[2]);
						mList[x][y][z][k] = mz;

						x++;
						if (x == xnodes) {
							x = 0;
							y++;
						}
						if (y == ynodes) {
							y = 0;
							z++;
						}
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			k++;
		}
		mListZ = mList;
		zWasRead = true;
		System.out.println(
				((-1.0 / 1000) * (Main.StartTime - System.currentTimeMillis())) + " Seconds, ListSize:" + mList.length);

	}
	
	private void readHeader(File f) {
		BufferedReader br = null;
		String line = "";

		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					if (line.contains(NUMBER_OF_VOXELS_IDENTIFIER_X)) {
						xnodes = Integer.parseInt(line.split(NUMBER_OF_VOXELS_IDENTIFIER_X)[1]);
					}
					if (line.contains(NUMBER_OF_VOXELS_IDENTIFIER_Y)) {
						ynodes = Integer.parseInt(line.split(NUMBER_OF_VOXELS_IDENTIFIER_Y)[1]);
					}
					if (line.contains(NUMBER_OF_VOXELS_IDENTIFIER_Z)) {
						znodes = Integer.parseInt(line.split(NUMBER_OF_VOXELS_IDENTIFIER_Z)[1]);
					}
				}
			}

		} catch (

		FileNotFoundException e)

		{
			e.printStackTrace();
		} catch (

		IOException e)

		{
			e.printStackTrace();
		} finally

		{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @return the number of xnodes
	 */
	public int getXnodes() {
		return xnodes;
	}

	/**
	 * @return the number of ynodes
	 */
	public int getYnodes() {
		return ynodes;
	}

	/**
	 * @return the number of znodes
	 */
	public int getZnodes() {
		return znodes;
	}

}
