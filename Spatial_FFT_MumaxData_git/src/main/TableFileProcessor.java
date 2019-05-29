package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jtransforms.fft.DoubleFFT_1D;
/**
 * 
 * @author Benjamin
 *Process Data
 */
public class TableFileProcessor {
	private File directory;
	private String[] header;
	private File tableFile;

	private int timesteps;

	private double[] time;
	private double[] frequency;

	private double[] mx;
	private double[] my;
	private double[] mz;

	private double[] fmx;
	private double[] fmy;
	private double[] fmz;
	
	boolean wasRead = false;
	boolean xWasTransformed = false;
	boolean yWasTransformed = false;
	boolean zWasTransformed = false;

	public TableFileProcessor(File directory) {
		super();
		this.directory = directory;
	}

	public TableFileProcessor(String directory) {
		super();
		this.directory = new File(directory);
	}
	
	/**
	 * 
	 * @return The real part of the Fourier transformation for the X component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTReX() throws Exception {
		double[] fft = getmFFTX();
		double[] fftRe = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftRe[i] = fft[2*i];
		
		}
		return fftRe;
	}
	
	/**
	 * 
	 * @return The imaginary value of the Fourier transformation for the X component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTImX() throws Exception {
		double[] fft = getmFFTX();
		double[] fftIm = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftIm[i] = fft[(2*i)+1];
		
		}
		return fftIm;
	}
	
	/**
	 * 
	 * @return The absolute value of the Fourier transformation for the X component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTAbsX() throws Exception {
		double[] fft = getmFFTX();
		double[] fftAbs = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftAbs[i] = Math.sqrt((fft[2*i]*fft[2*i])+(fft[(2*i)+1]*fft[(2*i)+1]));
		
		}
		return fftAbs;
	}
	
	/**
	 * 
	 * @return The complex argument of the Fourier transformation for the X component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTArgX() throws Exception {
		double[] fft = getmFFTX();
		double[] fftArg = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftArg[i] = Math.atan2(fft[2*i],fft[(2*i)+1]);
		
		}
		return fftArg;
	}
	
	/**
	 * 
	 * @return The real part of the Fourier transformation for the Y component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTReY() throws Exception {
		double[] fft = getmFFTY();
		double[] fftRe = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftRe[i] = fft[2*i];
		
		}
		return fftRe;
	}
	
	/**
	 * 
	 * @return The imaginary value of the Fourier transformation for the Y component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTImY() throws Exception {
		double[] fft = getmFFTY();
		double[] fftIm = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftIm[i] = fft[(2*i)+1];
		
		}
		return fftIm;
	}
	
	/**
	 * 
	 * @return The absolute value of the Fourier transformation for the Y component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTAbsY() throws Exception {
		double[] fft = getmFFTY();
		double[] fftAbs = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftAbs[i] = Math.sqrt((fft[2*i]*fft[2*i])+(fft[(2*i)+1]*fft[(2*i)+1]));
		
		}
		return fftAbs;
	}
	
	/**
	 * 
	 * @return The complex argument of the Fourier transformation for the Y component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTArgY() throws Exception {
		double[] fft = getmFFTY();
		double[] fftArg = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftArg[i] = Math.atan2(fft[2*i],fft[(2*i)+1]);
		
		}
		return fftArg;
	}
	
	/**
	 * 
	 * @return The real part of the Fourier transformation for the Z component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTReZ() throws Exception {
		double[] fft = getmFFTZ();
		double[] fftRe = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftRe[i] = fft[2*i];
		
		}
		return fftRe;
	}
	
	/**
	 * 
	 * @return The imaginary value of the Fourier transformation for the Z component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTImZ() throws Exception {
		double[] fft = getmFFTZ();
		double[] fftIm = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftIm[i] = fft[(2*i)+1];
		
		}
		return fftIm;
	}
	
	/**
	 * 
	 * @return The absolute value of the Fourier transformation for the Z component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTAbsZ() throws Exception {
		double[] fft = getmFFTZ();
		double[] fftAbs = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftAbs[i] = Math.sqrt((fft[2*i]*fft[2*i])+(fft[(2*i)+1]*fft[(2*i)+1]));
		
		}
		return fftAbs;
	}
	
	/**
	 * 
	 * @return The complex argument of the Fourier transformation for the Z component of the magnetization
	 * @throws Exception 
	 */
	public double[] getmFFTArgZ() throws Exception {
		double[] fft = getmFFTZ();
		double[] fftArg = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			fftArg[i] = Math.atan2(fft[2*i],fft[(2*i)+1]);
		
		}
		return fftArg;
	}
		
	public double[] getmFFTX() throws Exception {
		if(!xWasTransformed)
			transformX();
		return fmx;
	}
	
	public double[] getmFFTY() throws Exception {
		if(!yWasTransformed)
			transformY();
		return fmy;
	}
	
	public double[] getmFFTZ() throws Exception {
		if(!zWasTransformed)
			transformZ();
		return fmz;
	}
	
	public double[] getFrequencyTable() throws Exception
	{
		if(!wasRead)
			process();
		return frequency;
	}
	
	private void transformX() throws Exception
	{
		if(!wasRead)
			process();
		double[] input = mx;
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);
        fmx = fft;
        xWasTransformed = true;
	}
	
	private void transformY() throws Exception
	{
		if(!wasRead)
			process();
		double[] input = my;
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);
        fmy = fft;
        yWasTransformed = true;
	}
	
	private void transformZ() throws Exception
	{
		if(!wasRead)
			process();
		double[] input = mz;
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);
        fmz = fft;
        zWasTransformed = true;
	}
	
	private void process() throws Exception {
		File[] files = directory.listFiles();
		File tFile = null;
		Pattern p = Pattern.compile("table\\.txt");
		for (File f : files) {
			if (p.matcher(f.getName()).matches()) {
				tFile = f;
				break;
			}
		}
		if (tFile == null)
			throw new Exception("No table.txt File");
		this.tableFile = tFile;
		readHeader();
		read();
		wasRead = true;
	}

	private void read() {
		BufferedReader br = null;
		String line = "";
		String splitBy = "\t";
		ArrayList<String[]> rawData = new ArrayList<String[]>();
		try {
			br = new BufferedReader(new FileReader(tableFile));
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					rawData.add(line.split(splitBy));
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
		timesteps = rawData.size();
		System.out.println(timesteps);
		mx = new double[timesteps];
		my = new double[timesteps];
		mz = new double[timesteps];
		time = new double[timesteps];

		int n = 0;
		int tindex = 0;
		int mxindex = 1;
		int myindex = 2;
		int mzindex = 3;

		for (String s : header) {
			if (s.startsWith("t ")) {
				tindex = n;
			}
			if (s.startsWith("mx ")) {
				mxindex = n;
			}
			if (s.startsWith("my ")) {
				myindex = n;
			}
			if (s.startsWith("mz ")) {
				mzindex = n;
			}
			n++;
		}
		//System.out.println(tindex + ", " + mxindex + ", " + myindex + ", " + mzindex);
		String[] ss;
		for (int i = 0; i < timesteps; i++) {
			System.out.println(i);
			ss = rawData.get(i);
			time[i] = Double.parseDouble(ss[tindex]);
			/*Make time go backwards:
			mx[timesteps - (i+1)] = Double.parseDouble(ss[mxindex]);
			my[timesteps - (i+1)] = Double.parseDouble(ss[myindex]);
			mz[timesteps - (i+1)] = Double.parseDouble(ss[mzindex]);
			*/
			mx[i] = Double.parseDouble(ss[mxindex]);
			my[i] = Double.parseDouble(ss[myindex]);
			mz[i] = Double.parseDouble(ss[mzindex]);
		}
		double sampleRate = (1.0*timesteps)/(time[timesteps-1]);
		double increment = sampleRate/(1.0*timesteps);
		double[] freq = new double[timesteps];
		for(int i = 0; i < timesteps; i++)
		{
			freq[i] = i*increment;
		}
		frequency = freq;
	}

	private void readHeader() {
		BufferedReader br = null;
		String line = "";
		String splitBy = "\t";

		try {
			br = new BufferedReader(new FileReader(tableFile));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("# ")) {
					header = line.split("# ")[1].split(splitBy);
				} else if (line.startsWith("#")) {
					header = line.split("#")[1].split(splitBy);
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
}
