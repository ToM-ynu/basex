package org.basex.query.expr;

import static org.basex.query.QueryError.*;
import static org.basex.query.QueryText.*;

import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.seq.*;
import org.basex.query.value.type.*;
import org.basex.query.var.*;
import org.basex.util.*;
import org.basex.util.hash.*;

/**
 * Treat as expression.
 *
 * @author BaseX Team 2005-17, BSD License
 * @author Christian Gruen
 */
public final class Treat extends Single {
  /**
   * Constructor.
   * @param info input info
   * @param expr expression
   * @param seqType sequence type
   */
  public Treat(final InputInfo info, final Expr expr, final SeqType seqType) {
    super(info, expr);
    this.seqType = seqType;
  }

  @Override
  public Expr compile(final CompileContext cc) throws QueryException {
    return super.compile(cc).optimize(cc);
  }

  @Override
  public Expr optimize(final CompileContext cc) throws QueryException {
    return expr.isValue() ? cc.preEval(this) : this;
  }

  @Override
  public Iter iter(final QueryContext qc) throws QueryException {
    final Iter iter = qc.iter(expr);
    final Item it = iter.next();
    // input is empty
    if(it == null) {
      if(seqType.mayBeZero()) return Empty.ITER;
      throw NOTREAT_X_X_X.get(info, Empty.SEQ.seqType(), seqType, Empty.SEQ);
    }
    // treat as empty sequence
    if(seqType.zero()) throw NOTREAT_X_X_X.get(info, it.type, seqType, it);

    if(seqType.zeroOrOne()) {
      final Item n = iter.next();
      if(n != null) {
        final ValueBuilder vb = new ValueBuilder().add(it).add(n);
        if(iter.next() != null) vb.add(Str.get(DOTS));
        throw NOTREAT_X_X_X.get(info, expr.seqType(), seqType, vb.value());
      }
      if(!it.type.instanceOf(seqType.type)) throw NOTREAT_X_X_X.get(info, it.type, seqType, it);
      return it.iter();
    }

    return new Iter() {
      Item i = it;

      @Override
      public Item next() throws QueryException {
        if(i == null) return null;
        if(!i.type.instanceOf(seqType.type)) throw NOTREAT_X_X_X.get(info, i.type, seqType, i);
        final Item ii = i;
        i = iter.next();
        return ii;
      }
    };
  }

  @Override
  public Value value(final QueryContext qc) throws QueryException {
    final Value val = qc.value(expr);

    final long len = val.size();
    // input is empty
    if(len == 0) {
      if(seqType.mayBeZero()) return val;
      throw NOTREAT_X_X_X.get(info, Empty.SEQ.seqType(), seqType, Empty.SEQ);
    }
    // treat as empty sequence
    if(seqType.zero()) throw NOTREAT_X_X_X.get(info, val.type, seqType, val);

    if(seqType.zeroOrOne()) {
      if(len > 1) throw NOTREAT_X_X_X.get(info, val.seqType(), seqType, val);
      final Item it = val.itemAt(0);
      if(!it.type.instanceOf(seqType.type)) throw NOTREAT_X_X_X.get(info, it.type, seqType, it);
      return it;
    }

    for(long i = 0; i < len; i++) {
      final Item it = val.itemAt(i);
      if(!it.type.instanceOf(seqType.type)) throw NOTREAT_X_X_X.get(info, it.type, seqType, it);
    }
    return val;
  }

  @Override
  public Expr copy(final CompileContext cc, final IntObjMap<Var> vm) {
    return new Treat(info, expr.copy(cc, vm), seqType);
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj || obj instanceof Treat && seqType.eq(((Treat) obj).seqType) &&
        super.equals(obj);
  }

  @Override
  public void plan(final FElem plan) {
    addPlan(plan, planElem(TYP, seqType), expr);
  }

  @Override
  public String toString() {
    return '(' + expr.toString() + ") " + TREAT + ' ' + AS + ' ' + seqType;
  }
}
