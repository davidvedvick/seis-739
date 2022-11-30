package info.davidvedvick.seis739.catpics.users.authorization

import com.fasterxml.jackson.databind.ObjectMapper
import info.davidvedvick.seis739.catpics.cls
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(authenticationManager: AuthenticationManager, private val jwtTokenBuilder: BuildJwtTokens) : UsernamePasswordAuthenticationFilter(authenticationManager) {
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication? =
        request
            ?.inputStream
            ?.let { ObjectMapper().readValue(it, cls<UserRequest>())}
            ?.let {
                authenticationManager.authenticate(UnauthenticatedCatEmployee(it.email, it.password))
            }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        val token = jwtTokenBuilder.generateToken(authResult as AuthenticatedCatEmployee)

        response?.addHeader(AuthenticationConstants.AuthHeaderKey, "${AuthenticationConstants.TokenPrefix} $token")
    }
}