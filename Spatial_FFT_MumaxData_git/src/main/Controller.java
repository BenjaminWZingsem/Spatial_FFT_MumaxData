package main;

import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.SpinnerNumberModel;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import math.PeakFinder;

public class Controller {
	
	public static final int SELECTED_M_COMPONENT_X = 0;
	public static final int SELECTED_M_COMPONENT_Y = 1;
	public static final int SELECTED_M_COMPONENT_Z = 2;
	public static final int SELECTED_M_COMPONENT_DEFAULT = 2;

	private int kernelSize = 1;
	private double sigma = 0.1;
	private double offset = 0.0;
	private int spectrumID = 0;
	private double minFrequency = 0.0;
	private double maxFrequency = 30.0;
	private int[] peaksFound;
	private File[] folders = null;
	private int selected_m_component = Controller.SELECTED_M_COMPONENT_DEFAULT;
	TableFileProcessor tabProc;

	private MainFrame mainFrame;

	/**
	 * @return the mainFrame
	 */
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * @param mainFrame
	 *            the mainFrame to set
	 */
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public Controller(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public int[] findPeaks(double[] data) {

		int[] peaks = PeakFinder.findPeaks(data, PeakFinder.gaussianKernel(kernelSize, sigma), offset);
		return peaks;
	}

	public void processData() {
		if(folders == null)
			return;
		
		Dataset dat;
		
		double[][][][][] FFTXYZ;
		double reX = 0;
		double reY = 0;
		double reZ = 0;

		double imX = 0;
		double imY = 0;
		double imZ = 0;

		double absX = 0;
		double absY = 0;
		double absZ = 0;

		double argX = 0;
		double argY = 0;
		double argZ = 0;
		int[] pe;
		
		for (File f : folders) {
			dat = new Dataset(f);
			tabProc = new TableFileProcessor(f);			
			try {
				// Process Table
				double[] freq = tabProc.getFrequencyTable();
				
				double[] fz;
				
				switch (selected_m_component) {
				case SELECTED_M_COMPONENT_X:
					fz = tabProc.getmFFTAbsX();
					break;
				case SELECTED_M_COMPONENT_Y:
					fz = tabProc.getmFFTAbsY();
					break;
				case SELECTED_M_COMPONENT_Z:
					fz = tabProc.getmFFTAbsZ();
					break;

				default:
					fz = tabProc.getmFFTAbsZ();
					break;
				}
				
				int maxindex = freq.length - 1;
				int minindex = 0;
				for (int i = 0; i < freq.length; i++) {
					if (freq[i] < ( minFrequency * 1000000000)) {
						minindex = i;
					}
					if ((freq[i] > (minFrequency * 1000000000)) && (freq[i] < (maxFrequency * 1000000000))) {
						maxindex = i;
					}
				}
				int partlength = maxindex + 1;
				double[] fzPart = new double[partlength];
				for (int i = minindex; i < partlength; i++) {
					fzPart[i] = fz[i];
				}
				pe = findPeaks(fzPart);
				
				FFTXYZ = dat.getmFFTXYZ();
				
				for (int p : pe) {
					double frequency = freq[p];
					String sep = "\t";

					try (PrintWriter out = new PrintWriter(f.getAbsolutePath() + File.separatorChar + "spatialWave_" + p + "_" + frequency + ".txt")) {
						String exp = "# Frequency = " + frequency;
						out.println(exp);
						exp = "# xIndex" + sep + "yIndex" + sep + "zIndex" + sep + "AbsFmx" + sep + "AbsFmy" + sep
								+ "AbsFmz" + sep + "ArgFmx" + sep + "ArgFmy" + sep + "ArgFmz";
						out.println(exp);
						for (int x = 0; x < dat.getXnodes(); x++) {
							for (int y = 0; y < dat.getYnodes(); y++) {
								for (int z = 0; z < dat.getZnodes(); z++) {
									reX = FFTXYZ[x][y][z][0][2 * p];
									reY = FFTXYZ[x][y][z][1][2 * p];
									reZ = FFTXYZ[x][y][z][2][2 * p];
									imX = FFTXYZ[x][y][z][0][(2 * p) + 1];
									imY = FFTXYZ[x][y][z][1][(2 * p) + 1];
									imZ = FFTXYZ[x][y][z][2][(2 * p) + 1];

									absX = Math.sqrt((reX * reX) + (imX * imX));
									absY = Math.sqrt((reY * reY) + (imY * imY));
									absZ = Math.sqrt((reZ * reZ) + (imZ * imZ));

									argX = Math.atan2(reX, imX);
									argY = Math.atan2(reY, imY);
									argZ = Math.atan2(reZ, imZ);

									if (absX != 0 || absY != 0 || absZ != 0) {
										exp = x + sep + y + sep + z + sep + absX + sep + absY + sep + absZ + sep + argX
												+ sep + argY + sep + argZ;
										out.println(exp);
									}

								}
							}
						}
						out.close();
					}

				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updatePlot() {
		if(folders == null)
			return;
		tabProc = new TableFileProcessor(this.folders[spectrumID]);
		@SuppressWarnings("unchecked")
		DataTable data = new DataTable(Double.class, Double.class);
		try {
			// Process Table
			double[] freq = tabProc.getFrequencyTable();
			double[] fz;
			
			switch (selected_m_component) {
			case SELECTED_M_COMPONENT_X:
				fz = tabProc.getmFFTAbsX();
				break;
			case SELECTED_M_COMPONENT_Y:
				fz = tabProc.getmFFTAbsY();
				break;
			case SELECTED_M_COMPONENT_Z:
				fz = tabProc.getmFFTAbsZ();
				break;

			default:
				fz = tabProc.getmFFTAbsZ();
				break;
			}
			
			
			int maxindex = 0;
			int minindex = 0;
			for (int i = 0; i < freq.length; i++) {
				if (freq[i] < (minFrequency * 1000000000)) {
					minindex = i;
				}
				if ((freq[i] > (minFrequency * 1000000000)) && (freq[i] < (maxFrequency * 1000000000))) {
					data.add(freq[i] / (1000000000.), fz[i]);
					maxindex = i;
				}
			}
			int partlength = maxindex + 1;
			double[] fzPart = new double[partlength];
			for (int i = minindex; i < partlength; i++) {
				fzPart[i] = fz[i];
			}
			int[] peaks = findPeaks(fzPart);
			peaksFound = peaks;

			@SuppressWarnings("unchecked")
			DataTable peaksData = new DataTable(Double.class, Double.class);
			for (int p : peaks) {
				peaksData.add(freq[p] / (1000000000.), fzPart[p]);
			}
			XYPlot plot = new XYPlot(data, peaksData);
			LineRenderer lines = new DefaultLineRenderer2D();

			Color colorData = new Color(0.0f, 0.3f, 1.0f);
			Color colorPeaks = new Color(1.0f, 0.0f, 0.0f);
			lines.setColor(colorData);
			plot.setLineRenderers(data, lines);
			plot.getPointRenderers(data).get(0).setColor(colorData);
			plot.getPointRenderers(peaksData).get(0).setColor(colorPeaks);

			InteractivePanel plotPanel = new InteractivePanel(plot);
			mainFrame.setPanel_Center(plotPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gives a list of the positions at which peaks occur in @param data. the
	 * peaks need to be higher than offset, and survive a convolution
	 * with @param kernel
	 * 
	 * @param data
	 * @param kernel
	 * @param offset
	 * @return the peak positions. -1 If no peaks exist.
	 */
	public static int[] findPeaks(double[] data, double[] kernel, double offset) {
		double[] data1 = listConvolve(data, kernel);
		return findPeaks(data1, offset);
	}

	/**
	 * Gives a list of the positions at which peaks occur in @param data. the
	 * peaks need to be higher than offset
	 * 
	 * @param data
	 * @param offset
	 * @return the peak positions. -1 If no peaks exist.
	 */
	public static int[] findPeaks(double[] data, double offset) {
		int[] tmp = new int[data.length];
		int nPeaks = 0;
		for (int i = 0; i < data.length; i++) {
			if (i == 0) {
				if ((data[i] > offset) && (data[i] > data[i + 1])) {
					tmp[nPeaks] = i;
					nPeaks++;
				}
			} else if (i == data.length - 1) {
				if ((data[i] > offset) && (data[i] > data[i - 1])) {
					tmp[nPeaks] = i;
					nPeaks++;
				}
			} else if ((data[i] > offset) && (data[i] > data[i - 1]) && (data[i] > data[i + 1])) {
				tmp[nPeaks] = i;
				nPeaks++;
			}

		}
		if (nPeaks == 0) {
			int[] re = { -1 };
			return re;
		}
		int[] re = new int[nPeaks];
		for (int i = 0; i < nPeaks; i++) {
			re[i] = tmp[i];
		}
		return re;
	}

	/**
	 * If the kernel is of even length or larger than the input, no convolution
	 * will be performed
	 * 
	 * @param input
	 * @param kernel
	 * @return
	 */
	public static double[] listConvolve(double[] input, double[] kernel) {
		double[] re = new double[input.length];
		if (kernel.length % 2 == 0)
			return input;
		if (kernel.length >= input.length)
			return input;

		int iindex = 0;
		int jindex = 0;
		for (int i = 0; i < input.length; i++) {
			re[i] = 0;
			for (int j = 0; j < kernel.length; j++) {
				jindex = j - ((int) Math.rint(0.5 * kernel.length));
				iindex = (i + jindex) % input.length;
				while (iindex < 0) {
					iindex = (iindex + input.length) % input.length;
				}
				re[i] += input[iindex] * kernel[j];
			}
		}
		return re;
	}

	public static double[] gaussianKernel(int radius, double sigma) {
		double[] re = new double[(2 * radius) + 1];
		for (int i = 0; i < re.length; i++) {
			re[i] = (1 / (Math.sqrt(2 * Math.PI * Math.pow(sigma, 2))))
					* Math.exp(-(Math.pow(i - radius, 2) / (2 * Math.pow(sigma, 2))));
		}
		double norm = total(re);
		for (int i = 0; i < re.length; i++) {
			re[i] = re[i] / norm;
		}
		return re;
	}

	public static double total(double[] array) {
		double re = 0;
		for (int i = 0; i < array.length; i++) {
			re = re + array[i];
		}
		return re;
	}

	public int getKernelSize() {
		return kernelSize;
	}

	public void setKernelSize(int kernelSize) {
		this.kernelSize = kernelSize;
		updatePlot();
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
		updatePlot();
	}

	public int getSpectrumID() {
		return spectrumID;
	}

	public void setSpectrumID(int spectrumID) {
		if (spectrumID <= (getMaxSpectrumID()))
			this.spectrumID = spectrumID;
		updatePlot();
	}

	/**
	 * @return the maximal spectrum ID (folders.length -1)
	 */
	public int getMaxSpectrumID() {
		if (folders == null)
			return 0;
		return folders.length - 1;
	}

	/**
	 * @return the offset
	 */
	public double getOffset() {
		return offset;
	}

	/**
	 * @param offset
	 *            the offset to set
	 */
	public void setOffset(double offset) {
		this.offset = offset;
		updatePlot();
	}

	/**
	 * @return the minFrequency
	 */
	public double getMinFrequency() {
		return minFrequency;
	}

	/**
	 * @param minFrequency
	 *            the minFrequency to set
	 */
	public void setMinFrequency(double minFrequency) {
		this.minFrequency = minFrequency;
		updatePlot();
	}

	/**
	 * @return the maxFrequency
	 */
	public double getMaxFrequency() {
		return maxFrequency;
	}

	/**
	 * @param maxFrequency
	 *            the maxFrequency to set
	 */
	public void setMaxFrequency(double maxFrequency) {
		this.maxFrequency = maxFrequency;
		updatePlot();
	}

	/**
	 * @return the folders
	 */
	public File[] getFolders() {
		return folders;
	}

	/**
	 * @param folders
	 *            the folders to set
	 */
	public void setFolders(File[] folders) {
		this.folders = new File[folders.length];
		ArrayList<File> foldersTMPList = new ArrayList<File>();
		for (File f : folders) {
			foldersTMPList.add(f);
		}
		Collections.sort(foldersTMPList, new AlphanumComparator());
		for (int i = 0; i < foldersTMPList.size(); i++) {
			File f = foldersTMPList.get(i);
			this.folders[i] = f;
		}
		spectrumID = 0;
		updatePlot();
		mainFrame.getSpinner_KernelSize().setEnabled(true);
		mainFrame.getSpinner_Offset().setEnabled(true);
		mainFrame.getSpinner_Sigma().setEnabled(true);
		mainFrame.getSpinner_SpectrumID().setModel(new SpinnerNumberModel(new Integer(0), new Integer(0),
				new Integer(this.getMaxSpectrumID()), new Integer(1)));
		mainFrame.getSpinner_SpectrumID().setEnabled(true);
		mainFrame.getSpinner_MaximumFrequency().setEnabled(true);
		mainFrame.getSpinner_MinimumFrequency().setEnabled(true);
		mainFrame.getComboBox_MagnetizationComponent().setEnabled(true);
	}

	/**
	 * @return the selected_m_component
	 */
	public int getSelected_m_component() {
		return selected_m_component;
	}

	/**
	 * @param selected_m_component the selected_m_component to set
	 */
	public void setSelected_m_component(int selected_m_component) {
		this.selected_m_component = selected_m_component;
		updatePlot();
	}

	/**
	 * @return the peaksFound
	 */
	public int[] getPeaksFound() {
		return peaksFound;
	}

}
