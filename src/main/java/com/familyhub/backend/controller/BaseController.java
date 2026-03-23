package com.familyhub.backend.controller;

import com.familyhub.backend.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;

public abstract class BaseController {

    protected AuthenticatedUser currentUser(Authentication authentication) {
        return (AuthenticatedUser) authentication.getPrincipal();
    }
}
