package com.tiddev.authorization.client.controller.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
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
@Constraint(validatedBy = CollectionEmpty.CollectionEmptyValidator.class)
public @interface CollectionEmpty {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CollectionEmptyValidator implements ConstraintValidator<CollectionEmpty, Collection<?>> {

        @Override
        public void initialize(CollectionEmpty constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
        }

        @Override
        public boolean isValid(Collection<?> objects, ConstraintValidatorContext constraintValidatorContext) {
            if (ObjectUtils.isEmpty(objects) || objects.isEmpty())
                return false;
            return true;

        }
    }
}

