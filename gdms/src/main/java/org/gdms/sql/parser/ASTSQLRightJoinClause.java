/* Generated By:JJTree: Do not edit this line. ASTSQLRightJoinClause.java */

package org.gdms.sql.parser;

public class ASTSQLRightJoinClause extends SimpleNode {
  public ASTSQLRightJoinClause(int id) {
    super(id);
  }

  public ASTSQLRightJoinClause(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
