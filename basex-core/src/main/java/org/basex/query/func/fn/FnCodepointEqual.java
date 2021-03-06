package org.basex.query.func.fn;

import static org.basex.util.Token.*;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.value.item.*;
import org.basex.query.value.type.SeqType.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-17, BSD License
 * @author Christian Gruen
 */
public final class FnCodepointEqual extends StandardFunc {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final Item it1 = exprs[0].atomItem(qc, info), it2 = exprs[1].atomItem(qc, info);
    return it1 == null || it2 == null ? null : Bln.get(eq(toToken(it1), toToken(it2)));
  }

  @Override
  protected Expr opt(final CompileContext cc) {
    if(exprs[0].seqType().oneNoArray() && exprs[1].seqType().oneNoArray())
      seqType = seqType.withOcc(Occ.ONE);
    return this;
  }
}
