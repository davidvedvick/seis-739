package info.davidvedvick.seis739.catpics.users

import info.davidvedvick.seis739.catpics.users.authorization.AuthenticatedCatEmployee
import info.davidvedvick.seis739.catpics.users.authorization.UnauthenticatedCatEmployee
import info.davidvedvick.seis739.catpics.users.authorization.UserAuthenticationManager
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class `given a new user` {
    class `when logging the user in` {
        private val services by lazy {
            UserAuthenticationManager(
                mockk {
                    every { findByEmail(any()) } returns null

                    every { save(any()) } answers {
                        addedUser = firstArg()
                        firstArg()
                    }
                },
                mockk {
                    every { encode("SOyRfcI") } returns "eOLdjk"
                    every { matches("SOyRfcI", "eOLdjk") } returns true
                },
            )
        }

        private var addedUser: User? = null
        private var user: AuthenticatedCatEmployee? = null

        @BeforeAll
        fun act() {
            user = services.authenticate(UnauthenticatedCatEmployee("4Z00cpZ", "SOyRfcI")) as? AuthenticatedCatEmployee
        }

        @Test
        fun `then the email is correct`() {
            addedUser?.email `should be equal to` "4Z00cpZ"
        }

        @Test
        fun `then the password is correct`() {
            addedUser?.password `should be equal to` "eOLdjk"
        }

        @Test
        fun `then the authenticated user email is correct`() {
            user?.name `should be equal to` addedUser?.email
        }

        @Test
        fun `then the authenticated user password is correct`() {
            user?.credentials `should be equal to` addedUser?.password
        }
    }
}