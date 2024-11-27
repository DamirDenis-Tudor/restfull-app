import api.academia.Auth
import api.academia.AuthServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class AuthServiceGrpcTest {

    private var channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    private var blockingStub = AuthServiceGrpcKt.AuthServiceCoroutineStub(channel)

    @Test
    fun `test login`() = runBlocking {
        val request = Auth.LoginRequest
            .newBuilder()
            .setUsername("user1")
            .setPassword("password1")
            .build()

        val response = blockingStub.login(request)

        if(response.hasSuccess()){
            println(response.success)
        }else{
            println(response.error)
        }
    }

    @Test
    fun `test is valid`() = runBlocking {
        val request = Auth.TokenRequest
            .newBuilder()
            .setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUwMDUxIiwic3ViIjoiZWNiNWJjYTQtYWNjMy0xMWVmLTk2ODktMDI0MmFjMTUwMDAyIiwicm9sZSI6MSwiZXhwIjoxNzMyNzE4ODAzLCJqdGkiOiI0ZWEzZjM1YS0wNGYzLTRmNmYtYmFkYi1lYzY5YzA1NWM2ZDcifQ.nGXhwVFo6NICZT-5vBhDkCbTwzZA57kebIWeBRLeoKM")
            .build()

        val response = blockingStub.validateToken(request)

        if(response.hasSuccess()){
            println(response.success)
        }else{
            println(response.error)
        }
    }

    @Test
    fun `test add to blacklist`() = runBlocking {
        val request = Auth.TokenRequest
            .newBuilder()
            .setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUwMDUxIiwic3ViIjoiZWNiNWJjYTQtYWNjMy0xMWVmLTk2ODktMDI0MmFjMTUwMDAyIiwicm9sZSI6MSwiZXhwIjoxNzMyNzE4ODAzLCJqdGkiOiI0ZWEzZjM1YS0wNGYzLTRmNmYtYmFkYi1lYzY5YzA1NWM2ZDcifQ.nGXhwVFo6NICZT-5vBhDkCbTwzZA57kebIWeBRLeoKM")
            .build()

        val response = blockingStub.invalidateToken(request)

        if(response.hasSuccess()){
            println(response.success)
        }else{
            println(response.error)
        }
    }
}
