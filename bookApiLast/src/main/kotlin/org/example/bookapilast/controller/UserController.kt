package org.example.bookapilast.controller

import org.example.bookapilast.model.User
import org.example.bookapilast.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/auth")
class AuthController @Autowired constructor(
    private val authService: AuthService
) {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun login(@RequestBody user: User): ResponseEntity<Map<String, Any?>> {
        logger.info("Login attempt for username: ${user.username}")
        return try {
            if (authService.login(user.username, user.password)) {
                logger.info("Login successful for username: ${user.username}")
                ResponseEntity.ok(mapOf("status" to "success", "message" to "Login successful"))
            } else {
                logger.warn("Invalid login attempt for username: ${user.username}")
                ResponseEntity.status(401).body(mapOf("status" to "error", "message" to "Invalid username or password"))
            }
        } catch (e: Exception) {
            logger.error("Error during login attempt for username: ${user.username}", e)
            ResponseEntity.status(500).body(mapOf("status" to "error", "message" to "An error occurred", "details" to e.message))
        }
    }

    @PostMapping("/signup")
    fun signUp(@RequestBody user: User): ResponseEntity<Map<String, Any?>> {
        logger.info("Signup attempt for username: ${user.username}")
        return try {
            val createdUser = authService.signUp(user)
            logger.info("User created successfully with username: ${createdUser.username}, ID: ${createdUser.id}")
            ResponseEntity.ok(mapOf("status" to "success", "message" to "User created successfully", "data" to createdUser))
        } catch (e: IllegalArgumentException) {
            logger.warn("Validation error during signup for username: ${user.username} - ${e.message}")
            ResponseEntity.status(400).body(mapOf("status" to "error", "message" to "Validation error", "details" to e.message))
        } catch (e: Exception) {
            logger.error("Error during signup for username: ${user.username}", e)
            ResponseEntity.status(500).body(mapOf("status" to "error", "message" to "An error occurred", "details" to e.message))
        }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<Map<String, Any?>> {
        logger.info("GET request for user with id: $id")
        return try {
            val user = authService.getUserById(id)
            if (user != null) {
                logger.info("User found with id: $id")
                ResponseEntity.ok(mapOf("status" to "success", "data" to user))
            } else {
                logger.warn("User not found with id: $id")
                ResponseEntity.status(404).body(mapOf("status" to "error", "message" to "User not found"))
            }
        } catch (e: Exception) {
            logger.error("Error during GET request for user with id: $id - ${e.message}", e)
            ResponseEntity.status(500).body(mapOf("status" to "error", "message" to e.message))
        }
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody updatedUser: User): ResponseEntity<Map<String, String?>> {
        logger.info("Update request for user with id: $id")
        return try {
            val user = authService.updateUser(id, updatedUser)
            if (user != null) {
                logger.info("User updated successfully with id: $id")
                ResponseEntity.ok(mapOf("status" to "success", "message" to "User updated successfully"))
            } else {
                logger.warn("User not found for update with id: $id")
                ResponseEntity.status(404).body(mapOf("sta tus" to "error", "message" to "User not found"))
            }
        } catch (e: IllegalArgumentException) {
            logger.warn("Update failed for user with id: $id - ${e.message}")
            ResponseEntity.status(400).body(mapOf("status" to "error", "message" to e.message))
        } catch (e: Exception) {
            logger.error("Error during update for user with id: $id - ${e.message}", e)
            ResponseEntity.status(500).body(mapOf("status" to "error", "message" to e.message))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Map<String, String?>> {
        logger.info("Delete request for user with id: $id")
        return try {
            val isDeleted = authService.deleteUser(id)
            if (isDeleted) {
                logger.info("User deleted successfully with id: $id")
                ResponseEntity.ok(mapOf("status" to "success", "message" to "User deleted successfully"))
            } else {
                logger.warn("User not found for deletion with id: $id")
                ResponseEntity.status(404).body(mapOf("status" to "error", "message" to "User not found"))
            }
        } catch (e: Exception) {
            logger.error("Error during delete for user with id: $id - ${e.message}", e)
            ResponseEntity.status(500).body(mapOf("status" to "error", "message" to e.message))
        }
    }
}
