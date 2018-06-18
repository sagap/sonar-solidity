package org.sonarsource.solidity;

public class FileMeasures {
  private int functionNumber;
  private int contractNumber;
  private int statementNumber;
  private int contractComplexity;
  private int functionComplexity;
  private int fileComplexity;
  private int fileCognitiveComplexity;

  private int linesOfCodeNumber;
  private int commentLinesNumber;

  public int getFunctionNumber() {
    return functionNumber;
  }

  public void setFunctionNumber(int functionNumber) {
    this.functionNumber = functionNumber;
  }

  public int getContractNumber() {
    return contractNumber;
  }

  public void setContractNumber(int classNumber) {
    this.contractNumber = classNumber;
  }

  public int getStatementNumber() {
    return statementNumber;
  }

  public void setStatementNumber(int statementNumber) {
    this.statementNumber = statementNumber;
  }

  public int getContractComplexity() {
    return contractComplexity;
  }

  public void setContractComplexity(int classComplexity) {
    this.contractComplexity = classComplexity;
  }

  public int getFunctionComplexity() {
    return functionComplexity;
  }

  public void setFunctionComplexity(int functionComplexity) {
    this.functionComplexity = functionComplexity;
  }

  public int getFileComplexity() {
    return fileComplexity;
  }

  public void setFileComplexity(int fileComplexity) {
    this.fileComplexity = fileComplexity;
  }

  public int getFileCognitiveComplexity() {
    return fileCognitiveComplexity;
  }

  public void setFileCognitiveComplexity(int fileCognitiveComplexity) {
    this.fileCognitiveComplexity = fileCognitiveComplexity;
  }

  public int getLinesOfCodeNumber() {
    return linesOfCodeNumber;
  }

  public void setLinesOfCodeNumber(int linesOfCodeNumber) {
    this.linesOfCodeNumber = linesOfCodeNumber;
  }

  public int getCommentLinesNumber() {
    return commentLinesNumber;
  }

  public void setCommentLinesNumber(int commentLinesNumber) {
    this.commentLinesNumber = commentLinesNumber;
  }

  @Override
  public String toString() {
    return "FileMeasures [functionNumber=" + functionNumber + ", contractNumber=" + contractNumber + ", statementNumber=" + statementNumber + ", contractComplexity="
      + contractComplexity + ", functionComplexity=" + functionComplexity + ", fileComplexity=" + fileComplexity + ", fileCognitiveComplexity=" + fileCognitiveComplexity
      + ", linesOfCodeNumber=" + linesOfCodeNumber + ", commentLinesNumber=" + commentLinesNumber + "]";
  }

}
