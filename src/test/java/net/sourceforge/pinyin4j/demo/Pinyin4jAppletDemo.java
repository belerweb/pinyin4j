/**
 * This file is part of pinyin4j (http://sourceforge.net/projects/pinyin4j/) and distributed under
 * GNU GENERAL PUBLIC LICENSE (GPL).
 * 
 * pinyin4j is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * pinyin4j is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with pinyin4j.
 */

package net.sourceforge.pinyin4j.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free
 * for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using
 * Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS
 * CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * A demo show the functions of pinyin4j library
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
public class Pinyin4jAppletDemo extends JApplet {
  private static final Dimension APP_SIZE = new Dimension(600, 400);

  private static final long serialVersionUID = -1934962385592030162L;

  private JPanel jContentPane = null;

  private JTabbedPane jTabbedPane = null;

  private JPanel formattedCharPanel = null;

  private JPanel optionPanel = null;

  private JButton convertButton = null;

  private JPanel buttonPanel = null;

  private JTextArea formattedOutputField = null;

  private JComboBox toneTypesComboBox = null;

  private JComboBox vCharTypesComboBox = null;

  private JComboBox caseTypesComboBox = null;

  private static String appName = "pinyin4j-2.0.0 applet demo";

  /**
   * This method initializes charTextField
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getCharTextField() {
    if (charTextField == null) {
      charTextField = new JTextField();
      charTextField.setFont(new Font("Dialog", Font.PLAIN, 12)); // Generated
      charTextField.setText("和"); // Generated
      charTextField.setPreferredSize(new Dimension(26, 20)); // Generated
    }
    return charTextField;
  }

  /**
   * This method initializes unformattedCharPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getUnformattedCharPanel() {
    if (unformattedCharPanel == null) {
      unformattedHanyuPinyinLabel = new JLabel();
      unformattedHanyuPinyinLabel.setText("Hanyu Pinyin"); // Generated
      GridLayout gridLayout = new GridLayout();
      gridLayout.setRows(2); // Generated
      gridLayout.setHgap(1); // Generated
      gridLayout.setVgap(1); // Generated
      gridLayout.setColumns(3); // Generated
      unformattedCharPanel = new JPanel();
      unformattedCharPanel.setLayout(gridLayout); // Generated
      unformattedCharPanel.add(getUnformattedHanyuPinyinPanel(), null); // Generated
      unformattedCharPanel.add(getUnformattedTongyongPinyinPanel(), null); // Generated
      unformattedCharPanel.add(getUnformattedWadePinyinPanel(), null); // Generated
      unformattedCharPanel.add(getUnformattedMPS2PinyinPanel(), null); // Generated
      unformattedCharPanel.add(getUnformattedYalePinyinPanel(), null); // Generated
      unformattedCharPanel.add(getUnformattedGwoyeuRomatzyhPanel(), null); // Generated
    }
    return unformattedCharPanel;
  }

  /**
   * This method initializes unformattedHanyuPinyinTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getUnformattedHanyuPinyinTextArea() {
    if (unformattedHanyuPinyinTextArea == null) {
      unformattedHanyuPinyinTextArea = new JTextArea();
      unformattedHanyuPinyinTextArea.setEditable(false); // Generated
      unformattedHanyuPinyinTextArea.setLineWrap(true); // Generated
    }
    return unformattedHanyuPinyinTextArea;
  }

  /**
   * This method initializes unformattedHanyuPinyinPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getUnformattedHanyuPinyinPanel() {
    if (unformattedHanyuPinyinPanel == null) {
      unformattedHanyuPinyinPanel = new JPanel();
      unformattedHanyuPinyinPanel.setLayout(new BorderLayout()); // Generated
      unformattedHanyuPinyinPanel.add(unformattedHanyuPinyinLabel, BorderLayout.NORTH); // Generated
      unformattedHanyuPinyinPanel.add(getUnformattedHanyuPinyinScrollPane(), BorderLayout.CENTER); // Generated
    }
    return unformattedHanyuPinyinPanel;
  }

  /**
   * This method initializes unformattedTongyongPinyinPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getUnformattedTongyongPinyinPanel() {
    if (unformattedTongyongPinyinPanel == null) {
      unformattedTongyongPinyinLabel = new JLabel();
      unformattedTongyongPinyinLabel.setText("Tongyong Pinyin"); // Generated
      unformattedTongyongPinyinPanel = new JPanel();
      unformattedTongyongPinyinPanel.setLayout(new BorderLayout()); // Generated
      unformattedTongyongPinyinPanel.add(unformattedTongyongPinyinLabel,
          java.awt.BorderLayout.NORTH); // Generated
      unformattedTongyongPinyinPanel.add(getUnformattedTongyongPinyinScrollPane(),
          BorderLayout.CENTER); // Generated
    }
    return unformattedTongyongPinyinPanel;
  }

  /**
   * This method initializes unformattedTongyongPinyinTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getUnformattedTongyongPinyinTextArea() {
    if (unformattedTongyongPinyinTextArea == null) {
      unformattedTongyongPinyinTextArea = new JTextArea();
      unformattedTongyongPinyinTextArea.setEditable(false); // Generated
      unformattedTongyongPinyinTextArea.setLineWrap(true); // Generated
    }
    return unformattedTongyongPinyinTextArea;
  }

  /**
   * This method initializes unformattedWadePinyinPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getUnformattedWadePinyinPanel() {
    if (unformattedWadePinyinPanel == null) {
      unformattedWadePinyinLabel = new JLabel();
      unformattedWadePinyinLabel.setText("Wade-Giles  Pinyin"); // Generated
      unformattedWadePinyinPanel = new JPanel();
      unformattedWadePinyinPanel.setLayout(new BorderLayout()); // Generated
      unformattedWadePinyinPanel.add(unformattedWadePinyinLabel, java.awt.BorderLayout.NORTH); // Generated
      unformattedWadePinyinPanel.add(getUnformattedWadePinyinScrollPane(), BorderLayout.CENTER); // Generated
    }
    return unformattedWadePinyinPanel;
  }

  /**
   * This method initializes unformattedWadePinyinTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getUnformattedWadePinyinTextArea() {
    if (unformattedWadePinyinTextArea == null) {
      unformattedWadePinyinTextArea = new JTextArea();
      unformattedWadePinyinTextArea.setEditable(false); // Generated
      unformattedWadePinyinTextArea.setLineWrap(true); // Generated
    }
    return unformattedWadePinyinTextArea;
  }

  /**
   * This method initializes unformattedMPS2PinyinPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getUnformattedMPS2PinyinPanel() {
    if (unformattedMPS2PinyinPanel == null) {
      unformattedMPS2PinyinLabel = new JLabel();
      unformattedMPS2PinyinLabel.setText("MPSII Pinyin"); // Generated
      unformattedMPS2PinyinPanel = new JPanel();
      unformattedMPS2PinyinPanel.setLayout(new BorderLayout()); // Generated
      unformattedMPS2PinyinPanel.add(unformattedMPS2PinyinLabel, java.awt.BorderLayout.NORTH); // Generated
      unformattedMPS2PinyinPanel.add(getUnformattedMPS2PinyinScrollPane(), BorderLayout.CENTER); // Generated
    }
    return unformattedMPS2PinyinPanel;
  }

  /**
   * This method initializes unformattedMPS2PinyinTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getUnformattedMPS2PinyinTextArea() {
    if (unformattedMPS2PinyinTextArea == null) {
      unformattedMPS2PinyinTextArea = new JTextArea();
      unformattedMPS2PinyinTextArea.setEditable(false); // Generated
      unformattedMPS2PinyinTextArea.setLineWrap(true); // Generated
    }
    return unformattedMPS2PinyinTextArea;
  }

  /**
   * This method initializes unformattedYalePinyinPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getUnformattedYalePinyinPanel() {
    if (unformattedYalePinyinPanel == null) {
      unformattedYalePinyinLabel = new JLabel();
      unformattedYalePinyinLabel.setText("Yale Pinyin"); // Generated
      unformattedYalePinyinPanel = new JPanel();
      unformattedYalePinyinPanel.setLayout(new BorderLayout()); // Generated
      unformattedYalePinyinPanel.add(unformattedYalePinyinLabel, java.awt.BorderLayout.NORTH); // Generated
      unformattedYalePinyinPanel.add(getUnformattedYalePinyinScrollPane(), BorderLayout.CENTER); // Generated
    }
    return unformattedYalePinyinPanel;
  }

  /**
   * This method initializes unformattedYalePinyinTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getUnformattedYalePinyinTextArea() {
    if (unformattedYalePinyinTextArea == null) {
      unformattedYalePinyinTextArea = new JTextArea();
      unformattedYalePinyinTextArea.setEditable(false); // Generated
      unformattedYalePinyinTextArea.setLineWrap(true); // Generated
    }
    return unformattedYalePinyinTextArea;
  }

  /**
   * This method initializes unformattedGwoyeuRomatzyhPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getUnformattedGwoyeuRomatzyhPanel() {
    if (unformattedGwoyeuRomatzyhPanel == null) {
      unformattedGwoyeuRomatzyhLabel = new JLabel();
      unformattedGwoyeuRomatzyhLabel.setText("Gwoyeu Romatzyh"); // Generated
      unformattedGwoyeuRomatzyhPanel = new JPanel();
      unformattedGwoyeuRomatzyhPanel.setLayout(new BorderLayout()); // Generated
      unformattedGwoyeuRomatzyhPanel.add(unformattedGwoyeuRomatzyhLabel,
          java.awt.BorderLayout.NORTH); // Generated
      unformattedGwoyeuRomatzyhPanel.add(getUnformattedGwoyeuRomatzyhScrollPane(),
          BorderLayout.CENTER); // Generated
    }
    return unformattedGwoyeuRomatzyhPanel;
  }

  /**
   * This method initializes unformattedGwoyeuRomatzyhTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getUnformattedGwoyeuRomatzyhTextArea() {
    if (unformattedGwoyeuRomatzyhTextArea == null) {
      unformattedGwoyeuRomatzyhTextArea = new JTextArea();
      unformattedGwoyeuRomatzyhTextArea.setEditable(false); // Generated
      unformattedGwoyeuRomatzyhTextArea.setLineWrap(true); // Generated
    }
    return unformattedGwoyeuRomatzyhTextArea;
  }

  /**
   * This method initializes unformattedMPS2PinyinScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getUnformattedMPS2PinyinScrollPane() {
    if (unformattedMPS2PinyinScrollPane == null) {
      unformattedMPS2PinyinScrollPane = new JScrollPane();
      unformattedMPS2PinyinScrollPane.setViewportView(getUnformattedMPS2PinyinTextArea()); // Generated
    }
    return unformattedMPS2PinyinScrollPane;
  }

  /**
   * This method initializes unformattedHanyuPinyinScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getUnformattedHanyuPinyinScrollPane() {
    if (unformattedHanyuPinyinScrollPane == null) {
      unformattedHanyuPinyinScrollPane = new JScrollPane();
      unformattedHanyuPinyinScrollPane.setViewportView(getUnformattedHanyuPinyinTextArea()); // Generated
    }
    return unformattedHanyuPinyinScrollPane;
  }

  /**
   * This method initializes unformattedTongyongPinyinScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getUnformattedTongyongPinyinScrollPane() {
    if (unformattedTongyongPinyinScrollPane == null) {
      unformattedTongyongPinyinScrollPane = new JScrollPane();
      unformattedTongyongPinyinScrollPane.setViewportView(getUnformattedTongyongPinyinTextArea()); // Generated
    }
    return unformattedTongyongPinyinScrollPane;
  }

  /**
   * This method initializes unformattedWadePinyinScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getUnformattedWadePinyinScrollPane() {
    if (unformattedWadePinyinScrollPane == null) {
      unformattedWadePinyinScrollPane = new JScrollPane();
      unformattedWadePinyinScrollPane.setViewportView(getUnformattedWadePinyinTextArea()); // Generated
    }
    return unformattedWadePinyinScrollPane;
  }

  /**
   * This method initializes unformattedYalePinyinScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getUnformattedYalePinyinScrollPane() {
    if (unformattedYalePinyinScrollPane == null) {
      unformattedYalePinyinScrollPane = new JScrollPane();
      unformattedYalePinyinScrollPane.setViewportView(getUnformattedYalePinyinTextArea()); // Generated
    }
    return unformattedYalePinyinScrollPane;
  }

  /**
   * This method initializes unformattedGwoyeuRomatzyhScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getUnformattedGwoyeuRomatzyhScrollPane() {
    if (unformattedGwoyeuRomatzyhScrollPane == null) {
      unformattedGwoyeuRomatzyhScrollPane = new JScrollPane();
      unformattedGwoyeuRomatzyhScrollPane.setViewportView(getUnformattedGwoyeuRomatzyhTextArea()); // Generated
    }
    return unformattedGwoyeuRomatzyhScrollPane;
  }

  static public void main(String argv[]) {
    final Pinyin4jAppletDemo appletDemo = new Pinyin4jAppletDemo();

    System.runFinalizersOnExit(true);

    JFrame jFrame = new JFrame(appName);
    jFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent event) {
        appletDemo.stop();
        appletDemo.destroy();
        System.exit(0);
      }
    });
    jFrame.add("Center", appletDemo);

    appletDemo.init();
    appletDemo.start();

    jFrame.setSize(APP_SIZE);
    jFrame.pack();
    jFrame.setVisible(true);
  }

  /**
   * This is the default constructor
   */
  public Pinyin4jAppletDemo() {
    super();
    init();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  public void init() {
    this.setSize(APP_SIZE);
    this.setContentPane(getJContentPane());
    this.setName(appName);
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.add(getJTabbedPane(), java.awt.BorderLayout.CENTER); // Generated
      jContentPane.add(getOptionPanel(), java.awt.BorderLayout.NORTH); // Generated
      jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH); // Generated
    }
    return jContentPane;
  }

  /**
   * This method initializes jTabbedPane
   * 
   * @return javax.swing.JTabbedPane
   */
  private JTabbedPane getJTabbedPane() {
    if (jTabbedPane == null) {
      jTabbedPane = new JTabbedPane();
      jTabbedPane.addTab("Unformatted Chinese Romanization Systems", null,
          getUnformattedCharPanel(), null); // Generated
      jTabbedPane.addTab("Formatted Hanyu Pinyin", null, getFormattedCharPanel(), null); // Generated
    }
    return jTabbedPane;
  }

  /**
   * This method initializes jPanel3
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getFormattedCharPanel() {
    if (formattedCharPanel == null) {
      formattedCharPanel = new JPanel();
      formattedCharPanel.setLayout(new BorderLayout());
      formattedCharPanel.add(getFormattedOutputField(), java.awt.BorderLayout.CENTER); // Generated
    }
    return formattedCharPanel;
  }

  /**
   * This method initializes jPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getOptionPanel() {
    if (optionPanel == null) {
      charLabel = new JLabel();
      charLabel.setText("Input Chinese:"); // Generated

      toneLabel = new JLabel();
      toneLabel.setText(" Format:"); // Generated

      optionPanel = new JPanel();
      optionPanel.setPreferredSize(new java.awt.Dimension(640, 34)); // Generated
      optionPanel.add(charLabel, null); // Generated
      optionPanel.add(getCharTextField(), null); // Generated
      optionPanel.add(toneLabel, null); // Generated
      optionPanel.add(getToneTypesComboBox(), null); // Generated
      optionPanel.add(getVCharTypesComboBox(), null); // Generated
      optionPanel.add(getCaseTypesComboBox(), null); // Generated
    }
    return optionPanel;
  }

  /**
   * This method initializes jButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getConvertButton() {
    if (convertButton == null) {
      convertButton = new JButton();
      convertButton.setText("Convert to Pinyin"); // Generated
      convertButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          char chineseCharacter = getChineseCharText().charAt(0);

          updateUnformattedTextField(chineseCharacter);

          String toneSelection = (String) toneTypesComboBox.getSelectedItem();
          String vcharSelection = (String) vCharTypesComboBox.getSelectedItem();
          String caseSelection = (String) caseTypesComboBox.getSelectedItem();

          updateFormattedTextField(chineseCharacter, toneSelection, vcharSelection, caseSelection);
        }

        /**
         * @param chineseCharacter
         */
        private void updateUnformattedTextField(char chineseCharacter) {
          unformattedHanyuPinyinTextArea.setText(concatPinyinStringArray(PinyinHelper
              .toHanyuPinyinStringArray(chineseCharacter)));
          unformattedTongyongPinyinTextArea.setText(concatPinyinStringArray(PinyinHelper
              .toTongyongPinyinStringArray(chineseCharacter)));
          unformattedWadePinyinTextArea.setText(concatPinyinStringArray(PinyinHelper
              .toWadeGilesPinyinStringArray(chineseCharacter)));
          unformattedMPS2PinyinTextArea.setText(concatPinyinStringArray(PinyinHelper
              .toMPS2PinyinStringArray(chineseCharacter)));
          unformattedYalePinyinTextArea.setText(concatPinyinStringArray(PinyinHelper
              .toYalePinyinStringArray(chineseCharacter)));
          unformattedGwoyeuRomatzyhTextArea.setText(concatPinyinStringArray(PinyinHelper
              .toGwoyeuRomatzyhStringArray(chineseCharacter)));
        }

        /**
         * @param chineseCharacter
         * @param toneSelection
         * @param vcharSelection
         * @param caseSelection
         */
        private void updateFormattedTextField(char chineseCharacter, String toneSelection,
            String vcharSelection, String caseSelection) {
          HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();

          if (toneTypes[0] == toneSelection) {
            outputFormat.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
          } else if (toneTypes[1] == toneSelection) {
            outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
          } else if (toneTypes[2] == toneSelection) {
            outputFormat.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
          }

          if (vCharTypes[0] == vcharSelection) {
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
          } else if (vCharTypes[1] == vcharSelection) {
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
          } else if (vCharTypes[2] == vcharSelection) {
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
          }

          if (caseTypes[0] == caseSelection) {
            outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
          } else if (caseTypes[1] == caseSelection) {
            outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
          }

          String[] pinyinArray = null;
          try {
            pinyinArray = PinyinHelper.toHanyuPinyinStringArray(chineseCharacter, outputFormat);
          } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
          }
          String outputString = concatPinyinStringArray(pinyinArray);
          formattedOutputField.setText(outputString);
        }

        /**
         * @param pinyinArray
         * @return
         */
        private String concatPinyinStringArray(String[] pinyinArray) {
          StringBuffer pinyinStrBuf = new StringBuffer();

          if ((null != pinyinArray) && (pinyinArray.length > 0)) {
            for (int i = 0; i < pinyinArray.length; i++) {
              pinyinStrBuf.append(pinyinArray[i]);
              pinyinStrBuf.append(System.getProperty("line.separator"));
            }
          }
          String outputString = pinyinStrBuf.toString();
          return outputString;
        }
      });

    }
    return convertButton;
  }

  /**
   * This method initializes jPanel2
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel();
      buttonPanel.add(getConvertButton(), null); // Generated
    }
    return buttonPanel;
  }

  /**
   * This method initializes jTextArea1
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getFormattedOutputField() {
    if (formattedOutputField == null) {
      formattedOutputField = new JTextArea();
      formattedOutputField.setEditable(false); // Generated
    }
    return formattedOutputField;
  }

  /**
   * This method initializes jComboBox
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getToneTypesComboBox() {
    if (toneTypesComboBox == null) {
      toneTypesComboBox = new JComboBox(toneTypes);
      toneTypesComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (toneTypes[2] == (String) toneTypesComboBox.getSelectedItem()) {
            vCharTypesComboBox.setSelectedIndex(2);
            vCharTypesComboBox.setEnabled(false);
          } else {
            vCharTypesComboBox.setEnabled(true);
          }
        }
      });
    }
    return toneTypesComboBox;
  }

  /**
   * This method initializes jComboBox1
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getVCharTypesComboBox() {
    if (vCharTypesComboBox == null) {
      vCharTypesComboBox = new JComboBox(vCharTypes);
    }
    return vCharTypesComboBox;
  }

  /**
   * This method initializes jComboBox2
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getCaseTypesComboBox() {
    if (caseTypesComboBox == null) {
      caseTypesComboBox = new JComboBox(caseTypes);
    }
    return caseTypesComboBox;
  }

  /**
   * @return
   */
  private String getChineseCharText() {
    return charTextField.getText();
  }

  String[] caseTypes = {"LOWERCASE", "UPPERCASE"};

  String[] vCharTypes = {"WITH_U_AND_COLON", "WITH_V", "WITH_U_UNICODE"};

  String[] toneTypes = {"WITH_TONE_NUMBER", "WITHOUT_TONE", "WITH_TONE_MARK"};

  private JLabel toneLabel = null;

  private JLabel charLabel = null;

  private JTextField charTextField = null;

  private JPanel unformattedCharPanel = null;

  private JLabel unformattedHanyuPinyinLabel = null;

  private JTextArea unformattedHanyuPinyinTextArea = null;

  private JPanel unformattedHanyuPinyinPanel = null;

  private JPanel unformattedTongyongPinyinPanel = null;

  private JLabel unformattedTongyongPinyinLabel = null;

  private JTextArea unformattedTongyongPinyinTextArea = null;

  private JPanel unformattedWadePinyinPanel = null;

  private JLabel unformattedWadePinyinLabel = null;

  private JTextArea unformattedWadePinyinTextArea = null;

  private JPanel unformattedMPS2PinyinPanel = null;

  private JLabel unformattedMPS2PinyinLabel = null;

  private JTextArea unformattedMPS2PinyinTextArea = null;

  private JPanel unformattedYalePinyinPanel = null;

  private JLabel unformattedYalePinyinLabel = null;

  private JTextArea unformattedYalePinyinTextArea = null;

  private JPanel unformattedGwoyeuRomatzyhPanel = null;

  private JLabel unformattedGwoyeuRomatzyhLabel = null;

  private JTextArea unformattedGwoyeuRomatzyhTextArea = null;

  private JScrollPane unformattedMPS2PinyinScrollPane = null;

  private JScrollPane unformattedHanyuPinyinScrollPane = null;

  private JScrollPane unformattedTongyongPinyinScrollPane = null;

  private JScrollPane unformattedWadePinyinScrollPane = null;

  private JScrollPane unformattedYalePinyinScrollPane = null;

  private JScrollPane unformattedGwoyeuRomatzyhScrollPane = null;
} // @jve:decl-index=0:visual-constraint="10,10"
