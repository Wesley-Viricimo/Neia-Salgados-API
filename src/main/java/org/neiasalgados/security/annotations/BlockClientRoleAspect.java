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

@Aspect
@Component
public class BlockClientRoleAspect {

    @Around("@annotation(BlockClientRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        UserSecurity userSecurity = (UserSecurity) authentication.getPrincipal();
        if (UserRole.CLIENTE.name().equals(userSecurity.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))) {
            throw new AccessDeniedException("Acesso restrito. Este recurso está disponível apenas para usuários administrativos.");
        }

        return joinPoint.proceed();
    }
}