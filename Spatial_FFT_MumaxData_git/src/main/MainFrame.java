package main;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JSpinner;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.DefaultComboBoxModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class MainFrame {

	private JFrame frame;
	private JPanel panel_Center;
	private Controller controller;
	private JSpinner spinner_KernelSize;
	private JSpinner spinner_Sigma;
	private JSpinner spinner_Offset;
	private JSpinner spinner_SpectrumID;
	private JSpinner spinner_MaximumFrequency;
	private JSpinner spinner_MinimumFrequency;
	private JComboBox<String> comboBox_MagnetizationComponent;

	/**
	 * Create the application.
	 */
	public MainFrame(Controller controller) {
		this.controller = controller;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		getFrame().setBounds(100, 100, 450, 300);
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getFrame().getContentPane().setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane_Buttons = new JSplitPane();
		splitPane_Buttons.setResizeWeight(0.6);
		getFrame().getContentPane().add(splitPane_Buttons, BorderLayout.SOUTH);

		JButton btnChooseData = new JButton("Choose Data");
		btnChooseData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose Folders to process");
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				f.setToolTipText(
						"The selected folders must contain a table.txt file and the respective m\\*.ovf files in plain text form each");
				f.setMultiSelectionEnabled(true);
				f.showOpenDialog(frame);
				controller.setFolders(f.getSelectedFiles());
			}
		});
		splitPane_Buttons.setLeftComponent(btnChooseData);

		JButton btnProcess = new JButton("Process");
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] peaks = controller.getPeaksFound();
				if (peaks == null)
					return;
				JOptionPane optionPane = new JOptionPane(
						"Process spatial evaluation for approximately\n" + peaks.length + " peaks\nper spectrum?",
						JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
				JDialog diag = new JDialog(frame, "Start Processing now?", true);
				optionPane.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						String prop = e.getPropertyName();

						if (diag.isVisible() && (e.getSource() == optionPane)
								&& (JOptionPane.VALUE_PROPERTY.equals(prop))) {
							System.out.println(optionPane.getValue());
							// If you were going to check something
							// before closing the window, you'd do
							// it here.
							diag.setVisible(false);
						}
					}
				});
				diag.setContentPane(optionPane);
				diag.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

				diag.pack();
				diag.setLocationRelativeTo(frame);
				diag.setVisible(true);
				
				int value = ((Integer)optionPane.getValue()).intValue();
                if (value == JOptionPane.YES_OPTION) {
                    controller.processData();
                }
			}
		});
		splitPane_Buttons.setRightComponent(btnProcess);

		panel_Center = new JPanel();
		panel_Center.setToolTipText("A 2D XY Plot of the selected Spectrum.");
		getFrame().getContentPane().add(panel_Center, BorderLayout.CENTER);

		JPanel panel_East = new JPanel();
		getFrame().getContentPane().add(panel_East, BorderLayout.EAST);
		panel_East.setLayout(new BorderLayout(0, 0));

		JButton btnFindPeaks = new JButton("Find Peaks");
		panel_East.add(btnFindPeaks, BorderLayout.SOUTH);

		JPanel panel_Params = new JPanel();
		panel_East.add(panel_Params, BorderLayout.CENTER);
		GridBagLayout gbl_panel_Params = new GridBagLayout();
		gbl_panel_Params.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_Params.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_Params.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_Params.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_Params.setLayout(gbl_panel_Params);

		JLabel lblKernelSize = new JLabel("Kernel Size");
		lblKernelSize.setToolTipText(
				"Kernel size for Gaussian convolution in peak finding process. Must be an integer number.");
		lblKernelSize.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblKernelSize = new GridBagConstraints();
		gbc_lblKernelSize.anchor = GridBagConstraints.WEST;
		gbc_lblKernelSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblKernelSize.gridx = 0;
		gbc_lblKernelSize.gridy = 0;
		panel_Params.add(lblKernelSize, gbc_lblKernelSize);

		spinner_KernelSize = new JSpinner();
		spinner_KernelSize.setEnabled(false);
		spinner_KernelSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				controller.setKernelSize((Integer) spinner_KernelSize.getValue());
			}
		});
		spinner_KernelSize.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_spinner_KernelSize = new GridBagConstraints();
		gbc_spinner_KernelSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_KernelSize.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_KernelSize.gridx = 1;
		gbc_spinner_KernelSize.gridy = 0;
		panel_Params.add(spinner_KernelSize, gbc_spinner_KernelSize);

		JLabel lblSigma = new JLabel("Sigma");
		lblSigma.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblSigma = new GridBagConstraints();
		gbc_lblSigma.anchor = GridBagConstraints.WEST;
		gbc_lblSigma.insets = new Insets(0, 0, 5, 5);
		gbc_lblSigma.gridx = 0;
		gbc_lblSigma.gridy = 1;
		panel_Params.add(lblSigma, gbc_lblSigma);

		spinner_Sigma = new JSpinner();
		spinner_Sigma.setEnabled(false);
		spinner_Sigma.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				controller.setSigma((Double) spinner_Sigma.getValue());
			}
		});
		spinner_Sigma.setAutoscrolls(true);
		spinner_Sigma.setModel(new SpinnerNumberModel(new Double(0.5), 0.5, null, new Double(0.5)));
		GridBagConstraints gbc_spinner_Sigma = new GridBagConstraints();
		gbc_spinner_Sigma.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_Sigma.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_Sigma.gridx = 1;
		gbc_spinner_Sigma.gridy = 1;
		panel_Params.add(spinner_Sigma, gbc_spinner_Sigma);

		JLabel lblOffset = new JLabel("Offset");
		lblOffset.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblOffset = new GridBagConstraints();
		gbc_lblOffset.anchor = GridBagConstraints.WEST;
		gbc_lblOffset.insets = new Insets(0, 0, 5, 5);
		gbc_lblOffset.gridx = 0;
		gbc_lblOffset.gridy = 2;
		panel_Params.add(lblOffset, gbc_lblOffset);

		spinner_Offset = new JSpinner();
		spinner_Offset.setEnabled(false);
		spinner_Offset.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				controller.setOffset((Double) spinner_Offset.getValue());
			}
		});
		spinner_Offset.setModel(new SpinnerNumberModel(new Double(0.0), null, null, new Double(0.1)));
		GridBagConstraints gbc_spinner_Offset = new GridBagConstraints();
		gbc_spinner_Offset.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_Offset.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_Offset.gridx = 1;
		gbc_spinner_Offset.gridy = 2;
		panel_Params.add(spinner_Offset, gbc_spinner_Offset);

		JLabel lblSpectrumId = new JLabel("Spectrum ID");
		GridBagConstraints gbc_lblSpectrumId = new GridBagConstraints();
		gbc_lblSpectrumId.anchor = GridBagConstraints.WEST;
		gbc_lblSpectrumId.insets = new Insets(0, 0, 5, 5);
		gbc_lblSpectrumId.gridx = 0;
		gbc_lblSpectrumId.gridy = 3;
		panel_Params.add(lblSpectrumId, gbc_lblSpectrumId);

		spinner_SpectrumID = new JSpinner();
		spinner_SpectrumID.setEnabled(false);
		spinner_SpectrumID.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				controller.setSpectrumID((int) spinner_SpectrumID.getValue());
			}
		});
		GridBagConstraints gbc_spinner_SpectrumID = new GridBagConstraints();
		gbc_spinner_SpectrumID.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_SpectrumID.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_SpectrumID.gridx = 1;
		gbc_spinner_SpectrumID.gridy = 3;
		panel_Params.add(spinner_SpectrumID, gbc_spinner_SpectrumID);

		JLabel lblMinFrequencyghz = new JLabel("Min. Frequency [GHz]");
		GridBagConstraints gbc_lblMinFrequencyghz = new GridBagConstraints();
		gbc_lblMinFrequencyghz.anchor = GridBagConstraints.WEST;
		gbc_lblMinFrequencyghz.insets = new Insets(0, 0, 5, 5);
		gbc_lblMinFrequencyghz.gridx = 0;
		gbc_lblMinFrequencyghz.gridy = 4;
		panel_Params.add(lblMinFrequencyghz, gbc_lblMinFrequencyghz);

		spinner_MinimumFrequency = new JSpinner();
		spinner_MinimumFrequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				controller.setMinFrequency((double) spinner_MinimumFrequency.getValue());
			}
		});
		spinner_MinimumFrequency.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		spinner_MinimumFrequency.setEnabled(false);
		GridBagConstraints gbc_spinner_MinimumFrequency = new GridBagConstraints();
		gbc_spinner_MinimumFrequency.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_MinimumFrequency.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_MinimumFrequency.gridx = 1;
		gbc_spinner_MinimumFrequency.gridy = 4;
		panel_Params.add(spinner_MinimumFrequency, gbc_spinner_MinimumFrequency);

		JLabel lblMaxFrequencyghz = new JLabel("Max. Frequency [GHz]");
		GridBagConstraints gbc_lblMaxFrequencyghz = new GridBagConstraints();
		gbc_lblMaxFrequencyghz.anchor = GridBagConstraints.WEST;
		gbc_lblMaxFrequencyghz.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxFrequencyghz.gridx = 0;
		gbc_lblMaxFrequencyghz.gridy = 5;
		panel_Params.add(lblMaxFrequencyghz, gbc_lblMaxFrequencyghz);

		spinner_MaximumFrequency = new JSpinner();
		spinner_MaximumFrequency.setEnabled(false);
		spinner_MaximumFrequency.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				controller.setMaxFrequency((double) spinner_MaximumFrequency.getValue());
			}
		});
		spinner_MaximumFrequency.setModel(new SpinnerNumberModel(30.0, 0.0, null, 1.0));
		spinner_MaximumFrequency.setToolTipText("The maximum freqeuncy to which the spectrum is analyzed and plotted.");
		GridBagConstraints gbc_spinner_MaximumFrequency = new GridBagConstraints();
		gbc_spinner_MaximumFrequency.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_MaximumFrequency.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_MaximumFrequency.gridx = 1;
		gbc_spinner_MaximumFrequency.gridy = 5;
		panel_Params.add(spinner_MaximumFrequency, gbc_spinner_MaximumFrequency);

		JLabel lblMagnetizationComponent = new JLabel("Magnetization component");
		GridBagConstraints gbc_lblMagnetizationComponent = new GridBagConstraints();
		gbc_lblMagnetizationComponent.anchor = GridBagConstraints.EAST;
		gbc_lblMagnetizationComponent.insets = new Insets(0, 0, 0, 5);
		gbc_lblMagnetizationComponent.gridx = 0;
		gbc_lblMagnetizationComponent.gridy = 6;
		panel_Params.add(lblMagnetizationComponent, gbc_lblMagnetizationComponent);

		comboBox_MagnetizationComponent = new JComboBox<String>();
		comboBox_MagnetizationComponent.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				String s = comboBox_MagnetizationComponent.getSelectedItem().toString();
				if (s.equals("x")) {
					controller.setSelected_m_component(Controller.SELECTED_M_COMPONENT_X);
				}
				if (s.equals("y")) {
					controller.setSelected_m_component(Controller.SELECTED_M_COMPONENT_Y);
				}
				if (s.equals("z")) {
					controller.setSelected_m_component(Controller.SELECTED_M_COMPONENT_Z);
				}
			}
		});
		comboBox_MagnetizationComponent.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {

			}
		});
		comboBox_MagnetizationComponent.setToolTipText("The component of the magnetization that will be analyzed.");
		comboBox_MagnetizationComponent.setEnabled(false);
		comboBox_MagnetizationComponent.setModel(new DefaultComboBoxModel<String>(new String[] { "x", "y", "z" }));
		comboBox_MagnetizationComponent.setSelectedIndex(2);
		GridBagConstraints gbc_comboBox_MagnetizationComponent = new GridBagConstraints();
		gbc_comboBox_MagnetizationComponent.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_MagnetizationComponent.gridx = 1;
		gbc_comboBox_MagnetizationComponent.gridy = 6;
		panel_Params.add(comboBox_MagnetizationComponent, gbc_comboBox_MagnetizationComponent);
	}

	/**
	 * @param panel_Center
	 *            the panel_Center to set
	 */
	public void setPanel_Center(JPanel panel_Center) {
		getFrame().getContentPane().remove(this.panel_Center);
		this.panel_Center = panel_Center;
		getFrame().getContentPane().add(panel_Center, BorderLayout.CENTER);
		panel_Center.revalidate();
		panel_Center.repaint();
	}

	public JPanel getPanel_Center() {
		return panel_Center;
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	public JSpinner getSpinner_KernelSize() {
		return spinner_KernelSize;
	}

	public JSpinner getSpinner_Sigma() {
		return spinner_Sigma;
	}

	public JSpinner getSpinner_Offset() {
		return spinner_Offset;
	}

	public JSpinner getSpinner_SpectrumID() {
		return spinner_SpectrumID;
	}

	public JSpinner getSpinner_MaximumFrequency() {
		return spinner_MaximumFrequency;
	}

	public JSpinner getSpinner_MinimumFrequency() {
		return spinner_MinimumFrequency;
	}

	public JComboBox<String> getComboBox_MagnetizationComponent() {
		return comboBox_MagnetizationComponent;
	}
}
