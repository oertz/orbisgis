/* Generated By:JJTree: Do not edit this line. ASTSQLPattern.java */

package org.gdms.sql.parser;

public class ASTSQLPattern extends SimpleNode {
  public ASTSQLPattern(int id) {
    super(id);
  }

  public ASTSQLPattern(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
