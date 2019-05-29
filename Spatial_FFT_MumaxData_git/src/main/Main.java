package main;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
	public static long StartTime;

	public static void main(String[] args) {
		/* Total number of processors or cores available to the JVM */
		System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

		/* Total amount of free memory available to the JVM */
		System.out.println("Free memory (Giga bytes): " + Runtime.getRuntime().freeMemory() / (1024. * 1024. * 1024.));

		/* This will return Long.MAX_VALUE if there is no preset limit */
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		System.out.println("Maximum memory (Giga bytes): "
				+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / (1024. * 1024. * 1024.)));

		/* Total memory currently in use by the JVM */
		System.out
				.println("Total memory (Giga bytes): " + Runtime.getRuntime().totalMemory() / (1024. * 1024. * 1024.));

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = null;
					Controller controller = new Controller(window);
					window = new MainFrame(controller);
					controller.setMainFrame(window);
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/* Get a list of all filesystem roots on this system */
	// File[] roots = File.listRoots();

	/* For each filesystem root, print some info */
	// for (File root : roots) {
	// System.out.println("File system root: " + root.getAbsolutePath());
	// System.out.println("Total space (Giga bytes): " +
	// root.getTotalSpace() / (1024. * 1024. * 1024.));
	// System.out.println("Free space (Giga bytes): " + root.getFreeSpace()
	// / (1024. * 1024. * 1024.));
	// System.out.println("Usable space (Giga bytes): " +
	// root.getUsableSpace() / (1024. * 1024. * 1024.));
	// }

	// @SuppressWarnings("unchecked")
	// DataTable data = new DataTable(Double.class, Double.class);
	//
	// StartTime = System.currentTimeMillis();
	// TableFileProcessor tabProc = new
	// TableFileProcessor("DMI_strength_80e-5.out");
	// Dataset dat = new Dataset("DMI_strength_80e-5.out");
	// try {
	// // Process Table
	// double[] freq = tabProc.getFrequencyTable();
	// double[] fz = tabProc.getmFFTAbsZ();
	// int maxindex = 0;
	// for (int i = 0; i < freq.length; i++) {
	// if (freq[i] < (2 * 30. * 1000000000)) {
	// data.add(freq[i] / (2 * 1000000000.), fz[i]);
	// maxindex = i;
	// }
	// }
	// int partlength = maxindex + 1;
	// double[] fzPart = new double[partlength];
	// for (int i = 0; i < partlength; i++) {
	// fzPart[i] = fz[i];
	// }
	// LinePlotTest frame = new LinePlotTest(data);
	// frame.setVisible(true);

	// int[] peaks = findPeaks(fzPart, gaussianKernel(10, 5), 0.15);
	// double[] test = listConvolve(fzPart, gaussianKernel(10, 5));
	// System.out.println(fzPart.length + ", " + test.length + ", " +
	// test[10]);
	// for (int i = 0; i < peaks.length; i++) {
	// data2.add(freq[peaks[i]] / (2 * 1000000000.), fzPart[peaks[i]]);
	// }
	// LinePlotTest frame2 = new LinePlotTest(data2);
	// frame2.setVisible(true);

	// Process spatial
	// double[][][][] FFTAbsX = dat.getmFFTAbsX();
	// double[][][][] FFTAbsY = dat.getmFFTAbsY();
	// double[][][][] FFTAbsZ = dat.getmFFTAbsZ();
	// double[][][][] FFTArgX = dat.getmFFTArgX();
	// double[][][][] FFTArgY = dat.getmFFTArgY();
	// double[][][][] FFTArgZ = dat.getmFFTArgZ();

	// double[][][][][] FFTXYZ = dat.getmFFTXYZ();
	// double reX = 0;
	// double reY = 0;
	// double reZ = 0;
	//
	// double imX = 0;
	// double imY = 0;
	// double imZ = 0;
	//
	// double absX = 0;
	// double absY = 0;
	// double absZ = 0;
	//
	// double argX = 0;
	// double argY = 0;
	// double argZ = 0;
	//
	// int[] peaks = findPeaks(fzPart, gaussianKernel(10, 5), 0.15);
	// for (int p : peaks) {
	// double frequency = 0.5 * freq[p];
	// String sep = "\t";
	//
	// try (PrintWriter out = new PrintWriter("spatialWave_" + p + "_" +
	// frequency + ".txt")) {
	// String exp = "# Frequency = " + frequency;
	// out.println(exp);
	// exp = "# xIndex" + sep + "yIndex" + sep + "zIndex" + sep + "AbsFmx" + sep
	// + "AbsFmy" + sep
	// + "AbsFmz" + sep + "ArgFmx" + sep + "ArgFmy" + sep + "ArgFmz";
	// out.println(exp);
	// for (int x = 0; x < 128; x++) {
	// for (int y = 0; y < 128; y++) {
	// for (int z = 0; z < 1; z++) {
	// reX = FFTXYZ[x][y][z][0][2 * p];
	// reY = FFTXYZ[x][y][z][1][2 * p];
	// reZ = FFTXYZ[x][y][z][2][2 * p];
	// imX = FFTXYZ[x][y][z][0][(2 * p) + 1];
	// imY = FFTXYZ[x][y][z][1][(2 * p) + 1];
	// imZ = FFTXYZ[x][y][z][2][(2 * p) + 1];
	//
	// absX = Math.sqrt((reX * reX) + (imX * imX));
	// absY = Math.sqrt((reY * reY) + (imY * imY));
	// absZ = Math.sqrt((reZ * reZ) + (imZ * imZ));
	//
	// argX = Math.atan2(reX, imX);
	// argY = Math.atan2(reY, imY);
	// argZ = Math.atan2(reZ, imZ);
	//
	// if (absX != 0 || absY != 0 || absZ != 0) {
	// exp = x + sep + y + sep + z + sep + absX + sep + absY + sep + absZ + sep
	// + argX
	// + sep + argY + sep + argZ;
	// out.println(exp);
	// }
	//
	// }
	// }
	// }
	// out.close();
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// System.out.println((-1.0 / 1000) * (Main.StartTime -
	// System.currentTimeMillis()));
	// }

}
