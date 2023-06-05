# JWT Security

To Login you need have user accaunt, to register use 
/api/auth/registration endpoint. If you already have account
use /api/auth/login and specify your username and password 
to get JWT token. After that to get access to secure endpoints 
you need use Baerer Authentication and add in your request header 
[Authentication]: "Bearer 'JWT token'".

JwtAuthenticationEntryPoint - check if is error in request,
throw  UNAUTHORIZED exception or INTERNAL_SERVER_ERROR exception.

JwtAuthenticationFilter - before UsernamePasswordAuth filter
will work, check JWT token in request header. If token not valid
or not exist throw exception, else use JwtTokenProvider to get
userId from token, load UserDetails by userId, and pass it to
SecurityContextHolder to  authenticate user.

JwtTokenProvider - utility class for generate JWT token and
parse it. User NimbusJwtDecoder and Encoder with RSA public key.

JwtTokenValidator - validate token by NimbusJwtDecoder, if token
not valid throw exception.
