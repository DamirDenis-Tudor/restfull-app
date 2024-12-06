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
            .setUsername("admin.admin@example.com")
            .setPassword("admin")
            .build()

        val response = blockingStub.login(request)

        if(response.hasSuccess()){
            println(response.success)
        }else{
            println(response.error)
        }

        assert(response.hasSuccess())
    }

    @Test
    fun `test is valid`() = runBlocking {
        val request = Auth.TokenRequest
            .newBuilder()
            .setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUwMDUxIiwic3ViIjoidXNlcjEiLCJyb2xlIjoxLCJleHAiOjE3MzI4Nzk1MDAsImp0aSI6ImQ3ZGI0ODQ0LTUxMWYtNGM3Ny05MjY2LTYzYjdmYTY3YWRmMiJ9.Opw4b20Afaegy1eTYZCIdfnE2IS2oqXGQXcOu-wfrGo")
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
            .setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUwMDUxIiwic3ViIjoidXNlcjEiLCJyb2xlIjoxLCJleHAiOjE3MzI4Nzg5NjUsImp0aSI6ImQxNjU4ZTY4LTdjYmUtNGQ1OC04ODE3LTMxODI2ZDJiMDNhMyJ9.UfRFiziGmhwR9aQEDXUUvbgWdhzQOvC2y7jitW2SU2o")
            .build()

        val response = blockingStub.invalidateToken(request)

        if(response.hasSuccess()){
            println(response.success)
        }else{
            println(response.error)
        }
    }
}
