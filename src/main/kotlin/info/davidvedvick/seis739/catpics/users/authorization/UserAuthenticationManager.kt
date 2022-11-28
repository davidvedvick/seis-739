package info.davidvedvick.seis739.catpics.users.authorization

import info.davidvedvick.seis739.catpics.users.UserRepository
import info.davidvedvick.seis739.catpics.value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class UserAuthenticationManager(private val userRepository: UserRepository) : AuthenticationManager {
    override fun authenticate(authentication: Authentication?): Authentication? = authentication?.run {
        val name = authentication.name
        val password = authentication.credentials.toString()

        userRepository
            .findByEmail(name)
            .value
            ?.takeIf { it.password == password }
            ?.let { UsernamePasswordAuthenticationToken(name, password) }
    }
}