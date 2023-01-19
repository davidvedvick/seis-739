package info.davidvedvick.seis739.catpics.users

import info.davidvedvick.seis739.catpics.security.UnauthenticatedCatEmployee
import info.davidvedvick.seis739.catpics.security.UserAuthenticationManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.BadCredentialsException

class `given an existing user without a password` {
    class `when logging the user in` {
        private val services by lazy {
            UserAuthenticationManager(
                mockk {
                    coEvery { findByEmail("ZtyPVt") } returns CatEmployee(315, "ZtyPVt", "", true)
                },
                mockk(),
            )
        }

        private lateinit var exception: BadCredentialsException

        @BeforeAll
        fun act() {
            runBlocking {
                try {
                    services.authenticate(UnauthenticatedCatEmployee("ZtyPVt", "MnI875"))
                } catch (e: BadCredentialsException) {
                    exception = e
                }
            }
        }

        @Test
        fun `then the user is not authenticated`() {
            exception.message `should be` "No password"
        }
    }
}