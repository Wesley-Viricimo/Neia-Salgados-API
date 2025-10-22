package org.neiasalgados.security.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.security.UserSecurity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class AllowRoleAspect {

    @Around("@annotation(allowRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, AllowRole allowRole) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        UserSecurity userSecurity = (UserSecurity) authentication.getPrincipal();
        String userRole = userSecurity.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        UserRole[] allowedRoles = allowRole.allowedRoles();
        if (allowedRoles.length > 0) {
            boolean hasPermission = Arrays.stream(allowedRoles)
                    .anyMatch(role -> role.name().equals(userRole));

            if (!hasPermission) {
                throw new AccessDeniedException("Acesso negado. Você não tem permissão para acessar este recurso.");
            }
        }

        return joinPoint.proceed();
    }
}