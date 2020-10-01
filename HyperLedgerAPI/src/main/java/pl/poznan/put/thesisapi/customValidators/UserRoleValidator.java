package pl.poznan.put.thesisapi.customValidators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class UserRoleValidator implements ConstraintValidator<UserRole, String> {
    public final static String[] VALID_ROLES = {"supervisor", "student"};
    @Override
    public void initialize(UserRole constraintAnnotation) {
    }
    @Override
    public boolean isValid(String role, ConstraintValidatorContext context){
        return (validateRole(role));
    }
    private boolean validateRole(String role) {
        return Arrays.asList(VALID_ROLES).contains(role);
    }
}
