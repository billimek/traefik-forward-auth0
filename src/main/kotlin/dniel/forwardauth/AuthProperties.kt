package dniel.forwardauth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Validated
@ConfigurationProperties()
class AuthProperties {
    @NotEmpty
    lateinit var domain: String

    @NotEmpty
    lateinit var tokenEndpoint: String

    @NotEmpty
    lateinit var authorizeUrl: String

    @NotNull
    val default = Application()

    @NotNull
    val apps = ArrayList<Application>()

    class Application {
        @NotEmpty
        lateinit var name: String

        var clientId: String = ""
        var clientSecret: String = ""
        var audience: String = ""
        var scope: String = ""
        var redirectUri: String = ""
        var tokenCookieDomain: String = ""

        override fun toString(): String {
            return "Application(name='$name', clientId='$clientId', clientSecret='$clientSecret', audience='$audience', scope='$scope', redirectUrl='$redirectUri', tokenCookieDomain='$tokenCookieDomain')"
        }
    }

    override fun toString(): String {
        return "AuthProperties(domain='$domain', tokenEndpoint='$tokenEndpoint', authorizeUrl='$authorizeUrl', default=$default, apps=$apps)"
    }


    /**
     * Return application with application specific values, default values or inherited values.
     */
    fun findApplicationOrDefault(name: String?): Application {
        if (name == null) return default;

        val application = apps.find() { it.name == name }
        if (application !== null) {
            application.redirectUri = if (application.redirectUri.isNotEmpty()) application.redirectUri else default.redirectUri
            application.audience = if (application.audience.isNotEmpty()) application.audience else default.audience
            application.scope = if (application.scope.isNotEmpty()) application.scope else default.scope
            application.clientId = if (application.clientId.isNotEmpty()) application.clientId else default.clientId
            application.clientSecret = if (application.clientSecret.isNotEmpty()) application.clientSecret else default.clientSecret
            application.tokenCookieDomain = if (application.tokenCookieDomain.isNotEmpty()) application.tokenCookieDomain else default.tokenCookieDomain
            return application
        } else return default;
    }
}