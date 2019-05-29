=Run=
Compile and run the Java program, press "Choose Data", select navigate to this folder, select all folders and press open. 
Choose parameters for peak detection and press process. The sofware will then generate TSV files in *.txt format inside the .out folders.
These files contain phase and amplitude information of all vector components of the magnetization. 
1 file will be generated for each detected peak.

Suitable parameters for the given example:
Kernel Size: 2
Sigma: 0,5
Offset: 1
Min Frequency [GHz]: 5
Max Frequency [GHz]: 15

=Evaluate=
The Filename of the generated output has the form "spatialWave_156_1.0419620409696295E10.txt", where 156 denotes the index in the FFT of
the table data, at which the resonance is located and 1.0419620409696295E10 is its frequency in Hz. 
The content of each file contains only the voxels within which nonzero dynamic amplitude was measured. therefore each row in the TSV file
first states the indices of a given cell, and then the Fourier components of the magnetization on that cell.

=Optional=

The example data can be recalculated:

Windows:
If mumax is installed and added to the PATH "mumax3" or "mumax3.exe", simply run "runMumax.bat" to generate output.
Other
Succesively launch instances of mumax to process the provided mx3 files.