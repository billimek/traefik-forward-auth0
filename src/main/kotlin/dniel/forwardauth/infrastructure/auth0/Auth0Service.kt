package dniel.forwardauth.infrastructure.auth0

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mashape.unirest.http.Unirest
import dniel.forwardauth.AuthProperties
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Auth0Service(val properties: AuthProperties) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)
    val TOKEN_ENDPOINT = properties.tokenEndpoint
    val JSON = jacksonObjectMapper()

    /**
     * Call Auth0 to exchange received code with a JWT Token to decode.
     */
    fun authorizationCodeExchange(code: String, clientId: String, clientSecret: String, redirectUri: String): JSONObject {
        LOGGER.info("Entered authorizationCodeExchange: code=$code")
        val tokenRequest = AuthorizationCodeTokenRequest(
                code = code,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUrl = redirectUri)

        val response = Unirest.post(TOKEN_ENDPOINT)
                .header("content-type", "application/json")
                .body(JSON.writeValueAsString(tokenRequest))
                .asJson();

        LOGGER.info("response: " + response.toString())
        return response.getBody().getObject()
    }

    /**
     * Call Auth0 to exchange received code with a JWT Token to decode.
     */
    fun clientCredentialsExchange(clientId: String, clientSecret: String, audience: String): JSONObject {
        LOGGER.info("Entered clientCredentialsExchange")
        val tokenRequest = ClientCredentialsTokenRequest(
                clientId = clientId,
                clientSecret = clientSecret,
                audience = audience)

        val response = Unirest.post(TOKEN_ENDPOINT)
                .header("content-type", "application/json")
                .body(JSON.writeValueAsString(tokenRequest))
                .asJson();

        LOGGER.info("response: " + response.toString())
        return response.getBody().getObject()
    }

    /**
     * Just a simple data class for the token request.
     */
    private data class AuthorizationCodeTokenRequest(@JsonProperty("grant_type") val grantType: String = "authorization_code",
                                                     @JsonProperty("client_id") val clientId: String,
                                                     @JsonProperty("client_secret") val clientSecret: String,
                                                     @JsonProperty("redirect_uri") val redirectUrl: String,
                                                     val code: String,
                                                     val scope: String = "openid profile"
    )

    /**
     * Just a simple data class for the token request.
     */
    private data class ClientCredentialsTokenRequest(@JsonProperty("grant_type") val grantType: String = "client_credentials",
                                                     @JsonProperty("client_id") val clientId: String,
                                                     @JsonProperty("client_secret") val clientSecret: String,
                                                     @JsonProperty("audience") val audience: String

    )

}