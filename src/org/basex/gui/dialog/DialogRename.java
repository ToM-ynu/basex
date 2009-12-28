package org.basex.gui.dialog;

import static org.basex.core.Text.*;
import static org.basex.gui.layout.BaseXKeys.*;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.basex.core.Main;
import org.basex.core.proc.List;
import org.basex.gui.GUI;
import org.basex.gui.GUIConstants.Msg;
import org.basex.gui.layout.BaseXBack;
import org.basex.gui.layout.BaseXLabel;
import org.basex.gui.layout.BaseXTextField;
import org.basex.util.StringList;

/**
 * Rename database dialog.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class DialogRename extends Dialog {
  /** Old name. */
  final String old;
  /** New name. */
  final BaseXTextField name;
  /** Buttons. */
  final BaseXBack buttons;
  /** Info label. */
  final BaseXLabel info;
  /** Available databases. */
  final StringList db;

  /**
   * Default constructor.
   * @param dbname name of database
   * @param main reference to the main window
   * @param fs file system flag
   */
  public DialogRename(final String dbname, final GUI main, final boolean fs) {
    super(main, RENAMETITLE);
    old = dbname;
    db = fs ? List.listFS(main.context) : List.list(main.context);

    name = new BaseXTextField(dbname, this);
    name.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(final KeyEvent e) {
        if(!modifier(e)) action(pressed(ENTER, e) ? e.getSource() : null);
      }
    });
    info = new BaseXLabel(" ");

    final BaseXBack p = new BaseXBack();
    p.setLayout(new BorderLayout(0, 8));
    p.add(name, BorderLayout.NORTH);
    p.add(info, BorderLayout.CENTER);
    set(p, BorderLayout.CENTER);

    buttons = newButtons(this, BUTTONOK, BUTTONCANCEL);
    set(buttons, BorderLayout.SOUTH);
    action(null);
    finish(null);
  }

  @Override
  public void action(final Object cmp) {
    final String nm = name.getText();
    ok = !db.contains(nm) || nm.equals(old);
    String msg = ok ? null : RENAMEEXISTS;
    if(ok) {
      ok = !nm.isEmpty() && DialogCreate.dbValid(nm);
      if(!nm.isEmpty() && !ok) msg = Main.info(INVALID, EDITNAME);
    }
    info.setText(msg, Msg.ERR);
    enableOK(buttons, BUTTONOK, ok && !nm.isEmpty());
  }

  @Override
  public void close() {
    if(ok) super.close();
  }
}
