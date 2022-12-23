// Event loops in Vert.x are used to process asynchronous events. In Spring,
// asynchronous events can be processed using @Async methods and
// CompletableFuture.
// This codemod converts Vert.x event loops to Spring @Async methods and
// CompletableFuture.
// It uses JDT to parse the Java source code and then uses the AST to make the
// necessary changes.
// 
// Path: app/src/main/java/com/brysgo/VertxToSpring/ConvertVerticlesToSpringComponentsCodemod.java
// Compare this snippet from app/src/main/java/com/brysgo/VertxToSpring/ConvertVertxEventLoopsToAsyncCodemod.java:
//

package com.brysgo.VertxToSpring;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ConvertEventLoopsToSpringBootCodemod {

  @SuppressWarnings("unchecked")
  public static void install(CompilationUnit cu) {
    cu.accept(new ASTVisitor() {

      @Override
      public boolean visit(TypeDeclaration td) {
        for (MethodDeclaration md : td.getMethods()) {
          if (isEventLoopMethod(md)) {
            AST ast = td.getAST();
            ImportDeclaration asyncImport = ast.newImportDeclaration();
            Name asyncImportName = ast.newName("org.springframework.scheduling.annotation.Async");
            asyncImport.setName(asyncImportName);
            cu.imports().add(asyncImport);

            SimpleName asyncAnnotation = ast.newSimpleName("Async");
            MarkerAnnotation markerAnnotation = ast.newMarkerAnnotation();
            markerAnnotation.setTypeName(asyncAnnotation);
            md.modifiers().add(markerAnnotation);
          }
        }
        return true;
      }

      private boolean isEventLoopMethod(MethodDeclaration md) {
        if (md.parameters().size() != 1) {
          return false;
        }
        SingleVariableDeclaration param = (SingleVariableDeclaration) md.parameters().get(0);
        return "io.vertx.core.Handler".equals(param.getType().toString());
      }
    });
  }
}
