package dagger.internal.codegen;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;


//TODO fill such expression related with expression
//TODO fill some type arguments
public class CodeExprUtil {

    /**
     * An annotation that uses only the annotation type name.
     * <br/><code>@Override</code>
     */
    static CodeBlock makeMarkerAnnotationExpr(String annotation){
        return CodeBlock.of("@$L", annotation);
    }

    /**
     *
     * An annotation that has a single value. <br/><code>@Count(15)</code>
     */
    static CodeBlock makeSingleMemberAnnotationExpr(String annotation, CodeBlock memberValue){
        return CodeBlock.of("@$L($L)", annotation, memberValue);
    }

    /**
     *
     * An annotation that has zero or more key-value pairs.<br/><code>@Mapping(a=5, d=10)</code>
     */
    static CodeBlock makeNormalAnnotationExpr(String annotation, ImmutableList<CodeBlock> pairs){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("@$L(", annotation);
        for(int i = 0; i < pairs.size(); i ++){
            if(i != pairs.size() - 1){
                builder.add("$L,", pairs.get(i));
            }else{
                builder.add("$L", pairs.get(i));
            }
        }
        builder.add(")");
        return builder.build();
    }

    /**
     *
     * Array brackets [] being used to get a value from an array.
     * In <br/><code>getNames()[15*15]</code> the name expression is getNames() and the index expression is 15*15.
     */
    static CodeBlock makeArrayAccessExpr(CodeBlock name, CodeBlock index){
        return CodeBlock.of("$L.[$L]", name, index);
    }


    /**
     *
     * <code>new int[5][4][][]</code> or <code>new int[][]{{1},{2,3}}</code>.
     *
     * <br/>"int" is the element type.
     * <br/>All the brackets are stored in the levels field, from left to right.
     *
     *
     * In <code>new int[1][2];</code> there are two ArrayCreationLevel objects,
     * the first one contains the expression "1",
     * the second the expression "2".
     */
    static CodeBlock makeArrayCreationExpr(TypeName type, ImmutableList<CodeBlock> levels, Optional<CodeBlock> initializer){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("new $T", type);
        for(CodeBlock level : levels){
            builder.add("[$L]", level);
        }
        if(initializer.isPresent()){
            builder.add("$L", initializer.get());
        }
        return builder.build();
    }


    /**
     *
     * The initialization of an array. In the following sample, the outer { } is an ArrayInitializerExpr.
     * It has two expressions inside: two ArrayInitializerExprs.
     * These have two expressions each, one has 1 and 1, the other two and two.
     * <br/><code>new int[][]{{1, 1}, {2, 2}};</code>
     *
     */
    static CodeBlock makeArrayInitializeExpr(ImmutableList<CodeBlock> expressions){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("{");
        for(int i = 0; i < expressions.size(); i ++){
            if(i != expressions.size() - 1){
                builder.add("$L, ", expressions.get(i));
            }else{
                builder.add("$L", expressions.get(i));
            }
        }
        builder.add("}");
        return builder.build();
    }


    /**
     *
     * An assignment expression. It supports the operators that are found the the AssignExpr.Operator enum.
     * <br/><code>a=5</code>
     * <br/><code>time+=500</code>
     * <br/><code>watch.time+=500</code>
     * <br/><code>(((time)))=100*60</code>
     * <br/><code>peanut[a]=true</code>
     *
     */
    static CodeBlock makeAssignExpr(CodeBlock target, String operator, CodeBlock value){
        return CodeBlock.of("$L $L $L", target, operator, value);
    }

    /**
     * An expression with an expression on the left, an expression on the right, and an operator in the middle.
     * It supports the operators that are found the the BinaryExpr.Operator enum.
     * <br/><code>a && b</code>
     * <br/><code>155 * 33</code>
     *
     */
    static CodeBlock makeBinaryExpr(CodeBlock left, String operator, CodeBlock right){
        return CodeBlock.of("$L $L $L", left, operator, right);
    }

    /**
     *
     * The boolean literals.
     * <br/><code>true</code>
     * <br/><code>false</code>
     */
    static CodeBlock makeBoolLiteralExpr(boolean value){
        return CodeBlock.of("$L", String.valueOf(value));
    }

    /**
     *
     * A typecast. The (long) in <code>(long)15</code>
     * (int)10 / 2.0
     */
    //TODO may need to interpret the precedence
    static CodeBlock makeCastExpr(TypeName typeName, CodeBlock expression){
        return CodeBlock.of("($T)$L", typeName, expression);
    }

    /**
     *
     * A literal character.
     * <br/><code>'a'</code>
     * <br/><code>'\t'</code>
     * <br/><code>'Ω'</code>
     * <br/><code>'\177'</code>
     * <br/><code>'💩'</code>
     */
    static CodeBlock makeCharLiteralExpr(char c){
        return CodeBlock.of("$L", String.valueOf(c));
    }

    /**
     * Defines an expression that accesses the class of a type.
     * <br/><code>Object.class</code>
     *
     * use $T instead of $L
     */
    static CodeBlock makeClassExpr(TypeName typeName){
        return CodeBlock.of("$T.class", typeName);
    }

    /**
     * The ternary conditional expression.
     * In <code>b==0?x:y</code>, b==0 is the condition, x is thenExpr, and y is elseExpr.
     *
     */
    static CodeBlock makeConditionalExpr(CodeBlock condition, CodeBlock thenBlock, CodeBlock elseBlock){
        return CodeBlock.of("$L ? $L : $L", condition, thenBlock, elseBlock);
    }


    /**
     * A float or a double constant. This value is stored exactly as found in the source.
     * <br/><code>100.1f</code>
     * <br/><code>23958D</code>
     * <br/><code>0x4.5p1f</code>
     */
    static CodeBlock makeDoubleLiteralExpr(double d){
        return CodeBlock.of("$L", String.valueOf(d));
    }

    /**
     * An expression between ( ).
     * <br/><code>(1+1)</code>
     */
    static CodeBlock makeEnclosedExpr(CodeBlock enclosed){
        return CodeBlock.of("($L)", enclosed);
    }

    /**
     * Access of a field of an object.
     * <br/>In <code>person.name</code> "name" is the name and "person" is the scope.
     */
    //TODO this may have typeArguments but I ignore them now, shall be added in the future
    static CodeBlock makeFieldAccessExpr(CodeBlock scope, Optional<ImmutableList<TypeName>>typeArguments,  String name){
        return CodeBlock.of("$L.$L", scope, name);
    }


    /**
     * Usage of the instanceof operator.
     * <br/><code>tool instanceof Drill</code>
     */
    static CodeBlock makeInstanceOfExpr(CodeBlock expression, TypeName typeName){
        return CodeBlock.of("$L instanceof $T", expression,  typeName);
    }

    /**
     * All ways to specify an int literal.
     * <br/><code>8934</code>
     * <br/><code>0x01</code>
     * <br/><code>022</code>
     * <br/><code>0B10101010</code>
     * <br/><code>99999999L</code>
     */
    static CodeBlock makeIntegerLiteralExpr(int i){
        return CodeBlock.of("$L", String.valueOf(i));
    }

//TODO support this in the future
//    CodeBlock makeLamdaExpr()

    /**
     * All ways to specify a long literal.
     * <br/><code>8934l</code>
     * <br/><code>0x01L</code>
     * <br/><code>022l</code>
     * <br/><code>0B10101010L</code>
     * <br/><code>99999999L</code>
     *
     */
    static CodeBlock makeLongLiteralExpr(long l){
        return CodeBlock.of("$L", String.valueOf(l));
    }

    /**
     *
     * A method call on an object. <br/><code>circle.circumference()</code> <br/>In <code>a.&lt;String&gt;bb(15);</code> a
     * is the scope, String is a type argument, bb is the name and 15 is an argument.
     *
     *
     * may need more concern
     */
    static CodeBlock makeMethodCallExpr(Optional<CodeBlock> scope, Optional<ImmutableList<TypeName>>typeArguments, String name,  ImmutableList<CodeBlock> arguments){
        CodeBlock.Builder builder = CodeBlock.builder();
        if(scope.isPresent()){
            builder.add("$L.", scope.get());
        }
//        else{
//            builder.add("this.");
//        }
        builder.add("$L(", name);
        for(int i = 0; i < arguments.size(); i ++){
            if(i != arguments.size() - 1){
                builder.add("$L,", arguments.get(i));
            }else{
                builder.add("$L", arguments.get(i));
            }
        }
        builder.add(")");
        return builder.build();
    }

    /**
     * Whenever a SimpleName is used in an expression, it is wrapped in NameExpr.
     * <br/>In <code>int x = a + 3;</code> a is a SimpleName inside a NameExpr.
     *
     */
    static CodeBlock makeNameExpr(String name){
        return CodeBlock.of("$L", name);
    }

    /**
     * A literal "null".
     * <br/><code>null</code>
     *
     */
    static CodeBlock makeNullLiteralExpr(){
        return CodeBlock.of("null");
    }


    /**
     * A constructor call.
     * <br/>In <code>new HashMap.Entry&lt;String, Long>(15) {public String getKey() {return null;}};</code>
     * HashMap.Entry is the type, String and Long are type arguments, 15 is an argument, and everything in { }
     * is the anonymous class body.
     * <p/>In <code>class B { class C { public void a() { new B().new C(); } } }</code> the scope is <code>new B()</code>
     * of ObjectCreationExpr <code>new B().new C()</code>
     *
     */
    static CodeBlock makeObjectCreationExpr(Optional<CodeBlock> scope, ClassName className, Optional<ImmutableList<TypeName>>typeArguments, ImmutableList<CodeBlock> arguments,
                                     Optional<CodeBlock> initializer){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("new ");
//        if(scope.isPresent()){
//            builder.add()
//        }
        builder.add("$T", className);
        builder.add("(");
        for(int i = 0; i < arguments.size(); i ++){
            if(i != arguments.size() - 1){
                builder.add("$L, ", arguments.get(i));
            }else{
                builder.add("$L", arguments.get(i));
            }
        }
        builder.add(")");
        if(initializer.isPresent()){
            builder.add(initializer.get());
        }
        return builder.build();
    }

    /**
     * A literal string.
     * <br/><code>"Hello World!"</code>
     * <br/><code>"\"\n"</code>
     * <br/><code>"\u2122"</code>
     * <br/><code>"™"</code>
     * <br/><code>"💩"</code>
     */
    static CodeBlock makeStringLiteralExpr(String value){
        return CodeBlock.of("$L", value);
    }


    /**
     * An occurrence of the "super" keyword. <br/><code>World.super.greet()</code> is a MethodCallExpr of method name greet,
     * and scope "World.super" which is a SuperExpr with classExpr "World". <br/><code>super.name</code> is a
     * FieldAccessExpr of field greet, and a SuperExpr as its scope. The SuperExpr has no classExpr.
     *
     */
    static CodeBlock makeSuperExpr(Optional<CodeBlock> classExpr){
        if(classExpr.isPresent()){
            return CodeBlock.of("$L.super", classExpr.get());
        }else{
            return CodeBlock.of("super");
        }
    }

    /**
     * This class is just instantiated as scopes for MethodReferenceExpr nodes to encapsulate Types.
     * <br/>In <code>World::greet</code> the ClassOrInterfaceType "World" is wrapped in a TypeExpr
     * before it is set as the scope of the MethodReferenceExpr.
     *
     */
    static CodeBlock makeThisExpr(Optional<CodeBlock> classExpr){
        if(classExpr.isPresent()){
            return CodeBlock.of("$L.this", classExpr.get());
        }else{
            return CodeBlock.of("this");
        }
    }


    /**
     * An expression where an operator is applied to a single expression.
     * It supports the operators that are found the the UnaryExpr.Operator enum.
     * <br/><code>11++</code>
     * <br/><code>++11</code>
     * <br/><code>~1</code>
     * <br/><code>-333</code>
     *
     */
    static CodeBlock makeUnaryExpr(CodeBlock expression, String operator, boolean isPostfix){
        if(isPostfix){
            return CodeBlock.of("$L$L", expression, operator);
        }else{
            return CodeBlock.of("$L$L", operator, expression);
        }
    }


    /**
     * A declaration of variables.
     * It is an expression, so it can be put in places like the initializer of a for loop,
     * or the resources part of the try statement.
     * <br/><code>final int x=3, y=55</code>
     *
     * <br/>All annotations preceding the type will be set on this object, not on the type.
     * JavaParser doesn't know if it they are applicable to the method or the type.
     *
     */
    static CodeBlock makeVariableDeclarationExpr(ImmutableList<String> modifiers, ImmutableList<CodeBlock> annotations, ImmutableList<CodeBlock> variables){
        CodeBlock.Builder builder = CodeBlock.builder();
        for(String modifier : modifiers){
            builder.add("$L ", modifier);
        }

        for(CodeBlock annotation: annotations){
            builder.add("$L ", annotation);
        }

        for(int i = 0; i < variables.size(); i ++){
            if(i != variables.size() - 1){
                builder.add("$L, ", variables.get(i));
            }else{
                builder.add("$L", variables.get(i));
            }
        }
        return builder.build();
    }
}
