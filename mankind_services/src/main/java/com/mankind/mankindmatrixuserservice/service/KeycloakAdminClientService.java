package com.mankind.mankindmatrixuserservice.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeycloakAdminClientService {
    private static final Logger log = LoggerFactory.getLogger(KeycloakAdminClientService.class);

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakAdminClientService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public String createUser(String username, String email, String password, String firstName, String lastName, Map<String, String> customAttributes) {

        UsersResource usersResource = keycloak.realm(realm).users();

        List<UserRepresentation> found = usersResource.search(username);
        if (!found.isEmpty()) {
            throw new IllegalStateException("User already exists in Keycloak with username=" + username);
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        if (customAttributes != null && !customAttributes.isEmpty()) {
            user.setAttributes(customAttributes.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> List.of(e.getValue())
                    )));
        }

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        user.setCredentials(Collections.singletonList(cred));

        if (customAttributes != null) {
            customAttributes.forEach(user::singleAttribute);
        }

        log.debug("Creating Keycloak user '{}' in realm '{}'", username, realm);

        try (Response resp = keycloak.realm(realm).users().create(user)) {
            int status = resp.getStatus();
            if (status != Response.Status.CREATED.getStatusCode()) {
                throw new IllegalStateException("Keycloak create user failed: HTTP " + status + "): " +
                        resp.getStatusInfo().getReasonPhrase());
            }

            log.debug("Keycloak user '{}' created", username);
            return CreatedResponseUtil.getCreatedId(resp);
        }
    }

    /**
     * Update user profile fields (firstName, lastName, email) in Keycloak
     */
    public void updateUserProfile(String keycloakId, String firstName, String lastName, String email) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserRepresentation user = usersResource.get(keycloakId).toRepresentation();
        boolean changed = false;
        if (firstName != null && !firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
            changed = true;
        }
        if (lastName != null && !lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
            changed = true;
        }
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            changed = true;
        }
        if (changed) {
            usersResource.get(keycloakId).update(user);
            log.debug("Updated Keycloak user '{}' profile fields", keycloakId);
        }
    }

    /**
     * Reset a user's password in Keycloak.
     *
     * @param keycloakId the Keycloak user id
     * @param newPassword the new password
     * @param temporary whether the password should be marked as temporary
     */
    public void resetPassword(String keycloakId, String newPassword, boolean temporary) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(temporary);
        keycloak.realm(realm).users()
                .get(keycloakId)
                .resetPassword(credential);
        log.debug("Reset password for Keycloak user '{}'", keycloakId);
    }
}
