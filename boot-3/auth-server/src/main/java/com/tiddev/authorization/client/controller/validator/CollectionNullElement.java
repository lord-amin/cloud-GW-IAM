package com.tiddev.authorization.client.controller.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CollectionNullElement.CollectionNullElementValidator.class)
public @interface CollectionNullElement {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CollectionNullElementValidator implements ConstraintValidator<CollectionNullElement, Collection<?>> {

        @Override
        public void initialize(CollectionNullElement constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
        }

        @Override
        public boolean isValid(Collection<?> objects, ConstraintValidatorContext constraintValidatorContext) {
            int nullIndex = 0;
            for (Object object : objects) {
                if (ObjectUtils.isEmpty(object)) {
                    ((ConstraintValidatorContextImpl) constraintValidatorContext)
                            .addMessageParameter("index", nullIndex);
                    return false;
                }
                nullIndex++;
            }
            return false;
        }
    }
}

