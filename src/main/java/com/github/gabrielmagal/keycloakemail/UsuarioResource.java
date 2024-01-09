package com.github.gabrielmagal.keycloakemail;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Path("usuario")
public class UsuarioResource {
    Keycloak keycloak;

    @PostConstruct
    public void initKeycloak() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8081")
                .realm("master")
                .clientId("admin-cli")
                .grantType("password")
                .username("admin")
                .password("admin")
                .build();
    }

    @POST
    @Path("/keycloak-update-password")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateUserEmail(@QueryParam("username") String username) {
        UsersResource usersResource = keycloak.realm("master").users();
        List<UserRepresentation> users = usersResource.searchByUsername(username, true);
        if (!users.isEmpty()) {
            usersResource.get(users.get(0).getId()).executeActionsEmail(List.of("UPDATE_PASSWORD"));
            return Response.ok().encoding("Um e-mail de redefinição de senha foi enviado para o usuário.").build();
        }
        return Response.serverError().encoding("Usuário não encontrado.").build();
    }
}