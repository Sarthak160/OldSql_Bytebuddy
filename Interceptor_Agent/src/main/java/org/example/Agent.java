package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class Agent {

    public static void premain(String arguments, Instrumentation instrumentation) {

        System.out.println("Premain: Start Advice Agent to get running methods");

        new AgentBuilder.Default()

                .type(ElementMatchers.named("com.mysql.jdbc.MysqlIO"))
                .transform((builder, typeDescription, classLoader) -> {
                    ClassFileLocator.Compound compound = new ClassFileLocator.Compound(ClassFileLocator.ForClassLoader.of(classLoader),
                            ClassFileLocator.ForClassLoader.ofClassPath());
                    System.out.println("Inside Sql Transformer");
                    return builder.method(isFinal().and(named("sqlQueryDirect"))
                                    .and(returns(named("com.mysql.jdbc.ResultSetInternalMethods"))))
                            .intercept(MethodDelegation.to(TypePool.Default.of(compound).describe("org.example.Interceptor").resolve()));
                }).installOn(instrumentation);
    }
}