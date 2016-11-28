package io.netty.mvc.doc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.netty.mvc.utils.ClassUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;

/**
 * 展示Controller文档
 */
@Controller
@RequestMapping("/api")
public class APIAction {

    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/doc",method = RequestMethod.GET)
    public String show(ModelMap modelMap) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        StringBuffer stringBuffer = new StringBuffer();
        List<Class<?>> classes = ClassUtil.getClasses("io.netty.mvc.controller");
        int seq = 0;
        for (Class clas : classes) {
            CtClass ctClass = pool.get(clas.getName());
            Annotation[] annotations = clas.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequestMapping) {
                    RequestMapping anno_RequestMapping = (RequestMapping) annotation;
                    stringBuffer.append("<div class=\"title\" id=\""+seq+"\"><a onclick=\"showApi(this)\">类名："+ clas.getName()+"&nbsp;&nbsp;请求地址："+anno_RequestMapping.value()[0]+"&nbsp;&nbsp;类描述："+anno_RequestMapping.name()+"</a></div>");
                    stringBuffer.append("<table class=\"list\" id=\"list_"+seq+"\">");
                    Method[] methods = clas.getDeclaredMethods();
                    for (Method method : methods) {
                        boolean hasRequestMappings = method.isAnnotationPresent(RequestMapping.class);
                        if(hasRequestMappings){
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                            stringBuffer.append("<tr class=\"method\"><td>方法名："+method.getName()+"</td><td>方法描述："+requestMapping.name()+"</td>");
                            stringBuffer.append("<td>请求地址："+requestMapping.value()[0]+"</td><td>请求类型："+requestMapping.method()[0]+"</td></tr>");

                            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
                            if(ctMethod.getParameterTypes().length>0){
                                stringBuffer.append("<tr><td colspan=\"4\">");
                                stringBuffer.append("请求参数：(&nbsp;");
                                CodeAttribute codeAttribute = ctMethod.getMethodInfo().getCodeAttribute();
                                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                                        .getAttribute(LocalVariableAttribute.tag);
                                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                                String[] paramNames = new String[ctMethod.getParameterTypes().length];
                                for (int i = 0; i < paramNames.length; i++) {
                                    if(i==paramNames.length-1){
                                        stringBuffer.append(ctMethod.getParameterTypes()[i].getName()+"&nbsp;"+attr.variableName(i + pos));
                                    }else{
                                        stringBuffer.append(ctMethod.getParameterTypes()[i].getName()+"&nbsp;"+attr.variableName(i + pos)+"&nbsp;,&nbsp;");
                                    }
                                }
                                stringBuffer.append("&nbsp;)</td></tr>");
                            }else{
                                stringBuffer.append("<tr><td colspan=\"4\">请求参数：无</td></tr>");
                            }

                            stringBuffer.append("<tr><td colspan=\"4\">返回类型：" + method.getReturnType().getName()+"</td></tr>");
                        }
                    }
                    stringBuffer.append("</table>");
                    seq ++;
                }
            }
        }
        modelMap.addAttribute("apistrs",stringBuffer.toString());
        return "api";
    }
}
