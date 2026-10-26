package io.turismo.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.turismo.backend.config.SecurityConfig;
import io.turismo.backend.dto.user.UserLoginDTO;
import io.turismo.backend.model.User;
import org.springframework.http.ResponseEntity;

@Tag(name = "Autenticação", description = "Endpoints para login, renovação de token, logout e consulta do usuário autenticado")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public interface AuthDocs {

    @Operation(summary = "Realizar login", description = "Autentica o usuário com e-mail e senha, retornando o token de acesso e cookie HttpOnly com refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    ResponseEntity<?> login(UserLoginDTO loginDTO);

    @Operation(summary = "Renovar token de acesso", description = "Gera um novo access token e rotaciona o refresh token a partir do cookie HttpOnly")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token ausente, expirado ou inválido")
    })
    ResponseEntity<?> refreshToken(String refreshTokenStr);

    @Operation(summary = "Realizar logout", description = "Invalida o refresh token atual e remove o cookie HttpOnly")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    })
    ResponseEntity<?> logout(String refreshTokenStr);

    @Operation(summary = "Consultar usuário autenticado", description = "Retorna os dados do perfil do usuário autenticado no contexto atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do usuário retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    ResponseEntity<?> getCurrentUser(User user);
}
