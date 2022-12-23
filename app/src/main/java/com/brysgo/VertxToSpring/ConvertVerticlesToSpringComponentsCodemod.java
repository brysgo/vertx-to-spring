package com.brysgo.VertxToSpring;


import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ConvertVerticlesToSpringComponentsCodemod {

    @SuppressWarnings("unchecked")
    public static void install(CompilationUnit cu) {
        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(TypeDeclaration td) {
                SimpleType superclass = (SimpleType) td.getSuperclassType();
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
