# Spatial_FFT_MumaxData
Each peak in the spectrum of a dynamic simulation has to correspond to a spatial distribution of amplitude and phase; i.e. a mode profile. This software converts time dependent mumax OVF_TEXT output into TSV tables of the mode profile for all magnetization componentes.
It calculates the Fourier components (time domain to frequency) at a given frequency (i.e. phase and amplitude of all magnetization components) in each voxel of a mumax output. The resulting map of FFT information across the sample is the mode profle.

For further information on how to use the software, please see the Example folder