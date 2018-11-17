package com.cloudmade.xlogger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;

class WrapperDataGenerator extends ClassGenerator {

    private List<String> alreadyCreatedWrappers;

    WrapperDataGenerator(ProcessingEnvironment processingEnvironment, VelocityEngine velocityEngine) {
        super(processingEnvironment, velocityEngine);
        this.processingEnvironment = processingEnvironment;
        this.velocityEngine = velocityEngine;

        this.alreadyCreatedWrappers = new ArrayList<>();
    }

    /**
     * Generates wrapper classes
     * @param annotatedElementEntities list with info about annotated elements (field name and wrapper)
     */
    void generateWrappers(List<AnnotatedElementEntity> annotatedElementEntities) {
        for (AnnotatedElementEntity annotatedElementEntity : annotatedElementEntities) {
            if (alreadyCreatedWrappers.isEmpty() || !alreadyCreatedWrappers.contains(annotatedElementEntity.getWrapperData().wrapperShortName)) {
                createWrapper(annotatedElementEntity.getWrapperData());
            }
        }
    }

    /**
     * Generates wrapper class with overridden method set(value) which logs value which is set
     * @param wrapperData info about loggable field type
     */
    private void createWrapper(WrapperData wrapperData) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("package", Const.GENERATED_PACKAGE);
        velocityContext.put("sourceClass", wrapperData.superClassShortName);
        velocityContext.put("generic", wrapperData.isPrimitive ? "" : "<T>");
        velocityContext.put("resultClassName", wrapperData.wrapperShortName);
        velocityContext.put("valueType", wrapperData.isPrimitive ? wrapperData.valueTypeName : "T");

        if (writeFile(wrapperData.wrapperFullName, VelocityTemplate.OBSERVABLE_WRAPPER, velocityContext)) {
            alreadyCreatedWrappers.add(wrapperData.wrapperShortName);
        }
    }
}
