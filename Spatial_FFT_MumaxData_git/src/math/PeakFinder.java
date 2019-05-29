package math;
/**
 * Find Peaks
 * @author Benjamin
 *
 */
public class PeakFinder {
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
	 * I the kernel is of even length or larger than the input, no convolution
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
}
