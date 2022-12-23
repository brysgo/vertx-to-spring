package com.brysgo.VertxToSpring;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ConvertVerticlesToSpringComponentsCodemod {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: ConvertVerticlesToSpringComponentsCodemod <path-to-codebase>");
            System.exit(1);
        }

        String codebasePath = args[0];

        ASTParser parser = ASTParser.newParser(AST.JLS11);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);
        parser.setEnvironment(null, null, null, true);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(TypeDeclaration td) {
                SimpleType superclass = td.getSuperclassType();
                if (superclass != null && "AbstractVerticle".equals(superclass.getName().getFullyQualifiedName())) {
                    AST ast = td.getAST();
                    MarkerAnnotation componentAnnotation = ast.newMarkerAnnotation();
                    componentAnnotation.setTypeName(ast.newSimpleName("Component"));
                    td.modifiers().add(componentAnnotation);
                    ImportDeclaration componentImport = ast.newImportDeclaration();
                    Name componentImportName = ast.newName("org.springframework.stereotype.Component");
                    componentImport.setName(componentImportName);
                    cu.imports().add(componentImport);
                }
                return true;
            }
        });
    }
}
