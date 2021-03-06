package org.basex.query.func.fn;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-17, BSD License
 * @author Christian Gruen
 */
public final class FnParseXml extends Parse {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    return parseXml(qc, false);
  }

  @Override
  protected FnParseXml opt(final CompileContext cc) {
    singleOcc();
    return this;
  }
}
