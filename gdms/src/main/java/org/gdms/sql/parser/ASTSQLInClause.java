/* Generated By:JJTree: Do not edit this line. ASTSQLInClause.java */

package org.gdms.sql.parser;

public class ASTSQLInClause extends SimpleNode {
  public ASTSQLInClause(int id) {
    super(id);
  }

  public ASTSQLInClause(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
