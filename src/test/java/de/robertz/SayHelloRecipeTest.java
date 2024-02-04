package de.robertz;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class SayHelloRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new SayHelloRecipe());
    }

    @Test
    void addsHelloToFooBar() {
        rewriteRun(
                java(
                        """
                            package com.yourorg;
        
                            class FooBar {
                            }
                        """,
                        """
                            package com.yourorg;
        
                            class FooBar {
                                public String hello() {
                                    return "Hello from com.yourorg.FooBar!";
                                }
                            }
                        """
                )
        );
    }
}
