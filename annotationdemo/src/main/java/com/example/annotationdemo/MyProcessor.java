package com.example.annotationdemo;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.example.annotationdemo.BindId")
public class MyProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> bindIdElements = roundEnvironment.getElementsAnnotatedWith(BindId.class);
        StringBuilder sb = new StringBuilder();
        for (Element element : bindIdElements) {
            //1.包名
            PackageElement packageElement = mElementUtils.getPackageOf(element);
            String pkName = packageElement.getQualifiedName().toString();
            //包装类类型
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            String enclosingName = enclosingElement.getQualifiedName().toString();

            VariableElement bindViewElement = (VariableElement) element;
            //注解变量名
            String bindViewFiledName = bindViewElement.getSimpleName().toString();
            //注解的变量类型
            String bindViewFiledClassType = bindViewElement.asType().toString();

            //获取注解元数据
            BindId bindView = element.getAnnotation(BindId.class);
            int id = bindView.value();
            sb.append(bindViewFiledClassType);
            sb.append("___");
            sb.append(bindViewFiledName);
            sb.append("___");
            sb.append(id);
            sb.append("|||||");
            note(sb.toString());//编译期间在Gradle console可查看打印信息
        }

        //生成文件
        saveFile("com.example.annotationdemo", sb.toString());
        return true;
    }

    private void saveFile(String pkNameQ, String content) {
        String pkName = pkNameQ;
        try {
            JavaFileObject jfo = mFiler.createSourceFile(pkName + ".ViewBindId", new Element[]{});
            Writer writer = jfo.openWriter();
            writer.write(writeCode(pkName, content));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String writeCode(String pkName, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("package " + pkName + ";\n\n");
        builder.append("public class ViewBindId { \n\n");
        builder.append("public static void main(String[] args){ \n");
        builder.append("System.out.println(\"" + content + "\");\n");
        builder.append("}\n");
        builder.append("}");
        return builder.toString();
    }


    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void note(String format, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }
}
