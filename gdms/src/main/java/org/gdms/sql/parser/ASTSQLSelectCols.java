/* Generated By:JJTree: Do not edit this line. ASTSQLSelectCols.java */

package org.gdms.sql.parser;

public class ASTSQLSelectCols extends SimpleNode {
  public ASTSQLSelectCols(int id) {
    super(id);
  }

  public ASTSQLSelectCols(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
