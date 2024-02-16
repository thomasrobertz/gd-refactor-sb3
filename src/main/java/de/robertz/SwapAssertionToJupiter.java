package de.robertz;

import lombok.Value;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.AddImport;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RemoveImport;
import org.openrewrite.java.TreeVisitingPrinter;
import org.openrewrite.java.tree.*;
import org.openrewrite.java.tree.J.Annotation;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.J.MethodInvocation;
import java.util.Objects;

@Value
@EqualsAndHashCode(callSuper = false)
public class SwapAssertionToJupiter extends Recipe {

    @Override
    public @NotNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new SwapAssertion();
    }

    private class SwapAssertion extends JavaIsoVisitor<ExecutionContext> {

        @Override
        public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {

            // This resulted in only one import: import org.testng.annotations.Test
            // maybeRemoveImport("org.testng.annotations.Test");
            // maybeRemoveImport("org.testng.Assert");
            // maybeAddImport("org.junit.jupiter.api.Test");
            // maybeAddImport("org.junit.jupiter.api.Assertions.assertFalse");

            // Remove TestNG imports
            // I tried without force but that didn't work
            doAfterVisit(new RemoveImport<>("org.testng.annotations.Test", true));
            doAfterVisit(new RemoveImport<>("org.testng.Assert", true));

            // Add JUnit imports
            doAfterVisit(new AddImport<>("org.junit.jupiter.api.Test", null, false));
            // If I set onlyIfReferenced to true, the cycle goes away but so does the import
            doAfterVisit(new AddImport<>("org.junit.jupiter.api.Assertions.assertFalse", null, false));

            return super.visitCompilationUnit(cu, ctx);
        }

        @Override
        public MethodInvocation visitMethodInvocation(@NotNull MethodInvocation method,
                                                      @NotNull ExecutionContext executionContext) {

            // TODO Refactor (MethodDeclaration + Cursor messaging or similar approach)
            J.MethodDeclaration encloser = getCursor().firstEnclosing(MethodDeclaration.class);

            if (Objects.nonNull(encloser) && encloser.getLeadingAnnotations().stream()
                .map(Annotation::toString).toList().contains("@Test")
                    && Objects.nonNull(method.getSelect())
                    && method.getSelect().printTrimmed(getCursor()).equals("Assert")) {

                if(method.getSimpleName().startsWith("assert")) {
                    return method.withSelect(null).withName(method.getName().withPrefix(Space.EMPTY));
                }
            }

            return super.visitMethodInvocation(method, executionContext);
        }
    }

    private class DebugVisitor extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
            System.out.println(TreeVisitingPrinter.printTree(cu));
            return super.visitCompilationUnit(cu, ctx);
        }
    }

    @Override
    public String getDisplayName() {
        return "Refactor TestNG to Jupiter";
    }

    @Override
    public String getDescription() {
        return "Refactor TestNG to Jupiter.";
    }
}
