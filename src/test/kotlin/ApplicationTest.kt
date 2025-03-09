package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {



}

/*

Let's implement **JWT Refresh Tokens** in Ktor step by step! 🚀

---

# **✅ Steps to Implement JWT with Refresh Tokens in Ktor**
### **1️⃣ Generate Access & Refresh Tokens on Login**
### **2️⃣ Use Refresh Token to Get a New Access Token**
### **3️⃣ Protect Routes Using JWT Authentication**
### **4️⃣ Store Refresh Tokens Securely in the Database**
### **5️⃣ Logout by Revoking the Refresh Token**

---

## **🔹 Step 1: Setup Dependencies**
First, ensure you have these dependencies in your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.ktor:ktor-server-auth-jwt:2.3.4")  // JWT authentication
    implementation("com.auth0:java-jwt:4.2.1")           // JWT library
    implementation("org.jetbrains.exposed:exposed-core:0.41.1") // For database
}
```

---

## **🔹 Step 2: Create Token Generation Logic**
We need **two tokens**:
- **Access Token (JWT)** → Expires in **15 minutes**, used for API requests.
- **Refresh Token** → Expires in **7 days**, used to get a new access token.

### **🔹 Token Utility (TokenService.kt)**
```kotlin
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object TokenService {
    private const val SECRET = "super_secret_key" // 🔴 Change this in production!
    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateAccessToken(userId: Int, email: String): String {
        return JWT.create()
            .withClaim("id", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
            .sign(algorithm)
    }

    fun generateRefreshToken(userId: Int): String {
        return JWT.create()
            .withClaim("id", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
            .sign(algorithm)
    }
}
```

---

## **🔹 Step 3: Store Refresh Tokens in the Database**
### **🔹 Refresh Token Table**
```kotlin
object RefreshTokensTable : IntIdTable() {
    val userId = reference("user_id", UsersTable)
    val token = varchar("token", 512)
    val expiresAt = datetime("expires_at")
}
```

### **🔹 Refresh Token Entity**
```kotlin
class RefreshTokenEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RefreshTokenEntity>(RefreshTokensTable)

    var user by UsersEntity referencedOn RefreshTokensTable.userId
    var token by RefreshTokensTable.token
    var expiresAt by RefreshTokensTable.expiresAt
}
```

---

## **🔹 Step 4: Implement User Login with Refresh Tokens**
```kotlin
class AuthService(private val userRepository: UserRepository) {

    suspend fun login(email: String, password: String): AuthResponse {
        val user = userRepository.getUserByEmail(email) ?: throw AuthException("Invalid credentials")

        if (user.password != hashPassword(password)) { // 🔒 Hash password before checking
            throw AuthException("Invalid credentials")
        }

        val accessToken = TokenService.generateAccessToken(user.id, user.email)
        val refreshToken = TokenService.generateRefreshToken(user.id)

        // 🔹 Store Refresh Token in DB
        transaction {
            RefreshTokenEntity.new {
                this.user = user
                this.token = refreshToken
                this.expiresAt = DateTime.now().plusDays(7)
            }
        }

        return AuthResponse(accessToken, refreshToken)
    }
}
```

### **🔹 Response Model**
```kotlin
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
```

---

## **🔹 Step 5: Implement Refresh Token Endpoint**
When the **access token expires**, the user sends a **refresh token** to get a new access token.

```kotlin
class AuthController(private val userRepository: UserRepository) {
    suspend fun refreshToken(refreshToken: String): AuthResponse {
        val tokenEntity = transaction {
            RefreshTokenEntity.find { RefreshTokensTable.token eq refreshToken }
                .firstOrNull()
        } ?: throw AuthException("Invalid refresh token")

        if (tokenEntity.expiresAt.isBeforeNow) {
            throw AuthException("Refresh token expired")
        }

        val newAccessToken = TokenService.generateAccessToken(tokenEntity.user.id, tokenEntity.user.email)

        return AuthResponse(newAccessToken, refreshToken) // 🔄 Same refresh token, new access token
    }
}
```

---

## **🔹 Step 6: Protect Routes with JWT**
In `Application.kt`, configure authentication:

```kotlin
fun Application.configureSecurity() {
    authentication {
        jwt {
            verifier(
                JWT.require(Algorithm.HMAC256("super_secret_key")) // ⚠️ Use env variables in production!
                    .build()
            )
            validate { credential ->
                credential.payload.getClaim("id").asInt()?.let { JWTPrincipal(credential.payload) }
            }
        }
    }
}
```

---

## **🔹 Step 7: Logout (Delete Refresh Token)**
To log out a user, **delete their refresh token** from the database.

```kotlin
suspend fun logout(refreshToken: String) {
    transaction {
        RefreshTokenEntity.find { RefreshTokensTable.token eq refreshToken }
            .forEach { it.delete() }
    }
}
```

---

# **✅ How It Works**
### **1️⃣ Login**
- User enters email & password.
- Server **returns**:
  - **Access Token** (expires in 15 min)
  - **Refresh Token** (expires in 7 days)

### **2️⃣ Access Secure API**
- User sends **Access Token** in `Authorization: Bearer <token>`.
- If the token is valid, the request is allowed.

### **3️⃣ Refresh Token (When Access Token Expires)**
- User sends **Refresh Token** to `/refresh-token`.
- Server verifies and issues a **new Access Token**.

### **4️⃣ Logout**
- User sends `refreshToken`, and we **delete it from the database**.

---

# **🚀 Summary**
✅ **Access Token (JWT) for authentication** (short-lived, 15 min).
✅ **Refresh Token for re-authentication** (stored in DB, 7 days).
✅ **Query database only when necessary** (refreshing token).
✅ **Logout by revoking refresh token** (deleting it from DB).

Would you like me to write a complete **Ktor route for authentication**? 🚀
 */