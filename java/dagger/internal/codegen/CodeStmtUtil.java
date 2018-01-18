package dagger.internal.codegen;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;


public class CodeStmtUtil {

    private static void beginStmt(CodeBlock.Builder builder){
        builder.add("$[");
    }

    private static void endStmt(CodeBlock.Builder builder){
        builder.add(";\n$]");
    }

    /**
     * A usage of the keyword "assert"
     * <br/>In <code>assert dead : "Wasn't expecting to be dead here";</code> the check is "dead" and the message is the string.
     */
    static CodeBlock makeAssertStmt(CodeBlock check, Optional<CodeBlock> message){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        builder.add("assert check");
        if(message.isPresent()){
            builder.add(": $L", message.get());
        }
        endStmt(builder);
        return builder.build();
    }

    /**
     * Statements in between { and }.
     *
     */
    static CodeBlock makeBlockStmt(ImmutableList<CodeBlock> statements){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("");
        for(int i = 0; i < statements.size(); i ++){
            builder.add(statements.get(i));
        }
        builder.endControlFlow();
        return builder.build();
    }

    /**
     * A usage of the break keyword.
     * <br/>In <code>break abc;</code> the label is abc.
     *
     */
    static CodeBlock makeBreakStmt(Optional<String> label){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        if(label.isPresent()){
            builder.add("break $L", label);
        }else{
            builder.add("break");
        }
        endStmt(builder);
        return builder.build();
    }

    /**
     * The catch part of a try-catch-finally. <br/>In <code>try { ... } catch (Exception e) { ... }</code> the CatchClause
     * is <code>catch (Exception e) { ... }</code>. Exception e is the parameter. The { ... } is the body.
     */
    static CodeBlock makeCatchClause(CodeBlock parameter, CodeBlock body){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("catch($L)", parameter);
        builder.add(body);
        builder.endControlFlow();
        return builder.build();
    }

    /**
     * A continue statement with an optional label;
     * <br/><code>continue brains;</code>
     * <br/><code>continue;</code>
     *
     */
    static CodeBlock makeContinueStmt(Optional<String> label){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        if(label.isPresent()){
            builder.add("continue $L", label);
        }else{
            builder.add("continue");
        }
        endStmt(builder);
        return builder.build();
    }

    /**
     *
     * A do-while.
     * <br/><code>do { ... } while ( a==0 );</code>
     */
    static CodeBlock makeDoStmt(CodeBlock body, CodeBlock condition){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("do");
        builder.add(body);
        builder.endControlFlow("while($L)", condition);
        return builder.build();
    }

    /**
     * An empty statement is a ";" where a statement is expected.
     */
    static CodeBlock makeEmptyStmt(){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        builder.add(";");
        endStmt(builder);
        return builder.build();
    }

    /**
     * A call to super or this in a constructor or initializer.
     * <br/><code>class X { X() { super(15); } }</code>
     * <br/><code>class X { X() { this(1, 2); } }</code>
     *
     * not sure what the Optional(Expression) mean
     */
    static CodeBlock makeExplicitConstructorInvocationStmt(Optional<ImmutableList<TypeName>> typeArguments, boolean isThis, Optional<CodeBlock> expression, ImmutableList<CodeBlock> arguments){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        if(isThis){
            builder.add("this(");
        }else{
            builder.add("super(");
        }
        for(int i = 0; i < arguments.size(); i ++){
            if(i != arguments.size() - 1){
                builder.add("$L,", arguments.get(i));
            }else{
                builder.add("$L", arguments.get(i));
            }
        }
        builder.add(")");
        endStmt(builder);
        return builder.build();
    }


    /**
     * Used to wrap an expression so that it can take the place of a statement.
     *
     */
    static CodeBlock makeExpressionStatement(CodeBlock expression){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        builder.add(expression);
        endStmt(builder);
        return builder.build();
    }

    /**
     * A for-each statement.
     * <br/><code>for(Object o: objects) { ... }</code>
     *
     */
    static CodeBlock makeForeachStatement(CodeBlock variable, CodeBlock iterable, CodeBlock body){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("for($L : $L)", variable, iterable);
        builder.add(body);
        builder.endControlFlow();
        return builder.build();
    }


    /**
     * <h1>The classic for statement</h1>
     * Examples:
     * <ol>
     * <li><code>for(int a=3, b=5; a<99; a++, b++) hello();</code></li>
     * <li><code>for(a=3, b=5; a<99; a++) { hello(); }</code> </li>
     * <li><code>for(a(),b();;) hello();</code> </li>
     * </ol>
     * <ul>
     * <li><i>initialization</i> is a list of expressions.
     * These can be any kind of expression as can be seen in example 3,
     * but the common ones are a single VariableDeclarationExpr (which declares multiple variables) in example 1,
     * or a list of AssignExpr's in example 2.</li>
     * <li><i>compare</i> is an expression,
     * in example 1 and 2 it is a BinaryExpr.
     * In example 3 there is no expression, it is empty.</li>
     * <li><i>update</i> is a list of expressions,
     * in example 1 and 2 they are UnaryExpr's.
     * In example 3 there is no expression, the list empty.</li>
     * <li><i>body</i> is a statement,
     * in example 1 and 3 it is an ExpressionStmt.
     * in example 2 it is a BlockStmt.</li>
     * </ul>
     *
     */
    static CodeBlock makeForStatement(ImmutableList<CodeBlock> initialization, Optional<CodeBlock> compare, ImmutableList<CodeBlock> update, CodeBlock body){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("for(");
        for(int i = 0; i < initialization.size(); i ++){
            if(i != initialization.size() - 1){
                builder.add("$L, ", initialization.get(i));
            }else{
                builder.add("$L;", initialization.get(i));
            }
        }
        if(compare.isPresent()){
            builder.add("$L;", compare.get());
        }else{
            builder.add(";");
        }
        for(int i = 0; i < update.size(); i ++){
            if(i != update.size() - 1){
                builder.add("$L, ", update.get(i));
            }else{
                builder.add("$L", update.get(i));
            }
        }
        builder.beginControlFlow(")");
        builder.add(body);
        builder.endControlFlow();
        return builder.build();
    }

    /**
     * An if-then-else statement. The else is optional.
     * <br/>In <code>if(a==5) hurray() else boo();</code> the condition is a==5,
     * hurray() is the thenStmt, and boo() is the elseStmt.
     */
    static CodeBlock makeIfStatement(CodeBlock condition, CodeBlock thenStmt, CodeBlock elseStmt){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("if($L)", condition);
        builder.add(thenStmt);
        builder.nextControlFlow("else");
        builder.add(elseStmt);
        builder.endControlFlow();
        return builder.build();
    }

    /**
     * A statement that is labeled, like <code>label123: println("continuing");</code>
     */
    //NOTICE: already has a statement
    static CodeBlock makeLabeldStatement(String label, CodeBlock statement){
        CodeBlock.Builder builder = CodeBlock.builder();
//        beginStmt(builder);
        builder.add("$L : $L", label, statement);
//        endStmt(builder);
        return builder.build();
    }

    //TODO fulfill this
//    static CodeBlock makeLocalClassDeclarationStmt

    /**
     * The return statement, with an optional expression to return.
     * <br/><code>return 5 * 5;</code>
     */
    static CodeBlock makeReturnStatement(CodeBlock expression){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        builder.add("return $L", expression);
        endStmt(builder);
        return builder.build();
    }

    /**
     * One case in a switch statement.
     * <br/><pre>
     * switch (i) {
     * case 1:
     * case 2:
     * System.out.println(444);
     * break;
     * default:
     * System.out.println(0);
     * }
     * </pre>
     * This contains three SwitchEntryStmts.
     * <br/>The first one has label 1 and no statements.
     * <br/>The second has label 2 and two statements (the println and the break).
     * <br/>The third, the default, has no label and one statement.
     */
    static CodeBlock makeSwitchEntryStatement(Optional<CodeBlock> label, ImmutableList<CodeBlock> statements){
        CodeBlock.Builder builder = CodeBlock.builder();
        if(label.isPresent()){
            builder.add("case $L :", label);
        }else{
            builder.add("default :");
        }
        for(CodeBlock statement: statements){
            builder.add(statement);
        }
        return builder.build();
    }

    /**
     * A switch statement.
     * <br/>In <code>switch(a) { ... }</code> the selector is "a",
     * and the contents of the { ... } are the entries.
     *
     */
    static CodeBlock makeSwitchStatement(CodeBlock selector, ImmutableList<CodeBlock> entries){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("switch($L)", selector);
        for(CodeBlock entry: entries){
            builder.add(entry);
        }
        builder.endControlFlow();
        return builder.build();
    }

    /**
     * Usage of the synchronized keyword.
     * <br/>In <code>synchronized (a123) { ... }</code> the expression is a123 and { ... } is the body
     */
    static CodeBlock makeSynchronizedStatement(CodeBlock expression, CodeBlock body){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("synchronized($L)", expression);
        builder.add(body);
        builder.endControlFlow();
        return builder.build();
    }

    /**
     * Usage of the throw statement.
     * <br/><code>throw new Exception()</code>
     */
    static CodeBlock makeThrowStatement(CodeBlock expression){
        CodeBlock.Builder builder = CodeBlock.builder();
        beginStmt(builder);
        builder.add("throw $L", expression);
        endStmt(builder);
        return builder.build();
    }

    /**
     * <h1>The try statement</h1>
     * <h2>Java 1.0-6</h2>
     * <pre>
     * try {
     * // ...
     * } catch (IOException e) {
     * // ...
     * } finally {
     * // ...
     * }
     * </pre>
     * In this code, "// do things" is the content of the tryBlock, there is one catch clause that catches IOException e,
     * and there is a finally block.
     * <p>
     * The catch and finally blocks are optional, but they should not be empty at the same time.
     * <h2>Java 7-8</h2>
     * <pre>
     * try (InputStream i = new FileInputStream("file")) {
     * // ...
     * } catch (IOException|NullPointerException e) {
     * // ...
     * } finally {
     * // ...
     * }
     * </pre>
     * Java 7 introduced two things:
     * <ul>
     * <li>Resources can be specified after "try", but only variable declarations (VariableDeclarationExpr.)</li>
     * <li>A single catch can catch multiple exception types. This uses the IntersectionType.</li>
     * </ul>
     * <h2>Java 9+</h2>
     * <pre>
     * try (r) {
     * // ...
     * } catch (IOException|NullPointerException e) {
     * // ...
     * } finally {
     * // ...
     * }
     * </pre>
     * Java 9 finishes resources: you can now refer to a resource that was declared somewhere else.
     * The following types are allowed:
     * <ul>
     * <li>VariableDeclarationExpr: "X x = new X()" like in Java 7-8.</li>
     * <li>NameExpr: "a".</li>
     * <li>FieldAccessExpr: "x.y.z", "super.test" etc.</li>
     * </ul>
     */
    static CodeBlock makeTryStatement(ImmutableList<CodeBlock> resources, CodeBlock tryBlock, ImmutableList<CodeBlock> catchClauses, Optional<CodeBlock> finallyBlock){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("try");
        if(resources.size() != 0){
            builder.add("(");
            for(int i = 0; i < resources.size(); i ++){
                if(i != resources.size() - 1){
                    builder.add("$L," , resources.get(i));
                }else{
                    builder.add("$L)", resources.get(i));
                }
            }
        }
        builder.add(tryBlock);
        for(CodeBlock catchClause: catchClauses){
            builder.add(catchClause);
        }
        if(finallyBlock.isPresent()){
            builder.add(finallyBlock.get());
        }
        return builder.build();
    }

    /**
     * A while statement.
     * <br/><code>while(true) { ... }</code>
     */
    static CodeBlock makeWhileStatement(CodeBlock condition, CodeBlock body){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("while($L)", condition);
        builder.add(body);
        builder.endControlFlow();
        return builder.build();
    }
}
