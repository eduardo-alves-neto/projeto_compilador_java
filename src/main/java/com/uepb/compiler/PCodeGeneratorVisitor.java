package com.uepb.compiler;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser.AtribuicaoContext;
import com.uepb.ExprParser.AtomContext;
import com.uepb.ExprParser.CondicaoContext;
import com.uepb.ExprParser.CondicaoEContext;
import com.uepb.ExprParser.CondicaoOuContext;
import com.uepb.ExprParser.CondicaoPrimContext;
import com.uepb.ExprParser.DeclVarContext;
import com.uepb.ExprParser.ExprContext;
import com.uepb.ExprParser.ExprRelacionalContext;
import com.uepb.ExprParser.FatorContext;
import com.uepb.ExprParser.IfStmtContext;
import com.uepb.ExprParser.InputStmtContext;
import com.uepb.ExprParser.PrintStmtContext;
import com.uepb.ExprParser.ProgContext;
import com.uepb.ExprParser.StmtContext;
import com.uepb.ExprParser.TermoContext;
import com.uepb.ExprParser.UnarioContext;
import com.uepb.ExprParser.WhileStmtContext;


public class PCodeGeneratorVisitor extends ExprBaseVisitor<Void> {

    private final SymbolTable symbolTable;
    private final StringBuilder out;
    private int labelCounter = 0;

    public PCodeGeneratorVisitor(SymbolTable symbolTable, StringBuilder out) {
        this.symbolTable = symbolTable;
        this.out = out;
    }

    private void emit(String line) {
        out.append(line).append(System.lineSeparator());
    }

    private String newLabel() {
        return "L" + (labelCounter++);
    }


    @Override
    public Void visitProg(ProgContext ctx) {
        for (StmtContext stmt : ctx.stmt()) {
            visit(stmt);
        }
        emit("hlt");
        return null;
    }

    @Override
    public Void visitDeclVar(DeclVarContext ctx) {
        String name = ctx.ID().getText();
        int address = symbolTable.declare(name);

        emit("push $" + address);

        if (ctx.expr() != null) {
            visit(ctx.expr());
        } else {
            emit("push 0");
        }

        emit("sto");
        return null;
    }

    @Override
    public Void visitAtribuicao(AtribuicaoContext ctx) {
        String name = ctx.ID().getText();
        int address = symbolTable.getAddress(name);

        emit("push $" + address);
        visit(ctx.expr());
        emit("sto");
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmtContext ctx) {
        String endLabel = newLabel();

        visit(ctx.condicao());
        emit("fjp " + endLabel);

        for (StmtContext stmt : ctx.stmt()) {
            visit(stmt);
        }

        emit(endLabel + ":");
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmtContext ctx) {
        String startLabel = newLabel();
        String endLabel = newLabel();

        emit(startLabel + ":");
        visit(ctx.condicao());
        emit("fjp " + endLabel);

        for (StmtContext stmt : ctx.stmt()) {
            visit(stmt);
        }

        emit("ujp " + startLabel);
        emit(endLabel + ":");
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmtContext ctx) {
        if (ctx.expr() != null) {
            visit(ctx.expr());
        } else if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            emit("push " + text);
        }
        emit("out");
        return null;
    }

    @Override
    public Void visitInputStmt(InputStmtContext ctx) {
        String name = ctx.ID().getText();
        int address = symbolTable.getAddress(name);

        emit("push $" + address);
        emit("in");
        emit("sto");
        return null;
    }



    @Override
    public Void visitCondicao(CondicaoContext ctx) {
        return visitCondicaoOu(ctx.condicaoOu());
    }

    @Override
    public Void visitCondicaoOu(CondicaoOuContext ctx) {
        int n = ctx.condicaoE().size();
        visit(ctx.condicaoE(0));
        for (int i = 1; i < n; i++) {
            visit(ctx.condicaoE(i));
            emit("or");
        }
        return null;
    }

    @Override
    public Void visitCondicaoE(CondicaoEContext ctx) {
        int n = ctx.condicaoPrim().size();
        visit(ctx.condicaoPrim(0));
        for (int i = 1; i < n; i++) {
            visit(ctx.condicaoPrim(i));
            emit("and");
        }
        return null;
    }



    @Override
    public Void visitCondicaoPrim(CondicaoPrimContext ctx) {
        if (ctx.exprRelacional() != null) {
            visit(ctx.exprRelacional());
        } else if (ctx.TRUE() != null) {
            emit("push true");
        } else if (ctx.FALSE() != null) {
            emit("push false");
        } else if (ctx.condicao() != null) {
            visit(ctx.condicao());
        }
        return null;
    }

    @Override
    public Void visitExprRelacional(ExprRelacionalContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        String op = ctx.OP_REL().getText();
        switch (op) {
            case ">":
                emit("grt");
                break;
            case ">=":
                emit("gte");
                break;
            case "<":
                emit("let");
                break;
            case "<=":
                emit("lte");
                break;
            case "==":
                emit("equ");
                break;
            case "!=":
                emit("neq");
                break;
            default:
                throw new IllegalStateException("Operador relacional não suportado: " + op);
        }

        return null;
    }


    @Override
    public Void visitExpr(ExprContext ctx) {
        visit(ctx.termo(0));
        int n = ctx.termo().size();
        for (int i = 1; i < n; i++) {
            visit(ctx.termo(i));
            String op = ctx.OP1(i - 1).getText();
            if ("+".equals(op)) {
                emit("add");
            } else {
                emit("sub");
            }
        }
        return null;
    }

    @Override
    public Void visitTermo(TermoContext ctx) {
        visit(ctx.fator(0));
        int n = ctx.fator().size();
        for (int i = 1; i < n; i++) {
            visit(ctx.fator(i));
            String op = ctx.OP2(i - 1).getText();
            if ("*".equals(op)) {
                emit("mul");
            } else {
                emit("div");
            }
        }
        return null;
    }

    @Override
    public Void visitFator(FatorContext ctx) {
        visit(ctx.unario());
        if (ctx.POT() != null) {
            throw new UnsupportedOperationException("Operador ^ ainda não suportado na geração de P-Code");
        }
        return null;
    }

    @Override
    public Void visitUnario(UnarioContext ctx) {
        if (ctx.atom() != null) {
            if (ctx.OP1() != null && "-".equals(ctx.OP1().getText())) {
                emit("push 0");
                visit(ctx.atom());
                emit("sub");
            } else {
                visit(ctx.atom());
            }
        }
        return null;
    }


    @Override
    public Void visitAtom(AtomContext ctx) {
        if (ctx.INT() != null) {
            emit("push " + ctx.INT().getText());
        } else if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            int address = symbolTable.getAddress(name);
            emit("push $" + address);
            emit("lod");
        } else if (ctx.expr() != null) {
            visit(ctx.expr());
        }
        return null;
    }

  
}

