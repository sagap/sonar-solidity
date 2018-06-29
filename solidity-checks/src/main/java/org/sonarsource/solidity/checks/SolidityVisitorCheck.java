package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyAssignmentContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyBlockContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyCaseContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyForContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyFunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyFunctionReturnsContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyIdentifierListContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyIdentifierOrListContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyIfContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyItemContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyLiteralContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyLocalDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblyStackAssignmentContext;
import org.sonarsource.solidity.frontend.SolidityParser.AssemblySwitchContext;
import org.sonarsource.solidity.frontend.SolidityParser.BlockContext;
import org.sonarsource.solidity.frontend.SolidityParser.BreakStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ConstructorDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContinueStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractPartContext;
import org.sonarsource.solidity.frontend.SolidityParser.DoWhileStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ElementaryTypeNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.ElementaryTypeNameExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.EmitStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.EnumDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.EnumValueContext;
import org.sonarsource.solidity.frontend.SolidityParser.EventDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.EventParameterContext;
import org.sonarsource.solidity.frontend.SolidityParser.EventParameterListContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionListContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ForStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallArgumentsContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionTypeNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionTypeParameterContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionTypeParameterListContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierListContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ImportDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.ImportDirectiveContext;
import org.sonarsource.solidity.frontend.SolidityParser.InheritanceSpecifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.InlineAssemblyStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.LabelDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.MappingContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierInvocationContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierListContext;
import org.sonarsource.solidity.frontend.SolidityParser.NameValueContext;
import org.sonarsource.solidity.frontend.SolidityParser.NameValueListContext;
import org.sonarsource.solidity.frontend.SolidityParser.NumberLiteralContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterListContext;
import org.sonarsource.solidity.frontend.SolidityParser.PragmaDirectiveContext;
import org.sonarsource.solidity.frontend.SolidityParser.PragmaNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.PragmaValueContext;
import org.sonarsource.solidity.frontend.SolidityParser.PrimaryExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ReturnParametersContext;
import org.sonarsource.solidity.frontend.SolidityParser.ReturnStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SimpleStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.SolidityParser.StateMutabilityContext;
import org.sonarsource.solidity.frontend.SolidityParser.StateVariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StorageLocationContext;
import org.sonarsource.solidity.frontend.SolidityParser.StructDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.SubAssemblyContext;
import org.sonarsource.solidity.frontend.SolidityParser.ThrowStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.TupleExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.TypeNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.UserDefinedTypeNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.UsingForDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.VersionConstraintContext;
import org.sonarsource.solidity.frontend.SolidityParser.VersionContext;
import org.sonarsource.solidity.frontend.SolidityParser.VersionOperatorContext;
import org.sonarsource.solidity.frontend.SolidityParser.WhileStatementContext;
import org.sonarsource.solidity.frontend.SolidityVisitor;

public class SolidityVisitorCheck implements SolidityVisitor<ParseTree> {

  public ParseTree visitSourceUnit(SourceUnitContext ctx) {
    System.out.println("STARTS: ");
    ctx.accept(this);
    return null;
  }

  public ParseTree visit(ParseTree arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitChildren(RuleNode arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitErrorNode(ErrorNode arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitTerminal(TerminalNode arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitPragmaDirective(PragmaDirectiveContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitPragmaName(PragmaNameContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitPragmaValue(PragmaValueContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitVersion(VersionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitVersionOperator(VersionOperatorContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitVersionConstraint(VersionConstraintContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitImportDeclaration(ImportDeclarationContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitImportDirective(ImportDirectiveContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitContractDefinition(ContractDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitInheritanceSpecifier(InheritanceSpecifierContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitContractPart(ContractPartContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitStateVariableDeclaration(StateVariableDeclarationContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitUsingForDeclaration(UsingForDeclarationContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitStructDefinition(StructDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitConstructorDefinition(ConstructorDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitModifierDefinition(ModifierDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitModifierInvocation(ModifierInvocationContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitReturnParameters(ReturnParametersContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitModifierList(ModifierListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitEventDefinition(EventDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitEnumValue(EnumValueContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitEnumDefinition(EnumDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitParameterList(ParameterListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitParameter(ParameterContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitEventParameterList(EventParameterListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitEventParameter(EventParameterContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitFunctionTypeParameterList(FunctionTypeParameterListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitFunctionTypeParameter(FunctionTypeParameterContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitVariableDeclaration(VariableDeclarationContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitTypeName(TypeNameContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitUserDefinedTypeName(UserDefinedTypeNameContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitMapping(MappingContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitFunctionTypeName(FunctionTypeNameContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitStorageLocation(StorageLocationContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitStateMutability(StateMutabilityContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitBlock(BlockContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitStatement(StatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitExpressionStatement(ExpressionStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitIfStatement(IfStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitWhileStatement(WhileStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitSimpleStatement(SimpleStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitForStatement(ForStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitInlineAssemblyStatement(InlineAssemblyStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitDoWhileStatement(DoWhileStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitContinueStatement(ContinueStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitBreakStatement(BreakStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitReturnStatement(ReturnStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitThrowStatement(ThrowStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitEmitStatement(EmitStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitVariableDeclarationStatement(VariableDeclarationStatementContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitIdentifierList(IdentifierListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitElementaryTypeName(ElementaryTypeNameContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitExpression(ExpressionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitPrimaryExpression(PrimaryExpressionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitExpressionList(ExpressionListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitNameValueList(NameValueListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitNameValue(NameValueContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitFunctionCallArguments(FunctionCallArgumentsContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitFunctionCall(FunctionCallContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyBlock(AssemblyBlockContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyItem(AssemblyItemContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyExpression(AssemblyExpressionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyCall(AssemblyCallContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyLocalDefinition(AssemblyLocalDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyAssignment(AssemblyAssignmentContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyIdentifierOrList(AssemblyIdentifierOrListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyIdentifierList(AssemblyIdentifierListContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyStackAssignment(AssemblyStackAssignmentContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitLabelDefinition(LabelDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblySwitch(AssemblySwitchContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyCase(AssemblyCaseContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyFunctionDefinition(AssemblyFunctionDefinitionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyFunctionReturns(AssemblyFunctionReturnsContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyFor(AssemblyForContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyIf(AssemblyIfContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitAssemblyLiteral(AssemblyLiteralContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitSubAssembly(SubAssemblyContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitTupleExpression(TupleExpressionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitElementaryTypeNameExpression(ElementaryTypeNameExpressionContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitNumberLiteral(NumberLiteralContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  public ParseTree visitIdentifier(IdentifierContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }
}
