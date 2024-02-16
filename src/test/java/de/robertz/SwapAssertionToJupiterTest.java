package de.robertz;

import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class SwapAssertionToJupiterTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new SwapAssertionToJupiter());
        //spec.allSources(s -> s.markers(javaVersion(17)));
        spec.parser(JavaParser.fromJavaVersion()
            .classpathFromResources(new InMemoryExecutionContext(),
                    // Old
                    "testng-6.4",
                    // New
                    "junit-jupiter-5.5.2"
            ).logCompilationWarningsAndErrors(true));
    }

    @Test
    void swapsAssertionToJupiter() {
        rewriteRun(
                java(
                        """
                            package com.yourorg;
        
                            import org.testng.annotations.Test;
                            import org.testng.Assert;
                            
                            class SomeTest {
                                @Test
                                public void testSomething() {
                                    Assert.assertFalse(false);
                                }
                            }
                        """,
                        """
                            package com.yourorg;
        
                            import org.junit.jupiter.api.Test;
                            import org.junit.jupiter.api.Assertions.assertFalse;
                            
                            class SomeTest {
                                @Test
                                public void testSomething() {
                                    assertFalse(false);
                                }
                            }
                        """
                )
        );
    }
}
