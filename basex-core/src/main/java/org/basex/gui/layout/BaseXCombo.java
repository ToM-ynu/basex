package org.basex.gui.layout;

import java.awt.event.*;

import javax.swing.*;

import org.basex.util.*;
import org.basex.util.options.*;

/**
 * Project specific ComboBox implementation.
 *
 * @author BaseX Team 2005-17, BSD License
 * @author Christian Gruen
 */
public final class BaseXCombo extends JComboBox<Object> {
  /** Options. */
  private Options options;
  /** Option. */
  private Option<?> option;

  /**
   * Constructor.
   * @param win parent window
   * @param option option
   * @param options options
   * @param values combobox values
   */
  public BaseXCombo(final BaseXWindow win, final NumberOption option, final Options options,
      final String... values) {
    this(win, option, options, values[options.get(option)], values);
  }

  /**
   * Constructor.
   * @param win parent window
   * @param option option
   * @param options options
   */
  public BaseXCombo(final BaseXWindow win, final BooleanOption option, final Options options) {
    this(win, option, options, options.get(option), "true", "false");
  }

  /**
   * Constructor.
   * @param win parent window
   * @param option option
   * @param options options
   */
  public BaseXCombo(final BaseXWindow win, final EnumOption<?> option, final Options options) {
    this(win, option, options, options.get(option), option.strings());
  }

  /**
   * Constructor.
   * @param win parent window
   * @param option option
   * @param options options
   * @param selected selected value
   * @param values values
   */
  private BaseXCombo(final BaseXWindow win, final Option<?> option, final Options options,
      final Object selected, final String... values) {
    this(win, values);
    this.options = options;
    this.option = option;
    setSelectedItem(selected.toString());
  }

  /**
   * Constructor.
   * @param win parent window
   * @param values combobox values
   */
  public BaseXCombo(final BaseXWindow win, final String... values) {
    super(values);
    BaseXLayout.addInteraction(this, win);

    final BaseXDialog dialog = win.dialog();
    if(dialog != null) addItemListener(ie -> {
      if(isValid() && ie.getStateChange() == ItemEvent.SELECTED) dialog.action(ie.getSource());
    });
  }

  @Override
  public String getSelectedItem() {
    final Object o = super.getSelectedItem();
    return o == null ? null : o.toString();
  }

  @Override
  public void setSelectedItem(final Object object) {
    if(object == null) return;
    final String value = object.toString();
    final ComboBoxModel<Object> m = getModel();
    final int s = m.getSize();
    for(int i = 0; i < s; i++) {
      if(m.getElementAt(i).equals(value)) {
        super.setSelectedItem(value);
        return;
      }
    }
  }

  /**
   * Assigns the current checkbox value to the option specified in the constructor.
   */
  public void assign() {
    if(option instanceof NumberOption) {
      options.set((NumberOption) option, getSelectedIndex());
    } else if(option instanceof EnumOption) {
      options.set((EnumOption<?>) option, getSelectedItem());
    } else if(option instanceof StringOption) {
      options.set((StringOption) option, getSelectedItem());
    } else if(option instanceof BooleanOption) {
      options.set((BooleanOption) option, Boolean.parseBoolean(getSelectedItem()));
    } else {
      throw Util.notExpected("Option type not supported: " + option);
    }
  }
}
