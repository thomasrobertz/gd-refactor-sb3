package de.robertz;

import lombok.Value;
import lombok.EqualsAndHashCode;
import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;

@Value
@EqualsAndHashCode(callSuper = false)
public class SayHelloRecipe extends Recipe {

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new SayHelloVisitor();
    }

    public class SayHelloVisitor extends JavaIsoVisitor<ExecutionContext> {

        private final JavaTemplate helloTemplate =
                JavaTemplate.builder( "public String hello() { return \"Hello from #{}!\"; }")
                        .build();

        @Override
        public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl,
                ExecutionContext executionContext) {

            classDecl = classDecl.withBody(helloTemplate.apply(new Cursor(getCursor(), classDecl.getBody()),
                    classDecl.getBody().getCoordinates().lastStatement()));

            return classDecl;
        }
    }

    @Override
    public String getDisplayName() {
        return "HelloDisplay";
    }

    @Override
    public String getDescription() {
        return "HelloDesc";
    }
}
