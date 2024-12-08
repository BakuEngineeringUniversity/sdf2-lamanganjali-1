package org.example.bookapilast.service

import org.example.bookapilast.model.User
import org.example.bookapilast.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService @Autowired constructor(
    private val userRepository: UserRepository
) {

    private val passwordEncoder = BCryptPasswordEncoder()

    fun login(username: String, password: String): Boolean {
        return try {
            val user = userRepository.findByUsername(username)
            user != null && passwordEncoder.matches(password, user.password)
        } catch (e: Exception) {
            throw RuntimeException("Error during login: ${e.message}", e)
        }
    }

    fun signUp(user: User): User {
        return try {
            validateUserInput(user)

            val existingUser = userRepository.findByUsername(user.username)
            if (existingUser != null) {
                throw IllegalArgumentException("Username already exists")
            }

            val hashedPassword = hashPassword(user.password)
            val userToSave = user.copy(password = hashedPassword)
            userRepository.save(userToSave)
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Error during sign-up: ${e.message}", e)
        }
    }

    fun getUserById(id: Long): User? {
        return try {
            userRepository.findById(id).orElse(null)
        } catch (e: Exception) {
            throw RuntimeException("Error retrieving user with ID $id: ${e.message}", e)
        }
    }

    fun deleteUser(id: Long): Boolean {
        return try {
            val userExists = userRepository.existsById(id)
            if (userExists) {
                userRepository.deleteById(id)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            throw RuntimeException("Error deleting user with ID $id: ${e.message}", e)
        }
    }

    fun updateUser(id: Long, updatedUser: User): User? {
        return try {
            val existingUser = userRepository.findById(id).orElse(null)
            if (existingUser != null) {
                validateUserInput(updatedUser)

                val updated = existingUser.copy(
                    username = updatedUser.username,
                    password = hashPassword(updatedUser.password)
                )
                userRepository.save(updated)
            } else {
                null
            }
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Error updating user with ID $id: ${e.message}", e)
        }
    }

    private fun hashPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    private fun validateUserInput(user: User) {
        if (!user.username.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))) {
            throw IllegalArgumentException("Invalid email format for username")
        }
        if (user.password.length <= 6) {
            throw IllegalArgumentException("Password must be longer than 6 characters")
        }
    }
}
